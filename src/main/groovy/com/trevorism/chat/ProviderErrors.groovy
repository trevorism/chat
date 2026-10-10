package com.trevorism.chat

import com.google.gson.Gson
import com.trevorism.http.util.InvalidRequestException
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException

class ProviderErrors {

    private static final List<Integer> PASS_THROUGH_STATUS_CODES = [400, 429]
    private static final Gson gson = new Gson()

    static HttpStatusException toHttpStatusException(String provider, InvalidRequestException e) {
        HttpStatus status = e.statusCode in PASS_THROUGH_STATUS_CODES ? HttpStatus.valueOf(e.statusCode) : HttpStatus.BAD_GATEWAY
        return new HttpStatusException(status, "${provider} returned ${e.statusCode}: ${extractMessage(e)}")
    }

    private static String extractMessage(InvalidRequestException e) {
        String body = e.responseBody
        try {
            return gson.fromJson(body, Map)?.error?.message ?: body ?: e.message
        }
        catch (Exception ignored) {
            return body
        }
    }
}
