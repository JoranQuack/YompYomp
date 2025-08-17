-- version 1 of the schema
CREATE TABLE IF NOT EXISTS trails (
    id INTEGER PRIMARY KEY,
    name TEXT,
    description TEXT,
    difficulty TEXT,
    completion_time TEXT,
    --leaving out hasAlerts (doesnt seem needed at this point)
    type TEXT,
    thumb_URL TEXT,
    web_URL TEXT,
    date_loaded_raw TEXT,
    --shapeLength REAL, (dont need cause of kml??)
    x REAL,
    y REAL
);
--SPLIT
/* add other tables below (hut, etc...)