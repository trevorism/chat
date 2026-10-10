package com.trevorism.claude

import com.google.gson.Gson
import com.trevorism.claude.model.ClaudeContent
import com.trevorism.claude.model.ClaudeMessage
import com.trevorism.claude.model.ClaudeRequest
import com.trevorism.claude.model.ClaudeResponse
import com.trevorism.http.HeadersHttpResponse
import com.trevorism.http.HttpClient
import com.trevorism.http.util.HttpResponseBodyException
import com.trevorism.http.util.InvalidRequestException
import com.trevorism.https.token.ObtainTokenStrategy
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import org.junit.jupiter.api.Test

class DefaultClaudeClientTest {

    private static final String HELLO_RESPONSE = new Gson().toJson(new ClaudeResponse(content: [new ClaudeContent(type: "text", text: "hello")]))

    private String capturedUrl
    private Map capturedHeaders
    private String capturedBody

    private DefaultClaudeClient createClient() {
        DefaultClaudeClient client = new DefaultClaudeClient()
        client.httpClient = [post: { String url, String body, Map headers ->
            capturedUrl = url
            capturedBody = body
            capturedHeaders = headers
            return new HeadersHttpResponse(HELLO_RESPONSE, [:])
        }] as HttpClient
        client.obtainTokenFromPropertiesFile = [getToken: { -> "xyz" }] as ObtainTokenStrategy
        return client
    }

    @Test
    void testChat() {
        def result = createClient().chat(new ClaudeRequest(messages: [new ClaudeMessage(role: "user", content: "hi")]))
        assert result.content[0].text == "hello"

        assert capturedUrl == "https://api.anthropic.com/v1/messages"
        assert capturedHeaders["x-api-key"] == "xyz"
        assert capturedHeaders["anthropic-version"] == "2023-06-01"
        assert capturedHeaders["anthropic-beta"] == "server-side-fallback-2026-07-01"
    }

    @Test
    void testChatSerializesCurrentRequestShape() {
        createClient().chat(new ClaudeRequest(system: "be brief", messages: [new ClaudeMessage(role: "user", content: "hi")]))

        assert capturedBody == '{"model":"claude-opus-5-5","max_tokens":16000,"thinking":{"type":"adaptive"},"output_config":{"effort":"low"},"fallbacks":"default","system":"be brief","messages":[{"role":"user","content":"hi"}]}'
    }

    @Test
    void testChatWithoutFallbacksOmitsFieldsAndBetaHeader() {
        createClient().chat(new ClaudeRequest(model: "claude-haiku-4-5", thinking: null, output_config: null, fallbacks: null,
                messages: [new ClaudeMessage(role: "user", content: "hi")]))

        assert capturedBody == '{"model":"claude-haiku-4-5","max_tokens":16000,"messages":[{"role":"user","content":"hi"}]}'
        assert !capturedHeaders.containsKey("anthropic-beta")
    }

    @Test
    void testChatSurfacesProviderError() {
        DefaultClaudeClient client = createClient()
        client.httpClient = [post: { String url, String body, Map headers ->
            throw new InvalidRequestException(new HttpResponseBodyException(400, "Bad Request",
                    '{"type":"error","error":{"type":"invalid_request_error","message":"thinking.type: disabled is not supported"}}'), 400)
        }] as HttpClient

        try {
            client.chat(new ClaudeRequest(messages: [new ClaudeMessage(role: "user", content: "hi")]))
            assert false
        }
        catch (HttpStatusException e) {
            assert e.status == HttpStatus.BAD_REQUEST
            assert e.message == "Claude returned 400: thinking.type: disabled is not supported"
        }
    }
}
