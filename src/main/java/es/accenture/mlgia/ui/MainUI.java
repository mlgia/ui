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
import es.accenture.mlgia.ui.audio.MlgiaAudioPlayer;
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
	
	MlgiaAudioPlayer player = new MlgiaAudioPlayer();

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
		log.debug("Compoentes UI Ok");
		initBinder();
		//initConversation();
		consumerTextToSpeech.invoke(player, "esto es una prueba");
		//String itemName = "track1.wav";
		//ByteBuffer fileBytes = decodeToPcm(itemName, TEST_FILE_PATH);
		//Stream stream = createWaveStream(fileBytes, new WaveEncoder());
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
		consumerTextToSpeech.invoke(player, messageDTO.getMessageOut());
	}

	private void clickSendText(ClickEvent event) {
		if (binder.getBean().getMessageIn() != null && !binder.getBean().getMessageIn().isEmpty()) {
			ChatUtils.getMessageUser(vlContentArea, binder.getBean().getMessageIn());
			MessageDTO messageDTO = consumerAssistant.invokeAssistant(binder.getBean());
			ChatUtils.getMessageWatson(vlContentArea, messageDTO);
			consumerTextToSpeech.invoke(player, messageDTO.getMessageOut());
			binder.getBean().setConversationId(messageDTO.getConversationId());
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
		// rootLayout.setSizeFull();
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
		// rootLayout.addComponentsAndExpand(vlContentArea);
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

//		btPlaySound = new Button(VaadinIcons.MICROPHONE);
//		btPlaySound.addStyleName(MaterialTheme.BUTTON_FLAT + " " + MaterialTheme.BUTTON_FLOATING_ACTION);
//		btPlaySound.addClickListener(this::playSound);
//		btPlaySound.setHeight("50px");
//		btPlaySound.setWidth("50px");

		HorizontalLayout hlBotom = new HorizontalLayout();
		hlBotom.addComponents(btMicro, tfQuery, btSendText/*, btPlaySound*/);
		hlBotom.setComponentAlignment(btSendText, Alignment.MIDDLE_RIGHT);
		//hlBotom.setComponentAlignment(btPlaySound, Alignment.MIDDLE_RIGHT);
		hlBotom.setComponentAlignment(btMicro, Alignment.MIDDLE_LEFT);
		hlBotom.setComponentAlignment(tfQuery, Alignment.MIDDLE_CENTER);
		hlBotom.setExpandRatio(tfQuery, 1);
		hlBotom.setWidth("100%");
		hlBotom.setHeight("60px");
		hlBotom.addStyleName("footer");
		// hlBotom.addStyleName("myfooter");

		// hlBotom.addStyleName("position: fixed; bottom: 0; left: 0; right: 0; z-index:
		// 10;");

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

		// new InitializerThread(recorder).start();
		stopper.start();
		// start recording
		recorder.start();

		log.debug("Se captura el fichero generado");
		File fileIn = new File(audioFileRecordedTemp);
		log.debug("FICHERO:" + fileIn.getAbsolutePath());
		String salida = consumerSpeechToText.invoke(fileIn);
		fileIn.delete();
		log.debug("Salida:" + salida);
		// binder.getBean().setMessageIn(salida);
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

//	private void playSound(ClickEvent event) {
//		if (player == null) return;
//		if (player.isStopped()) {
//			player.play();
//		} else if (player.isPaused()) {
//			player.resume();
//		} else if (player.isPlaying()){
//			player.stop();
//		}
//	}

	/**
	 * Returns a ByteBuffer filled with PCM data. If the original audio file is using
	 * a different encoding, this method attempts to decode it into PCM signed data.
	 * @param fname 	filename
	 * @param dir		directory in which the file exists
	 * @return ByteBuffer containing byte[] of PCM data
	 */
//	private static ByteBuffer decodeToPcm(String fname, String dir) {
//		ByteBuffer buffer = null;
//		try {
//			// load audio file
//			Path path = Paths.get(dir + fname);
//			System.out.println(path.toAbsolutePath());
//			byte[] bytes = Files.readAllBytes(path);
//			// create input stream with audio file bytes
//			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(bytes));
//			AudioFormat.Encoding encoding = audioInputStream.getFormat().getEncoding();
//			// handle current encoding
//			if (encoding.equals(AudioFormat.Encoding.ULAW)) {
//				buffer = ULawUtil.decodeULawToPcm(audioInputStream);
//			} else {
//				// for now assume it is PCM data and load it straight into byte buffer
//				buffer = ByteBuffer.wrap(bytes);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return buffer;
//	}
//
//	private static Stream createWaveStream(ByteBuffer waveFile, Encoder outputEncoder) {
//		int startOffset = WaveUtil.getDataStartOffset(waveFile);
//		int dataLength = WaveUtil.getDataLength(waveFile);
//		int chunkLength = 5000;
//		PCMFormat dataFormat = WaveUtil.getDataFormat(waveFile);
//		System.out.println(dataFormat.toString());
//		System.out.println("arrayLength: " + waveFile.array().length
//				+ "\n\rstartOffset: " + startOffset
//				+ "\n\rdataLength: " + dataLength
//				+ "\r\nsampleRate: " + dataFormat.getSampleRate());
//		ByteBuffer dataBuffer = ByteBuffer.wrap(waveFile.array(),startOffset,dataLength);
//		Stream stream = new Stream(dataBuffer,dataFormat,outputEncoder, chunkLength);
//		return stream;
//	}
}
