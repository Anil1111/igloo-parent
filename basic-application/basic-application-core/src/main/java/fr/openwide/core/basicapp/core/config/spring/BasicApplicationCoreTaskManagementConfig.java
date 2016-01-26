package fr.openwide.core.basicapp.core.config.spring;

import java.util.Collection;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.openwide.core.basicapp.core.business.task.model.BasicApplicationTaskQueueId;
import fr.openwide.core.jpa.more.business.task.model.IQueueId;
import fr.openwide.core.jpa.more.config.spring.AbstractTaskManagementConfig;

@Configuration
public class BasicApplicationCoreTaskManagementConfig extends AbstractTaskManagementConfig {

	@Override
	@Bean
	public Collection<? extends IQueueId> queueIds() {
		return EnumUtils.getEnumList(BasicApplicationTaskQueueId.class);
	}

}
