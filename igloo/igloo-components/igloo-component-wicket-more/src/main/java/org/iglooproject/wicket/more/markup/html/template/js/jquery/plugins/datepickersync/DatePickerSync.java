package org.iglooproject.wicket.more.markup.html.template.js.jquery.plugins.datepickersync;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.wicketstuff.wiquery.core.javascript.ChainableStatement;
import org.wicketstuff.wiquery.core.javascript.JsStatement;
import org.wicketstuff.wiquery.core.options.Options;
import org.wicketstuff.wiquery.ui.datepicker.DateOption;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import org.iglooproject.commons.util.CloneUtils;
import org.iglooproject.wicket.more.markup.html.form.DatePicker;

public class DatePickerSync implements ChainableStatement, IDetachable, Serializable {

	private static final long serialVersionUID = 5661268666461455313L;
	
	private static final String DATE_PICKER_SYNC = "datePickerSync";
	
	private static final String JS_ARRAY_START = "[";
	private static final String JS_ARRAY_CLOSE = "]";
	
	@Deprecated
	private final DatePicker courant;
	
	private final List<DatePicker> precedents = Lists.newArrayList();
	private final List<IModel<? extends Date>> precedentsModels = Lists.newArrayList();
	
	private final List<DatePicker> suivants = Lists.newArrayList();
	private final List<IModel<? extends Date>> suivantsModels = Lists.newArrayList();

	private final DatePickerSyncActionOnUpdate actionOnUpdate;

	public DatePickerSync() {
		this.courant = null;
		this.actionOnUpdate = DatePickerSyncActionOnUpdate.NOTHING;
	}

	/**
	 * @deprecated Use {@link #DatePickerSync(DatePicker, DatePicker, DatePickerSyncActionOnUpdate)} instead with
	 * {@link DatePickerSyncActionOnUpdate#NOTHING}
	 * @param courant
	 */
	public DatePickerSync(DatePicker precedent, DatePicker suivant) {
		this(precedent, suivant, DatePickerSyncActionOnUpdate.NOTHING);
	}

	public DatePickerSync(DatePicker precedent, DatePicker suivant, DatePickerSyncActionOnUpdate actionOnUpdate) {
		this.courant = null;
		if (precedent != null) {
			addPrecedents(precedent);
		}
		if (suivant != null) {
			addSuivants(suivant);
		}
		this.actionOnUpdate = actionOnUpdate;
	}
	
	/**
	 * @deprecated Use {@link #DatePickerSync()} instead.
	 * @param courant
	 */
	@Deprecated
	public DatePickerSync(DatePicker courant) {
		this(courant, (DatePicker) null, (DatePicker) null);
	}

	/**
	 * @deprecated Use {@link #DatePickerSync(DatePicker, DatePicker, DatePickerSyncActionOnUpdateS)} instead with
	 * {@link DatePickerSyncActionOnUpdate#NOTHING}
	 * @param courant
	 */
	public DatePickerSync(DatePicker courant, DatePicker precedent, DatePicker suivant) {
		super();
		
		Args.notNull(courant, "courant");
		this.courant = courant;
		
		if (precedent != null) {
			addPrecedents(precedent);
		}
		if (suivant != null) {
			addSuivants(suivant);
		}
		this.actionOnUpdate = DatePickerSyncActionOnUpdate.NOTHING;
	}
	
	@Override
	public String chainLabel() {
		return DATE_PICKER_SYNC;
	}
	
	@Override
	public CharSequence[] statementArgs() {
		Options options = new Options();
		
		if (!precedents.isEmpty()) {
			options.put("precedents", componentsToJSArray(precedents));
		}
		
		Collection<Date> precedentsModelsDates = getDates(precedentsModels);
		if (!precedentsModelsDates.isEmpty()) {
			options.put("precedentsModelsMaxDate", new DateOption(Ordering.<Date>natural().max(precedentsModelsDates)));
		}
		
		if (!suivants.isEmpty()) {
			options.put("suivants", componentsToJSArray(suivants));
		}
		
		Collection<Date> suivantsModelsDates = getDates(suivantsModels);
		if (!suivantsModelsDates.isEmpty()) {
			options.put("suivantsModelsMinDate", new DateOption(Ordering.<Date>natural().min(suivantsModelsDates)));
		}
		
		if (actionOnUpdate != null) {
			options.putLiteral("actionOnUpdate", actionOnUpdate.name());
		} else {
			options.putLiteral("actionOnUpdate", DatePickerSyncActionOnUpdate.NOTHING.name());
		}
		
		return new CharSequence[] { options.getJavaScriptOptions() };
	}
	
	public List<DatePicker> getPrecedents() {
		return CloneUtils.clone(precedents);
	}
	
	public DatePickerSync addPrecedents(DatePicker first, DatePicker ... rest) {
		for (DatePicker precedent : Lists.asList(first, rest)) {
			if (precedent != null) {
				if (precedent.equals(courant)) {
					throw new IllegalArgumentException("Le date picker courant ne peut pas être ajouté en tant que précédent.");
				}
				if (!this.precedents.contains(precedent)) {
					this.precedents.add(precedent);
				}
			}
		}
		return this;
	}
	
	public List<IModel<? extends Date>> getPrecedentsModels() {
		return CloneUtils.clone(precedentsModels);
	}
	
	@SafeVarargs
	public final DatePickerSync addPrecedentsModels(IModel<? extends Date> first, IModel<? extends Date> ... rest) {
		for (IModel<? extends Date> precedent : Lists.asList(first, rest)) {
			if (precedent != null) {
				if (!this.precedentsModels.contains(precedent)) {
					this.precedentsModels.add(precedent);
				}
			}
		}
		return this;
	}
	
	public List<DatePicker> getSuivants() {
		return CloneUtils.clone(suivants);
	}
	
	public DatePickerSync addSuivants(DatePicker first, DatePicker ... rest) {
		for (DatePicker suivant : Lists.asList(first, rest)) {
			if (suivant != null) {
				if (suivant.equals(courant)) {
					throw new IllegalArgumentException("Le date picker courant ne peut pas être ajouté en tant que suivant.");
				}
				if (!this.suivants.contains(suivant)) {
					this.suivants.add(suivant);
				}
			}
		}
		return this;
	}
	
	public List<IModel<? extends Date>> getSuivantsModels() {
		return CloneUtils.clone(suivantsModels);
	}

	@SafeVarargs
	public final DatePickerSync addSuivantsModels(IModel<? extends Date> first, IModel<? extends Date> ... rest) {
		for (IModel<? extends Date> suivant : Lists.asList(first, rest)) {
			if (suivant != null) {
				if (!this.suivantsModels.contains(suivant)) {
					this.suivantsModels.add(suivant);
				}
			}
		}
		return this;
	}
	
	private static String componentsToJSArray(Collection<? extends Component> components) {
		StringBuilder componentsSb = new StringBuilder(JS_ARRAY_START);
		for (Component component : components) {
			if (componentsSb.length() > JS_ARRAY_START.length()) {
				componentsSb.append(", ");
			}
			componentsSb.append(new JsStatement().$(component).render(false).toString());
		}
		componentsSb.append(JS_ARRAY_CLOSE);
		return componentsSb.toString();
	}
	
	private static Collection<Date> getDates(Collection<? extends IModel<? extends Date>> models) {
		List<Date> result = Lists.newArrayList();
		for (IModel<? extends Date> model : models) {
			Date date = model.getObject();
			if (date != null) {
				result.add(date);
			}
		}
		return result;
	}
	
	@Override
	public void detach() {
		for (IModel<?> model : precedentsModels) {
			model.detach();
		}
		for (IModel<?> model : suivantsModels) {
			model.detach();
		}
	}
}
