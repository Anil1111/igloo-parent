/*
 * Copyright (C) 2009-2011 Open Wide
 * Contact: contact@openwide.fr
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.openwide.core.wicket.more.markup.html.basic;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import org.bindgen.Binding;

import fr.openwide.core.jpa.more.business.generic.model.GenericListItem;
import fr.openwide.core.jpa.more.business.generic.model.GenericListItemBinding;
import fr.openwide.core.wicket.markup.html.basic.CoreLabel;

/**
 * Use {@code new CoreLabel(id, model)} and register a {@code Renderer} for the specific type instead.
 * Use {@code new CoreLabel(id, MyTypeRenderer.get(...).asModel(model))} to display with a non-default renderer.
 * @deprecated
 */
@Deprecated
public class GenericListItemLabel extends CoreLabel {

	private static final long serialVersionUID = -902689514465301799L;
	
	@SuppressWarnings("rawtypes")
	private static final GenericListItemBinding<?> GENERIC_LIST_ITEM_BINDING = new GenericListItemBinding();
	
	private final Binding<String> binding;

	public GenericListItemLabel(String id, IModel<? extends GenericListItem<?>> listItemModel, Binding<String> binding) {
		super(id, listItemModel);
		this.binding = binding;
	}
	
	public GenericListItemLabel(String id, IModel<? extends GenericListItem<?>> listItemModel) {
		this(id, listItemModel, GENERIC_LIST_ITEM_BINDING.label());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <C> IConverter<C> getConverter(Class<C> type) {
		if (GenericListItem.class.isAssignableFrom(type)) {
			return (IConverter<C>) new GenericListItemConverter(binding);
		} else {
			return super.getConverter(type);
		}
	}
	
}