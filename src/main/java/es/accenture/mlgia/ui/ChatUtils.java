package es.accenture.mlgia.ui;

import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import es.accenture.mlgia.dto.MessageDTO;
import es.accenture.mlgia.ui.panel.ChatPanelUser;
import es.accenture.mlgia.ui.panel.ChatPanelWatson;

public class ChatUtils {

	private static String functionJS = "document.getElementById('mlgia-content-area').scrollTo(0,document.body.scrollHeight);";
	
	public static void getMessageWatson(VerticalLayout vlContentArea, MessageDTO messageDTO) {
		ChatPanelWatson panel = new ChatPanelWatson(messageDTO.getMessageOut());
		vlContentArea.addComponent(panel);
		vlContentArea.setComponentAlignment(panel, Alignment.TOP_LEFT);

		if(messageDTO.getMessagePredictOut()!=null && !messageDTO.getMessagePredictOut().isEmpty()) {
			ChatPanelWatson panelPredict = new ChatPanelWatson(messageDTO.getMessagePredictOut());
			vlContentArea.addComponent(panelPredict);
			vlContentArea.setComponentAlignment(panelPredict, Alignment.TOP_LEFT);
		}
		
		Page.getCurrent().getJavaScript().execute(ChatUtils.functionJS);
	}

	public static void getMessageUser(VerticalLayout vlContentArea, String message) {
		Panel panel = new ChatPanelUser(message);
		vlContentArea.addComponent(panel);
		vlContentArea.setComponentAlignment(panel, Alignment.TOP_LEFT);
		Page.getCurrent().getJavaScript().execute(ChatUtils.functionJS);
	}

}
