package com.trevorism.service

import com.trevorism.claude.model.ClaudeContent
import com.trevorism.claude.model.ClaudeMessage
import com.trevorism.claude.model.ClaudeRequest
import com.trevorism.claude.model.ClaudeResponse
import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import org.junit.jupiter.api.Test

class ClaudeChatConverterTest {

    @Test
    void testConvertRequest() {
        ChatRequest chatRequest = new ChatRequest(context: "as a crazy person", previousMessages: ["I am a crazy person", "That sounds hard"], message: "What will happen to me?", model: "claude-sonnet-5")
        ClaudeRequest claudeRequest = ClaudeChatConverter.convert(chatRequest)
        assert claudeRequest
        assert claudeRequest.model == "claude-sonnet-5"
        assert claudeRequest.system == "as a crazy person"
        assert claudeRequest.messages.size() == 3
        assert claudeRequest.messages[0].role == ClaudeMessage.ROLE_USER
        assert claudeRequest.messages[0].content == "I am a crazy person"
        assert claudeRequest.messages[1].role == ClaudeMessage.ROLE_ASSISTANT
        assert claudeRequest.messages[1].content == "That sounds hard"
        assert claudeRequest.messages[2].role == ClaudeMessage.ROLE_USER
        assert claudeRequest.messages[2].content == "What will happen to me?"
    }

    @Test
    void testConvertRequestUsesDefaults() {
        ClaudeRequest claudeRequest = ClaudeChatConverter.convert(new ChatRequest(message: "hi"))
        assert claudeRequest.model == "claude-opus-5"
        assert claudeRequest.max_tokens == 4096
        assert claudeRequest.thinking.type == "disabled"
        assert !claudeRequest.system
        assert claudeRequest.messages.size() == 1
        assert claudeRequest.messages[0].role == ClaudeMessage.ROLE_USER
        assert claudeRequest.messages[0].content == "hi"
    }

    @Test
    void testConvertRequestOmitsThinkingForFable() {
        ClaudeRequest claudeRequest = ClaudeChatConverter.convert(new ChatRequest(message: "hi", model: "claude-fable-5"))
        assert claudeRequest.model == "claude-fable-5"
        assert claudeRequest.thinking == null
        assert claudeRequest.max_tokens == 16000
    }

    @Test
    void testConvertRequestOmitsThinkingForMythos() {
        ClaudeRequest claudeRequest = ClaudeChatConverter.convert(new ChatRequest(message: "hi", model: "Claude-Mythos-5"))
        assert claudeRequest.thinking == null
        assert claudeRequest.max_tokens == 16000
    }

    @Test
    void testConvertRequestDisablesThinkingForOtherModels() {
        ["claude-opus-5", "claude-sonnet-5", "claude-haiku-4-5", "claude-opus-4-8"].each { model ->
            ClaudeRequest claudeRequest = ClaudeChatConverter.convert(new ChatRequest(message: "hi", model: model))
            assert claudeRequest.thinking.type == "disabled"
            assert claudeRequest.max_tokens == 4096
        }
    }

    @Test
    void testConvertRequestMergesConsecutiveUserMessages() {
        ChatRequest chatRequest = new ChatRequest(previousMessages: ["first ask"], message: "second ask")
        ClaudeRequest claudeRequest = ClaudeChatConverter.convert(chatRequest)
        assert claudeRequest.messages.size() == 1
        assert claudeRequest.messages[0].role == ClaudeMessage.ROLE_USER
        assert claudeRequest.messages[0].content == "first ask\n\nsecond ask"
    }

    @Test
    void testConvertResponse() {
        def mockResponse = new ClaudeResponse(content: [new ClaudeContent(type: "text", text: "hello")])
        ChatResponse response = ClaudeChatConverter.convert(mockResponse)
        assert response
        assert response.message == "hello"
    }

    @Test
    void testConvertResponseSkipsNonTextContent() {
        def mockResponse = new ClaudeResponse(content: [new ClaudeContent(type: "thinking"), new ClaudeContent(type: "text", text: "hello")])
        assert ClaudeChatConverter.convert(mockResponse).message == "hello"
    }

    @Test
    void testConvertResponseWithoutTextFails() {
        def mockResponse = new ClaudeResponse(stop_reason: "refusal", content: [])
        try {
            ClaudeChatConverter.convert(mockResponse)
            assert false
        }
        catch (IllegalStateException e) {
            assert e.message.contains("refusal")
        }
    }

    @Test
    void testConvertNullResponseContentFails() {
        try {
            ClaudeChatConverter.convert(new ClaudeResponse())
            assert false
        }
        catch (IllegalStateException e) {
            assert e.message
        }
    }
}
