package com.trevorism.claude

import com.trevorism.claude.model.ClaudeRequest
import com.trevorism.claude.model.ClaudeResponse

interface ClaudeClient {

    public static String CLAUDE_BASE_URL = "https://api.anthropic.com/v1"
    public static String ANTHROPIC_VERSION = "2023-06-01"

    ClaudeResponse chat(ClaudeRequest request)

}
