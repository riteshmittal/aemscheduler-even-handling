package com.aem.community.core.services;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = EventListener.class, immediate = true)
public class EventListenerExample implements EventListener {

	private static final String APPS_MY_AEM_PROJECT = "/apps/myAEMProject";

	private static final Logger log = LoggerFactory.getLogger(EventListenerExample.class);

	@Reference
	private ResourceResolverFactory resolverFactory;

	private ResourceResolver resolver;

	@Reference
	private SlingRepository repository;

	private Session session;

	@Activate
	protected void activate(ComponentContext componentContext) {

		try {
			getServiceResourceResolver();
			session = resolver.adaptTo(Session.class);
			addEvent();
		} catch (LoginException | RepositoryException e) {
			log.error("Exception occurred {}", e);
		}
	}

	@Override
	public void onEvent(EventIterator myEvents) {
		try {
			while (myEvents.hasNext()) {
				String eventPath = myEvents.nextEvent().getPath();
				Resource resource = resolver.getResource(eventPath);
				log.info("resource ={} ", resource);
			}
		} catch (RepositoryException e) {
			log.error("Exception occurred {}", e);
		}
	}

	@Deactivate
	protected void deactivate() {
		if (session != null) {
			session.logout();
		}
	}

	private void getServiceResourceResolver() throws LoginException {
		Map<String, Object> params = new HashMap<>();
		params.put(ResourceResolverFactory.SUBSERVICE, "myEventService");
		resolver = resolverFactory.getServiceResourceResolver(params);
	}

	private void addEvent() throws UnsupportedRepositoryOperationException, RepositoryException {

		ObservationManager observationManager = session.getWorkspace().getObservationManager();
		observationManager.addEventListener(this,
				Event.PROPERTY_ADDED | Event.NODE_ADDED | Event.NODE_REMOVED | Event.PROPERTY_CHANGED,
				APPS_MY_AEM_PROJECT, true, null, null, false);
	}
}