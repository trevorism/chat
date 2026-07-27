package com.trevorism.openai

import com.trevorism.chat.ChatProvider
import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import com.trevorism.service.OpenAiChatConverter
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class OpenAiChatProvider implements ChatProvider {

    @Inject
    OpenAiClient openAiClient

    @Override
    ChatResponse chat(ChatRequest request) {
        OpenAiChatConverter.convert(openAiClient.chat(OpenAiChatConverter.convert(request)))
    }
}
