Feature: Chat requests reach both upstream providers
  In order to relay chat messages, the deployed service must hold valid credentials for, and be able to reach, each provider it routes to

  Scenario: A request without a model is answered by the default OpenAI model
    Given the chat application is alive
    When I send a chat message with no model
    Then a chat response with a message is returned
    And the response was answered by "gpt-6.1-sol"

  Scenario Outline: A supported model is answered by its provider
    Given the chat application is alive
    When I send a chat message using the "<model>" model with "<effort>" effort
    Then a chat response with a message is returned

    Examples:
      | model             | effort |
      | gpt-6-luna        | medium |
      | claude-sonnet-5-5 | low    |
      | claude-haiku-4-5  | low    |

  Scenario: An unsupported model is rejected
    Given the chat application is alive
    When I send a chat message using the unsupported "gpt-3.5-turbo" model
    Then the request is rejected as a bad request
