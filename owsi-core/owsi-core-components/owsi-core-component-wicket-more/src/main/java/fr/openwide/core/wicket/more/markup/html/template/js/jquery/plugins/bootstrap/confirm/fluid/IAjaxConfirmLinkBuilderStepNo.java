package fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.fluid;

import org.apache.wicket.model.IModel;

public interface IAjaxConfirmLinkBuilderStepNo<O> {

	IAjaxConfirmLinkBuilderStepOnclick<O> no(IModel<String> noLabelModel);
	
	IAjaxConfirmLinkBuilderStepOnclick<O> no(IModel<String> noLabelModel, IModel<String> noIconModel);
	
	IAjaxConfirmLinkBuilderStepOnclick<O> no(IModel<String> noLabelModel, IModel<String> noIconModel, IModel<String> noButtonModel);
	
}
