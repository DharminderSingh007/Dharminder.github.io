Feature: Google Search
  Scenario: Search for WebDriverIO
    Given I open Google's homepage
    When I search for "WebDriverIO"
    Then the results page shows links related to WebDriverIO
