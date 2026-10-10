package com.trevorism.chat

import com.trevorism.claude.ClaudeChatProvider
import com.trevorism.openai.OpenAiChatProvider
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ChatProviderResolver {

    @Inject
    ClaudeChatProvider claudeChatProvider

    @Inject
    OpenAiChatProvider openAiChatProvider

    ChatProvider resolve(String model) {
        if (!model) {
            return openAiChatProvider
        }
        List<ChatProvider> providers = [openAiChatProvider, claudeChatProvider]
        ChatProvider provider = providers.find { model.toLowerCase() in it.models }
        if (!provider) {
            String supportedModels = providers.collectMany { it.models }.join(", ")
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Unsupported model '${model}'. Supported models: ${supportedModels}")
        }
        return provider
    }
}
