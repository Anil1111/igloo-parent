package fr.openwide.core.wicket.more.link.descriptor;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.Url;

import fr.openwide.core.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import fr.openwide.core.wicket.more.link.descriptor.builder.state.IAddedParameterMappingState;
import fr.openwide.core.wicket.more.link.descriptor.builder.state.IValidatorState;
import fr.openwide.core.wicket.more.link.descriptor.parameter.validator.LinkParameterSerializedFormValidationException;
import fr.openwide.core.wicket.more.link.descriptor.parameter.validator.LinkParameterValidationRuntimeException;

/**
 * A {@link Link} whose parameters may change during the page life cycle (for instance on an Ajax refresh).
 * <p><strong>WARNING:</strong> if this link is rendered while its parameters are invalid, then a {@link LinkParameterValidationRuntimeException}
 * will be thrown when executing {@link #onComponentTag(org.apache.wicket.markup.ComponentTag) onComponentTag}. Similarly, if the target is invalid, then a
 * {@link LinkInvalidTargetRuntimeException} will be thrown.
 * By default, this should not happen as the link will be disabled unless it is completely valid. See
 * {@link #disableIfInvalid()} (the default), {@link #hideIfInvalid()} and {@link #throwExceptionIfInvalid()} for more information. 
 * @see LinkInvalidTargetRuntimeException
 * @see LinkParameterSerializedFormValidationException
 * @see LinkDescriptorBuilder
 * @see IAddedParameterMappingState#mandatory()
 * @see IAddedParameterMappingState#optional()
 * @see IValidatorState#validator(fr.openwide.core.wicket.more.link.descriptor.parameter.validator.ILinkParameterValidator)
 */
public abstract class AbstractDynamicBookmarkableLink extends Link<Void> {
	
	private static final long serialVersionUID = 1L;
	
	private static enum BehaviorIfInvalid {
		THROW_EXCEPTION {
			@Override
			protected void onConfigure(AbstractDynamicBookmarkableLink link) {
				link.setVisible(true); // If invalid, the exception will be thrown when rendering
			}
		},
		HIDE {
			@Override
			protected void onConfigure(AbstractDynamicBookmarkableLink link) {
				link.setVisible(link.isValid());
			}
		},
		DISABLE {
			@Override
			protected void onConfigure(AbstractDynamicBookmarkableLink link) {
				link.setEnabled(link.isValid());
			}
		};
		
		protected abstract void onConfigure(AbstractDynamicBookmarkableLink link);
	}
	
	private BehaviorIfInvalid behaviorIfInvalid = BehaviorIfInvalid.DISABLE;
	
	private boolean absolute = false;

	public AbstractDynamicBookmarkableLink(String wicketId) {
		super(wicketId);
	}
	
	/**
	 * @deprecated Use {@link #hideIfInvalid()} instead.
	 */
	@Deprecated
	public AbstractDynamicBookmarkableLink setAutoHideIfInvalid(boolean autoHideIfInvalid) {
		if (autoHideIfInvalid) {
			hideIfInvalid();
		} else {
			this.behaviorIfInvalid = BehaviorIfInvalid.THROW_EXCEPTION;
		}
		return this;
	}

	/**
	 * Sets the link up so that it will automatically hide (using {@link #setVisible(boolean)}) when its target or parameters are invalid.
	 * <p>Default behavior is to automatically disable the link.
	 */
	public AbstractDynamicBookmarkableLink hideIfInvalid() {
		this.behaviorIfInvalid = BehaviorIfInvalid.HIDE;
		return this;
	}

	/**
	 * Sets the link up so that it will automatically be disabled when its target or parameters are invalid.
	 * @deprecated This is the default behavior, so calling this method is generally useless. The method is here for
	 * compatibility reasons.
	 */
	@Deprecated
	public AbstractDynamicBookmarkableLink disableIfInvalid() {
		this.behaviorIfInvalid = BehaviorIfInvalid.DISABLE;
		return this;
	}

	/**
	 * Sets the link up so that it will throw a {@link LinkInvalidTargetRuntimeException} or a {@link LinkParameterValidationRuntimeException}
	 * if the target or the parameters are found to be invalid when executing {@link #onComponentTag(org.apache.wicket.markup.ComponentTag)}.
	 * <p>Default behavior is to automatically disable the link.
	 */
	public AbstractDynamicBookmarkableLink throwExceptionIfInvalid() {
		this.behaviorIfInvalid = BehaviorIfInvalid.THROW_EXCEPTION;
		return this;
	}
	
	/*
	 * TODO YRO Delete this once the Wicket team cleaned the AbstractLink class (will happen in Wicket 7, it seems)
	 */
	@Override
	protected void disableLink(ComponentTag tag) {
		setBeforeDisabledLink("");
		setAfterDisabledLink("");
		// if the tag is an anchor proper
		if (tag.getName().equalsIgnoreCase("a") || tag.getName().equalsIgnoreCase("link") ||
			tag.getName().equalsIgnoreCase("area"))
		{
			// Do NOT change anchor link to span tag (difference with super implementation)
//			tag.setName("span");

			// Remove any href from the old link
			tag.remove("href");

			tag.remove("onclick");
		}
		// if the tag is a button or input
		else if ("button".equalsIgnoreCase(tag.getName()) ||
			"input".equalsIgnoreCase(tag.getName()))
		{
			tag.put("disabled", "disabled");
		}
	}
	
	public boolean isAbsolute() {
		return absolute;
	}

	/**
	 * Sets whether the link should be absolute or relative.
	 * <p> Default is false: the link is relative by default.
	 * 
	 * @param absolute True to make the link absolute.
	 */
	public AbstractDynamicBookmarkableLink setAbsolute(boolean absolute) {
		this.absolute = absolute;
		return this;
	}

	protected abstract boolean isValid();

	@Override
	protected void onConfigure() {
		super.onConfigure();
		
		behaviorIfInvalid.onConfigure(this);
	}
	
	@Override
	protected final CharSequence getURL() throws LinkInvalidTargetRuntimeException, LinkParameterValidationRuntimeException {
		CharSequence relativeUrl = getRelativeURL();
		if (isAbsolute()) {
			return getRequestCycle().getUrlRenderer().renderFullUrl(Url.parse(relativeUrl));
		} else {
			return relativeUrl;
		}
	}
	
	protected abstract CharSequence getRelativeURL() throws LinkInvalidTargetRuntimeException, LinkParameterValidationRuntimeException;
	
	@Override
	protected boolean getStatelessHint() {
		return true;
	}
	
	/**
	 * No click event is allowed.
	 */
	@Override
	public final void onClick() {
		// Unused
	}

}
