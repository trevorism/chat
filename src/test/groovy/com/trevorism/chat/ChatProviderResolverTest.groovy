package com.trevorism.chat

import com.trevorism.claude.ClaudeChatProvider
import com.trevorism.openai.OpenAiChatProvider
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import org.junit.jupiter.api.Test

class ChatProviderResolverTest {

    private ChatProviderResolver resolver = new ChatProviderResolver(
            claudeChatProvider: new ClaudeChatProvider(),
            openAiChatProvider: new OpenAiChatProvider())

    @Test
    void testResolveClaudeModels() {
        ["claude-opus-5-5", "claude-sonnet-5-5", "claude-fable-5-1", "claude-haiku-4-5"].each {
            assert resolver.resolve(it).is(resolver.claudeChatProvider)
        }
    }

    @Test
    void testResolveIgnoresCase() {
        assert resolver.resolve("Claude-Opus-5-5").is(resolver.claudeChatProvider)
        assert resolver.resolve("GPT-6-Astra").is(resolver.openAiChatProvider)
    }

    @Test
    void testResolveOpenAiModels() {
        ["gpt-6.1-sol", "gpt-6-astra", "gpt-6-luna"].each {
            assert resolver.resolve(it).is(resolver.openAiChatProvider)
        }
    }

    @Test
    void testResolveMissingModelUsesOpenAi() {
        assert resolver.resolve(null).is(resolver.openAiChatProvider)
        assert resolver.resolve("").is(resolver.openAiChatProvider)
    }

    @Test
    void testResolveUnsupportedModelIsBadRequest() {
        ["gpt-5.4", "claude-opus-5", "gemini-3-pro"].each { model ->
            try {
                resolver.resolve(model)
                assert false
            }
            catch (HttpStatusException e) {
                assert e.status == HttpStatus.BAD_REQUEST
                assert e.message.contains(model)
                assert e.message.contains("gpt-6.1-sol")
                assert e.message.contains("claude-opus-5-5")
            }
        }
    }
}
