package com.trevorism.controller

import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import org.junit.jupiter.api.Test

class ChatControllerTest {

    @Test
    void testChatController(){
        ChatController chatController = new ChatController()
        ChatResponse response = chatController.chat(new ChatRequest(message: "What will happen to the BBAI company?"))

        println response.message
    }
}

