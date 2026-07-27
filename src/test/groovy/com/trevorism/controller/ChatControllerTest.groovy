package com.trevorism.controller

import com.trevorism.chat.ChatProviderResolver
import com.trevorism.claude.ClaudeChatProvider
import com.trevorism.claude.ClaudeClient
import com.trevorism.claude.model.ClaudeContent
import com.trevorism.claude.model.ClaudeResponse
import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import com.trevorism.openai.OpenAiChatProvider
import com.trevorism.openai.OpenAiClient
import com.trevorism.openai.model.OpenAiMessage
import com.trevorism.openai.model.OpenAiResponse
import com.trevorism.openai.model.OpenAiResponseChoices
import org.junit.jupiter.api.Test

class ChatControllerTest {

    private static ChatController createChatController() {
        ClaudeChatProvider claudeChatProvider = new ClaudeChatProvider(claudeClient: { it ->
            new ClaudeResponse(content: [new ClaudeContent(type: "text", text: "hello from claude")])
        } as ClaudeClient)

        OpenAiChatProvider openAiChatProvider = new OpenAiChatProvider(openAiClient: { it ->
            new OpenAiResponse(choices: [new OpenAiResponseChoices(message: new OpenAiMessage(content: "hello"))])
        } as OpenAiClient)

        new ChatController(chatProviderResolver: new ChatProviderResolver(
                claudeChatProvider: claudeChatProvider, openAiChatProvider: openAiChatProvider))
    }

    @Test
    void testChatController() {
        ChatResponse response = createChatController().chat(new ChatRequest(message: "What will happen to me?"))

        assert response.message == "hello"
    }

    @Test
    void testChatControllerRoutesToClaude() {
        ChatResponse response = createChatController().chat(new ChatRequest(message: "What will happen to me?", model: "claude-opus-5"))

        assert response.message == "hello from claude"
    }

}
