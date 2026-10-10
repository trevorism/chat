package com.trevorism.service

import com.trevorism.chat.Effort
import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import com.trevorism.openai.model.OpenAiMessage
import com.trevorism.openai.model.OpenAiOutputContent
import com.trevorism.openai.model.OpenAiOutputItem
import com.trevorism.openai.model.OpenAiReasoning
import com.trevorism.openai.model.OpenAiRequest
import com.trevorism.openai.model.OpenAiResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException

class OpenAiChatConverter {

    static OpenAiRequest convert(ChatRequest chatRequest){
        OpenAiRequest openAiRequest = new OpenAiRequest()
        if(chatRequest.model){
            openAiRequest.model = chatRequest.model.toLowerCase()
        }
        openAiRequest.reasoning = new OpenAiReasoning(effort: Effort.resolve(chatRequest.effort))
        if(chatRequest.context){
            openAiRequest.instructions = chatRequest.context
        }
        if(chatRequest.previousMessages){
            chatRequest.previousMessages.eachWithIndex { message, index ->
                String role = index % 2 == 0 ? OpenAiMessage.ROLE_USER : OpenAiMessage.ROLE_ASSISTANT
                openAiRequest.input << new OpenAiMessage(role: role, content: message)
            }
        }
        openAiRequest.input << new OpenAiMessage(role: OpenAiMessage.ROLE_USER, content: chatRequest.message)

        return openAiRequest
    }

    static ChatResponse convert(OpenAiResponse openAiResponse){
        List<OpenAiOutputContent> parts = openAiResponse.output
                ?.findAll { it.type == OpenAiOutputItem.TYPE_MESSAGE }
                ?.collectMany { it.content ?: [] } ?: []
        OpenAiOutputContent refusal = parts.find { it.type == OpenAiOutputContent.TYPE_REFUSAL }
        if(refusal){
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "OpenAI declined the request: ${refusal.refusal}")
        }
        if(openAiResponse.status == OpenAiResponse.STATUS_INCOMPLETE){
            throw new HttpStatusException(HttpStatus.BAD_GATEWAY, "OpenAI response was incomplete, reason: ${openAiResponse.incomplete_details?.reason}")
        }
        String text = parts.findAll { it.type == OpenAiOutputContent.TYPE_OUTPUT_TEXT }.collect { it.text }.join("")
        if(!text){
            throw new HttpStatusException(HttpStatus.BAD_GATEWAY, "No text content in the OpenAI response, status: ${openAiResponse.status}, error: ${openAiResponse.error?.message}")
        }
        return new ChatResponse(message: text, model: openAiResponse.model)
    }
}
