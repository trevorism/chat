package com.trevorism.service

import com.trevorism.chat.Effort
import com.trevorism.claude.model.ClaudeContent
import com.trevorism.claude.model.ClaudeMessage
import com.trevorism.claude.model.ClaudeOutputConfig
import com.trevorism.claude.model.ClaudeRequest
import com.trevorism.claude.model.ClaudeResponse
import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException

class ClaudeChatConverter {

    private static final List<String> MODELS_WITHOUT_ADAPTIVE_THINKING = ["claude-haiku-4-5"]

    static ClaudeRequest convert(ChatRequest chatRequest){
        ClaudeRequest claudeRequest = new ClaudeRequest()
        if(chatRequest.model){
            claudeRequest.model = chatRequest.model.toLowerCase()
        }
        claudeRequest.output_config = new ClaudeOutputConfig(effort: Effort.resolve(chatRequest.effort))
        if(claudeRequest.model in MODELS_WITHOUT_ADAPTIVE_THINKING){
            claudeRequest.thinking = null
            claudeRequest.output_config = null
            claudeRequest.fallbacks = null
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
        if(claudeResponse.stop_reason == ClaudeResponse.STOP_REASON_REFUSAL){
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Claude declined the request, category: ${claudeResponse.stop_details?.category}")
        }
        if(claudeResponse.stop_reason == ClaudeResponse.STOP_REASON_MAX_TOKENS){
            throw new HttpStatusException(HttpStatus.BAD_GATEWAY, "Claude response was truncated at the max_tokens limit")
        }
        String text = claudeResponse.content?.findAll { it.type == ClaudeContent.TYPE_TEXT }?.collect { it.text }?.join("")
        if(!text){
            throw new HttpStatusException(HttpStatus.BAD_GATEWAY, "No text content in the Claude response, stop reason: ${claudeResponse.stop_reason}")
        }
        return new ChatResponse(message: text, model: claudeResponse.model)
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
