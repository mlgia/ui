package es.accenture.mlgia.ui.consumer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import es.accenture.mlgia.dto.InputTextToSpeechDTO;
import es.accenture.mlgia.dto.TextToSpeechDTO;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConsumerTextToSpeech {

	@Value("${url.textToSpeech}")
	private String urlTextToSpeech;

	@Value("${message.fail}")
	private String MESSAGE_FAIL;
	
	@Value("${audio.file.temp}")
	private String audioFileTmp;

	public TextToSpeechDTO invoke(String in) {
		log.info("> TextToSpeech Request: " + in);

		TextToSpeechDTO out = null;
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		try {
			HttpEntity<Object> request = new HttpEntity<>(InputTextToSpeechDTO.builder().text(in).build(), headers);
			out = restTemplate.postForObject(urlTextToSpeech, request, TextToSpeechDTO.class);

			try {
				FileUtils.writeByteArrayToFile(new File(audioFileTmp), out.getMessage());
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		
	        try(FileInputStream fis=new FileInputStream(audioFileTmp)){
	            Player player = new Player(fis);
	            player.play();
	        }catch (JavaLayerException e1) {
	            log.error("no es audio");
	        }catch (IOException ex) {
	            log.error(ex.getLocalizedMessage());
	        }

		} catch (Exception e) {
			log.error("Error: " + e.getMessage(), e);
		}

		log.info("< Watson TextToSpeech: " + out.toString());

		return out;
	}
}
