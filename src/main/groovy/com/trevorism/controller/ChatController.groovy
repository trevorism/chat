package com.trevorism.controller

import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import com.trevorism.openai.DefaultOpenAiClient
import com.trevorism.openai.OpenAiClient
import com.trevorism.openai.model.OpenAiResponse
import com.trevorism.secure.Roles
import com.trevorism.secure.Secure
import com.trevorism.service.ChatConverter
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

@Controller("/api/chat")
class ChatController {

    OpenAiClient openAiClient = new DefaultOpenAiClient()

    @Tag(name = "Chat Operations")
    @Operation(summary = "Send a chat message and get a response")
    @Post(value = "/", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER)
    ChatResponse chat(ChatRequest request) {
        OpenAiResponse response = openAiClient.chat(ChatConverter.convert(request))
        ChatConverter.convert(response)
    }
}
