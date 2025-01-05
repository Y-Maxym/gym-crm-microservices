Feature: Manage Trainings

  @positive
  Scenario: Add training with valid data
    Given I have valid data for creating a training
    When I send a request to add a training
    Then the training should be successfully added

  @negative
  Scenario: Attempt to add training with invalid data
    Given I have invalid data for creating a training
    When I send a request to add a training with invalid data
    Then I should be informed about the training creation error

  @positive
  Scenario: Get trainee trainings by criteria
    Given I have a username "John.Doe"
    And I have a date range from "2020-01-01" to "2020-01-02"
    And I have a trainer name "Emily"
    When I request to get trainee trainings by criteria
    Then I should receive 1 trainee training

  @positive
  Scenario: Get trainer trainings by criteria
    Given I am a trainer with username "Emily.Davis"
    And I specify a date range from "2020-01-01" to "2020-01-02"
    And I specify a trainee name "John"
    When I request to get my trainings by criteria
    Then I should receive 1 trainer training

  @positive
  Scenario: Get training types
    When I request to get training types
    Then I should receive 5 training types