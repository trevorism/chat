package com.trevorism.chat

import com.trevorism.model.ChatRequest
import com.trevorism.model.ChatResponse

interface ChatProvider {

    ChatResponse chat(ChatRequest request)

}
