Feature: View All Trails (UC_4)
  I would like to view all the trails available on the application

Scenario: User skips profile quiz and straight into application (Basic Flow) (AT_10)
  Given the user has loaded the application and is on the welcome screen
  When the user selects to skip profile set up
  And user selects the Trails button
  Then the system changes to the all-trails screen
  And system displays a list of trails in alphabetical order

Scenario: User has completed the profile quiz (Alternative Flow) (AT_11)
  Given the user has completed the profile quiz
  And the dashboard screen of personalised recommended trails is shown
  When user selects the Trails button
  Then the system changes to the all-trails screen
  And system displays a list of trails in alphabetical order