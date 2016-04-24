package com.example.domain.mapper

import com.example.domain.model.MeetingRoom
import com.example.domain.model.MeetingRoomCriteria
import com.example.domain.model.Room
import org.apache.ibatis.annotations.*
import org.apache.ibatis.jdbc.SQL

interface MeetingRoomAnnotationMapper {

    /**
     * Find a meeting room by id.
     * @param roomId room id
     * @return
     */
    @Select('''
        SELECT
            room_id,
            room_name,
            capacity
        FROM
            meeting_room
        WHERE
            room_id = #{roomId}
    ''')
    @Results([
            @Result(column = "room_id", property = "roomId", id = true),
            @Result(column = "room_name", property = "roomName")
    ])
    MeetingRoom findOne(String roomId)

    @Select('''
        SELECT
            COUNT(*)
        FROM
            meeting_room
     ''')
    long count()

    @Select('''
        SELECT
            room_id,
            room_name,
            capacity
        FROM
            meeting_room
         ORDER BY
            room_id
    ''')
    List<MeetingRoom> findAll()

    @Insert('''
        INSERT INTO meeting_room (
            room_id,
            room_name,
            capacity
        )
        VALUES (
            #{roomId},
            #{roomName},
            #{capacity}
        )
    ''')
    void create(MeetingRoom meetingRoom)

    @Insert('''
        INSERT INTO meeting_room (
            room_id,
            room_name,
            capacity
        )
        VALUES (
            #{roomId},
            #{roomName},
            #{capacity}
        )
    ''')
    @SelectKey(statement = "SELECT RANDOM_UUID()", keyProperty = "roomId", before = true, resultType = String.class)
    void createWithSelectKey(MeetingRoom meetingRoom)

    @Insert('''
        INSERT INTO room (
            room_name,
            capacity
        )
        VALUES (
            #{roomName},
            #{capacity}
        )
    ''')
    @Options(useGeneratedKeys = true, keyProperty = "roomId")
    void createWithIdColumn(Room room)

    @Update('''
        UPDATE
            meeting_room
        SET
            room_name = #{roomName},
            capacity = #{capacity}
        WHERE
            room_id = #{roomId}
    ''')
    boolean update(MeetingRoom meetingRoom)

    @Delete('''
        DELETE FROM
            meeting_room
        WHERE
            room_id = #{roomId}
    ''')
    boolean delete(String roomId)


    @SelectProvider(type = MeetingRoomSqlProvider.class, method = "findAllByCriteria")
    List<MeetingRoom> findAllByCriteria(MeetingRoomCriteria criteria)

    @SelectProvider(type = MeetingRoomSqlProvider.class, method = "findAllByMapCriteria")
    List<MeetingRoom> findAllByMapCriteria(Map<String, Object> criteria)

    @SelectProvider(type = MeetingRoomSqlProvider.class, method = "findAllByCriteriaOrderBy")
    List<MeetingRoom> findAllByCriteriaOrderBy(
            @Param("criteria") MeetingRoomCriteria criteria,
            @Param("orderByColumn") String orderByColumn)


    static class MeetingRoomSqlProvider {

        String findAllByCriteria(MeetingRoomCriteria criteria) {
            return new SQL() {
                {
                    SELECT("room_id AS roomId, room_name AS roomName, capacity");
                    FROM("meeting_room");
                    if (criteria != null) {
                        if (criteria.getRoomId() != null) {
                            WHERE("room_id like '%' || #{roomId} || '%'");
                        }
                        if (criteria.getRoomName() != null) {
                            WHERE("room_name like '%' || #{roomName} || '%'");
                        }
                        if (criteria.getCapacity() != null) {
                            WHERE("capacity >= #{capacity}");
                        }
                    }
                    ORDER_BY("room_id DESC");
                }
            }.toString();
        }

        String findAllByMapCriteria(Map<String, Object> criteria) {
            return new SQL() {
                {
                    SELECT("room_id AS roomId, room_name AS roomName, capacity");
                    FROM("meeting_room");
                    if (criteria != null) {
                        if (criteria.containsKey("roomId")) {
                            WHERE("room_id like '%' || #{roomId} || '%'");
                        }
                        if (criteria.containsKey("roomName")) {
                            WHERE("room_name like '%' || #{roomName} || '%'");
                        }
                        if (criteria.containsKey("capacity")) {
                            WHERE("capacity >= #{capacity}");
                        }
                    }
                    ORDER_BY("room_id DESC");
                }
            }.toString();
        }

        String findAllByCriteriaOrderBy(MeetingRoomCriteria criteria, String orderByColumn) {
            return new SQL() {
                {
                    SELECT("room_id AS roomId, room_name AS roomName, capacity");
                    FROM("meeting_room");
                    if (criteria != null) {
                        if (criteria.getRoomId() != null) {
                            WHERE("room_id like '%' || #{criteria.roomId} || '%'");
                        }
                        if (criteria.getRoomName() != null) {
                            WHERE("room_name like '%' || #{criteria.roomName} || '%'");
                        }
                        if (criteria.getCapacity() != null) {
                            WHERE("capacity >= #{criteria.capacity}");
                        }
                    }
                    ORDER_BY(orderByColumn);
                }
            }.toString();
        }
    }
}