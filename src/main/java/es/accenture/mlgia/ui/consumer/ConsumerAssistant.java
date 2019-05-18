package es.accenture.mlgia.ui.consumer;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import es.accenture.mlgia.dto.MessageDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
//@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ConsumerAssistant {
	
	@Value("${url.assistant}")
	private String urlAssistant;
	
	private static final String MESSAGE_FAIL = "I'm sorry, I have a problem with my connection.";
	
	public MessageDTO initAssistant() {
		return invokeImpl(MessageDTO.builder().messageIn("").conversationId("").build());
	}

	public MessageDTO invokeAssistant(MessageDTO in) {
		return invokeImpl(in);
	}

	private MessageDTO invokeImpl(MessageDTO in) {
		log.info("> Watson Request: " + in.toString());
		
		MessageDTO out = null;
		RestTemplate restTemplate = new RestTemplate();
		
		// Parámetros de  entrada
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<Object> request = new HttpEntity<>(in, headers);

		try{
			//out = restTemplate.postForObject(urlAssistant, request, MessageDTO.class);	
		}
		catch(Exception e) {
			out = MessageDTO.builder().messageOut(MESSAGE_FAIL).conversationId("").build();
		}
		
		log.info("< Watson Response: " + out.toString());
		
		return out;
	}
}
