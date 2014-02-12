package fr.openwide.core.wicket.more.markup.html.template.flash.zeroclipboard;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class ZeroClipboardDataAttributeModifier extends Behavior {

	private static final long serialVersionUID = 2564881748188747597L;

	public static final String DATA_CLIPBOARD = "data-clipboard";

	public static final String DATA_CLIPBOARD_TEXT = "data-clipboard-text";

	private IModel<Boolean> dataClipboardModel;

	private IModel<String> dataClipboardTextModel;
	
	public ZeroClipboardDataAttributeModifier(IModel<String> dataClipboardTextModel) {
		this(Model.of(true), dataClipboardTextModel);
	}

	public ZeroClipboardDataAttributeModifier(IModel<Boolean> dataClipboardModel, IModel<String> dataClipboardTextModel) {
		this.dataClipboardModel = dataClipboardModel;
		this.dataClipboardTextModel = dataClipboardTextModel;
	}

	@Override
	public void onConfigure(Component component) {
		super.onConfigure(component);
		component.add(new AttributeModifier(DATA_CLIPBOARD, dataClipboardModel));
		component.add(new AttributeModifier(DATA_CLIPBOARD_TEXT, dataClipboardTextModel));
	}

	@Override
	public void detach(Component component) {
		if (dataClipboardModel != null) {
			dataClipboardModel.detach();
		}
		if (dataClipboardTextModel != null) {
			dataClipboardTextModel.detach();
		}
		super.detach(component);
	}
}