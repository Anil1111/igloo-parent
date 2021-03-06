package org.iglooproject.infinispan.utils;

import java.util.Properties;

import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationChildBuilder;
import org.infinispan.configuration.global.GlobalJmxStatisticsConfigurationBuilder;
import org.infinispan.configuration.global.TransportConfigurationBuilder;
import org.infinispan.jmx.PlatformMBeanServerLookup;

public class GlobalDefaultReplicatedTransientConfigurationBuilder extends GlobalConfigurationBuilder {

	public GlobalDefaultReplicatedTransientConfigurationBuilder() {
		this(null);
	}

	public GlobalDefaultReplicatedTransientConfigurationBuilder(Properties transportProperties) {
		super();
		// not planned to use JBoss, so we use directly PlatformMBeanServerLookup
		globalJmxStatistics().mBeanServerLookup(new PlatformMBeanServerLookup());
		Properties properties = new Properties();
		if (transportProperties != null) {
			properties.putAll(transportProperties);
		}
		
		// override configurationFile if passed as a property
		String jgroupsConfigurationFile = "default-configs/enhanced-jgroups-udp.xml";
		if (properties.containsKey("configurationFile")) {
			jgroupsConfigurationFile = properties.getProperty("configurationFile");
			properties.remove("configurationFile");
		}
		transport().defaultTransport().addProperty("configurationFile", jgroupsConfigurationFile);
		// Jgroups needs to lookup placeholder values in system properties
		System.getProperties().putAll(properties);
	}

	/**
	 * @see GlobalJmxStatisticsConfigurationBuilder#cacheManagerName(String)
	 */
	public GlobalDefaultReplicatedTransientConfigurationBuilder cacheManagerName(String cacheManagerName) {
		super.defaultCacheName(cacheManagerName);
		globalJmxStatistics().enable().cacheManagerName(cacheManagerName).jmxDomain(cacheManagerName);
		return this;
	}

	/**
	 * @see TransportConfigurationBuilder#nodeName(String)
	 */
	public GlobalConfigurationChildBuilder nodeName(String nodeName) {
		return transport().nodeName(nodeName);
	}

}
