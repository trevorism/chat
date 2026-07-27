package com.trevorism.service

import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import com.trevorism.openai.model.OpenAiMessage
import com.trevorism.openai.model.OpenAiRequest
import com.trevorism.openai.model.OpenAiResponse
import com.trevorism.openai.model.OpenAiResponseChoices
import org.junit.jupiter.api.Test

class OpenAiChatConverterTest {

    @Test
    void testConvertRequest() {
        ChatRequest chatRequest = new ChatRequest(context: "as a crazy person", previousMessages: ["I am a crazy person"], message: "What will happen to me?", model: "gpt-3.5-turbo")
        OpenAiRequest openAiRequest = OpenAiChatConverter.convert(chatRequest)
        assert openAiRequest
        assert openAiRequest.model == "gpt-3.5-turbo"
        assert openAiRequest.messages.size() == 3
        assert openAiRequest.messages[0].role == "system"
        assert openAiRequest.messages[0].content == "as a crazy person"
        assert openAiRequest.messages[1].role == "user"
        assert openAiRequest.messages[1].content == "I am a crazy person"
        assert openAiRequest.messages[2].role == "user"
        assert openAiRequest.messages[2].content == "What will happen to me?"
    }

    @Test
    void testConvertRequestUsesDefaults() {
        OpenAiRequest openAiRequest = OpenAiChatConverter.convert(new ChatRequest(message: "hi"))
        assert openAiRequest.model == "gpt-5.4"
        assert openAiRequest.messages.size() == 1
        assert openAiRequest.messages[0].role == "user"
    }

    @Test
    void testConvertRequestAlternatesUserAndAssistant() {
        ChatRequest chatRequest = new ChatRequest(previousMessages: ["first ask", "first answer", "second ask"], message: "third ask")
        OpenAiRequest openAiRequest = OpenAiChatConverter.convert(chatRequest)
        assert openAiRequest.messages.size() == 4
        assert openAiRequest.messages[0].role == OpenAiMessage.ROLE_USER
        assert openAiRequest.messages[1].role == OpenAiMessage.ROLE_ASSISTANT
        assert openAiRequest.messages[1].content == "first answer"
        assert openAiRequest.messages[2].role == OpenAiMessage.ROLE_USER
        assert openAiRequest.messages[3].role == OpenAiMessage.ROLE_USER
        assert openAiRequest.messages[3].content == "third ask"
    }

    @Test
    void testConvertResponse() {
        def mockResponse = new OpenAiResponse(choices: [new OpenAiResponseChoices(message: new OpenAiMessage(content: "hello"))])
        ChatResponse response = OpenAiChatConverter.convert(mockResponse)
        assert response
        assert response.message == "hello"

    }
}
