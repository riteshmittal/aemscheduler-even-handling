package com.aem.community.core.services;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = JobConsumer.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "=AEM demo for job consumer", JobConsumer.PROPERTY_TOPICS + "=aem/myjob" })
public class JobConsumerExample implements JobConsumer {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public JobResult process(Job job) {
		String path = (String) job.getProperty("path");
		logger.info("Job is " + "{}", path);
		return JobConsumer.JobResult.OK;
	}
}