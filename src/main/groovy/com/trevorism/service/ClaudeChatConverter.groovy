package com.trevorism.service

import com.trevorism.claude.model.ClaudeContent
import com.trevorism.claude.model.ClaudeMessage
import com.trevorism.claude.model.ClaudeRequest
import com.trevorism.claude.model.ClaudeResponse
import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse

class ClaudeChatConverter {

    private static final List<String> ALWAYS_THINKING_MODEL_PREFIXES = ["claude-fable", "claude-mythos"]
    private static final int ALWAYS_THINKING_MAX_TOKENS = 16000

    static ClaudeRequest convert(ChatRequest chatRequest){
        ClaudeRequest claudeRequest = new ClaudeRequest()
        if(chatRequest.model){
            claudeRequest.model = chatRequest.model.toLowerCase()
        }
        if(thinkingCannotBeDisabled(claudeRequest.model)){
            claudeRequest.thinking = null
            claudeRequest.max_tokens = ALWAYS_THINKING_MAX_TOKENS
        }
        if(chatRequest.context){
            claudeRequest.system = chatRequest.context
        }
        if(chatRequest.previousMessages){
            chatRequest.previousMessages.eachWithIndex { message, index ->
                String role = index % 2 == 0 ? ClaudeMessage.ROLE_USER : ClaudeMessage.ROLE_ASSISTANT
                appendOrMerge(claudeRequest.messages, role, message)
            }
        }
        appendOrMerge(claudeRequest.messages, ClaudeMessage.ROLE_USER, chatRequest.message)

        return claudeRequest
    }

    static ChatResponse convert(ClaudeResponse claudeResponse){
        ClaudeContent textContent = claudeResponse.content?.find { it.type == ClaudeContent.TYPE_TEXT }
        if(!textContent){
            throw new IllegalStateException("No text content in the Claude response, stop reason: ${claudeResponse.stop_reason}")
        }
        ChatResponse response = new ChatResponse()
        response.message = textContent.text
        return response
    }

    private static boolean thinkingCannotBeDisabled(String model){
        String lowercaseModel = model.toLowerCase()
        ALWAYS_THINKING_MODEL_PREFIXES.any { lowercaseModel.startsWith(it) }
    }

    private static void appendOrMerge(List<ClaudeMessage> messages, String role, String content){
        ClaudeMessage previous = messages ? messages.last() : null
        if(previous?.role == role){
            previous.content = "${previous.content}\n\n${content}"
        }
        else{
            messages << new ClaudeMessage(role: role, content: content)
        }
    }
}
