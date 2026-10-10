package com.trevorism.chat

import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import org.junit.jupiter.api.Test

class EffortTest {

    @Test
    void testResolveDefaultsToLow() {
        assert Effort.resolve(null) == "low"
        assert Effort.resolve("") == "low"
    }

    @Test
    void testResolveNormalizesCase() {
        assert Effort.resolve("XHigh") == "xhigh"
    }

    @Test
    void testResolveAcceptsEveryLevel() {
        ["low", "medium", "high", "xhigh", "max"].each { assert Effort.resolve(it) == it }
    }

    @Test
    void testResolveRejectsUnknownEffort() {
        try {
            Effort.resolve("none")
            assert false
        }
        catch (HttpStatusException e) {
            assert e.status == HttpStatus.BAD_REQUEST
            assert e.message.contains("none")
        }
    }
}
