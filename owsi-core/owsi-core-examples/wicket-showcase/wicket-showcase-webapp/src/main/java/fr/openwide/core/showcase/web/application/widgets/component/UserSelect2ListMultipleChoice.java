package fr.openwide.core.showcase.web.application.widgets.component;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;
import org.retzlaff.select2.Select2Behavior;

import fr.openwide.core.showcase.core.business.user.model.User;

public class UserSelect2ListMultipleChoice extends ListMultipleChoice<User> {
	
	private static final UserChoiceRenderer USER_CHOICE_RENDERER = new UserChoiceRenderer();
	
	public UserSelect2ListMultipleChoice(String id, IModel<? extends Collection<User>> model, List<? extends User> choices) {
		super(id, model, choices, USER_CHOICE_RENDERER);
		addSelect2Behavior();
	}

	public UserSelect2ListMultipleChoice(String id, List<? extends User> choices) {
		super(id, choices, USER_CHOICE_RENDERER);
		addSelect2Behavior();
	}
	
	private static final long serialVersionUID = -794964174746182964L;
	
	private User last;

	private void addSelect2Behavior() {
		Select2Behavior<User, User> behavior = Select2Behavior.forChoice(this);
		behavior.getSettings().setPlaceholderKey("widgets.selectbox.empty");
		behavior.getSettings().setNoMatchesKey("widgets.selectbox.noMatches");
		behavior.getSettings().setInputTooShortKey("widgets.selectbox.inputTooShort");
		behavior.getSettings().setSelectionTooBigKey("widgets.selectbox.selectionTooBig");
		behavior.getSettings().setMaximumSelectionSize(3);
		add(behavior);
	}
	
	private boolean isLast(int index) {
		return index - 1 == getChoices().size();
	}
	
	private boolean isFirst(int index) {
		return index == 0;
	}
	
	private boolean isNewGroup(User current) {
		return last == null || !current.getUserName().equals(last.getUserName());
	}
	
	private String getGroupLabel(User current) {
		return current.getUserName();
	}
	
	@Override
	protected void appendOptionHtml(AppendingStringBuffer buffer, User choice, int index, String selected) {
		if (isNewGroup(choice)) {
			if (!isFirst(index)) {
				buffer.append("</optgroup>");
			}
			buffer.append("<optgroup label='");
			buffer.append(Strings.escapeMarkup(getGroupLabel(choice)));
			buffer.append("'>");
		}
		super.appendOptionHtml(buffer, choice, index, selected);
		if (isLast(index)) {
			buffer.append("</optgroup>");
		}
		last = choice;
	}
	
	private static class UserChoiceRenderer extends ChoiceRenderer<User> {
		private static final long serialVersionUID = 1L;
		
		@Override
		public Object getDisplayValue(User user) {
			return user != null ? user.getFullName() : "";
		}
		
		@Override
		public String getIdValue(User user, int index) {
			return user != null ? String.valueOf(user.getId()) : "-1";
		}
	}
}
