CREATE TABLE IF NOT EXISTS task (
    id              INTEGER
        PRIMARY KEY,
    name            VARCHAR(64)
        NOT NULL UNIQUE,
    -- Effectively "in_progress" must be either NULL or True
    in_progress     BOOLEAN
        DEFAULT NULL UNIQUE CHECK (in_progress),
    time_created    TIMESTAMP
        NOT NULL,
    time_started    TIMESTAMP,
    time_worked     TIMESTAMP,
    time_stopped    TIMESTAMP,
    due_by          TIMESTAMP
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
    -- should this be a cascade?
    -- we just want this row deleted if either key is deleted
    -- look up what CASCADE does
    FOREIGN KEY(task_id) REFERENCES task(id)
        ON DELETE CASCADE,
    FOREIGN KEY(tag_id) REFERENCES tag(id)
        ON DELETE CASCADE
);

--CREATE TRIGGER IF NOT EXISTS trg_task_set_time_created
--    AFTER INSERT ON task
--    BEGIN
--        UPDATE task
--        SET time_created = CURRENT_TIMESTAMP
--        WHERE id = new.id;
--    END
--;

--CREATE TRIGGER IF NOT EXISTS trg_task_calculate_time_worked
--    AFTER UPDATE OF in_progress ON task
--    BEGIN
--        -- test this trigger
--        CASE WHEN (
--            old.in_progress = True
--            AND new.in_progress = NULL
--        ) THEN
--            UPDATE task
--            --TODO
--            SET time_stopped = CURRENT_TIMESTAMP,
--            time_worked = time_started - time_stopped
--            WHERE id = old.id;
--        ELSE
--            -- The only other state is to set from NULL to True
--            UPDATE task
--            SET time_started = CURRENT_TIMESTAMP
--            WHERE id = old.id;
--        END
--    END
--;

CREATE VIEW IF NOT EXISTS task_join_tags AS
    SELECT ta.name AS task_name, GROUP_CONCAT(tg.name) AS task_tags
        FROM task AS ta
        JOIN task_tags ON task_id=ta.id
        JOIN tag AS tg ON tag_id=tg.id
        GROUP BY ta.name
;

--CREATE VIEW IF NOT EXISTS time_spent AS
--    -- TODO
--    SELECT * from task
--;
