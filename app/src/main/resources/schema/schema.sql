-- Remember to increment the schema version here when making changes!
-- Schema version: 2.3
PRAGMA foreign_keys = off;

BEGIN TRANSACTION;

-- Table: category
DROP TABLE IF EXISTS category;

CREATE TABLE
    IF NOT EXISTS category (id INTEGER PRIMARY KEY, name TEXT UNIQUE);

-- Table: keyword
DROP TABLE IF EXISTS keyword;

CREATE TABLE
    IF NOT EXISTS keyword (
        value TEXT NOT NULL,
        categoryId INTEGER NOT NULL REFERENCES category (id),
        PRIMARY KEY (value, categoryId)
    );

-- Table: trail
DROP TABLE IF EXISTS trail;

CREATE TABLE
    IF NOT EXISTS trail (
        id INTEGER,
        name TEXT,
        translation TEXT,
        region TEXT,
        difficulty TEXT,
        description TEXT,
        completionInfo TEXT,
        minCompletionTimeMinutes INTEGER,
        maxCompletionTimeMinutes INTEGER,
        completionType TEXT,
        timeUnit TEXT,
        isMultiDay BOOL,
        hasVariableTime BOOL,
        thumbUrl TEXT,
        webUrl TEXT,
        cultureUrl TEXT,
        userWeight REAL,
        lat REAL,
        lon REAL,
        PRIMARY KEY (id)
    );

-- Table: trailCategory
DROP TABLE IF EXISTS trailCategory;

CREATE TABLE
    IF NOT EXISTS trailCategory (
        trailId INTEGER NOT NULL REFERENCES trail (id),
        categoryId INTEGER NOT NULL REFERENCES category (id),
        PRIMARY KEY (trailId, categoryId)
    );

-- Table: user
DROP TABLE IF EXISTS user;

CREATE TABLE
    IF NOT EXISTS user (
        id INTEGER PRIMARY KEY,
        name TEXT,
        regions TEXT,
        isFamilyFriendly BOOL,
        isAccessible BOOL,
        experienceLevel INTEGER,
        gradientPreference INTEGER,
        bushPreference INTEGER,
        reservePreference INTEGER,
        lakeRiverPreference INTEGER,
        coastPreference INTEGER,
        mountainPreference INTEGER,
        wildlifePreference INTEGER,
        historicPreference INTEGER,
        waterfallPreference INTEGER,
        isProfileComplete BOOL DEFAULT 0,
        profilePicture VARCHAR(260)
    );

-- Table: filterOptions
DROP TABLE IF EXISTS filterOptions;

CREATE TABLE
    IF NOT EXISTS filterOptions (
        filterType TEXT NOT NULL,
        optionValue TEXT NOT NULL,
        displayOrder INTEGER DEFAULT 0,
        PRIMARY KEY (filterType, optionValue)
    );

-- Table: trail log
DROP TABLE IF EXISTS trailLog;

CREATE TABLE
    IF NOT EXISTS trailLog (
        id INTEGER PRIMARY KEY,
        trailId INTEGER NOT NULL REFERENCES trail (id),
        startDate TEXT NOT NULL,
        completionTime INTEGER,
        timeUnit TEXT,
        completionType TEXT,
        rating INTEGER CHECK(rating BETWEEN 1 AND 5),
        perceivedDifficulty TEXT,
        notes TEXT
    );


COMMIT TRANSACTION;

PRAGMA foreign_keys = ON;