package com.trevorism.openai

import com.trevorism.http.HeadersHttpResponse
import com.trevorism.http.HttpClient
import com.trevorism.http.util.HttpResponseBodyException
import com.trevorism.http.util.InvalidRequestException
import com.trevorism.https.token.ObtainTokenStrategy
import com.trevorism.openai.model.OpenAiMessage
import com.trevorism.openai.model.OpenAiRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import org.junit.jupiter.api.Test

class DefaultOpenAiClientTest {

    private static final String RESPONSES_JSON = '''{
        "id": "resp_1", "object": "response", "created_at": 1760000000, "status": "completed", "model": "gpt-6.1-sol",
        "output": [
            {"type": "reasoning", "id": "rs_1", "summary": [], "content": []},
            {"type": "message", "id": "msg_1", "role": "assistant", "status": "completed",
             "content": [{"type": "output_text", "text": "hello", "annotations": [], "logprobs": []}]}
        ],
        "usage": {"input_tokens": 5, "output_tokens": 9, "output_tokens_details": {"reasoning_tokens": 7}, "total_tokens": 14}
    }'''

    @Test
    void testChat() {
        DefaultOpenAiClient client = new DefaultOpenAiClient()
        String capturedUrl = null
        Map capturedHeaders = null
        String capturedBody = null
        client.httpClient = [post: { String url, String body, Map headers ->
            capturedUrl = url
            capturedBody = body
            capturedHeaders = headers
            return new HeadersHttpResponse(RESPONSES_JSON, [:])
        }] as HttpClient
        client.obtainTokenFromPropertiesFile = [getToken: { -> "xyz" }] as ObtainTokenStrategy

        def result = client.chat(new OpenAiRequest(instructions: "be brief", input: [new OpenAiMessage(role: "user", content: "hi")]))

        assert result.model == "gpt-6.1-sol"
        assert result.output[1].content[0].text == "hello"
        assert capturedUrl == "https://api.openai.com/v1/responses"
        assert capturedHeaders["Authorization"] == "Bearer xyz"
        assert capturedBody == '{"model":"gpt-6.1-sol","instructions":"be brief","input":[{"role":"user","content":"hi"}],"reasoning":{"effort":"low"},"max_output_tokens":25000,"store":false}'
    }

    @Test
    void testChatSurfacesProviderError() {
        DefaultOpenAiClient client = new DefaultOpenAiClient()
        client.httpClient = [post: { String url, String body, Map headers ->
            throw new InvalidRequestException(new HttpResponseBodyException(429, "Too Many Requests",
                    '{"error":{"message":"Rate limit reached","type":"requests","param":null,"code":"rate_limit_exceeded"}}'), 429)
        }] as HttpClient
        client.obtainTokenFromPropertiesFile = [getToken: { -> "xyz" }] as ObtainTokenStrategy

        try {
            client.chat(new OpenAiRequest(input: [new OpenAiMessage(role: "user", content: "hi")]))
            assert false
        }
        catch (HttpStatusException e) {
            assert e.status == HttpStatus.TOO_MANY_REQUESTS
            assert e.message == "OpenAI returned 429: Rate limit reached"
        }
    }
}
