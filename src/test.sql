BEGIN;

INSERT INTO task (id, name) VALUES
    (0, 'Take out trash'),
    (1, 'Do homework'),
    (2, 'Sleep'),
    (4, 'Eat breakfast'),
    (5, 'Video games'),
    (6, 'Make coffee'),
    (3, 'Eat dinner');

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

UPDATE task SET time_stopped = '2023-12-04 08:00:00' WHERE id = 5;

COMMIT;
