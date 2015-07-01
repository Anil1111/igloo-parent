package fr.openwide.core.wicket.settings.def;

import org.apache.wicket.settings.def.RequestCycleSettings;
import org.apache.wicket.util.time.Duration;

public class AdaptativeTimeoutRequestCycleSettings extends RequestCycleSettings {
	
	private Duration longRunningPageTimeout = Duration.minutes(10);

	public Duration getLongRunningPageTimeout() {
		return longRunningPageTimeout;
	}

	public void setLongRunningPageTimeout(Duration longRunningPageTimeout) {
		if (longRunningPageTimeout == null) {
			throw new IllegalArgumentException("timeout cannot be null");
		}
		this.longRunningPageTimeout = longRunningPageTimeout;
	}

}
