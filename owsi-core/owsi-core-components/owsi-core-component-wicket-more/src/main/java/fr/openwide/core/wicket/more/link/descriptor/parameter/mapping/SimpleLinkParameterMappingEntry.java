package fr.openwide.core.wicket.more.link.descriptor.parameter.mapping;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Args;
import org.javatuples.Unit;

import com.google.common.collect.ImmutableList;

import fr.openwide.core.wicket.more.link.descriptor.parameter.extractor.LinkParameterExtractionException;
import fr.openwide.core.wicket.more.link.descriptor.parameter.injector.LinkParameterInjectionException;
import fr.openwide.core.wicket.more.link.descriptor.parameter.mapping.factory.AbstractLinkParameterMappingEntryFactory;
import fr.openwide.core.wicket.more.link.descriptor.parameter.mapping.factory.ILinkParameterMappingEntryFactory;
import fr.openwide.core.wicket.more.link.descriptor.parameter.validator.ILinkParameterValidator;
import fr.openwide.core.wicket.more.link.descriptor.parameter.validator.SimpleMandatoryLinkParameterValidator;
import fr.openwide.core.wicket.more.link.service.ILinkParameterConversionService;

public class SimpleLinkParameterMappingEntry<T> extends AbstractLinkParameterMappingEntry {
	
	private static final long serialVersionUID = -8490340879965229874L;
	
	public static <T> ILinkParameterMappingEntryFactory<Unit<IModel<T>>> factory(final String parameterName, final Class<T> valueType) {
		Args.notNull(parameterName, "parameterName");
		Args.notNull(valueType, "valueType");
		
		return new AbstractLinkParameterMappingEntryFactory<Unit<IModel<T>>>() {
			private static final long serialVersionUID = 1L;
			@Override
			public ILinkParameterMappingEntry create(Unit<IModel<T>> parameters) {
				return new SimpleLinkParameterMappingEntry<T>(parameterName, parameters.getValue0(), valueType);
			}
		};
	}
	
	protected final String parameterName;
	protected final IModel<T> mappedModel;
	protected final Class<T> mappedType;
	
	public SimpleLinkParameterMappingEntry(String parameterName, IModel<T> mappedModel, Class<T> mappedType) {
		this.parameterName = parameterName;
		this.mappedModel = mappedModel;
		this.mappedType = mappedType;
	}
	
	@Override
	public void inject(PageParameters targetParameters, ILinkParameterConversionService conversionService) throws LinkParameterInjectionException {
		inject(targetParameters, conversionService, parameterName, mappedModel.getObject());
	}
	
	@Override
	public void extract(PageParameters sourceParameters, ILinkParameterConversionService conversionService) throws LinkParameterExtractionException {
		mappedModel.setObject(extract(sourceParameters, conversionService, parameterName, mappedType));
	}
	
	@Override
	public ILinkParameterMappingEntry wrap(Component component) {
		IModel<T> wrappedModel = wrap(mappedModel, component);
		return new SimpleLinkParameterMappingEntry<T>(parameterName, wrappedModel, mappedType);
	}
	
	@Override
	public ILinkParameterValidator mandatoryValidator() {
		return new SimpleMandatoryLinkParameterValidator(ImmutableList.of(parameterName), ImmutableList.of(mappedModel));
	}

	@Override
	public void detach() {
		mappedModel.detach();
	}

}
