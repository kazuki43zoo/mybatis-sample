package com.example.domain.mapper;


import com.example.domain.model.MeetingRoom;
import com.example.domain.model.MeetingRoomCriteria;
import com.example.domain.model.OrderBy;
import com.example.domain.model.Room;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MeetingRoomMapper {
    MeetingRoom findOne(String roomId); // (1) (2)

    long count(); // (3)

    List<MeetingRoom> findAll(); // (4)

    List<MeetingRoom> findAllOrderByColumn(String orderByColumn); // (4)

    List<MeetingRoom> findAllOrderBy(OrderBy orderBy); // (4)

    List<MeetingRoom> findAllByCriteria(
            @Param("criteria") MeetingRoomCriteria criteria,
            @Param("orderByColumn") String orderByColumn);

    List<MeetingRoom> findByCapacityClass(@Param("capacityClass") String capacityClass);

    List<MeetingRoom> findByRoomIds(List<String> roomIds);


    void create(MeetingRoom meetingRoom); // (1) (2)

    void createWithSelectKey(MeetingRoom meetingRoom); // (1) (2)

    void createWithIdColumn(Room room); // (1) (2)

    void createWithParams(
            @Param("roomId") String roomId,
            @Param("roomName") String roomName,
            @Param("capacity") int capacity);

    boolean update(MeetingRoom meetingRoom); // (1) (2)

    boolean updateWithConditional(MeetingRoom meetingRoom);


    boolean delete(String roomId); // (1)



}
