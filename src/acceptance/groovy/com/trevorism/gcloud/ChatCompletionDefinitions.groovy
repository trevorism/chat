package com.trevorism.gcloud

import com.google.gson.Gson
import com.trevorism.https.AppClientSecureHttpClient
import com.trevorism.https.SecureHttpClient

this.metaClass.mixin(io.cucumber.groovy.Hooks)
this.metaClass.mixin(io.cucumber.groovy.EN)

SecureHttpClient client = new AppClientSecureHttpClient()
Gson gson = new Gson()
String baseUrl = System.getenv("ACCEPTANCE_BASE_URL") ?: "https://chat.action.trevorism.com"
String prompt = "Reply with the single word: pong"
String json

def sendChat = { String requestJson ->
    try {
        return client.post("${baseUrl}/api/chat", requestJson)
    }
    catch (Exception ignored) {
        Thread.sleep(10000)
        return client.post("${baseUrl}/api/chat", requestJson)
    }
}

When(~/^I send a chat message with no model$/) { ->
    json = sendChat(gson.toJson([message: prompt]))
}

When(~/^I send a chat message using the "([^"]*)" model$/) { String model ->
    json = sendChat(gson.toJson([message: prompt, model: model]))
}

Then(~/^a chat response with a message is returned$/) { ->
    assert json
    Map response = gson.fromJson(json, Map)
    assert response.message
    assert !response.message.trim().isEmpty()
}
