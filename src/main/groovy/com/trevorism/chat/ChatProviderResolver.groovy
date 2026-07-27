package com.trevorism.chat

import com.trevorism.claude.ClaudeChatProvider
import com.trevorism.openai.OpenAiChatProvider
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ChatProviderResolver {

    public static String CLAUDE_MODEL_PREFIX = "claude"

    @Inject
    ClaudeChatProvider claudeChatProvider

    @Inject
    OpenAiChatProvider openAiChatProvider

    ChatProvider resolve(String model) {
        model?.toLowerCase()?.startsWith(CLAUDE_MODEL_PREFIX) ? claudeChatProvider : openAiChatProvider
    }
}
