Feature: View personalised recommended trails
As a profiled user
I want to view my personalised recommended trails
So that I can see trails that best match my preferences


Scenario: View recommended trails as a profiled user (basic flow) (AT_1)
  Given the user has completed the profile quiz
  And all trail data is available and has been loaded
  When the system begins match making
  Then the user sees a loading screen for between 1 and 10 seconds with the message "Matchmaking in progress..."
  And 8 recommended trails are displayed
  And the trails are ordered by highest to lowest match

Scenario: View recommended trails as a returning profiled user with saved match making results (Alternative Flow) (AT_2)
  Given the user had previously completed the profile quiz and has matchmaking results saved
  And the user opens the application and selects “Continue” button
  When the system loads the personalised recommendations
  Then the user is shown the previously calculated personalised recommended trails screen directly
  And the trails are ordered from highest match to lowest

Scenario: View recommended trails as a profiled user. Matchmaking fails once (Exceptional Flow) (AT_3)
  Given the user has completed the profile quiz
  And matchmaking fails on the first attempt
  When the system retries matchmaking
  Then user sees the error message "Matchmaking failed, please try again."
  And system attempts matchmaking again automatically up to 3 times

Scenario: View recommended trails as a profiled user. Matchmaking fails more than 3 times (Exceptional flow) (AT_4)
  Given the user has completed the profile quiz
  And matchmaking fails on the first attempt
  When the system retries matchmaking
  And the matchmaking retries over 3 times
  Then an error message of "Matchmaking has exceeded the number of possible retries. Please restart the application and redo the quiz" is displayed
  And the user is directed to the general recommended trail screen used for guest mode