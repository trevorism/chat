package com.trevorism.service

import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse
import com.trevorism.openai.model.OpenAiMessage
import com.trevorism.openai.model.OpenAiRequest
import com.trevorism.openai.model.OpenAiResponse

class ChatConverter {

    static OpenAiRequest convert(ChatRequest chatRequest){
        OpenAiRequest openAiRequest = new OpenAiRequest()

        if(chatRequest.context){
            openAiRequest.messages << new OpenAiMessage(role: OpenAiMessage.ROLE_SYSTEM, content: chatRequest.context)
        }
        if(chatRequest.previousMessages){
            chatRequest.previousMessages.eachWithIndex { message, index ->
                String role = index % 2 == 0 ? OpenAiMessage.ROLE_USER : OpenAiMessage.ROLE_SYSTEM
                openAiRequest.messages << new OpenAiMessage(role: role, content: message)
            }
        }
        openAiRequest.messages << new OpenAiMessage(role: OpenAiMessage.ROLE_USER, content: chatRequest.message)

        return openAiRequest
    }

    static ChatResponse convert(OpenAiResponse chatResponse){
        ChatResponse response = new ChatResponse()
        response.message = chatResponse.choices[0].message.content
        return response
    }
}
