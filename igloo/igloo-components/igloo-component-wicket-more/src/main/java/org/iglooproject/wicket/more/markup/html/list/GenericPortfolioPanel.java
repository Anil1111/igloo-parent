package org.iglooproject.wicket.more.markup.html.list;

import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import org.iglooproject.jpa.business.generic.model.GenericEntity;
import org.iglooproject.wicket.more.markup.html.navigation.paging.HideablePagingNavigator;

public abstract class GenericPortfolioPanel<E extends GenericEntity<Long, ?>> extends AbstractGenericItemListPanel<E> {

	private static final long serialVersionUID = 3343071412882576215L;
	
	public GenericPortfolioPanel(String id, IModel<? extends List<E>> listModel, int itemsPerPage) {
		super(id, listModel, itemsPerPage);
	}
	
	public GenericPortfolioPanel(String id, IDataProvider<E> dataProvider, int itemsPerPage) {
		super(id, dataProvider, itemsPerPage);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		add(new HideablePagingNavigator("pager", getDataView()));
	}

}