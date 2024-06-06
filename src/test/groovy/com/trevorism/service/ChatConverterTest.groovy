package com.trevorism.service

import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import com.trevorism.openai.model.OpenAiMessage
import com.trevorism.openai.model.OpenAiRequest
import com.trevorism.openai.model.OpenAiResponse
import com.trevorism.openai.model.OpenAiResponseChoices
import org.junit.jupiter.api.Test

class ChatConverterTest {

    @Test
    void testConvertRequest() {
        //Construct a ChatRequest object
        ChatRequest chatRequest = new ChatRequest(context: "as a crazy person", previousMessages: ["I am a crazy person"], message: "What will happen to me?", model: "gpt-3.5-turbo")
        OpenAiRequest openAiRequest = ChatConverter.convert(chatRequest)
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
    void testConvertResponse() {
        def mockResponse = new OpenAiResponse(choices: [new OpenAiResponseChoices(message: new OpenAiMessage(content: "hello"))])
        ChatResponse response = ChatConverter.convert(mockResponse)
        assert response
        assert response.message == "hello"

    }
}
