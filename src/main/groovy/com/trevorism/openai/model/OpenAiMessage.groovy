package com.trevorism.openai.model

class OpenAiMessage {
    public static String ROLE_USER = "user"
    public static String ROLE_SYSTEM = "system"
    public static String ROLE_ASSISTANT = "assistant"

    String role
    String content

}
