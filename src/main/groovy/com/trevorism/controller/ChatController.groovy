package com.trevorism.controller

import com.trevorism.chat.ChatProviderResolver
import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import com.trevorism.secure.Roles
import com.trevorism.secure.Secure
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
    ChatProviderResolver chatProviderResolver

    @Tag(name = "Chat Operations")
    @Operation(summary = "Send a chat message and get a response. Supported OpenAI models route to the Responses API and supported Claude models to the Messages API; no model uses gpt-6.1-sol. effort is low, medium, high, xhigh, or max (default low) **Secure")
    @Post(value = "/", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER)
    ChatResponse chat(@Body ChatRequest request) {
        chatProviderResolver.resolve(request.model).chat(request)
    }

}
