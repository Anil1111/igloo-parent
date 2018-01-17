package org.iglooproject.wicket.more.markup.html.bootstrap.label.model;

public enum BootstrapColor implements IBootstrapColor {
	@Deprecated
	DEFAULT("default"),
	PRIMARY("primary"),
	SECONDARY("secondary"),
	SUCCESS("success"),
	INFO("info"),
	TODO("todo"),
	WARNING("warning"),
	WARNING_SEVERE("warning-severe"),
	DANGER("danger");
	
	private final String cssClassSuffix;
	
	private BootstrapColor(String cssClassSuffix) {
		this.cssClassSuffix = cssClassSuffix;
	}
	
	@Override
	public String getCssClassSuffix() {
		return cssClassSuffix;
	}

}
