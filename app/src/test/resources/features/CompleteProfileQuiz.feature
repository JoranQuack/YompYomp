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
  When the user selects the "Redo Quiz" button on the dashboard
  Then the user will be taken back to original questions for the profile
  And the basic flow of the application is followed

Scenario: Completed profile quiz. Matchmaking fails once (Exceptional Flow) (AT_3)
  Given the user has completed the profile quiz
  And matchmaking fails
  When the system retries matchmaking
  Then user sees the error message "Matchmaking failed, please try again."
  And system attempts matchmaking again automatically up to 3 times

#TODO change according to new AT3
Scenario: Completed profile quiz. Matchmaking fails more than 3 times (Exceptional flow) (AT_4)
  Given the user has completed the profile quiz
  And matchmaking fails
  When the system retries matchmaking
  And the matchmaking retries over 3 times
  Then an error message of "Matchmaking has exceeded the number of possible retries. Please restart the application and redo the quiz" is displayed
  And the user is directed to the general recommended trail screen used for guest mode