package com.trevorism.service

import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import com.trevorism.openai.model.OpenAiMessage
import com.trevorism.openai.model.OpenAiOutputContent
import com.trevorism.openai.model.OpenAiOutputItem
import com.trevorism.openai.model.OpenAiRequest
import com.trevorism.openai.model.OpenAiResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import org.junit.jupiter.api.Test

class OpenAiChatConverterTest {

    private static OpenAiOutputItem message(OpenAiOutputContent... parts) {
        new OpenAiOutputItem(type: "message", role: "assistant", content: parts.toList())
    }

    @Test
    void testConvertRequest() {
        ChatRequest chatRequest = new ChatRequest(context: "as a crazy person", previousMessages: ["I am a crazy person"], message: "What will happen to me?", model: "GPT-6-Astra", effort: "medium")
        OpenAiRequest openAiRequest = OpenAiChatConverter.convert(chatRequest)
        assert openAiRequest.model == "gpt-6-astra"
        assert openAiRequest.instructions == "as a crazy person"
        assert openAiRequest.reasoning.effort == "medium"
        assert openAiRequest.input.size() == 2
        assert openAiRequest.input[0].role == "user"
        assert openAiRequest.input[0].content == "I am a crazy person"
        assert openAiRequest.input[1].role == "user"
        assert openAiRequest.input[1].content == "What will happen to me?"
    }

    @Test
    void testConvertRequestUsesDefaults() {
        OpenAiRequest openAiRequest = OpenAiChatConverter.convert(new ChatRequest(message: "hi"))
        assert openAiRequest.model == "gpt-6.1-sol"
        assert openAiRequest.reasoning.effort == "low"
        assert openAiRequest.max_output_tokens == 25000
        assert !openAiRequest.store
        assert !openAiRequest.instructions
        assert openAiRequest.input.size() == 1
        assert openAiRequest.input[0].role == "user"
    }

    @Test
    void testConvertRequestAlternatesUserAndAssistant() {
        ChatRequest chatRequest = new ChatRequest(previousMessages: ["first ask", "first answer", "second ask"], message: "third ask")
        OpenAiRequest openAiRequest = OpenAiChatConverter.convert(chatRequest)
        assert openAiRequest.input.size() == 4
        assert openAiRequest.input[0].role == OpenAiMessage.ROLE_USER
        assert openAiRequest.input[1].role == OpenAiMessage.ROLE_ASSISTANT
        assert openAiRequest.input[1].content == "first answer"
        assert openAiRequest.input[2].role == OpenAiMessage.ROLE_USER
        assert openAiRequest.input[3].role == OpenAiMessage.ROLE_USER
        assert openAiRequest.input[3].content == "third ask"
    }

    @Test
    void testConvertRequestRejectsUnknownEffort() {
        try {
            OpenAiChatConverter.convert(new ChatRequest(message: "hi", effort: "minimal"))
            assert false
        }
        catch (HttpStatusException e) {
            assert e.status == HttpStatus.BAD_REQUEST
        }
    }

    @Test
    void testConvertResponse() {
        def mockResponse = new OpenAiResponse(model: "gpt-6.1-sol", status: "completed", output: [
                new OpenAiOutputItem(type: "reasoning"),
                message(new OpenAiOutputContent(type: "output_text", text: "hel"), new OpenAiOutputContent(type: "output_text", text: "lo"))])
        ChatResponse response = OpenAiChatConverter.convert(mockResponse)
        assert response.message == "hello"
        assert response.model == "gpt-6.1-sol"
    }

    @Test
    void testConvertRefusalIsUnprocessable() {
        def mockResponse = new OpenAiResponse(status: "completed", output: [message(new OpenAiOutputContent(type: "refusal", refusal: "I can't help with that"))])
        try {
            OpenAiChatConverter.convert(mockResponse)
            assert false
        }
        catch (HttpStatusException e) {
            assert e.status == HttpStatus.UNPROCESSABLE_ENTITY
            assert e.message.contains("I can't help with that")
        }
    }

    @Test
    void testConvertIncompleteResponseIsBadGateway() {
        def mockResponse = new OpenAiResponse(status: "incomplete", incomplete_details: [reason: "max_output_tokens"], output: [new OpenAiOutputItem(type: "reasoning")])
        try {
            OpenAiChatConverter.convert(mockResponse)
            assert false
        }
        catch (HttpStatusException e) {
            assert e.status == HttpStatus.BAD_GATEWAY
            assert e.message.contains("max_output_tokens")
        }
    }

    @Test
    void testConvertFailedResponseIsBadGateway() {
        def mockResponse = new OpenAiResponse(status: "failed", error: [code: "server_error", message: "boom"])
        try {
            OpenAiChatConverter.convert(mockResponse)
            assert false
        }
        catch (HttpStatusException e) {
            assert e.status == HttpStatus.BAD_GATEWAY
            assert e.message.contains("boom")
        }
    }
}
