package es.accenture.mlgia.ui.panel;

import com.github.appreciated.material.MaterialTheme;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class ChatPanelWatson extends HorizontalLayout {
	
	private static final long serialVersionUID = 1L;

	public ChatPanelWatson(String message) {
		// imagen  
		Image image = new Image("", new ThemeResource("img/watson.png"));
		addComponent(image);
		setComponentAlignment(image, Alignment.TOP_LEFT);
		
		// ContentPanel
		Panel contentPanel = new Panel();
		contentPanel.addStyleName(MaterialTheme.CARD_0_5);
		Label lbMessage = new Label(message);
		
		VerticalLayout vlContent = new VerticalLayout();
		vlContent.addComponents(lbMessage);
		vlContent.addStyleName("delete-padding");
		contentPanel.setContent(vlContent);
		
		addComponent(contentPanel);
		setComponentAlignment(image, Alignment.MIDDLE_LEFT);	
	}
	
}
