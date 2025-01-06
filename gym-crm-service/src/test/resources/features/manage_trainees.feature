Feature: Manage Trainees

  @PositiveCase
  Scenario: Create trainee profile with valid data
    Given I have valid data for creating a trainee profile
    When I send a request to create a trainee profile using the valid data
    Then the trainee profile should be successfully created with non-empty username and password

  @NegativeCase
  Scenario: Attempt to create trainee profile with invalid data
    Given I have invalid data for creating a trainee profile
    When I attempt to create a trainee profile using the invalid data
    Then I should be informed about the trainee creation error

  @PositiveCase
  Scenario: Find trainee profile by valid username
    Given I have a valid username "John.Doe"
    When I request to find the trainee profile by this username
    Then I should retrieve the trainee profile with first name "John" and last name "Doe"

  @NegativeCase
  Scenario: Attempt to find trainee profile by invalid username
    Given I have an invalid username "invalid"
    When I attempt to find the trainee profile by this username
    Then I should be informed that the trainee profile was not found

  @PositiveCase
  Scenario: Update trainee profile with valid data
    Given I have valid data to update a trainee profile for username "John.Doe"
    When I request to update the trainee profile with valid data
    Then the trainee profile should be successfully updated

  @NegativeCase
  Scenario: Attempt to update trainee profile with invalid data
    Given I have invalid data to update a trainee profile for username "John.Doe"
    When I attempt to update the trainee profile with invalid data
    Then I should be informed about the trainee update error

  @PositiveCase
  Scenario: Delete trainee profile by username
    Given a trainee profile exists with username "John.Doe"
    When I delete the trainee profile by username "John.Doe"
    Then the trainee profile should be deleted

  @NegativeCase
  Scenario: Attempt to delete trainee profile by incorrect username
    Given a trainee profile non exists with username "John.Doe"
    When I attempt to delete the trainee profile by username "John.Doe"
    Then I should receive an error message "Invalid username"

  @PositiveCase
  Scenario: Add trainer to trainee
    Given a trainee with username "John.Doe" exists
    When I add a new trainer to the trainee "John.Doe"
    Then the trainee "John.Doe" should have 2 trainers