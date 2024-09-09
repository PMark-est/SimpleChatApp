insert into users(name, password, is_online, last_online) values
('admin', '$2a$10$ycBnBRwpd2VHtHXE7s0wAelKm402uW.AmkzX5eQwG0BrwEFZti2J2', false, '10-04-2023'),
('marko', 'salasona', false, '10-04-2022'),
('juhan', 'salasona', false, '10-04-2023'),
('best', '$2a$10$jyjgaT3soSb4cXmJWZ.3ze.CZe9p7GAwNQ2gZIoXlXe7Z9k28ARfa', false, '10-04-2023'); -- salasona best
insert into groups(name, owner_id, direct_message) values ('global chat', 1, false), ('my chat', 4, false);
insert into user_groups(user_id, group_id) values
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(4, 2);
insert into message(user_id, group_id, message, time) values
(1, 1, 'mingi sõnum', '04-10-2022'),
(2, 1, 'mingi teine sonum', '04-10-2022'),
(2, 2, 'wau vaata kui lahe', '04-10-2022');
