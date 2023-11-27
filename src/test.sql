BEGIN;

INSERT INTO task (id, name, in_progress, time_created) VALUES
    (0, 'Take out trash', True, CURRENT_TIMESTAMP),
    (1, 'Do homework', NULL, CURRENT_TIMESTAMP),
    (2, 'Sleep', NULL, CURRENT_TIMESTAMP),
    (4, 'Eat breakfast', NULL, CURRENT_TIMESTAMP),
    (5, 'Video games', NULL, CURRENT_TIMESTAMP),
    (6, 'Make coffee', NULL, CURRENT_TIMESTAMP),
    (3, 'Eat dinner', NULL, CURRENT_TIMESTAMP);

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

COMMIT;
