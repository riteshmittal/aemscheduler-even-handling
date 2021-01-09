package com.aem.community.core.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = SchedulerExample.class)
@Designate(ocd = SchedulerConfig.class)
public class SchedulerExample implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(SchedulerExample.class);

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private SlingRepository repository;

	@Reference
	private Scheduler scheduler;

	@Reference
	JobManager jobManager;

	@Activate
	protected void activate(SchedulerConfig config) {
		schedule(config);
	}

	@Modified
	protected void modified(SchedulerConfig config) {
		unschedule();
		schedule(config);
	}

	@Deactivate
	protected void deactivate(SchedulerConfig config) {
		unschedule();
	}

	private void unschedule() {
		scheduler.unschedule("myschedular");
	}

	private void schedule(SchedulerConfig config) {
		ScheduleOptions scheduleOptions = scheduler.EXPR(config.time());
		scheduleOptions.name("myschedular");
		scheduleOptions.canRunConcurrently(false);
		scheduler.schedule(this, scheduleOptions);
	}

	@Override
	public void run() {
		log.info("AEM sheduler is running");
		try {

			String dataFromAPI = "my data"; // assume we are calling API and getting this data

			try (ResourceResolver resourceResolver = getServiceResourceResolver()) {
				Resource resource = resourceResolver.getResource("/content/we-retail/ca/en/about-us/jcr:content");
				ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
				if (Optional.ofNullable(properties).isPresent()) {
					properties.put("myprop", dataFromAPI);
				}
				resourceResolver.commit();
			}

			// OR
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("path", "/content/we-retail/ca/en/about-us/jcr:content");
			jobManager.addJob("aem/myjob", properties);

		} catch (PersistenceException | LoginException e) {
			log.error("Exception occurred {}", e);
		}

	}

	private ResourceResolver getServiceResourceResolver() throws LoginException {
		Map<String, Object> params = new HashMap<>();
		params.put(ResourceResolverFactory.SUBSERVICE, "myEventService");
		return resolverFactory.getServiceResourceResolver(params);
	}
}