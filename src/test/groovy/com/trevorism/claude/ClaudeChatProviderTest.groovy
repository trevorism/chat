package com.trevorism.claude

import com.trevorism.claude.model.ClaudeContent
import com.trevorism.claude.model.ClaudeResponse
import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import org.junit.jupiter.api.Test

class ClaudeChatProviderTest {

    @Test
    void testChat() {
        ClaudeChatProvider provider = new ClaudeChatProvider()
        provider.claudeClient = { request ->
            assert request.messages[0].content == "What will happen to me?"
            new ClaudeResponse(model: "claude-opus-5-5", content: [new ClaudeContent(type: "text", text: "hello")])
        } as ClaudeClient

        ChatResponse response = provider.chat(new ChatRequest(message: "What will happen to me?"))

        assert response.message == "hello"
        assert response.model == "claude-opus-5-5"
    }

    @Test
    void testModels() {
        assert new ClaudeChatProvider().models == ClaudeChatProvider.MODELS
    }
}
