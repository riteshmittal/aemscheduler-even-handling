package com.aem.community.core.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "SchedulerConfig", description = "Scheduler config")
public @interface SchedulerConfig {

	@AttributeDefinition(name = "My Cron Expression", description = "My Cron Expression", type = AttributeType.STRING)
	public String time() default "0 */3 * ? * *";

}