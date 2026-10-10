package com.trevorism.openai

import com.google.gson.Gson
import com.trevorism.chat.ProviderErrors
import com.trevorism.http.HeadersHttpResponse
import com.trevorism.http.HttpClient
import com.trevorism.http.JsonHttpClient
import com.trevorism.http.util.InvalidRequestException
import com.trevorism.https.token.ObtainTokenFromPropertiesFile
import com.trevorism.https.token.ObtainTokenStrategy
import com.trevorism.openai.model.OpenAiRequest
import com.trevorism.openai.model.OpenAiResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@jakarta.inject.Singleton
class DefaultOpenAiClient implements OpenAiClient {

    private static final Logger log = LoggerFactory.getLogger(DefaultOpenAiClient)
    private HttpClient httpClient = new JsonHttpClient()
    private Gson gson = new Gson()
    private ObtainTokenStrategy obtainTokenFromPropertiesFile = new ObtainTokenFromPropertiesFile()

    @Override
    OpenAiResponse chat(OpenAiRequest request) {
        String requestJson = gson.toJson(request)
        Map authHeader = ["Authorization":"Bearer ${obtainTokenFromPropertiesFile.getToken()}"]
        HeadersHttpResponse response
        try {
            response = httpClient.post("${OPENAI_BASE_URL}/responses", requestJson, authHeader)
        }
        catch (InvalidRequestException e) {
            log.warn("OpenAI request failed with ${e.statusCode}: ${e.responseBody}")
            throw ProviderErrors.toHttpStatusException("OpenAI", e)
        }
        log.debug("OpenAI response: ${response.value}")
        return gson.fromJson(response.value, OpenAiResponse)
    }
}
