Feature: Complete Profile Quiz (UC_1)
  I would like to complete a profile set up quiz so I can see
  which trails match my preferences and how much they match my
  preferences


Scenario: User completes profile quiz for the first time (basic flow) (AT_1)
  Given the user has completed the profile quiz
  And all trail data is available and has been loaded
  When the system begins match making
  Then the user sees a loading screen for between 1 and 10 seconds with the message "Matchmaking in progress..."

Scenario: User selected to redo quiz (Alternative Flow) (AT_2)
  Given the user had previously completed the profile quiz and has matchmaking results saved
  When the user selects the "Change Quiz Preferences" button on the dashboard
  Then the user will be taken back to original questions for the profile
  And the basic flow of the application is followed

Scenario: Completed profile quiz. Matchmaking fails (Exceptional Flow) (AT_3)
  Given the user has completed the profile quiz
  When matchmaking fails
  Then user sees the error message "Matchmaking failed, please close the application and try again."
  And user is displayed the View Highlighted Trails screen

