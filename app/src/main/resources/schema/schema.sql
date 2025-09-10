--
-- File generated with SQLiteStudio v3.4.17 on Tue Sept 9 18:15:24 2025
--
-- Text encoding used: UTF-8
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: category
DROP TABLE IF EXISTS category;

CREATE TABLE IF NOT EXISTS category (
    id   INTEGER PRIMARY KEY,
    name TEXT    UNIQUE
);


-- Table: keyword
DROP TABLE IF EXISTS keyword;

CREATE TABLE IF NOT EXISTS keyword (
    value       TEXT    NOT NULL,
    category_id INTEGER NOT NULL
                        REFERENCES category (id),
    PRIMARY KEY (
        value,
        category_id
    )
);


-- Table: trail
DROP TABLE IF EXISTS trail;

CREATE TABLE IF NOT EXISTS trail (
    id              INTEGER,
    name            TEXT,
    description     TEXT,
    difficulty      TEXT,
    completion_time TEXT,
    type            TEXT,
    thumb_URL       TEXT,
    web_URL         TEXT,
    date_loaded_raw TEXT,
    x               REAL,
    y               REAL,
    user_weight     REAL,
    PRIMARY KEY (
        id
    )
);


-- Table: user
DROP TABLE IF EXISTS user;

CREATE TABLE IF NOT EXISTS user (
    id                  INTEGER PRIMARY KEY,
    type                TEXT,
    name                TEXT,
    regions             TEXT,
    isFamilyFriendly    BOOL,
    isAccessible        BOOL,
    experienceLevel     INTEGER,
    gradientPreference  INTEGER,
    bushPreference      INTEGER,
    reservePreference   INTEGER,
    lakeRiverPreference INTEGER,
    coastPreference     INTEGER,
    mountainPreference  INTEGER,
    wildlifePreference  INTEGER,
    historicPreference  INTEGER,
    waterfallPreference INTEGER
);


COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
