package org.iglooproject.wicket.bootstrap4.console.maintenance.task.component;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Classes;
import org.iglooproject.jpa.more.business.task.util.TaskStatus;
import org.iglooproject.wicket.markup.html.panel.GenericPanel;
import org.iglooproject.wicket.more.condition.Condition;
import org.iglooproject.wicket.more.markup.html.basic.ComponentBooleanProperty;
import org.iglooproject.wicket.more.markup.html.basic.ComponentBooleanPropertyBehavior;
import org.iglooproject.wicket.more.markup.html.basic.impl.AbstractConfigurableComponentBooleanPropertyBehavior.Operator;

public class TaskStatusPanel extends GenericPanel<TaskStatus> {

	private static final long serialVersionUID = -7752211078831691283L;

	private boolean hideIfEmpty;
	
	private String faSize = "fa-lg";
	
	public TaskStatusPanel(String id, final IModel<TaskStatus> statusModel) {
		super(id, statusModel);
		
		add(
				new WebMarkupContainer("status") {
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void onComponentTag(ComponentTag tag) {
						super.onComponentTag(tag);
						StringBuilder iconClass = new StringBuilder(faSize);
						TaskStatus status = statusModel.getObject();
						if (status != null) {
							switch (status) {
								case TO_RUN:
									iconClass.append(" fa-clock text-info");
									break;
								case RUNNING:
									iconClass.append(" fa-redo text-info");
									break;
								case COMPLETED:
									iconClass.append(" fa-check-circle text-success");
									break;
								case FAILED:
									iconClass.append(" fa-times-circle text-danger");
									break;
								case INTERRUPTED:
									iconClass.append(" fa-pause-circle text-danger");
									break;
								case CANCELLED:
									iconClass.append(" fa-ban text-danger");
									break;
							}
							tag.append("class", iconClass, " ");
							tag.put("title", getString(Classes.simpleName(TaskStatus.class) + "." + status.name()));
						}
					}
				}
				.add(Condition.modelNotNull(statusModel).thenShow())
		);
		
		add(
				new ComponentBooleanPropertyBehavior(ComponentBooleanProperty.VISIBILITY_ALLOWED, Operator.WHEN_ANY_TRUE) {
					private static final long serialVersionUID = 1L;
					
					@Override
					public boolean isEnabled(Component component) {
						return hideIfEmpty;
					}
					
				}.condition(Condition.modelNotNull(statusModel))
		);
	}
	
	public TaskStatusPanel hideIfEmpty() {
		hideIfEmpty = true;
		return this;
	}
	
	public TaskStatusPanel faSize(String faSize) {
		this.faSize = faSize;
		return this;
	}
}