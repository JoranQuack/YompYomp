Feature: View All Trails (UC_3)
  I would like to view all the trails available on the application

Scenario: User skips profile quiz and straight into application (Basic Flow) (AT_7)
  Given the user has loaded the application and is on the welcome screen
  When the user selects to skip profile set up
  And user selects the Trails button
  Then the system changes to the all-trails screen
#  And system displays a list of trails in alphabetical order

Scenario: User has completed the profile quiz (Alternative Flow) (AT_8)
  Given the user has completed the profile quiz
  And the dashboard screen of personalised recommended trails is shown
  When user selects the Trails button
  Then the system changes to the all-trails screen
#  And system displays a list of trails in alphabetical order

Scenario: View All Trails. Loading all trails page fails (Exceptional flow) (AT_9)
  Given the user has the application page open and is either on highlighted trails if they haven't completed the quiz or recommended trails if they have
  When user selects the Trails button
  And system fails to load all trails screen
  Then an error message of "Failed to load trails, please restart the application" is displayed
  And the user is brought back to either the highlighted trails or recommended trails respectively
