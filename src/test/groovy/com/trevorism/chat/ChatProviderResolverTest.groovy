package com.trevorism.chat

import com.trevorism.claude.ClaudeChatProvider
import com.trevorism.openai.OpenAiChatProvider
import org.junit.jupiter.api.Test

class ChatProviderResolverTest {

    private ChatProviderResolver resolver = new ChatProviderResolver(
            claudeChatProvider: new ClaudeChatProvider(),
            openAiChatProvider: new OpenAiChatProvider())

    @Test
    void testResolveClaudeModel() {
        assert resolver.resolve("claude-opus-5").is(resolver.claudeChatProvider)
    }

    @Test
    void testResolveClaudeModelIgnoresCase() {
        assert resolver.resolve("Claude-Opus-5").is(resolver.claudeChatProvider)
    }

    @Test
    void testResolveOpenAiModel() {
        assert resolver.resolve("gpt-5.4").is(resolver.openAiChatProvider)
    }

    @Test
    void testResolveMissingModelUsesOpenAi() {
        assert resolver.resolve(null).is(resolver.openAiChatProvider)
        assert resolver.resolve("").is(resolver.openAiChatProvider)
    }
}
