<div style="display: flex; justify-content: center">
    <img src="app/src/main/resources/images/YompYompLogo.png" alt="YOMPYOMP_LOGO" width="500"/>
</div>

# SENG202-25-Team5 - YompYomp

6-person group project to create a New Zealand tramping app, as part of the SENG202 2025 course at the University of Canterbury.

The name of this app was chosen from the definition of the word "yomp", which means "a march with heavy equipment over difficult terrain". The decision to multiply it by 2 was to create a more interesting name for the user, and help with memorability.

## Description

This app loads DOC datasets into a database, profiles the user, and recommends trails to the user based on their preferences. The user can also access each trail to view further information, and will be able to view the route on a map, and the current weather at the location. A trails screen is also included to help user filter, search, and sort for the trail that they would like to find manually.

## Installation and Usage

### Prerequisites

- JDK >= 21 [click here to get the latest stable OpenJDK release](https://jdk.java.net/21/)
- _(optional)_ Gradle [Download](https://gradle.org/releases/) and [Install](https://gradle.org/install/)

### Run Locally

Clone the project

```bash
  git clone https://eng-git.canterbury.ac.nz/seng202-2025/seng202-25-team5
```

Go to the project directory and run

```bash
  ./gradlew run
```

### Build Project

Create a packaged Jar

```bash
  ./gradlew jar
```

Go to build directory

```bash
  cd app/build/libs
```

Open the application

```bash
  java -jar yompyomp.jar
```

> [!IMPORTANT]
> This Jar is NOT cross-platform, so you must build the jar on the appropriate OS (and machine) to where you want to run it.

### Running Tests

To run tests, run the following command

```bash
  ./gradlew test
```

## Current Product Version

### Use Cases and Features Implemented

- **UC_1** Complete Profile Quiz
- **UC_2** View Personalised Recommended Trails
- **UC_3** View All Trails
- **UC_4** Search Trails
- **UC_5** View Single Trail (without map and weather)
- **UC_6** Add Missing Trail
- **UC_7** Modify Existing Trail

### Highlighted Features

These are features the team is most proud of.

- Matchmaking recommendation algorithm
- Description scanning algorithm for keywords and trail categorisation
- Setup quiz interface
- Filtering and sorting features
- Database operation seamlessness
- Guest mode functionality
- Using components to reduce code complexity

### Use Cases and Features Not Yet Implemented

- **UC_8** Log Trip
- **UC_9** Rate Trip
- **UC_10** Access Logbook
- Map features for **UC_5**
- Weather features for **UC_5**

## Authors

- [Hayley Kawelenga](https://eng-git.canterbury.ac.nz/hka105)
- [Jonny Chan](https://eng-git.canterbury.ac.nz/jch485)
- [Joran Le Quellec](https://eng-git.canterbury.ac.nz/jle209)
- [Lam (Lucy) Tran](https://eng-git.canterbury.ac.nz/ltr53)
- [Sam Ladbrook](https://eng-git.canterbury.ac.nz/sla202)
- [Sienna Robinson](https://eng-git.canterbury.ac.nz/sro179)
