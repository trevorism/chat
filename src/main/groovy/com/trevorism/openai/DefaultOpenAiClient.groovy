package com.trevorism.openai

import com.google.gson.Gson
import com.trevorism.http.HeadersHttpResponse
import com.trevorism.http.HttpClient
import com.trevorism.http.JsonHttpClient
import com.trevorism.https.token.ObtainTokenFromPropertiesFile
import com.trevorism.https.token.ObtainTokenStrategy
import com.trevorism.openai.model.OpenAiRequest
import com.trevorism.openai.model.OpenAiResponse

@jakarta.inject.Singleton
class DefaultOpenAiClient implements OpenAiClient {

    private HttpClient httpClient = new JsonHttpClient()
    private Gson gson = new Gson()
    private ObtainTokenStrategy obtainTokenFromPropertiesFile = new ObtainTokenFromPropertiesFile()

    @Override
    OpenAiResponse chat(OpenAiRequest request) {
        String requestJson = gson.toJson(request)
        Map authHeader = ["Authorization":"Bearer ${obtainTokenFromPropertiesFile.getToken()}"]
        HeadersHttpResponse response = httpClient.post("${OPENAI_BASE_URL}/chat/completions", requestJson, authHeader)
        String responseJson = response.value
        return gson.fromJson(responseJson, OpenAiResponse)
    }
}
