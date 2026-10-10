package com.trevorism.claude

import com.google.gson.Gson
import com.trevorism.chat.ProviderErrors
import com.trevorism.claude.model.ClaudeRequest
import com.trevorism.claude.model.ClaudeResponse
import com.trevorism.http.HeadersHttpResponse
import com.trevorism.http.HttpClient
import com.trevorism.http.JsonHttpClient
import com.trevorism.http.util.InvalidRequestException
import com.trevorism.https.token.ObtainTokenFromPropertiesFile
import com.trevorism.https.token.ObtainTokenStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@jakarta.inject.Singleton
class DefaultClaudeClient implements ClaudeClient {

    private static final Logger log = LoggerFactory.getLogger(DefaultClaudeClient)
    private static final String API_KEY_PROPERTY_NAME = "apiKey"

    private HttpClient httpClient = new JsonHttpClient()
    private Gson gson = new Gson()
    private ObtainTokenStrategy obtainTokenFromPropertiesFile =
            new ObtainTokenFromPropertiesFile(ObtainTokenStrategy.DEFAULT_PROPERTIES_FILE_NAME, API_KEY_PROPERTY_NAME)

    @Override
    ClaudeResponse chat(ClaudeRequest request) {
        String requestJson = gson.toJson(request)
        Map headers = ["x-api-key": obtainTokenFromPropertiesFile.getToken(), "anthropic-version": ANTHROPIC_VERSION]
        if (request.fallbacks) {
            headers["anthropic-beta"] = SERVER_SIDE_FALLBACK_BETA
        }
        HeadersHttpResponse response
        try {
            response = httpClient.post("${CLAUDE_BASE_URL}/messages", requestJson, headers)
        }
        catch (InvalidRequestException e) {
            log.warn("Claude request failed with ${e.statusCode}: ${e.responseBody}")
            throw ProviderErrors.toHttpStatusException("Claude", e)
        }
        log.debug("Claude response: ${response.value}")
        return gson.fromJson(response.value, ClaudeResponse)
    }
}
