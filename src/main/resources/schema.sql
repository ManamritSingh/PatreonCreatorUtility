DROP TABLE IF EXISTS conversation;

CREATE TABLE conversation (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    session_id TEXT NOT NULL,
    role TEXT NOT NULL,
    message TEXT NOT NULL,
    timestamp INTEGER NOT NULL
);