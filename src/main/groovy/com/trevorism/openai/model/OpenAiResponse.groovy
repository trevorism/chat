package com.trevorism.openai.model

class OpenAiResponse {
    String id
    String object
    String created
    String model
    Map usage
    List<OpenAiResponseChoices> choices
}

class OpenAiResponseChoices {
    int index
    OpenAiMessage message
    String finish_reason
}
