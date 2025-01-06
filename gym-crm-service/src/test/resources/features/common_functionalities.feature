Feature: Common Functionalities

  @PositiveCase
  Scenario: Change password with valid credentials
    Given I have valid change password credentials
    When I request to change the password
    Then the password should be successfully changed

  @NegativeCase
  Scenario: Attempt to change password with invalid user
    Given I have valid change password credentials and invalid user
    When I attempt to change the password
    Then I should receive an error message "Invalid username or password" with invalid user

  @NegativeCase
  Scenario: Attempt to change password with invalid password
    Given I have invalid change password credentials
    When I attempt to change the password with invalid credentials
    Then I should receive an error message "Invalid username or password" with invalid credentials

  @NegativeCase
  Scenario: Activate profile when profile is already activated
    Given I have an activated profile with username "John.Doe"
    When I request to activate the profile for "John.Doe"
    Then the profile for "John.Doe" should remain activated

  @PositiveCase
  Scenario: Activate profile when profile is deactivated
    Given I have a deactivated profile with activated username "John.Doe"
    When I request to activate the profile for "John.Doe" with deactivated profile
    Then the profile for "John.Doe" should be activated

  @NegativeCase
  Scenario: Deactivate profile when profile is already deactivated
    Given I have a deactivated profile with deactivated username "John.Doe"
    When I request to deactivate the profile for "John.Doe"
    Then the profile for "John.Doe" should remain deactivated

  @PositiveCase
  Scenario: Authenticate with valid credentials
    Given I have valid user credentials
    When I request to authenticate
    Then authentication should be successful