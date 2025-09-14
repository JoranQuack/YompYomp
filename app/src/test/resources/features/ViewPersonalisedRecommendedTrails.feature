Feature: View personalised recommended trails
As a profiled user
I want to view my personalised recommended trails
So that I can see trails that best match my preferences


Scenario: View recommended trails as a profiled user (basic flow)
  Given the user has completed the profile quiz
  And all trail data is available and has been loaded
  When the system begins match making
  Then the user sees a loading screen for between 1 and 10 seconds with the message "Matchmaking in progress..."
  And 8 recommended trails are displayed
  And the trails are ordered by highest to lowest match

Scenario: View recommended trails as a returning profiled user with saved match making results (Alternative Flow)
  Given the user had previously completed the profile quiz and has matchmaking results saved
  And the user opens the application and selects “Continue” button
  When the system loads the personalised recommendations
  Then the user is shown the previously calculated personalised recommended trails screen directly
  And the trails are ordered from highest match to lowest
#
#Scenario: Matchmaking fails (exceptional flow)
#  Given the matchmaking process fails 3 times
#  Then the system displays "Matchmaking has exceed the number of possible retries. Please restart the application and redo the quiz."
#  And the user is redirected to the general recommended trails screen
