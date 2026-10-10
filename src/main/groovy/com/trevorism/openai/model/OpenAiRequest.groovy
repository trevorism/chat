package com.trevorism.openai.model

class OpenAiRequest {
    String model = "gpt-6.1-sol"
    String instructions
    List<OpenAiMessage> input = []
    OpenAiReasoning reasoning = new OpenAiReasoning()
    int max_output_tokens = 25000
    boolean store = false
}
