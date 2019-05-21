package es.accenture.mlgia.ui.panel;

import com.github.appreciated.material.MaterialTheme;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class ChatPanelUser extends Panel {

	private static final long serialVersionUID = 1L;

	public ChatPanelUser(String message) {
		
		addStyleName(MaterialTheme.CARD_0_5);
		Label lbMessage = new Label(message);
		lbMessage.addStyleName("boxuser");
		HorizontalLayout hlMessage = new HorizontalLayout();
		hlMessage.addStyleName("delete-height");
		hlMessage.addComponents(lbMessage);
		hlMessage.setComponentAlignment(lbMessage,Alignment.MIDDLE_LEFT);
//		addStyleName("background: #00BCD4;");
		
		setContent(hlMessage);		
	}

}
