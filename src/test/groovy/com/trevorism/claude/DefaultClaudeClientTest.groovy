package com.trevorism.claude

import com.google.gson.Gson
import com.trevorism.claude.model.ClaudeContent
import com.trevorism.claude.model.ClaudeMessage
import com.trevorism.claude.model.ClaudeRequest
import com.trevorism.claude.model.ClaudeResponse
import com.trevorism.http.HeadersHttpResponse
import com.trevorism.http.HttpClient
import com.trevorism.https.token.ObtainTokenStrategy
import org.junit.jupiter.api.Test

class DefaultClaudeClientTest {

    @Test
    void testChat() {
        DefaultClaudeClient client = new DefaultClaudeClient()
        Gson gson = new Gson()

        def mockResponse = new ClaudeResponse(content: [new ClaudeContent(type: "text", text: "hello")])
        String json = gson.toJson(mockResponse)

        String capturedUrl = null
        Map capturedHeaders = null
        String capturedBody = null

        client.httpClient = [post: { String url, String body, Map headers ->
            capturedUrl = url
            capturedBody = body
            capturedHeaders = headers
            return new HeadersHttpResponse(json, [:])
        }] as HttpClient
        client.obtainTokenFromPropertiesFile = [getToken: { -> "xyz" }] as ObtainTokenStrategy

        def result = client.chat(new ClaudeRequest(messages: [new ClaudeMessage(role: "user", content: "hi")]))
        assert result
        assert result.content
        assert result.content[0].text == "hello"

        assert capturedUrl == "https://api.anthropic.com/v1/messages"
        assert capturedHeaders["x-api-key"] == "xyz"
        assert capturedHeaders["anthropic-version"] == "2023-06-01"
        Map body = gson.fromJson(capturedBody, Map)
        assert body.model == "claude-opus-5"
        assert body.max_tokens == 4096
        assert body.thinking.type == "disabled"
        assert body.messages[0].content == "hi"
    }

    @Test
    void testChatSerializesSystemPrompt() {
        DefaultClaudeClient client = new DefaultClaudeClient()
        String capturedBody = null

        client.httpClient = [post: { String url, String body, Map headers ->
            capturedBody = body
            return new HeadersHttpResponse(new Gson().toJson(new ClaudeResponse(content: [new ClaudeContent(type: "text", text: "hello")])), [:])
        }] as HttpClient
        client.obtainTokenFromPropertiesFile = [getToken: { -> "xyz" }] as ObtainTokenStrategy

        client.chat(new ClaudeRequest(system: "be brief", messages: [new ClaudeMessage(role: "user", content: "hi")]))

        assert capturedBody == '{"model":"claude-opus-5","max_tokens":4096,"thinking":{"type":"disabled"},"system":"be brief","messages":[{"role":"user","content":"hi"}]}'
    }
}
