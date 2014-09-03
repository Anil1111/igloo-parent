package fr.openwide.core.wicket.more.link.descriptor.builder.impl;

import static fr.openwide.core.wicket.more.condition.Condition.anyPermission;

import java.util.Collection;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.bindgen.BindingRoot;
import org.bindgen.binding.AbstractBinding;
import org.springframework.core.convert.TypeDescriptor;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.wicket.more.condition.Condition;
import fr.openwide.core.wicket.more.link.descriptor.ILinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.builder.state.IAddedParameterMappingState;
import fr.openwide.core.wicket.more.link.descriptor.builder.state.IParameterMappingState;
import fr.openwide.core.wicket.more.link.descriptor.parameter.mapping.CollectionLinkParameterMappingEntry;
import fr.openwide.core.wicket.more.link.descriptor.parameter.mapping.ILinkParameterMappingEntry;
import fr.openwide.core.wicket.more.link.descriptor.parameter.mapping.InjectOnlyLinkParameterMappingEntry;
import fr.openwide.core.wicket.more.link.descriptor.parameter.mapping.LinkParametersMapping;
import fr.openwide.core.wicket.more.link.descriptor.parameter.mapping.SimpleLinkParameterMappingEntry;
import fr.openwide.core.wicket.more.link.descriptor.parameter.validator.ConditionLinkParameterValidator;
import fr.openwide.core.wicket.more.link.descriptor.parameter.validator.ILinkParameterValidator;
import fr.openwide.core.wicket.more.link.descriptor.parameter.validator.LinkParameterValidators;
import fr.openwide.core.wicket.more.model.BindingModel;

public class CoreLinkDescriptorBuilderParametersStateImpl<L extends ILinkDescriptor>
		implements IParameterMappingState<L>, IAddedParameterMappingState<L> {
	
	private final CoreLinkDescriptorBuilderFactory<L> factory;
	private final Collection<ILinkParameterMappingEntry> parameterMappingEntries;
	private final Collection<ILinkParameterValidator> parameterValidators;
	
	private ILinkParameterMappingEntry lastAddedParameterMappingEntry;
	
	public CoreLinkDescriptorBuilderParametersStateImpl(CoreLinkDescriptorBuilderFactory<L> factory) {
		this.factory = factory;
		this.parameterMappingEntries = Lists.newArrayList();
		this.parameterValidators = Lists.newArrayList();
	}

	@Override
	public <T> IAddedParameterMappingState<L> map(String name, IModel<T> valueModel, Class<T> valueType) {
		Args.notNull(name, "name");
		Args.notNull(valueModel, "valueModel");
		Args.notNull(valueType, "valueType");

		return map(new SimpleLinkParameterMappingEntry<T>(name, valueModel, valueType));
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public <RawC extends Collection, C extends RawC, T> IAddedParameterMappingState<L> mapCollection(
			String parameterName, IModel<C> valueModel, Class<RawC> rawCollectionType, Class<T> elementType) {
		return mapCollection(parameterName, valueModel, rawCollectionType, TypeDescriptor.valueOf(elementType));
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public <RawC extends Collection, C extends RawC, T> IAddedParameterMappingState<L> mapCollection(
			String parameterName, IModel<C> valueModel, Class<RawC> rawCollectionType, TypeDescriptor elementTypeDescriptor) {
		return map(new CollectionLinkParameterMappingEntry<RawC, C>(
				parameterName, valueModel, rawCollectionType, elementTypeDescriptor
				));
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public <RawC extends Collection, C extends RawC, T> IAddedParameterMappingState<L> mapCollection(
			String parameterName, IModel<C> valueModel, Class<RawC> rawCollectionType, TypeDescriptor elementTypeDescriptor,
			Supplier<C> emptyCollectionSupplier) {
		return map(new CollectionLinkParameterMappingEntry<RawC, C>(
				parameterName, valueModel, rawCollectionType, elementTypeDescriptor, emptyCollectionSupplier
				));
	}
	
	@Override
	public IAddedParameterMappingState<L> map(ILinkParameterMappingEntry parameterMappingEntry) {
		Args.notNull(parameterMappingEntry, "parameterMappingEntry");
		
		parameterMappingEntries.add(parameterMappingEntry);
		lastAddedParameterMappingEntry = parameterMappingEntry;
		
		return this;
	}
	
	@Override
	public <T> IAddedParameterMappingState<L> renderInUrl(String parameterName, IModel<T> valueModel) {
		return map(new InjectOnlyLinkParameterMappingEntry<>(parameterName, valueModel));
	}
	
	@Override
	public <R, T> IAddedParameterMappingState<L> renderInUrl(String parameterName, IModel<R> rootModel, AbstractBinding<R, T> binding) {
		return map(new InjectOnlyLinkParameterMappingEntry<>(parameterName, BindingModel.of(rootModel, binding)));
	}
	
	@Override
	public IParameterMappingState<L> optional() {
		return this;
	}
	
	@Override
	public IParameterMappingState<L> mandatory() {
		parameterValidators.add(lastAddedParameterMappingEntry.mandatoryValidator());
		return this;
	}

	@Override
	public IParameterMappingState<L> validator(ILinkParameterValidator validator) {
		Args.notNull(validator, "validator");
		parameterValidators.add(validator);
		return this;
	}

	@Override
	public IParameterMappingState<L> validator(Condition condition) {
		Args.notNull(condition, "condition");
		parameterValidators.add(new ConditionLinkParameterValidator(condition));
		return this;
	}
	
	@Override
	public IParameterMappingState<L> permission(IModel<?> model, String permissionName) {
		return validator(new ConditionLinkParameterValidator(Condition.permission(model, permissionName)));
	}
	
	@Override
	public IParameterMappingState<L> permission(IModel<?> model,
			String firstPermissionName, String... otherPermissionNames) {
		return validator(new ConditionLinkParameterValidator(anyPermission(model, firstPermissionName, otherPermissionNames)));
	}
	
	@Override
	public <R, T> IParameterMappingState<L> permission(
			IModel<R> model, BindingRoot<R, T> binding, String firstPermissionName, String... otherPermissionNames) {
		return permission(BindingModel.of(model, binding), firstPermissionName, otherPermissionNames);
	}
	
	@Override
	public final L build() {
		LinkParametersMapping parametersMapping = new LinkParametersMapping(parameterMappingEntries);
		ILinkParameterValidator validator = LinkParameterValidators.chain(parameterValidators);
		return factory.create(parametersMapping, validator);
	}

}
