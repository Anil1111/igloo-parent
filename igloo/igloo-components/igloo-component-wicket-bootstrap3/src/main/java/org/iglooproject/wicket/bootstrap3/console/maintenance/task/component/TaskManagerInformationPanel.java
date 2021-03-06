package org.iglooproject.wicket.bootstrap3.console.maintenance.task.component;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.iglooproject.jpa.more.business.task.service.IQueuedTaskHolderManager;
import org.iglooproject.wicket.behavior.ClassAttributeAppender;
import org.iglooproject.wicket.bootstrap3.console.maintenance.task.page.ConsoleMaintenanceTaskListPage;
import org.iglooproject.wicket.markup.html.basic.CountLabel;
import org.iglooproject.wicket.more.markup.html.feedback.FeedbackUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskManagerInformationPanel extends Panel {

	private static final long serialVersionUID = 6507651164801791278L;

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskManagerInformationPanel.class);

	@SpringBean
	private IQueuedTaskHolderManager queuedTaskHolderManager;

	public TaskManagerInformationPanel(String id) {
		super(id);
		WebMarkupContainer statusContainer = new WebMarkupContainer("statusContainer");
		add(statusContainer);
		statusContainer.add(new ClassAttributeAppender(new Model<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				if (queuedTaskHolderManager.isActive()) {
					return "alert-success";
				} else {
					return "alert-danger";
				}
			}
		}));

		IModel<String> queueStatusStringModel = new IModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				if (queuedTaskHolderManager.isActive()) {
					return "active";
				} else {
					return "inactive";
				}
			}
		};

		statusContainer.add(new Label("status", new StringResourceModel("console.maintenance.task.manager.status.${}")
						.setModel(queueStatusStringModel)));

		add(new CountLabel("queueSize", "console.maintenance.task.manager.queueSize", new Model<Integer>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Integer getObject() {
				return queuedTaskHolderManager.getNumberOfWaitingTasks();
			}
		}));

		add(new AjaxLink<Void>("stop") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(queuedTaskHolderManager.isActive());
			}

			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					if (queuedTaskHolderManager.isAvailableForAction()) {
						queuedTaskHolderManager.stop();
						Session.get().success(getString("console.maintenance.task.manager.stop.success"));
					} else {
						Session.get().error(getString("console.maintenance.task.manager.action.unavailable"));
					}

					FeedbackUtils.refreshFeedback(target, getPage());
					throw new RestartResponseException(ConsoleMaintenanceTaskListPage.class);
				} catch (RestartResponseException e) {
					throw e;
				} catch (Exception e) {
					LOGGER.error("Unexpected error while trying to stop the task manager.", e);
					Session.get().error(getString("console.maintenance.task.manager.stop.error"));
				}

				FeedbackUtils.refreshFeedback(target, getPage());
			}
		});

		add(new AjaxLink<Void>("start") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(!queuedTaskHolderManager.isActive());
			}

			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					if (queuedTaskHolderManager.isAvailableForAction()) {
						queuedTaskHolderManager.start();
						Session.get().success(getString("console.maintenance.task.manager.start.success"));
					} else {
						Session.get().error(getString("console.maintenance.task.manager.action.unavailable"));
					}

					FeedbackUtils.refreshFeedback(target, getPage());
					throw new RestartResponseException(ConsoleMaintenanceTaskListPage.class);
				} catch (RestartResponseException e) {
					throw e;
				} catch (Exception e) {
					LOGGER.error("Unexpected error while trying to start the task manager.", e);
					Session.get().error(getString("console.maintenance.task.manager.start.error"));
				}

				FeedbackUtils.refreshFeedback(target, getPage());
			}
		});
	}
}
