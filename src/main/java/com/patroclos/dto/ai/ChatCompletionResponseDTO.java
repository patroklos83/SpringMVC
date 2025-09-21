package com.patroclos.dto.ai;

import lombok.Data;

import java.util.List;

@Data
public class ChatCompletionResponseDTO {
    private List<Choice> choices;
    private long created;
    private String model;
    private String systemFingerprint;
    private String object;
    private Usage usage;
    private String id;
    private Timings timings;

    @Data
    public static class Choice {
        private String finishReason;
        private int index;
        private Message message;
    }

    @Data
    public static class Message {
        private String role;
        private String content;
    }

    @Data
    public static class Usage {
        private int completionTokens;
        private int promptTokens;
        private int totalTokens;
    }

    @Data
    public static class Timings {
        private int promptN;
        private double promptMs;
        private double promptPerTokenMs;
        private double promptPerSecond;
        private int predictedN;
        private double predictedMs;
        private double predictedPerTokenMs;
        private double predictedPerSecond;
    }
}
