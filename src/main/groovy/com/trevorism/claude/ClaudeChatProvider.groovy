package com.trevorism.claude

import com.trevorism.chat.ChatProvider
import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import com.trevorism.service.ClaudeChatConverter
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ClaudeChatProvider implements ChatProvider {

    @Inject
    ClaudeClient claudeClient

    @Override
    ChatResponse chat(ChatRequest request) {
        ClaudeChatConverter.convert(claudeClient.chat(ClaudeChatConverter.convert(request)))
    }
}
