package com.trevorism.openai

import com.trevorism.chat.ChatProvider
import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import com.trevorism.service.OpenAiChatConverter
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class OpenAiChatProvider implements ChatProvider {

    public static final List<String> MODELS = ["gpt-6.1-sol", "gpt-6-astra", "gpt-6-luna"]

    @Inject
    OpenAiClient openAiClient

    @Override
    List<String> getModels() {
        MODELS
    }

    @Override
    ChatResponse chat(ChatRequest request) {
        OpenAiChatConverter.convert(openAiClient.chat(OpenAiChatConverter.convert(request)))
    }
}
