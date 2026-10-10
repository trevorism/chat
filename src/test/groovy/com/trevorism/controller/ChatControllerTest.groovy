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
import com.trevorism.openai.model.OpenAiOutputContent
import com.trevorism.openai.model.OpenAiOutputItem
import com.trevorism.openai.model.OpenAiResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import org.junit.jupiter.api.Test

class ChatControllerTest {

    private static ChatController createChatController() {
        ClaudeChatProvider claudeChatProvider = new ClaudeChatProvider(claudeClient: { request ->
            new ClaudeResponse(model: request.model, content: [new ClaudeContent(type: "text", text: "hello from claude")])
        } as ClaudeClient)

        OpenAiChatProvider openAiChatProvider = new OpenAiChatProvider(openAiClient: { request ->
            new OpenAiResponse(model: request.model, status: "completed",
                    output: [new OpenAiOutputItem(type: "message", content: [new OpenAiOutputContent(type: "output_text", text: "hello")])])
        } as OpenAiClient)

        new ChatController(chatProviderResolver: new ChatProviderResolver(
                claudeChatProvider: claudeChatProvider, openAiChatProvider: openAiChatProvider))
    }

    @Test
    void testChatController() {
        ChatResponse response = createChatController().chat(new ChatRequest(message: "What will happen to me?"))

        assert response.message == "hello"
        assert response.model == "gpt-6.1-sol"
    }

    @Test
    void testChatControllerRoutesToClaude() {
        ChatResponse response = createChatController().chat(new ChatRequest(message: "What will happen to me?", model: "claude-opus-5-5"))

        assert response.message == "hello from claude"
        assert response.model == "claude-opus-5-5"
    }

    @Test
    void testChatControllerRejectsUnsupportedModel() {
        try {
            createChatController().chat(new ChatRequest(message: "What will happen to me?", model: "gpt-5.4"))
            assert false
        }
        catch (HttpStatusException e) {
            assert e.status == HttpStatus.BAD_REQUEST
        }
    }

}
