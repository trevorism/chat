package com.trevorism.claude.model

class ClaudeRequest {
    String model = "claude-opus-5"
    int max_tokens = 4096
    ClaudeThinking thinking = new ClaudeThinking()
    String system
    List<ClaudeMessage> messages = []
}
