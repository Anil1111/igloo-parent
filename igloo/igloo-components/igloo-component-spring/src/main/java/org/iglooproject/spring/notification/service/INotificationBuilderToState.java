package org.iglooproject.spring.notification.service;

import java.util.Collection;

import org.iglooproject.spring.notification.model.INotificationRecipient;

public interface INotificationBuilderToState {
	
	/**
	 * @deprecated Use {@link #toAddress(String, String...)} instead.
	 */
	@Deprecated
	INotificationBuilderBuildState to(String... to);
	
	INotificationBuilderBuildState toAddress(String toFirst, String... toOthers);
	
	INotificationBuilderBuildState toAddress(Collection<String> to);
	
	INotificationBuilderBuildState to(INotificationRecipient toFirst, INotificationRecipient ... toOthers);

	INotificationBuilderBuildState to(Collection<? extends INotificationRecipient> to);
}
