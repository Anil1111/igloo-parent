package org.iglooproject.wicket.bootstrap4.console.maintenance.queuemanager.component;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.iglooproject.functional.Functions2;
import org.iglooproject.infinispan.model.INode;
import org.iglooproject.infinispan.service.IInfinispanClusterService;
import org.iglooproject.jpa.more.business.sort.ISort;
import org.iglooproject.jpa.more.business.task.service.IQueuedTaskHolderManager;
import org.iglooproject.jpa.more.infinispan.action.SwitchStatusQueueTaskManagerResult;
import org.iglooproject.jpa.more.infinispan.model.QueueTaskManagerStatus;
import org.iglooproject.jpa.more.infinispan.model.TaskQueueStatus;
import org.iglooproject.jpa.more.infinispan.service.IInfinispanQueueTaskManagerService;
import org.iglooproject.wicket.bootstrap4.console.maintenance.infinispan.renderer.INodeRenderer;
import org.iglooproject.wicket.bootstrap4.console.maintenance.queuemanager.renderer.QueueManagerRenderer;
import org.iglooproject.wicket.bootstrap4.console.maintenance.queuemanager.renderer.QueueTaskRenderer;
import org.iglooproject.wicket.bootstrap4.markup.html.bootstrap.component.BootstrapBadge;
import org.iglooproject.wicket.markup.html.basic.CoreLabel;
import org.iglooproject.wicket.more.condition.Condition;
import org.iglooproject.wicket.more.markup.html.factory.AbstractComponentFactory;
import org.iglooproject.wicket.more.markup.html.feedback.FeedbackUtils;
import org.iglooproject.wicket.more.markup.repeater.collection.CollectionView;
import org.iglooproject.wicket.more.markup.repeater.table.DecoratedCoreDataTablePanel.AddInPlacement;
import org.iglooproject.wicket.more.markup.repeater.table.builder.DataTableBuilder;
import org.iglooproject.wicket.more.markup.repeater.table.column.AbstractCoreColumn;
import org.iglooproject.wicket.more.model.BindingModel;
import org.iglooproject.wicket.more.model.ReadOnlyCollectionModel;
import org.iglooproject.wicket.more.util.binding.CoreWicketMoreBindings;
import org.iglooproject.wicket.more.util.model.Detachables;
import org.iglooproject.wicket.more.util.model.Models;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleMaintenanceQueueManagerNodePanel extends Panel {

	private static final long serialVersionUID = -8384901751717369676L;

	public static final Logger LOGGER = LoggerFactory.getLogger(ConsoleMaintenanceQueueManagerNodePanel.class);

	@SpringBean
	private IInfinispanClusterService infinispanClusterService;

	@SpringBean
	private IInfinispanQueueTaskManagerService infinispanQueueTaskManagerService;

	private final IModel<List<INode>> nodesModel;

	public ConsoleMaintenanceQueueManagerNodePanel(String id) {
		super(id);
		setOutputMarkupId(true);

		nodesModel = new LoadableDetachableModel<List<INode>>() {
			private static final long serialVersionUID = 1L;
			@Override
			protected List<INode> load() {
				return infinispanClusterService.getAllNodes();
			}
		};

		add(
				new CollectionView<INode>("nodes", nodesModel, Models.<INode>serializableModelFactory()) {
					private static final long serialVersionUID = 1L;
					@Override
					protected void populateItem(Item<INode> item) {
						IModel<INode> nodeModel = item.getModel();
						item.add(
								new NodeFragment("node", nodeModel)
						);
					}
				}
		);
	}

	private class NodeFragment extends Fragment {

		private static final long serialVersionUID = 1L;

		private final IModel<QueueTaskManagerStatus> queueTaskManagerStatusModel;

		private final IModel<Collection<TaskQueueStatus>> taskQueuesStatusModel;

		public NodeFragment(String id, IModel<INode> nodeModel){
			super(id, "node", ConsoleMaintenanceQueueManagerNodePanel.this, nodeModel);
			setOutputMarkupId(true);

			queueTaskManagerStatusModel = new LoadableDetachableModel<QueueTaskManagerStatus>() {
				private static final long serialVersionUID = 1L;
				@Override
				protected QueueTaskManagerStatus load() {
					return infinispanQueueTaskManagerService.getQueueTaskManagerStatus(nodeModel.getObject());
				}
			};

			taskQueuesStatusModel = new LoadableDetachableModel<Collection<TaskQueueStatus>>() {
				private static final long serialVersionUID = 1L;
				@Override
				protected Collection<TaskQueueStatus> load() {
					if (queueTaskManagerStatusModel.getObject() == null) {
						return null;
					}
					return queueTaskManagerStatusModel.getObject().getTaskQueueStatusById().values();
				}
			};

			add(
					DataTableBuilder.start(
							ReadOnlyCollectionModel.of(taskQueuesStatusModel, Models.serializableModelFactory())
					)
							.addLabelColumn(
									new ResourceModel("business.queuemanager.queueid"),
									CoreWicketMoreBindings.taskQueueStatus().id()
							)
							.addBootstrapLabelColumn(
									new ResourceModel("business.queuemanager.status"),
									Functions2.identity(),
									QueueTaskRenderer.status()
							)
							.addColumn(
									new AbstractCoreColumn<TaskQueueStatus, ISort<?>>(new ResourceModel("business.queuemanager.thread")) {
										private static final long serialVersionUID = 1L;
										@Override
										public void populateItem(Item<ICellPopulator<TaskQueueStatus>> cellItem, String componentId, IModel<TaskQueueStatus> rowModel) {
											cellItem.add(
													new QueueThreadsFragment(componentId, "threads", rowModel.getObject().getNumberOfThreads())
											);
										}
									}
							)
							.addColumn(
									new AbstractCoreColumn<TaskQueueStatus, ISort<?>>(new ResourceModel("business.queuemanager.thread.running")) {
										private static final long serialVersionUID = 1L;
										@Override
										public void populateItem(Item<ICellPopulator<TaskQueueStatus>> cellItem, String componentId, IModel<TaskQueueStatus> rowModel) {
											cellItem.add(
													new QueueThreadsFragment(componentId, "threads", rowModel.getObject().getNumberOfRunningTasks())
											);
										}
									}
							)
							.addColumn(
									new AbstractCoreColumn<TaskQueueStatus, ISort<?>>(new ResourceModel("business.queuemanager.thread.waiting")) {
										private static final long serialVersionUID = 1L;
										@Override
										public void populateItem(Item<ICellPopulator<TaskQueueStatus>> cellItem, String componentId, IModel<TaskQueueStatus> rowModel) {
											cellItem.add(
													new QueueThreadsFragment(componentId, "threads", rowModel.getObject().getNumberOfWaitingTasks())
											);
										}
									}
							)
							.bootstrapCard()
									.title(new AbstractComponentFactory<Component>() {
										private static final long serialVersionUID = 1L;
										@Override
										public Component create(String wicketId) {
											return new NodeTitleFragment(wicketId, nodeModel, queueTaskManagerStatusModel);
										}
									})
									.addIn(AddInPlacement.HEADING_RIGHT, new AbstractComponentFactory<NodeActionsFragment>() {
										private static final long serialVersionUID = 1L;
										@Override
										public NodeActionsFragment create(String wicketId) {
											return new NodeActionsFragment(wicketId, nodeModel, queueTaskManagerStatusModel, NodeFragment.this);
										}
									})
									.responsive(Condition.alwaysTrue())
									.build("queueIds")
			);
		}

		@Override
		protected void onDetach() {
			super.onDetach();
			Detachables.detach(queueTaskManagerStatusModel, taskQueuesStatusModel);
		}

	}

	private class NodeActionsFragment extends Fragment{

		private static final long serialVersionUID = 1L;

		private final IModel<Boolean> isActive;

		public NodeActionsFragment(String id, IModel<INode> nodeModel, IModel<QueueTaskManagerStatus> queueTaskManagerStatusModel, NodeFragment nodeFragment) {
			super(id, "nodeActions", ConsoleMaintenanceQueueManagerNodePanel.this, nodeModel);
			setOutputMarkupId(true);

			isActive = new LoadableDetachableModel<Boolean>() {
				private static final long serialVersionUID = 1L;
				@Override
				protected Boolean load() {
					if(queueTaskManagerStatusModel.getObject() == null){
						return null;
					}
					return queueTaskManagerStatusModel.getObject().isQueueManagerActive();
				}
			};

			add(
					new AjaxLink<INode>("start", nodeModel) {
						private static final long serialVersionUID = 1L;
						@Override
						public void onClick(AjaxRequestTarget target) {
							try {
								SwitchStatusQueueTaskManagerResult result = infinispanQueueTaskManagerService.startQueueManager(nodeModel.getObject());
								if (SwitchStatusQueueTaskManagerResult.STARTED.equals(result)) {
									Session.get().success(getString("console.maintenance.queuemanager.actions.start.succes"));
								} else {
									Session.get().error(String.format("Erreur lors du démarrage du QueueTaskManager (%s)", result));
								}
								target.add(nodeFragment);
							} catch (Exception e) {
								LOGGER.error("Erreur lors du démarrage du QueueTaskManager", e);
								Session.get().error(getString("common.error.unexpected"));
							}
							FeedbackUtils.refreshFeedback(target, getPage());
						}
					}
							.add(
									Condition.isFalse(isActive)
											.thenShow()
							),
					new AjaxLink<INode>("stop", nodeModel) {
						private static final long serialVersionUID = 1L;
						@Override
						public void onClick(AjaxRequestTarget target) {
							try {
								SwitchStatusQueueTaskManagerResult result = infinispanQueueTaskManagerService.stopQueueManager(nodeModel.getObject());
								if (SwitchStatusQueueTaskManagerResult.STOPPED.equals(result)) {
									Session.get().success(getString("console.maintenance.queuemanager.actions.stop.succes"));
								} else {
									Session.get().error(String.format("Erreur lors de l'arrêt du QueueTaskManager (%s)", result));
								}
								target.add(nodeFragment);
							} catch (Exception e) {
								LOGGER.error("Erreur lors de l'arrêt du QueueTaskManager", e);
								Session.get().error(getString("common.error.unexpected"));
							}
							FeedbackUtils.refreshFeedback(target, getPage());
						}
					}
							.add(
									Condition.isTrue(isActive)
											.thenShow()
							)
			);
		}

		@Override
		protected void onDetach() {
			super.onDetach();
			Detachables.detach(isActive);
		}

	}

	private class NodeTitleFragment extends Fragment{

		private static final long serialVersionUID = 1L;

		public NodeTitleFragment(String id, IModel<INode> nodeModel, IModel<QueueTaskManagerStatus> queueTaskManagerStatusModel) {
			super(id, "nodeTitle", ConsoleMaintenanceQueueManagerNodePanel.this);
			setOutputMarkupId(true);

			add(
					new CoreLabel("node", nodeModel),
					new BootstrapBadge<>("local", BindingModel.of(nodeModel, CoreWicketMoreBindings.iNode()), INodeRenderer.local()),
					new BootstrapBadge<>("status", queueTaskManagerStatusModel, QueueManagerRenderer.status())
			);
		}

	}

	private class QueueThreadsFragment extends Fragment{

		private static final long serialVersionUID = 1L;

		@SpringBean
		private IQueuedTaskHolderManager queuedTaskHolderManager;

		private final IModel<Integer> nbThreadsModel;

		public QueueThreadsFragment(String id, String markupId, int nbThreads) {
			super(id, markupId, ConsoleMaintenanceQueueManagerNodePanel.this);
			setOutputMarkupId(true);

			nbThreadsModel = new LoadableDetachableModel<Integer>(){
				private static final long serialVersionUID = 1L;
				@Override
				protected Integer load() {
					return nbThreads;
				}
			};

			add(
					new CoreLabel("thread", nbThreadsModel)
			);
		}

		@Override
		protected void onDetach() {
			super.onDetach();
			Detachables.detach(nbThreadsModel);
		}

	}

	@Override
	protected void onDetach() {
		super.onDetach();
		Detachables.detach(nodesModel);
	}

}
