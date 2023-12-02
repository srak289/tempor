CREATE TABLE IF NOT EXISTS task (
    id              INTEGER
        PRIMARY KEY,
    name            VARCHAR(64)
        NOT NULL UNIQUE,
    -- Effectively "in_progress" must be either NULL or True
    in_progress     BOOLEAN
        DEFAULT NULL UNIQUE CHECK (in_progress),
    time_created    TIMESTAMP,
    time_started    TIMESTAMP,
    time_worked     INTEGER
        DEFAULT 0 NOT NULL, -- in seconds
    time_stopped    TIMESTAMP,
    due_by          TIMESTAMP,
    allowed_time    INTEGER
);

CREATE TABLE IF NOT EXISTS tag (
    id      INTEGER
        PRIMARY KEY,
    name    VARCHAR(16)
        NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS task_tags (
    task_id INTEGER,
    tag_id INTEGER,
    -- we cascade here because the join table row should
    -- drop if either parent record does
    FOREIGN KEY(task_id) REFERENCES task(id)
        ON DELETE CASCADE,
    FOREIGN KEY(tag_id) REFERENCES tag(id)
        ON DELETE CASCADE
);

CREATE TRIGGER IF NOT EXISTS trg_task_set_time_created
    AFTER INSERT ON task
    BEGIN
        UPDATE task
        SET time_created = CURRENT_TIMESTAMP
        WHERE id = new.id;
    END
;

CREATE TRIGGER IF NOT EXISTS trg_task_set_time_stopped
    BEFORE UPDATE OF in_progress ON task
    WHEN new.in_progress IS NULL
    BEGIN
        UPDATE task
        SET time_stopped = CURRENT_TIMESTAMP
        WHERE id = old.id;
    END
;

CREATE TRIGGER IF NOT EXISTS trg_task_set_time_started
    AFTER UPDATE OF in_progress ON task
    WHEN new.in_progress = True
    BEGIN
        UPDATE task
        SET time_started = CURRENT_TIMESTAMP,
            time_stopped = NULL
        WHERE id = old.id;
    END
;

CREATE TRIGGER IF NOT EXISTS trg_task_calculate_time_worked
    AFTER UPDATE OF in_progress ON task
    -- new.in_progress will be NULL when we are stopping a task
    WHEN new.in_progress IS NULL
    BEGIN
        UPDATE task
        SET time_worked = (
            SELECT time_worked + unixepoch(time_stopped) - unixepoch(time_started)
            FROM task
            where id = old.id
        )
        WHERE id = old.id;
    END
;

CREATE VIEW IF NOT EXISTS task_join_tags AS
    SELECT ta.name AS task_name, GROUP_CONCAT(tg.name) AS task_tags
    FROM task AS ta
    JOIN task_tags ON task_id = ta.id
    JOIN tag AS tg ON tag_id = tg.id
    GROUP BY ta.name
;

CREATE VIEW IF NOT EXISTS time_per_tag AS
    -- TODO
    SELECT time_worked AS SUM(ta.time)
    FROM task AS ta
    JOIN task_tags ON task_id = ta.id
    JOIN tag AS tg ON tag_id = tg.id
    GROUP BY tg.id;
;
