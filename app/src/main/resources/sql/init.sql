-- version 1 of the schema
CREATE TABLE IF NOT EXISTS trails (
    id INTEGER PRIMARY KEY,
    name TEXT,
    description TEXT,
    difficulty TEXT,
    completionTime TEXT,
    --leaving out hasAlerts (doesnt seem needed at this point)
    thumbnailURL TEXT,
    webpageURL TEXT,
    dateLoadedRaw TEXT,
    shapeLength REAL,
    x REAL,
    y REAL,
    updatedAt TEXT DEFAULT CURRENT_TIMESTAMP
);
--SPLIT
/* add other tables below (hut, etc...)