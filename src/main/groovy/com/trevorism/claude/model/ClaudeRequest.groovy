package com.trevorism.claude.model

class ClaudeRequest {
    public static String FALLBACKS_DEFAULT = "default"

    String model = "claude-opus-5-5"
    int max_tokens = 16000
    ClaudeThinking thinking = new ClaudeThinking()
    ClaudeOutputConfig output_config = new ClaudeOutputConfig()
    String fallbacks = FALLBACKS_DEFAULT
    String system
    List<ClaudeMessage> messages = []
}
