package org.iglooproject.wicket.more.markup.html.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import org.iglooproject.wicket.more.markup.repeater.collection.CollectionView;
import org.iglooproject.wicket.more.util.model.Models;

/**
 * An {@link AbstractGenericCollectionView} that uses item models which serialize and deserialize the item object when the page is serialized/deserialized.
 * <p>This view and its subclasses are especially useful when dealing with lists of non-database objects, such as {@link Date Dates}, {@link Enum Enums}, and
 * when item models must refer to the same value even when the list changed between a server response and the next client request (for example, when the user
 * clicks a button in the list).
 * <p>While this view supports lists, you may want to use {@link SerializedItemListView} when possible, since it provides more functionalities.
 * @see SerializedItemCollectionView
 * @see SerializedItemSetView
 * @see SerializedItemSortedSetView
 * @see SerializedItemListView
 * @deprecated use {@link CollectionView} with {@link Models} instead : 
 * <pre>
 * {@code 
 * new SerializedItem*View<T>("id", model);
 * ->
 * new CollectionView<T>("id", model, Models.<T>serializableModelFactory());
 * }
 * </pre>
 */
@Deprecated
public abstract class AbstractSerializedItemCollectionView<T extends Serializable, C extends Collection<? extends T>> extends AbstractGenericCollectionView<T, C> {

	private static final long serialVersionUID = -6717993103476751931L;
	
	public AbstractSerializedItemCollectionView(String id, IModel<? extends C> model) {
		super(id, model);
	}
	
	@Override
	protected IModel<T> getItemModel(T object) {
		return new Model<T>(object); // Model object is serialized along with the item 
	}

}
