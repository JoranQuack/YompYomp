<div align="center">
    <img src="app/src/main/resources/images/YompYompLogo.png" alt="YOMPYOMP_LOGO" width="500"/>
</div>

<div align="center">

[![Pipeline](https://eng-git.canterbury.ac.nz/seng202-2025/seng202-25-team5/badges/main/pipeline.svg)](https://eng-git.canterbury.ac.nz/seng202-2025/seng202-25-team5/)
[![coverage report](https://eng-git.canterbury.ac.nz/seng202-2025/seng202-25-team5/badges/main/coverage.svg)](https://eng-git.canterbury.ac.nz/seng202-2025/seng202-25-team5/-/commits/main)

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
> This Jar is NOT cross-platform, so you must build the jar on the appropriate OS (and machine) to where you want to run it.<br>

> [!NOTE]
> When the application is first launched images will slowly populate alphabetically.

### Running Tests

To run tests, run the following command

```bash
  ./gradlew test
```

## API Reference

The weather system requires an API key from OpenWeatherMap. Either set your system environment or include the key in a config.properties file.

| Option      | Parameter             | Instructions                        |
| :---------- | :-------------------- | :---------------------------------- |
| Config File | `openweather.api.key` | Move to app/src/main/resources/     |
| Environment | `OPENWEATHER_API_KEY` | Add to system environment variables |

## Current Product Version

### Use Cases Implemented

- **UC_1** Complete Profile Quiz
- **UC_2** View Personalised Recommended Trails
- **UC_3** View Highlighted Trails
- **UC_4** View All Trails
- **UC_5** Search Trails
- **UC_6** View Single Trail (without map and weather)
- **UC_7** Add Missing Trail
- **UC_8** Modify Existing Trail
- **UC_9** Log Trail
- **UC_10** Access Logbook
- **UC_11** Access Account
- **UC_12** View and Customise Profile

### Feature Packages Implemented

- **Iwi** Bi-cultural component
- **Kehu** Basic UI and Loading Existing Data
- **Hillary** Extended Viewing and Data Filtering
- **Tasman** Basic Exploration
- **Tia** Extended Loading and Data Input
- **Dudley Dobson** Data Validation
- **Brunner** Persistent Data Storage
- **Colenso** Graphs & Charts
- **Heaphy** Map Browsing
- **Kupe** Map Visualisations

### Highlighted Features

These are features the team is most proud of.

- Minimalist and consistent UI
- Matchmaking recommendation algorithm
- Filtering and sorting features
- Setup quiz interface
- Map interface
- Weather system
- Logbook features
- Modification and addition of trails
- Guest mode functionality
- Account statistics

### Potential future features (not implemented)

- Multiple user profiles
- Trail routes
- Mobile app support
- External database (not local)
- Upload new dataset

## Authors

- [Hayley Kawelenga](https://eng-git.canterbury.ac.nz/hka105)
- [Jonny Chan](https://eng-git.canterbury.ac.nz/jch485)
- [Joran Le Quellec](https://eng-git.canterbury.ac.nz/jle209)
- [Lam (Lucy) Tran](https://eng-git.canterbury.ac.nz/ltr53)
- [Sam Ladbrook](https://eng-git.canterbury.ac.nz/sla202)
- [Sienna Robinson](https://eng-git.canterbury.ac.nz/sro179)
