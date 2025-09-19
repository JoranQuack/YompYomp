Feature: View Highlighted Trails (UC_3)
  As a guest user I would like to view the highlighted trails

  Scenario: Guest user accesses dashboard to view highlighted trails (Basic Flow) (AT_7)
    Given the user selects to skip profile set up
    When the system loads up the application for the guest user
    Then the system directs the user from the start screen to the dashboard highlighted trails
    And 8 recommended trails are displayed

  Scenario: User has previously set up profile (Alternative flow) (AT_8)
  Given the user had previously completed the profile quiz and has matchmaking results saved
  When the user navigates to the main.db file
  And user deletes the file resetting the application
  Then the user can restart the application
  And follow the basic flow instructions to find the highlighted page



