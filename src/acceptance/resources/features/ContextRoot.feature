Feature: Context Root of this API
  In order to use the API, it must be available

  Scenario: Root of the API HTTPS
    Given the chat application is alive
    When I navigate to "https://chat.action.trevorism.com"
    Then then a link to the help page is displayed

  Scenario: Ping HTTPS
    Given the chat application is alive
    When I ping the application deployed to "https://chat.action.trevorism.com"
    Then pong is returned, to indicate the service is alive

