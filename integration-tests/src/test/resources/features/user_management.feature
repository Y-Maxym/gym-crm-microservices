Feature: User Management and Authentication

  Scenario: Successful authentication of an existing user
    Given I have an existing user with a username "admin" and password "password"
    When I attempt to authenticate with these credentials
    Then I receive a successful JWT token

  Scenario: Successful creation of a user and their authentication
    Given I have data for creating a new user
      | firstName | lastName | dateOfBirth | address |
      | John      | Doe      | 2000-01-01  | W.st.   |
    When I send this data to the server
    Then The user is successfully created
    And I attempt to authenticate with the new user's credentials
    Then The authentication is successful and I receive a JWT token