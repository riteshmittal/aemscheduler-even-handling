package com.aem.community.core.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = EventHandler.class, property = {
		Constants.SERVICE_DESCRIPTION + "= This event handler listens the events on page activation",
		EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/ADDED",
		EventConstants.EVENT_FILTER + "=(path=/content/we-retail/us/en/men/*)"

})
public class EventHandlerExample implements EventHandler {

	private static final Logger log = LoggerFactory.getLogger(EventHandlerExample.class);

	@Reference
	private ResourceResolverFactory resolverFactory;

	private ResourceResolver resolver;

	@Reference
	JobManager jobManager;

	@Override
	public void handleEvent(Event event) {
		try {
			getServiceResourceResolver();
			log.info("Event properties : {}", event.getPropertyNames());
			String[] props = event.getPropertyNames();

			for (String prop : props) {
				switch (prop) {
				case "path":
					String resourcePath = event.getProperty("path").toString();
					Resource resource = resolver.getResource(resourcePath);
					log.info("Resource {}", resource);
					Map<String, Object> properties = new HashMap<String, Object>();
					properties.put("path", resourcePath);
					jobManager.addJob("aem/myjob", properties);
					break;

				case "anotherProp":
					// some logic here
					break;
				default:
					// some fallback use case
					log.info("Default case");
				}
			}
		} catch (LoginException e) {
			log.error("Exception occurred", e);
		}
	}

	private void getServiceResourceResolver() throws LoginException {
		Map<String, Object> params = new HashMap<>();
		params.put(ResourceResolverFactory.SUBSERVICE, "myEventService");
		resolver = resolverFactory.getServiceResourceResolver(params);
	}
}