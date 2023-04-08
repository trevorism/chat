package com.trevorism.openai

import com.trevorism.openai.model.OpenAiRequest
import com.trevorism.openai.model.OpenAiResponse

interface OpenAiClient {

    public static String OPENAI_BASE_URL = "https://api.openai.com/v1"

    OpenAiResponse chat(OpenAiRequest request)

}