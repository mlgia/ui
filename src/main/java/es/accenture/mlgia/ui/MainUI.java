package es.accenture.mlgia.ui;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.appreciated.material.MaterialTheme;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import es.accenture.mlgia.dto.MessageDTO;
import es.accenture.mlgia.ui.audio.AudioRecorder;
import es.accenture.mlgia.ui.consumer.ConsumerAssistant;
import es.accenture.mlgia.ui.consumer.ConsumerSpeechToText;
import es.accenture.mlgia.ui.consumer.ConsumerTextToSpeech;
import lombok.extern.slf4j.Slf4j;

@Push
@Slf4j
@SpringUI()
@Theme("mlgia")
@Viewport("width=device-width,initial-scale=1.0,user-scalable=no")
public class MainUI extends UI {

	private static final long serialVersionUID = 1L;
	public static final String TEST_FILE_PATH = "src/main/resources/";

	BeanValidationBinder<MessageDTO> binder;
	TextField tfQuery;
	Button btSendText;
	Label lbinfo;
	Panel contentPanel;
	VerticalLayout vlContentArea;
	VerticalLayout rootLayout;

	@Autowired
	ConsumerAssistant consumerAssistant;

	@Autowired
	ConsumerTextToSpeech consumerTextToSpeech;

	@Autowired
	ConsumerSpeechToText consumerSpeechToText;

	@Value("${audio.file.recorded.temp}")
	private String audioFileRecordedTemp;

	@Override
	protected void init(VaadinRequest request) {
		buildUI();
		initBinder();
		initConversation();
	}

	private void initBinder() {
		binder = new BeanValidationBinder<>(MessageDTO.class);
		binder.setBean(MessageDTO.builder().build());
		binder.forField(tfQuery).bind(MessageDTO::getMessageIn, MessageDTO::setMessageIn);
	}

	private void initConversation() {
		MessageDTO messageDTO = consumerAssistant.initAssistant();
		ChatUtils.getMessageWatson(vlContentArea, messageDTO);
		binder.getBean().setConversationId(messageDTO.getConversationId());
		//playSound(messageDTO.getMessageOut());
	}

	private void clickSendText(ClickEvent event) {
		if (binder.getBean().getMessageIn() != null && !binder.getBean().getMessageIn().isEmpty()) {
			ChatUtils.getMessageUser(vlContentArea, binder.getBean().getMessageIn());
			
			MessageDTO messageOut = consumerAssistant.invokeAssistant(binder.getBean());
			ChatUtils.getMessageWatson(vlContentArea, messageOut);
			//playSound(messageOut.getMessageOut());
			binder.getBean().setConversationId(messageOut.getConversationId());
			
			tfQuery.setValue("");
			binder.getBean().setMessageIn("");
		}
	}

	private void buildUI() {
		setupLayout();
		addHeader();
		addContentArea();
		addFooter();
	}

	private void setupLayout() {
		rootLayout = new VerticalLayout();
		rootLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		setContent(rootLayout);
	}

	private void addHeader() {
		lbinfo = new Label("MLGIA Car Park");
		lbinfo.addStyleName(MaterialTheme.LABEL_BOLD);
		lbinfo.setHeight("20px");
		rootLayout.addComponent(lbinfo);
		rootLayout.setComponentAlignment(lbinfo, Alignment.TOP_CENTER);
	}

	private void addContentArea() {
		vlContentArea = new VerticalLayout();
		vlContentArea.setSizeFull();
		vlContentArea.setId("mlgia-content-area");
		rootLayout.addComponent(vlContentArea);
	}

	private void addFooter() {
		Button btMicro = new Button(VaadinIcons.MICROPHONE);
		btMicro.addStyleName(MaterialTheme.BUTTON_FLAT + " " + MaterialTheme.BUTTON_FLOATING_ACTION);
		btMicro.setHeight("50px");
		btMicro.setWidth("50px");
		btMicro.addClickListener(this::clickMicro);

		tfQuery = new TextField();
		tfQuery.setPlaceholder("Enter your query");
		tfQuery.setWidth("100%");
		tfQuery.setMaxLength(100);

		btSendText = new Button(VaadinIcons.PAPERPLANE);
		btSendText.addStyleName(MaterialTheme.BUTTON_FLAT + " " + MaterialTheme.BUTTON_FLOATING_ACTION);
		btSendText.addClickListener(this::clickSendText);
		btSendText.setHeight("50px");
		btSendText.setWidth("50px");
		btSendText.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		HorizontalLayout hlBotom = new HorizontalLayout();
		hlBotom.addComponents(btMicro, tfQuery, btSendText);
		hlBotom.setComponentAlignment(btSendText, Alignment.MIDDLE_RIGHT);
		hlBotom.setComponentAlignment(btMicro, Alignment.MIDDLE_LEFT);
		hlBotom.setComponentAlignment(tfQuery, Alignment.MIDDLE_CENTER);
		hlBotom.setExpandRatio(tfQuery, 1);
		hlBotom.setWidth("100%");
		hlBotom.setHeight("60px");
		hlBotom.addStyleName("footer");

		rootLayout.addComponent(hlBotom);
		rootLayout.setComponentAlignment(hlBotom, Alignment.BOTTOM_CENTER);
	}

	private void clickMicro(ClickEvent event) {
		final AudioRecorder recorder = new AudioRecorder(audioFileRecordedTemp);
		Thread stopper = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				recorder.finish();
			}
		});

		stopper.start();
		recorder.start();

		log.debug("Se captura el fichero generado");
		File fileIn = new File(audioFileRecordedTemp);
		log.debug("FICHERO:" + fileIn.getAbsolutePath());
		String salida = consumerSpeechToText.invoke(fileIn);
		fileIn.delete();
		log.debug("Salida:" + salida);
		tfQuery.setValue(salida);
		clickSendText(null);
	}

	class InitializerThread extends Thread {
		AudioRecorder recorder;
		InitializerThread(AudioRecorder recorder) {
			this.recorder = recorder;
		}
		@Override
		public void run() {
			// start recording
			log.debug("Start recording");
			recorder.start();
			try {
				Thread.sleep(5000);
				recorder.finish();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			log.debug("Finish record");
		}
	}
	
	public void playSound(String message) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				consumerTextToSpeech.invoke(message);
			}
		});
		t.start();
	}
	
	public void sendTextToAssistant(MessageDTO message) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				
			}
		});
		t.start();
	}

}
