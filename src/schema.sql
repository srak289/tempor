CREATE TABLE task (
    id              INTEGER
        PRIMARY KEY,
    name            VARCHAR(64)
        NOT NULL UNIQUE,
    in_progress     BOOLEAN
        DEFAULT True UNIQUE CHECK (in_progress),
    time_created    TIMESTAMP
        NOT NULL,
    time_started    TIMESTAMP,
    time_worked     TIMESTAMP,
    time_stopped    TIMESTAMP,
    due_by          TIMESTAMP
);

CREATE TABLE tag (
    id      INTEGER
        PRIMARY KEY,
    name    VARCHAR(16)
        NOT NULL UNIQUE
);

CREATE TABLE task_tags (
    task_id INTEGER,
    tag_id INTEGER,
    FOREIGN KEY(task_id) REFERENCES task(id) ON DELETE CASCADE,
    FOREIGN KEY(tag_id) REFERENCES tag(id) ON DELETE CASCADE
);

CREATE VIEW task_join_tags AS
    SELECT ta.name AS task_name, GROUP_CONCAT(tg.name) AS task_tags
        FROM task AS ta
        JOIN task_tags ON task_id=ta.id
        JOIN tag AS tg ON tag_id=tg.id
        GROUP BY ta.name;
