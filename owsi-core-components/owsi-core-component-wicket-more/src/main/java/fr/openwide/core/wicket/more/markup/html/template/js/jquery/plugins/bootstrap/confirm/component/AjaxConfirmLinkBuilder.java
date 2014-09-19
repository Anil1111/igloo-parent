package fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

import fr.openwide.core.commons.util.functional.SerializableFunction;
import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.fluid.IAjaxConfirmLinkBuilderStepContent;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.fluid.IAjaxConfirmLinkBuilderStepEndContent;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.fluid.IAjaxConfirmLinkBuilderStepNo;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.fluid.IAjaxConfirmLinkBuilderStepOnclick;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.fluid.IAjaxConfirmLinkBuilderStepStart;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.fluid.IAjaxConfirmLinkBuilderStepTerminal;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.util.AjaxResponseAction;

public class AjaxConfirmLinkBuilder<O> implements IAjaxConfirmLinkBuilderStepStart<O>, IAjaxConfirmLinkBuilderStepContent<O>,
		IAjaxConfirmLinkBuilderStepEndContent<O>, IAjaxConfirmLinkBuilderStepNo<O>, IAjaxConfirmLinkBuilderStepOnclick<O>,
		IAjaxConfirmLinkBuilderStepTerminal<O> {

	private String wicketId;
	
	private IModel<O> model;
	
	private IModel<String> titleModel;
	
	private IModel<String> contentModel;
	
	private IModel<String> yesLabelModel;
	
	private IModel<String> noLabelModel;
	
	private IModel<String> cssClassNamesModel;
	
	private boolean keepMarkup = false;
	
	private AjaxResponseAction onClick;
	
	protected AjaxConfirmLinkBuilder(String wicketId, IModel<O> model) {
		this.wicketId = wicketId;
		this.model = model;
	}
	
	@Override
	public IAjaxConfirmLinkBuilderStepContent<O> title(IModel<String> titleModel) {
		this.titleModel = titleModel;
		return this;
	}
	
	@Override
	public IAjaxConfirmLinkBuilderStepEndContent<O> content(IModel<String> contentModel) {
		this.contentModel = contentModel;
		return this;
	}
	
	@Override
	public IAjaxConfirmLinkBuilderStepOnclick<O> deleteConfirmation() {
		confirm();
		this.titleModel = new ResourceModel("common.confirmTitle");
		if (model != null && model.getObject() instanceof GenericEntity<?, ?>) {
			GenericEntity<?, ?> genericEntity = (GenericEntity<?, ?>) model.getObject();
			this.contentModel = new StringResourceModel("common.deleteConfirmation.object", null, new Object[] { genericEntity.getDisplayName() });
		} else {
			this.contentModel = new ResourceModel("common.deleteConfirmation");
		}
		return this;
	}

	@Override
	public IAjaxConfirmLinkBuilderStepEndContent<O> keepMarkup() {
		this.keepMarkup = true;
		return this;
	}

	@Override
	public IAjaxConfirmLinkBuilderStepNo<O> yes(IModel<String> yesLabelModel) {
		this.yesLabelModel = yesLabelModel;
		return this;
	}
	
	@Override
	public IAjaxConfirmLinkBuilderStepOnclick<O> no(IModel<String> noLabelModel) {
		this.noLabelModel = noLabelModel;
		return this;
	}

	@Override
	public IAjaxConfirmLinkBuilderStepOnclick<O> yesNo() {
		this.yesLabelModel = new ResourceModel("common.yes");
		this.noLabelModel = new ResourceModel("common.no");
		return this;
	}

	@Override
	public IAjaxConfirmLinkBuilderStepOnclick<O> confirm() {
		this.yesLabelModel = new ResourceModel("common.confirm");
		this.noLabelModel = new ResourceModel("common.cancel");
		return this;
	}

	@Override
	public IAjaxConfirmLinkBuilderStepOnclick<O> validate() {
		this.yesLabelModel = new ResourceModel("common.validate");
		this.noLabelModel = new ResourceModel("common.cancel");
		return this;
	}

	@Override
	public IAjaxConfirmLinkBuilderStepOnclick<O> save() {
		this.yesLabelModel = new ResourceModel("common.save");
		this.noLabelModel = new ResourceModel("common.cancel");
		return this;
	}

	/**
	 * @deprecated Use {@link #onClick(AjaxResponseAction)} instead.
	 */
	@Deprecated
	@Override
	public IAjaxConfirmLinkBuilderStepTerminal<O> onClick(final SerializableFunction<AjaxRequestTarget, Void> onClick) {
		this.onClick = new AjaxResponseAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void execute(AjaxRequestTarget target) {
				onClick.apply(target);
			}
		};
		return this;
	}

	@Override
	public IAjaxConfirmLinkBuilderStepTerminal<O> onClick(AjaxResponseAction onClick) {
		this.onClick = onClick;
		return this;
	}

	@Override
	public AjaxConfirmLink<O> create() {
		try {
			return new FunctionalAjaxConfirmLink<O>(wicketId, model, titleModel, contentModel,
					yesLabelModel, noLabelModel, cssClassNamesModel, keepMarkup, onClick);
		} finally {
			wicketId = null;
			model = null;
			titleModel = null;
			contentModel = null;
			yesLabelModel = null;
			noLabelModel = null;
			cssClassNamesModel = null;
		}
	}
	
	private static class FunctionalAjaxConfirmLink<O> extends AjaxConfirmLink<O> {
		private static final long serialVersionUID = -2098954474307467112L;
		
		private final AjaxResponseAction onClick;
		
		public FunctionalAjaxConfirmLink(String id, IModel<O> model, IModel<String> titleModel, IModel<String> textModel,
				IModel<String> yesLabelModel, IModel<String> noLabelModel, IModel<String> cssClassNamesModel,
				boolean textNoEscape, AjaxResponseAction onClick) {
			super(id, model, titleModel, textModel, yesLabelModel, noLabelModel, cssClassNamesModel, textNoEscape);
			this.onClick = onClick;
		}
		
		@Override
		public void onClick(AjaxRequestTarget target) {
			this.onClick.execute(target);
			this.onClick.detach();
		}
		
	}

}