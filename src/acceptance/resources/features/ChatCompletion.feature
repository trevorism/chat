Feature: Chat requests reach both upstream providers
  In order to relay chat messages, the deployed service must hold valid credentials for, and be able to reach, each provider it routes to

  Scenario: A request without a model is answered by OpenAI
    Given the chat application is alive
    When I send a chat message with no model
    Then a chat response with a message is returned

  Scenario: A claude model is answered by Claude
    Given the chat application is alive
    When I send a chat message using the "claude-haiku-4-5" model
    Then a chat response with a message is returned
