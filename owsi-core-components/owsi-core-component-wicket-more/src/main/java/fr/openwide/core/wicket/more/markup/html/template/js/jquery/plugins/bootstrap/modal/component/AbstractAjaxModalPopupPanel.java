package fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;

public abstract class AbstractAjaxModalPopupPanel<O> extends AbstractModalPopupPanel<O> implements IAjaxModalPopupPanel {

	private static final long serialVersionUID = 2483564542384270295L;

	private final boolean resetInputOnShow;

	private boolean shown = false;

	/**
	 * Le fonctionnement par défaut provoque le vidage des saisies utilisateur lors du show. Utiliser le constructeur
	 * avec le paramètre {@link AbstractAjaxModalPopupPanel#resetInputOnShow} si vous désirez conserver les saisies
	 * utilisateur après ouverture / fermeture du popup.
	 */
	public AbstractAjaxModalPopupPanel(String id, IModel<? extends O> model) {
		this(id, model, true);
	}

	/**
	 * 
	 * 
	 * @param id identifiant wicket du composant
	 * @param model modèle du composant
	 * @param resetInputOnShow indique si le ou les formulaires doivent être réinitialisée lors du show (ouverture). La
	 *                         réinitialisation consiste à ne pas prendre en compte les saisies utilisateur précédentes
	 *                         et à se baser intégralement sur le modèle. Ce fonctionnement implique un parcours du
	 *                         composant pour chercher les éléments de type {@link Form}
	 */
	public AbstractAjaxModalPopupPanel(String id, IModel<? extends O> model, boolean resetInputOnShow) {
		super(id, model);
		this.resetInputOnShow = resetInputOnShow;
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		getBody().setVisible(shown);
		getHeader().setVisible(shown);
		getFooter().setVisible(shown);
	}

	@Override
	public final void show(AjaxRequestTarget target) {
		shown = true;
		getBody().setVisible(shown);
		getHeader().setVisible(shown);
		getFooter().setVisible(shown);
		target.add(getContainer());
		onShow(target);
		if (resetInputOnShow) {
			visitChildren(Form.class, new IVisitor<Form<?>, Void>() {
				@Override
				public void component(Form<?> component, IVisit<Void> visit) {
					component.clearInput();
				}
			});
		}
		FeedbackUtils.refreshFeedback(target, getPage());
	}

	protected final void closePopup(AjaxRequestTarget target) {
		target.prependJavaScript(closeStatement().render(true));
	}

	protected void onShow(AjaxRequestTarget target) {
	}

}
