package com.trevorism.openai

import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import com.trevorism.openai.model.OpenAiMessage
import com.trevorism.openai.model.OpenAiResponse
import com.trevorism.openai.model.OpenAiResponseChoices
import org.junit.jupiter.api.Test

class OpenAiChatProviderTest {

    @Test
    void testChat() {
        OpenAiChatProvider provider = new OpenAiChatProvider()
        provider.openAiClient = { request ->
            assert request.messages[0].content == "What will happen to me?"
            new OpenAiResponse(choices: [new OpenAiResponseChoices(message: new OpenAiMessage(content: "hello"))])
        } as OpenAiClient

        ChatResponse response = provider.chat(new ChatRequest(message: "What will happen to me?"))

        assert response.message == "hello"
    }
}
