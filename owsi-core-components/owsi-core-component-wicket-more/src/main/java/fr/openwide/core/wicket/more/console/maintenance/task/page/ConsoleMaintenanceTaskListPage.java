package fr.openwide.core.wicket.more.console.maintenance.task.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import fr.openwide.core.wicket.more.console.maintenance.task.component.TaskFilterPanel;
import fr.openwide.core.wicket.more.console.maintenance.task.component.TaskManagerInformationPanel;
import fr.openwide.core.wicket.more.console.maintenance.task.component.TaskResultsPanel;
import fr.openwide.core.wicket.more.console.maintenance.task.model.QueuedTaskHolderDataProvider;
import fr.openwide.core.wicket.more.console.maintenance.template.ConsoleMaintenanceTemplate;
import fr.openwide.core.wicket.more.console.template.ConsoleTemplate;
import fr.openwide.core.wicket.more.link.descriptor.IPageLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;

public class ConsoleMaintenanceTaskListPage extends ConsoleMaintenanceTemplate {

	private static final long serialVersionUID = -4085517848301335018L;

	public static final IPageLinkDescriptor linkDescriptor() {
		return new LinkDescriptorBuilder()
				.page(ConsoleMaintenanceTaskListPage.class)
				.build();
	}

	public ConsoleMaintenanceTaskListPage(PageParameters parameters) {
		super(parameters);

		addHeadPageTitleKey("console.maintenance.tasks");

		QueuedTaskHolderDataProvider queuedTaskHolderDataProvider = new QueuedTaskHolderDataProvider();

		TaskResultsPanel resultsPanel = new TaskResultsPanel("resultsPanel", queuedTaskHolderDataProvider, 20);
		add(resultsPanel);

		add(new TaskManagerInformationPanel("taskManagerInformationPanel"));

		add(new TaskFilterPanel("filterPanel", queuedTaskHolderDataProvider, resultsPanel.getPageable()));
	}

	@Override
	protected Class<? extends ConsoleTemplate> getMenuItemPageClass() {
		return ConsoleMaintenanceTaskListPage.class;
	}
}
