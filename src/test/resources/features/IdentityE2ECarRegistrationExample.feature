@Test
Feature: IdentityE2E Car Registration Number Checker Example

  @Test
  Scenario: Get Car Registration numbers from file and check them on WeBuyAnyCar
    Given I have loaded the registration numbers of the cars I wish to check
    When I navigate to the WeBuyAnyCar Website and enter the loaded registration numbers
    Then I confirm that the value is correct for the mileage given
