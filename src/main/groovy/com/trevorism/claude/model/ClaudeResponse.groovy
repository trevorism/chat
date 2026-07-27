package com.trevorism.claude.model

class ClaudeResponse {
    String id
    String type
    String role
    String model
    String stop_reason
    Map usage
    List<ClaudeContent> content
}

class ClaudeContent {
    public static String TYPE_TEXT = "text"

    String type
    String text
}
