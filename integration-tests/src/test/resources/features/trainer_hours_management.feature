Feature: Trainer hours registration and verification

  Scenario: Register and verify trainer's hours
    Given I have user credentials
      | username | admin    |
      | password | password |
    When I send a request for authentication to authentication-service
    Then I should receive a valid JWT token
    When I register a new training session for a trainer with gym-crm-service
      | traineeUsername  | John.Doe    |
      | trainerUsername  | Emily.Davis |
      | trainingName     | Training 1  |
      | trainingDate     | 2023-09-01  |
      | trainingDuration | 3           |
    And I attempt to retrieve the training session details from trainer-hours-service
    Then I can verify the training session details with trainer-hours-service