package fr.openwide.core.test.spring.config;

import fr.openwide.core.spring.config.AbstractConfigurer;

public class OwsiCoreTestConfigurer extends AbstractConfigurer {

	private static final String PROPERTY1 = "property1";
	private static final String PROPERTY2 = "property2";
	private static final String PROPERTY3 = "property3";
	private static final String PROPERTY4 = "property4";
	private static final String PROPERTY6 = "property6";
	public static final String PROPERTY6_DEFAULT = "default_value_6";
	
	public OwsiCoreTestConfigurer() {
	}
	
	public String getProperty1() {
		return getPropertyAsString(PROPERTY1);
	}
	
	public String getProperty2() {
		return getPropertyAsString(PROPERTY2);
	}
	
	public String getProperty3() {
		return getPropertyAsString(PROPERTY3);
	}
	
	public String getProperty4() {
		return getPropertyAsString(PROPERTY4);
	}
	
	public String getProperty6() {
		return getPropertyAsString(PROPERTY6, PROPERTY6_DEFAULT);
	}
}
