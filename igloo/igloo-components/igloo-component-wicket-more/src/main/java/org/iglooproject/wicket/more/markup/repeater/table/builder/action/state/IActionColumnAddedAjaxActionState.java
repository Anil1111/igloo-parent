package org.iglooproject.wicket.more.markup.repeater.table.builder.action.state;

import org.apache.wicket.model.IModel;
import org.iglooproject.functional.SerializablePredicate2;
import org.iglooproject.wicket.more.condition.Condition;
import org.iglooproject.wicket.more.markup.html.factory.IDetachableFactory;
import org.springframework.security.acls.model.Permission;

public interface IActionColumnAddedAjaxActionState<T, I> extends IActionColumnAddedElementState<T, I> {

	@Override
	IActionColumnAddedAjaxActionState<T, I> showLabel();

	@Override
	IActionColumnAddedAjaxActionState<T, I> showLabel(Condition showLabelCondition);

	@Override
	IActionColumnAddedAjaxActionState<T, I> hideLabel();

	@Override
	IActionColumnAddedAjaxActionState<T, I> hideLabel(Condition hideLabelCondition);

	@Override
	IActionColumnAddedAjaxActionState<T, I> showTooltip();

	@Override
	IActionColumnAddedAjaxActionState<T, I> showTooltip(Condition showTooltipCondition);

	@Override
	IActionColumnAddedAjaxActionState<T, I> hideTooltip();

	@Override
	IActionColumnAddedAjaxActionState<T, I> hideTooltip(Condition hideTooltipCondition);

	@Override
	IActionColumnAddedAjaxActionState<T, I> showIcon();

	@Override
	IActionColumnAddedAjaxActionState<T, I> showIcon(Condition showIconCondition);

	@Override
	IActionColumnAddedAjaxActionState<T, I> hideIcon();

	@Override
	IActionColumnAddedAjaxActionState<T, I> hideIcon(Condition hideIconCondition);

	@Override
	IActionColumnAddedAjaxActionState<T, I> showPlaceholder();

	@Override
	IActionColumnAddedAjaxActionState<T, I> showPlaceholder(Condition showPlaceholderCondition);

	@Override
	IActionColumnAddedAjaxActionState<T, I> hidePlaceholder();

	@Override
	IActionColumnAddedAjaxActionState<T, I> hidePlaceholder(Condition hidePlaceholderCondition);

	@Override
	IActionColumnAddedAjaxActionState<T, I> when(IDetachableFactory<? super IModel<? extends T>, ? extends Condition> conditionFactory);

	@Override
	IActionColumnAddedAjaxActionState<T, I> when(Condition condition);

	@Override
	IActionColumnAddedAjaxActionState<T, I> whenPredicate(SerializablePredicate2<? super T> predicate);

	@Override
	IActionColumnAddedAjaxActionState<T, I> whenPermission(String permission);

	@Override
	IActionColumnAddedAjaxActionState<T, I> whenPermission(Permission permission);

	@Override
	IActionColumnAddedAjaxActionState<T, I> withClass(String cssClass);

}
