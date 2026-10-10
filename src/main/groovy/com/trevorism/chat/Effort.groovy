package com.trevorism.chat

import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException

class Effort {

    public static final String DEFAULT = "low"
    public static final List<String> LEVELS = ["low", "medium", "high", "xhigh", "max"]

    static String resolve(String effort) {
        if (!effort) {
            return DEFAULT
        }
        String normalized = effort.toLowerCase()
        if (!(normalized in LEVELS)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Unsupported effort '${effort}'. Supported efforts: ${LEVELS.join(', ')}")
        }
        return normalized
    }
}
