#Feature: View All Trails (UC_3)
#  I would like to view all the trails available on the application
#
#Scenario: User skips profile quiz and straight into application (Basic Flow) (AT_9)
#  Given the user has loaded the application and is on the start screen
#  When the user selects to skip profile set up
#  And user selects the Trails button
#  Then the system changes to the all-trails screen
#  And system displays a list of trails in alphabetical order, retrieved from the database with no match bar
#
#Scenario: User has completed the profile quiz (Alternative Flow) (AT_10)
#  Given the user has completed the profile quiz
#  And the dashboard screen of personalised recommended trails is shown
#  When user selects "All Trails" button
#  Then system changes to the all-trails screen
#  And system displays a list of trails in alphabetical order, retrieved from the database with each trail shown having a match bar percentage
#
#Scenario: View All Trails. Loading all trails page fails (Exceptional flow) (AT_11)
#  Given the user has the application page and is either on highlighted trails if they haven't completed the quiz or recommended trails if they have
#  When user selects "All Trails" button
#  And system fails to load all trails screen
#  And there have been 3 attempts at automatically loading it again
#  Then an error message of "Failed to load trails, please restart the application" is displayed
#  And the user is brought back to either the highlighted trails or recommended trails respectively
