package com.trevorism.controller

import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import com.trevorism.openai.OpenAiClient
import com.trevorism.openai.model.OpenAiMessage
import com.trevorism.openai.model.OpenAiResponse
import com.trevorism.openai.model.OpenAiResponseChoices
import org.junit.jupiter.api.Test

class ChatControllerTest {

    @Test
    void testChatController() {
        ChatController chatController = new ChatController()
        chatController.openAiClient = { it -> new OpenAiResponse(choices: [
                new OpenAiResponseChoices(message: new OpenAiMessage(content: "hello"))]) } as OpenAiClient
        ChatResponse response = chatController.chat(new ChatRequest(message: "What will happen to me?"))

        assert response.message == "hello"
    }

}

