Feature: View personalised recommended trails
As a profiled user
I want to view my personalised recommended trails
So that I can see tramos that best match my preferences

Background:
  Given the user has completed the profile quiz

Scenario: View personalised recommended trails (basic flow)
  When the user completes the quiz
  Then the system shows a loading screen with "Matchmaking..."
  And after matchmaking the system displays 8 personalised trails ordered by best match

Scenario: View previously calculated personalised trails (alternate flow)
  Given the user has previously completed matchmaking
  When the user selects "Continue" on the start screen
  Then the system displays the previously calculated personalised recommended trails

Scenario: Matchmaking fails (exceptional flow)
  When the matchmaking process fails 3 times
  Then the system displays "Matchmaking has exceed the number of possible retries. Please restart the appliaction and redo the quiz."
  And the user is redirected to the general recommended trails screen
