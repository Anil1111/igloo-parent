package fr.openwide.core.basicapp.core.config.application;

import fr.openwide.core.basicapp.core.config.util.Environment;
import fr.openwide.core.spring.config.CoreConfigurer;

public class BasicApplicationConfigurer extends CoreConfigurer {

	private static final Integer PORTFOLIO_ITEMS_PER_PAGE_DEFAULT_VALUE = 20;

	private static final Integer AUTOCOMPLETE_LIMIT_DEFAULT_VALUE = 20;

	public int getPortfolioItemsPerPage() {
		return getPropertyAsInteger("portfolio.itemsPerPage", PORTFOLIO_ITEMS_PER_PAGE_DEFAULT_VALUE);
	}

	public Environment getEnvironment() {
		return getPropertyAsEnum("environment", Environment.class, Environment.production);
	}

	public String getMaintenanceUrl() {
		return getPropertyAsString("maintenance.url");
	}

	public int getAutocompleteLimit() {
		return getPropertyAsInteger("autocomplete.limit", AUTOCOMPLETE_LIMIT_DEFAULT_VALUE);
	}

}
