package com.trevorism.controller

import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import com.trevorism.openai.OpenAiClient
import com.trevorism.openai.model.OpenAiResponse
import com.trevorism.secure.Roles
import com.trevorism.secure.Secure
import com.trevorism.service.ChatConverter
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.inject.Inject

@Controller("/api/chat")
class ChatController {

    @Inject
    OpenAiClient openAiClient

    @Tag(name = "Chat Operations")
    @Operation(summary = "Send a chat message and get a response **Secure")
    @Post(value = "/", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER)
    ChatResponse chat(@Body ChatRequest request) {
        OpenAiResponse response = openAiClient.chat(ChatConverter.convert(request))
        ChatConverter.convert(response)
    }
}
