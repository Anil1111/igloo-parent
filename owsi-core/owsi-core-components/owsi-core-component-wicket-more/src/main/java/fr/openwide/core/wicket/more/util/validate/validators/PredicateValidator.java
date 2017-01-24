package fr.openwide.core.wicket.more.util.validate.validators;

import java.util.Objects;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import com.google.common.base.Predicate;

import fr.openwide.core.wicket.more.util.model.Detachables;

public class PredicateValidator<T> extends Behavior implements IValidator<T> {

	private static final long serialVersionUID = 2351404391379818307L;

	private final Predicate<? super T> predicate;

	private IModel<String> errorKeyModel;

	private IModel<String> errorMessageModel;

	public PredicateValidator<T> of(Predicate<? super T> predicate) {
		return new PredicateValidator<T>(predicate);
	}

	/**
	 * @deprecated Use {@link #of(Predicate)} and {@link PredicateValidator#errorKey(String)}
	 */
	@Deprecated
	public PredicateValidator(Predicate<? super T> predicate, String errorKey) {
		this(predicate);
		errorKey(errorKey);
	}

	public PredicateValidator(Predicate<? super T> predicate) {
		super();
		this.predicate = Objects.requireNonNull(predicate);
	}

	public Predicate<? super T> getPredicate() {
		return predicate;
	}

	public PredicateValidator<T> errorKey(String errorKey) {
		return errorKey(Model.of(errorKey));
	}

	public PredicateValidator<T> errorKey(IModel<String> errorKeyModel) {
		this.errorKeyModel = errorKeyModel;
		return this;
	}

	public PredicateValidator<T> errorMessage(IModel<String> errorMessageModel) {
		this.errorMessageModel = errorMessageModel;
		return this;
	}

	@Override
	public void validate(IValidatable<T> validatable) {
		if (!predicate.apply(validatable.getValue())) {
			validatable.error(decorate(new ValidationError(this)));
		}
	}

	private ValidationError decorate(ValidationError validationError) {
		if (errorKeyModel != null) {
			validationError.addKey(errorKeyModel.getObject());
		}
		if (errorMessageModel != null) {
			validationError.setMessage(errorMessageModel.getObject());
		}
		return validationError;
	}

	@Override
	public void detach(Component component) {
		super.detach(component);
		Detachables.detach(errorKeyModel, errorMessageModel);
	}

}
