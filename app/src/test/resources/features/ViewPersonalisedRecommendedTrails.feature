Feature: View Personalised Recommended Trails (UC_2)
  As a profiled user I would like to view the trails that
  are recommended for me based on my answers to the profile
  set up quiz questions

Scenario: User views their recommended trails for the first time (Basic Flow) (AT_5)
  Given the user has completed the profile quiz
  When the system begins match making
  Then the user sees a loading screen for between 1 and 10 seconds with the message "Matchmaking in progress..."
  And 8 recommended trails are displayed
  And the trails are ordered by highest to lowest match

Scenario: Returning user views recommended trail (Alternative flow) AT_6
  Given the user has completed the profile quiz
  When the user reloads up the application
  And user selects the "Continue" button on the start screen
  Then the user is shown the previously calculated personalised recommended trails screen directly

Scenario: View Personalised Trail. Matchmaking calculations fail and produce an error (first time) (Exceptional flow) AT_7
  Given the user has completed the profile quiz
  When matchmaking fails on the first attempt
  Then user sees the error message "Matchmaking failed, please try again."

Scenario: View Personalised Trail. Matchmaking calculations fail and produce an error (3 times) (Exceptional Flow) (AT_8)
  Given the user has completed the profile quiz
  When matchmaking fails on the first attempt
  And the matchmaking retries over 3 times
  Then an error message of "Matchmaking has exceeded the number of possible retries. Please restart the application and redo the quiz" is displayed
  And the user is directed to the general recommended trail screen used for guest mode


