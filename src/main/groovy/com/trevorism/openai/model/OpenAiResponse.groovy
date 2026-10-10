package com.trevorism.openai.model

class OpenAiResponse {
    public static String STATUS_INCOMPLETE = "incomplete"

    String id
    String object
    String model
    String status
    Map error
    Map incomplete_details
    Map usage
    List<OpenAiOutputItem> output
}

class OpenAiOutputItem {
    public static String TYPE_MESSAGE = "message"

    String type
    String role
    List<OpenAiOutputContent> content
}

class OpenAiOutputContent {
    public static String TYPE_OUTPUT_TEXT = "output_text"
    public static String TYPE_REFUSAL = "refusal"

    String type
    String text
    String refusal
}
