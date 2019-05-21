package es.accenture.mlgia.ui.consumer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import es.accenture.mlgia.dto.InputSpeechToTextDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConsumerSpeechToText {

	@Value("${url.SpeechToText}")
	private String urlSpeechToText;

	public String invoke(File fileIn) {
		log.info("> SpeechToText Request: ");

		RestTemplate restTemplate = new RestTemplate();

		byte[] fileByteEncoded = null;
		try {
			byte[] fileByte = Files.readAllBytes(fileIn.toPath());
			fileByteEncoded = Base64.getEncoder().encode(fileByte);
		} catch (IOException e) {
			log.error("Se ha producido un erro al obtener los bytes" + e.getMessage());
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<Object> request = new HttpEntity<>(InputSpeechToTextDTO.builder().text(fileByteEncoded).build(),
				headers);

		ResponseEntity<String> result = restTemplate.postForEntity(urlSpeechToText, request, String.class);

		log.info("< Watson SpeechToText: " + result.getBody());

		return result.getBody().trim();

	}
}
