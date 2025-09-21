package com.patroclos.dto.ai;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ChatRequestDTO {
	
    private String model;
    private List<Message> messages;

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}