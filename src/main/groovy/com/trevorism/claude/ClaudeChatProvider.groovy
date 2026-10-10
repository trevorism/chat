package com.trevorism.claude

import com.trevorism.chat.ChatProvider
import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import com.trevorism.service.ClaudeChatConverter
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ClaudeChatProvider implements ChatProvider {

    public static final List<String> MODELS = ["claude-opus-5-5", "claude-sonnet-5-5", "claude-fable-5-1", "claude-haiku-4-5"]

    @Inject
    ClaudeClient claudeClient

    @Override
    List<String> getModels() {
        MODELS
    }

    @Override
    ChatResponse chat(ChatRequest request) {
        ClaudeChatConverter.convert(claudeClient.chat(ClaudeChatConverter.convert(request)))
    }
}
