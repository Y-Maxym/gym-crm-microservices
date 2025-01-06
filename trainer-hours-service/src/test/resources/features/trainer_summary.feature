Feature: Trainer Summary

  @PositiveCase
  Scenario: Valid request updates the monthly summary correctly
    Given a valid request for updating trainer summary
    When the sumTrainerSummary is executed
    Then the monthly summary is updated correctly and equals 120

  @PositiveCase
  Scenario: Get correct workload for a given trainer, year, and month
    Given the trainer John.Doe has a total training duration of 120
    When I request the workload for trainer "John.Doe" year 2024 month 5
    Then the workload returned should be 120

  @NegativeCase
  Scenario: Attempt to get workload for a trainer with no workload should throw an exception
    When I request the workload for trainer "John.Doe" for 5 year 2024
    Then an exception should be thrown indicating "User information not found"