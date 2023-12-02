CREATE TABLE IF NOT EXISTS task (
    id              INTEGER
        PRIMARY KEY,
    name            VARCHAR(64)
        UNIQUE NOT NULL,
    -- "in_progress" MUST be NULL or True
    -- because only one task may be "in_progress"
    -- at a single time
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
        UNIQUE NOT NULL
);

-- JOIN table for tasks and tags
CREATE TABLE IF NOT EXISTS task_tags (
    task_id INTEGER
        NOT NULL,
    tag_id INTEGER
        NOT NULL,
    -- we cascade here because the join table row should
    -- drop if either parent record does
    FOREIGN KEY(task_id) REFERENCES task(id)
        ON DELETE CASCADE,
    FOREIGN KEY(tag_id) REFERENCES tag(id)
        ON DELETE CASCADE
);

CREATE TRIGGER IF NOT EXISTS trg_task_set_time_created
    -- set the creation time AFTER INSERT
    AFTER INSERT ON task
    BEGIN
        UPDATE task
        SET time_created = CURRENT_TIMESTAMP
        WHERE id = new.id;
    END
;

CREATE TRIGGER IF NOT EXISTS trg_task_set_time_started
    AFTER UPDATE OF in_progress ON task
    WHEN new.in_progress = True
    BEGIN
        UPDATE task
        SET time_started = CURRENT_TIMESTAMP,
            time_stopped = NULL
        WHERE id = new.id;
    END
;

CREATE TRIGGER IF NOT EXISTS trg_task_set_time_stopped
    -- we need to set the stop time before the 
    -- calculate trigger fires so this must 
    -- be BEFORE UPDATE
    BEFORE UPDATE OF in_progress ON task
    WHEN new.in_progress IS NULL
    BEGIN
        UPDATE task
        SET time_stopped = CURRENT_TIMESTAMP
        WHERE id = new.id;
    END
;

CREATE TRIGGER trg_task_calculate_time_worked
    -- when time_stopped has been set we
    -- should recalculate the time_worked column
    AFTER UPDATE OF time_stopped ON task
    WHEN new.time_stopped IS NOT NULL
    BEGIN
        UPDATE task
        SET time_worked = (
            -- we use unixepoch() to get the seconds
            -- back from the TIMESTAMP
            SELECT time_worked + unixepoch(time_stopped) - unixepoch(time_started)
            FROM task
            WHERE id = new.id
        )
        WHERE id = new.id;
    END
;

-- convenient view functions for JOINs
-- that we do not want to program
CREATE VIEW IF NOT EXISTS vw_tags_by_task AS
    SELECT ta.name AS task_name, GROUP_CONCAT(tg.name) AS task_tags
    FROM task AS ta
    JOIN task_tags ON task_id = ta.id
    JOIN tag AS tg ON tag_id = tg.id
    GROUP BY ta.name
;

CREATE VIEW IF NOT EXISTS vw_tasks_by_tag AS
    SELECT tg.name AS tag_name, GROUP_CONCAT(ta.name) AS tag_tasks
    FROM task AS ta
    JOIN task_tags ON task_id = ta.id
    JOIN tag AS tg ON tag_id = tg.id
    GROUP BY tg.name
;

CREATE VIEW IF NOT EXISTS vw_time_per_tag AS
    -- divide by 3600 to get hours back
    SELECT tg.name AS tag_name, ROUND(TOTAL(ta.time_worked)/3600, 2) AS hours_worked
    FROM task AS ta
    JOIN task_tags ON task_id = ta.id
    JOIN tag AS tg ON tag_id = tg.id
    GROUP BY tg.id;
;
