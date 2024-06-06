package com.trevorism.openai

import com.google.gson.Gson
import com.trevorism.http.HeadersHttpResponse
import com.trevorism.http.HttpClient
import com.trevorism.openai.model.OpenAiMessage
import com.trevorism.openai.model.OpenAiRequest
import com.trevorism.openai.model.OpenAiResponse
import com.trevorism.openai.model.OpenAiResponseChoices
import org.junit.jupiter.api.Test

class DefaultOpenAiClientTest {

    @Test
    void testChat() {
        DefaultOpenAiClient client = new DefaultOpenAiClient()
        Gson gson = new Gson()

        def mockResponse = new OpenAiResponse(choices: [new OpenAiResponseChoices(message: new OpenAiMessage(content: "hello"))])
        String json = gson.toJson(mockResponse)

        client.httpClient = [post : { String url, String body, Map headers -> return new HeadersHttpResponse(json, [:]) }] as HttpClient
        def result = client.chat(new OpenAiRequest())
        assert result
        assert result.choices
        assert result.choices[0].message.content == "hello"

    }
}
