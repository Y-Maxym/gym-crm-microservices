Feature: Manage Trainers

  Scenario: Creation of a trainer profile with valid data
    Given I have a valid data for creating a trainer profile
    When I send a request to create a trainer profile using the valid data
    Then the trainer profile should be successfully created with non-empty username and password

  Scenario: Attempt to create a trainer profile with invalid data
    Given I have invalid data for creating a trainer profile
    When I attempt to create a trainer profile using the invalid data
    Then I should be informed about the trainer creation error

  Scenario: Find trainer profile by valid username
    Given there is a trainer profile with username "David.Brown"
    When I request to find the trainer profile by username "David.Brown"
    Then I should retrieve the trainer profile with first name "David" and last name "Brown"

  Scenario: Attempt to find trainer profile by invalid username
    Given I have the username "invalid"
    When I attempt to find the trainer profile by this username
    Then I should be informed that the trainer profile was not found

  Scenario: Update trainer profile with valid data
    Given I have valid data to update a trainer profile for username "Emily.Davis"
    When I request to update the trainer profile with valid data
    Then the trainer profile should be successfully updated

  Scenario: Attempt to update trainer profile with invalid data
    Given I have invalid data to update a trainer profile for username "Emily.Davis"
    When I attempt to update the trainer profile with invalid data
    Then I should be informed about the trainer update error

  Scenario: Retrieve list of trainers not assigned by trainee's username
    Given the trainee "John.Doe" exists
    When I request a list of trainers not assigned to "John.Doe"
    Then I should receive a list of trainers with a size of 1