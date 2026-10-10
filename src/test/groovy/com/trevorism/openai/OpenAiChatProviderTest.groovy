package com.trevorism.openai

import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import com.trevorism.openai.model.OpenAiOutputContent
import com.trevorism.openai.model.OpenAiOutputItem
import com.trevorism.openai.model.OpenAiResponse
import org.junit.jupiter.api.Test

class OpenAiChatProviderTest {

    @Test
    void testChat() {
        OpenAiChatProvider provider = new OpenAiChatProvider()
        provider.openAiClient = { request ->
            assert request.input[0].content == "What will happen to me?"
            new OpenAiResponse(model: "gpt-6.1-sol", status: "completed",
                    output: [new OpenAiOutputItem(type: "message", content: [new OpenAiOutputContent(type: "output_text", text: "hello")])])
        } as OpenAiClient

        ChatResponse response = provider.chat(new ChatRequest(message: "What will happen to me?"))

        assert response.message == "hello"
        assert response.model == "gpt-6.1-sol"
    }

    @Test
    void testModels() {
        assert new OpenAiChatProvider().models == OpenAiChatProvider.MODELS
    }
}
