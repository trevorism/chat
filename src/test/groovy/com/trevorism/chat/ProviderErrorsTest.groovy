package com.trevorism.chat

import com.trevorism.http.util.HttpResponseBodyException
import com.trevorism.http.util.InvalidRequestException
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import org.junit.jupiter.api.Test

class ProviderErrorsTest {

    private static InvalidRequestException failure(int statusCode, String body) {
        new InvalidRequestException(new HttpResponseBodyException(statusCode, "reason", body), statusCode)
    }

    @Test
    void testBadRequestPassesThroughWithProviderMessage() {
        HttpStatusException e = ProviderErrors.toHttpStatusException("Claude",
                failure(400, '{"type":"error","error":{"type":"invalid_request_error","message":"bad thinking"}}'))
        assert e.status == HttpStatus.BAD_REQUEST
        assert e.message == "Claude returned 400: bad thinking"
    }

    @Test
    void testRateLimitPassesThrough() {
        assert ProviderErrors.toHttpStatusException("OpenAI", failure(429, '{"error":{"message":"slow down"}}')).status == HttpStatus.TOO_MANY_REQUESTS
    }

    @Test
    void testOtherFailuresBecomeBadGateway() {
        HttpStatusException e = ProviderErrors.toHttpStatusException("OpenAI", failure(401, '{"error":{"message":"bad key"}}'))
        assert e.status == HttpStatus.BAD_GATEWAY
        assert e.message == "OpenAI returned 401: bad key"
    }

    @Test
    void testNonJsonBodyIsReturnedAsIs() {
        assert ProviderErrors.toHttpStatusException("Claude", failure(503, "<html>overloaded</html>")).message == "Claude returned 503: <html>overloaded</html>"
    }

    @Test
    void testMissingBodyUsesExceptionMessage() {
        HttpStatusException e = ProviderErrors.toHttpStatusException("Claude", new InvalidRequestException(new IOException("timed out")))
        assert e.status == HttpStatus.BAD_GATEWAY
        assert e.message.contains("timed out")
    }
}
