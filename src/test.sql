BEGIN;

INSERT INTO task (id, name, time_started, time_stopped, time_worked) VALUES
    (
        0,
        'Take out trash',
        (SELECT DATETIME(CURRENT_TIMESTAMP, '-47 hours')),
        (SELECT DATETIME(CURRENT_TIMESTAMP, '-46 hours')),
        3600
    ),
    (
        1,
        'Do homework',
        (SELECT DATETIME(CURRENT_TIMESTAMP, '-45 hours')),
        (SELECT DATETIME(CURRENT_TIMESTAMP, '-44 hours')),
        3600
    ),
    (
        2,
        'Sleep',
        (SELECT DATETIME(CURRENT_TIMESTAMP, '-43 hours')),
        (SELECT DATETIME(CURRENT_TIMESTAMP, '-42 hours')),
        3600
    ),
    (
        4,
        'Eat breakfast',
        (SELECT DATETIME(CURRENT_TIMESTAMP, '-41 hours')),
        (SELECT DATETIME(CURRENT_TIMESTAMP, '-40 hours')),
        3600
    ),
    (
        5,
        'Video games',
        (SELECT DATETIME(CURRENT_TIMESTAMP, '-39 hours')),
        (SELECT DATETIME(CURRENT_TIMESTAMP, '-30 hours')),
        32400
    ),
    (
        6,
        'Make coffee',
        (SELECT DATETIME(CURRENT_TIMESTAMP, '-29 hours')),
        (SELECT DATETIME(CURRENT_TIMESTAMP, '-28 hours')),
        3600
    ),
    (
        3,
        'Eat dinner',
        (SELECT DATETIME(CURRENT_TIMESTAMP, '-27 hours')),
        (SELECT DATETIME(CURRENT_TIMESTAMP, '-24 hours')),
        7200
    );

INSERT INTO tag (id, name) VALUES
    (0, 'High Importance'),
    (1, 'Do later'),
    (3, 'School'),
    (4, 'Home'),
    (2, 'Probably never');

INSERT INTO task_tags (task_id, tag_id) VALUES
    (0, 1),
    (0, 4),
    (1, 1),
    (1, 3),
    (2, 4),
    (2, 2),
    (4, 2),
    (5, 0),
    (3, 1);


UPDATE task SET in_progress = True WHERE id = 5;

UPDATE task SET in_progress = NULL WHERE id = 5;

COMMIT;
