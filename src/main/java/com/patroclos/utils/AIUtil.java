package com.patroclos.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patroclos.dto.ai.ChatCompletionResponseDTO;
import com.patroclos.dto.ai.ChatRequestDTO;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AIUtil {
	
	private ObjectMapper mapper = new ObjectMapper();

    private final WebClient webClient;

    public AIUtil() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:12434")
                .build();
    }

    public String callModelRunner(ChatRequestDTO chatRequestDTO) {
        try {
			var response = webClient.post()
			        .uri("/engines/llama.cpp/v1/chat/completions")
			        .header("Content-Type", "application/json")
			        .bodyValue(mapper.writeValueAsString(chatRequestDTO))
			        .retrieve()
			        .bodyToMono(ChatCompletionResponseDTO.class)
			        .onErrorResume(e -> {
			            e.printStackTrace();
			            return Mono.empty();
			        })
			        .block();
			
			if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
				return null;
			}
			
			log.info("AI Response: " + response.getChoices().get(0).getMessage().getContent());
		
			return response.getChoices().get(0).getMessage().getContent();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
}
