package org.iglooproject.basicapp.web.application.referencedata.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import org.iglooproject.basicapp.core.business.referencedata.model.City;
import org.iglooproject.basicapp.web.application.referencedata.model.AbstractLocalizedReferenceDataDataProvider;
import org.iglooproject.jpa.more.business.generic.model.search.EnabledFilter;
import org.iglooproject.wicket.more.markup.html.feedback.FeedbackUtils;
import org.iglooproject.wicket.more.markup.html.form.EnumDropDownSingleChoice;
import org.iglooproject.wicket.more.markup.html.form.LabelPlaceholderBehavior;
import org.wicketstuff.wiquery.core.events.StateEvent;

public class CitySearchPanel extends Panel {
	
	private static final long serialVersionUID = -2395663840251286432L;

	public CitySearchPanel(String id, final AbstractLocalizedReferenceDataDataProvider<City, ?> dataProvider, final Component table) {
		super(id);
		
		Form<Void> form = new Form<Void>("form");
		add(form);
		
		form.add(
				new AjaxFormSubmitBehavior(form, StateEvent.CHANGE.getEventLabel()) {
					private static final long serialVersionUID = 1L;
					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						// Just in case the dataProvider's content was loaded before search parameters changed
						dataProvider.detach();
						
						target.add(table);
						FeedbackUtils.refreshFeedback(target, getPage());
					}
				}
		);
		
		form.add(
				new TextField<String>("label", dataProvider.getLabelModel(), String.class)
						.setLabel(new ResourceModel("business.localizedReferenceData.label"))
						.add(new LabelPlaceholderBehavior()),
				new TextField<String>("postalCode", dataProvider.getCodeModel(), String.class)
						.setLabel(new ResourceModel("business.city.postalCode"))
						.add(new LabelPlaceholderBehavior()),
				new EnumDropDownSingleChoice<EnabledFilter>("enabledFilter", dataProvider.getEnabledFilterModel(), EnabledFilter.class)
						.setLabel(new ResourceModel("business.referenceData.enabled.state"))
						.add(new LabelPlaceholderBehavior())
		);
	}
}
