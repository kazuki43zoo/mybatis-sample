DROP TABLE IF EXISTS meeting_room;
DROP TABLE IF EXISTS room;

CREATE TABLE meeting_room (
  room_id   VARCHAR(36) NOT NULL,
  room_name VARCHAR(255) NOT NULL,
  capacity INTEGER NOT NULL,
  PRIMARY KEY (room_id)
);

CREATE TABLE room (
  room_id   IDENTITY,
  room_name VARCHAR(255) NOT NULL,
  capacity INTEGER NOT NULL,
);

