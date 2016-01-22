package fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;
import org.wicketstuff.wiquery.core.events.Event;
import org.wicketstuff.wiquery.core.events.MouseEvent;
import org.wicketstuff.wiquery.core.events.WiQueryEventBehavior;
import org.wicketstuff.wiquery.core.javascript.JsScope;
import org.wicketstuff.wiquery.core.javascript.JsScopeEvent;

import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.behavior.ConfirmContentBehavior;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.fluid.IAjaxConfirmLinkBuilderStepStart;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.statement.BootstrapConfirmStatement;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.BootstrapModalJavaScriptResourceReference;

public abstract class AjaxConfirmLink<O> extends AjaxLink<O> {

	private static final long serialVersionUID = -645345859108195615L;
	
	public static <O> IAjaxConfirmLinkBuilderStepStart<O> build() {
		return new AjaxConfirmLinkBuilder<O>();
	}
	
	/**
	 * @deprecated Use {@link #build()} instead.
	 */
	@Deprecated
	public static <O> IAjaxConfirmLinkBuilderStepStart<O> build(String wicketId, IModel<O> model) {
		return new AjaxConfirmLinkBuilder<O>(wicketId, model);
	}
	
	/**
	 * @deprecated Use {@link #build()} instead.
	 */
	@Deprecated
	public static IAjaxConfirmLinkBuilderStepStart<Void> build(String wicketId) {
		return new AjaxConfirmLinkBuilder<Void>(wicketId, null);
	}

	protected AjaxConfirmLink(String id, IModel<O> model, IModel<String> titleModel, IModel<String> textModel,
			IModel<String> yesLabelModel, IModel<String> noLabelModel, IModel<String> cssClassNamesModel,
			boolean textNoEscape) {
		super(id, model);
		setOutputMarkupId(true);
		add(new ConfirmContentBehavior(titleModel, textModel, yesLabelModel, noLabelModel, cssClassNamesModel, textNoEscape));
		
		// Lors du clic, on ouvre la popup de confirmation. Si l'action est confirmée, 
		// on délenche un évènement 'confirm'.
		// l'événement click habituel est supprimé par surcharge de newAjaxEventBehavior ci-dessous
		Event clickEvent = new Event(MouseEvent.CLICK) {
			private static final long serialVersionUID = 1L;
			@Override
			public JsScope callback() {
				return JsScopeEvent.quickScope(BootstrapConfirmStatement.confirm(AjaxConfirmLink.this).append("event.preventDefault();"));
			}
		};
		add(new WiQueryEventBehavior(clickEvent) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isEnabled(Component component) {
				return AjaxConfirmLink.this.isEnabledInHierarchy();
			}
		});
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(BootstrapModalJavaScriptResourceReference.get()));
	}

	/**
	 * Cette méthode fournit normalement le handler pour l'événement onclick. On le remplace par l'événement de
	 * confirmation (le onclick est géré sans ajax au-dessus).
	 */
	@Override
	protected AjaxEventBehavior newAjaxEventBehavior(String event) {
		// Lorsque l'évènement 'confirm' est détecté, on déclenche l'action à proprement parler.
		return new AjaxEventBehavior("confirm") {
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isEnabled(Component component) {
				// On ajoute le handler seulement si le lien est activé
				return AjaxConfirmLink.this.isEnabledInHierarchy();
			}
			
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				onClick(target);
			}
			
			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				AjaxConfirmLink.this.updateAjaxAttributes(attributes);
			}
		};
	}
}
