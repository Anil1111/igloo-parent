package fr.openwide.core.wicket.more.link.descriptor.builder.impl;

import org.apache.wicket.Page;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.ResourceReference;
import org.springframework.util.Assert;

import fr.openwide.core.wicket.more.link.descriptor.IImageResourceLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.ILinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.IPageLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.IResourceLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.impl.CoreImageResourceLinkDescriptorImpl;
import fr.openwide.core.wicket.more.link.descriptor.impl.CorePageLinkDescriptorImpl;
import fr.openwide.core.wicket.more.link.descriptor.impl.CoreResourceLinkDescriptorImpl;
import fr.openwide.core.wicket.more.link.descriptor.parameter.mapping.ILinkParameterMappingEntry;
import fr.openwide.core.wicket.more.link.descriptor.parameter.mapping.LinkParametersMapping;
import fr.openwide.core.wicket.more.link.descriptor.parameter.validator.ILinkParameterValidator;
import fr.openwide.core.wicket.more.link.descriptor.parameter.validator.LinkParameterValidators;
import fr.openwide.core.wicket.more.model.ClassModel;
import fr.openwide.core.wicket.more.model.ReadOnlyModel;

public abstract class CoreLinkDescriptorBuilderFactory<T extends ILinkDescriptor> implements IBuilderFactory<T>, IDetachable {

	private static final long serialVersionUID = 1L;
	
	private CoreLinkDescriptorBuilderFactory() { }

	public static <P extends Page> CoreLinkDescriptorBuilderFactory<IPageLinkDescriptor> page(Class<P> pageClass) {
		IModel<Class<P>> pageClassModel = ClassModel.of(pageClass);

		return new CorePageLinkDescriptorBuilderFactory(pageClassModel);
	}

	public static CoreLinkDescriptorBuilderFactory<IPageLinkDescriptor> page(IModel<? extends Class<? extends Page>> pageClassModel) {
		return new CorePageLinkDescriptorBuilderFactory(pageClassModel);
	}
	
	@Override
	public T create(Iterable<? extends ILinkParameterMappingEntry> parameterMappingEntries,
			Iterable<? extends ILinkParameterValidator> validators) {
		LinkParametersMapping parametersMapping = new LinkParametersMapping(parameterMappingEntries);
		ILinkParameterValidator validator = LinkParameterValidators.chain(validators);
		return create(parametersMapping, validator);
	}
	
	protected abstract T create(LinkParametersMapping parametersMapping, ILinkParameterValidator validator);

	private static class CorePageLinkDescriptorBuilderFactory extends CoreLinkDescriptorBuilderFactory<IPageLinkDescriptor> {
		private static final long serialVersionUID = 1L;
		
		final IModel<Class<? extends Page>> readOnlyPageClassModel;
		
		public CorePageLinkDescriptorBuilderFactory(IModel<? extends Class<? extends Page>> pageClassModel) {
			super();
			Assert.notNull(pageClassModel, "pageClassModel cannot be null");
			this.readOnlyPageClassModel = ReadOnlyModel.of(pageClassModel);
		}

		@Override
		public IPageLinkDescriptor create(LinkParametersMapping parametersMapping, ILinkParameterValidator validator) {
			return new CorePageLinkDescriptorImpl(readOnlyPageClassModel, parametersMapping, validator);
		}
		
		@Override
		public void detach() {
			readOnlyPageClassModel.detach();
		}
	}
	
	public static CoreLinkDescriptorBuilderFactory<IResourceLinkDescriptor> resource(ResourceReference resourceReference) {
		return new CoreResourceLinkDescriptorBuilderFactory(new Model<ResourceReference>(resourceReference));
	}
	
	public static CoreLinkDescriptorBuilderFactory<IResourceLinkDescriptor> resource(IModel<? extends ResourceReference> resourceReferenceModel) {
		return new CoreResourceLinkDescriptorBuilderFactory(resourceReferenceModel);
	}
	
	private static class CoreResourceLinkDescriptorBuilderFactory extends CoreLinkDescriptorBuilderFactory<IResourceLinkDescriptor> {
		private static final long serialVersionUID = 1L;
		
		private IModel<ResourceReference> resourceReferenceModel = null;
		
		public CoreResourceLinkDescriptorBuilderFactory(IModel<? extends ResourceReference> resourceReferenceModel) {
			super();
			Assert.notNull(resourceReferenceModel, "resourceReferenceModel cannot be null");
			this.resourceReferenceModel = ReadOnlyModel.of(resourceReferenceModel);
		}
		
		@Override
		public IResourceLinkDescriptor create(LinkParametersMapping parametersMapping, ILinkParameterValidator validator) {
			return new CoreResourceLinkDescriptorImpl(resourceReferenceModel, parametersMapping, validator);
		}

		@Override
		public void detach() {
			resourceReferenceModel.detach();
		}
	}
	
	public static CoreLinkDescriptorBuilderFactory<IImageResourceLinkDescriptor> imageResource(ResourceReference resourceReference) {
		return new CoreImageResourceLinkDescriptorBuilderFactory(new Model<ResourceReference>(resourceReference));
	}
	
	public static CoreLinkDescriptorBuilderFactory<IImageResourceLinkDescriptor> imageResource(IModel<? extends ResourceReference> resourceReferenceModel) {
		return new CoreImageResourceLinkDescriptorBuilderFactory(resourceReferenceModel);
	}
	
	private static class CoreImageResourceLinkDescriptorBuilderFactory extends CoreLinkDescriptorBuilderFactory<IImageResourceLinkDescriptor> {
		private static final long serialVersionUID = 1L;
		
		private IModel<ResourceReference> resourceReferenceModel = null;
		
		public CoreImageResourceLinkDescriptorBuilderFactory(IModel<? extends ResourceReference> resourceReferenceModel) {
			super();
			Assert.notNull(resourceReferenceModel, "resourceReferenceModel cannot be null");
			this.resourceReferenceModel = ReadOnlyModel.of(resourceReferenceModel);
		}
		
		@Override
		public IImageResourceLinkDescriptor create(LinkParametersMapping parametersMapping, ILinkParameterValidator validator) {
			return new CoreImageResourceLinkDescriptorImpl(resourceReferenceModel, parametersMapping, validator);
		}

		@Override
		public void detach() {
			resourceReferenceModel.detach();
		}
	}
}
