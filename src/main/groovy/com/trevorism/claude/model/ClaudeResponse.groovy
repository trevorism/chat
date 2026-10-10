package com.trevorism.claude.model

class ClaudeResponse {
    public static String STOP_REASON_REFUSAL = "refusal"
    public static String STOP_REASON_MAX_TOKENS = "max_tokens"

    String id
    String type
    String role
    String model
    String stop_reason
    Map stop_details
    Map usage
    List<ClaudeContent> content
}

class ClaudeContent {
    public static String TYPE_TEXT = "text"

    String type
    String text
}
