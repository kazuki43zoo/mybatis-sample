package com.example;

import com.example.domain.mapper.MeetingRoomMapper;
import com.example.domain.model.MeetingRoom;
import com.example.domain.model.MeetingRoomCriteria;
import com.example.domain.model.OrderBy;
import com.example.domain.model.Room;
import org.apache.ibatis.session.RowBounds;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MybatisSampleApplication.class)
@Transactional
public class MybatisSampleApplicationTests {

    @Autowired
    MeetingRoomMapper meetingRoomMapper;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    public void findOne() {
        MeetingRoom meetingRoom = meetingRoomMapper.findOne("R001");
        Assert.assertThat(meetingRoom.getRoomId(), Is.is("R001"));
        Assert.assertThat(meetingRoom.getRoomName(), Is.is("会議室１"));
        Assert.assertThat(meetingRoom.getCapacity(), Is.is(10));
    }

    @Test
    public void count() {
        long count = meetingRoomMapper.count();
        Assert.assertThat(count, Is.is(3L));
    }

    @Test
    public void findAll() {
        List<MeetingRoom> meetingRooms = meetingRoomMapper.findAll();
        Assert.assertThat(meetingRooms.size(), Is.is(3));
        Assert.assertThat(meetingRooms.get(0).getRoomId(), Is.is("R001"));
        Assert.assertThat(meetingRooms.get(0).getRoomName(), Is.is("会議室１"));
        Assert.assertThat(meetingRooms.get(0).getCapacity(), Is.is(10));
        Assert.assertThat(meetingRooms.get(1).getRoomId(), Is.is("R002"));
        Assert.assertThat(meetingRooms.get(1).getRoomName(), Is.is("会議室２"));
        Assert.assertThat(meetingRooms.get(1).getCapacity(), Is.is(50));
        Assert.assertThat(meetingRooms.get(2).getRoomId(), Is.is("R003"));
        Assert.assertThat(meetingRooms.get(2).getRoomName(), Is.is("会議室３"));
        Assert.assertThat(meetingRooms.get(2).getCapacity(), Is.is(100));
    }

    @Test
    public void create() {
        {
            MeetingRoom meetingRoom = new MeetingRoom();
            meetingRoom.setRoomId("R999");
            meetingRoom.setRoomName("会議室９９９");
            meetingRoom.setCapacity(1000);
            meetingRoomMapper.create(meetingRoom);
        }
        {
            MeetingRoom meetingRoom = meetingRoomMapper.findOne("R999");
            Assert.assertThat(meetingRoom.getRoomId(), Is.is("R999"));
            Assert.assertThat(meetingRoom.getRoomName(), Is.is("会議室９９９"));
            Assert.assertThat(meetingRoom.getCapacity(), Is.is(1000));
        }
    }


    @Test
    public void createWithSelectKey() {
        String createdMeetingRoomId;
        {
            MeetingRoom meetingRoom = new MeetingRoom();
            meetingRoom.setRoomName("会議室９９９");
            meetingRoom.setCapacity(1000);
            meetingRoomMapper.createWithSelectKey(meetingRoom);
            createdMeetingRoomId = meetingRoom.getRoomId();
        }
        {
            MeetingRoom meetingRoom = meetingRoomMapper.findOne(createdMeetingRoomId);
            Assert.assertThat(meetingRoom.getRoomId(), Is.is(createdMeetingRoomId));
            Assert.assertThat(meetingRoom.getRoomName(), Is.is("会議室９９９"));
            Assert.assertThat(meetingRoom.getCapacity(), Is.is(1000));
        }
    }

    @Test
    public void createWithIdColumn() {
        int createdMeetingRoomId;
        {
            Room room = new Room();
            room.setRoomName("会議室９９９");
            room.setCapacity(1000);
            meetingRoomMapper.createWithIdColumn(room);
            createdMeetingRoomId = room.getRoomId();
        }
        {
            Room room = jdbcTemplate.queryForObject("SELECT * FROM room WHERE room_id = ?", new BeanPropertyRowMapper<>(Room.class), createdMeetingRoomId);

            Assert.assertThat(room.getRoomId(), Is.is(createdMeetingRoomId));
            Assert.assertThat(room.getRoomName(), Is.is("会議室９９９"));
            Assert.assertThat(room.getCapacity(), Is.is(1000));
        }
    }

    @Test
    public void createWithParams() {
        {
            meetingRoomMapper.createWithParams("R999", "会議室９９９", 1000);
        }
        {
            MeetingRoom meetingRoom = meetingRoomMapper.findOne("R999");
            Assert.assertThat(meetingRoom.getRoomId(), Is.is("R999"));
            Assert.assertThat(meetingRoom.getRoomName(), Is.is("会議室９９９"));
            Assert.assertThat(meetingRoom.getCapacity(), Is.is(1000));
        }
    }

    @Test
    public void update() {
        {
            MeetingRoom meetingRoom = new MeetingRoom();
            meetingRoom.setRoomId("R999");
            meetingRoom.setRoomName("会議室９９９");
            meetingRoom.setCapacity(1000);
            meetingRoomMapper.create(meetingRoom);

            meetingRoom.setRoomName("会議室９９９９");
            meetingRoom.setCapacity(2000);
            meetingRoomMapper.update(meetingRoom);
        }
        {
            MeetingRoom meetingRoom = meetingRoomMapper.findOne("R999");
            Assert.assertThat(meetingRoom.getRoomId(), Is.is("R999"));
            Assert.assertThat(meetingRoom.getRoomName(), Is.is("会議室９９９９"));
            Assert.assertThat(meetingRoom.getCapacity(), Is.is(2000));
        }
    }

    @Test
    public void updateWithConditional() {
        {
            MeetingRoom meetingRoom = new MeetingRoom();
            meetingRoom.setRoomId("R999");
            meetingRoom.setRoomName("会議室９９９");
            meetingRoom.setCapacity(1000);
            meetingRoomMapper.create(meetingRoom);

            meetingRoom.setRoomName("会議室９９９９");
            meetingRoom.setCapacity(0);
            meetingRoomMapper.updateWithConditional(meetingRoom);
        }
        {
            MeetingRoom meetingRoom = meetingRoomMapper.findOne("R999");
            Assert.assertThat(meetingRoom.getRoomId(), Is.is("R999"));
            Assert.assertThat(meetingRoom.getRoomName(), Is.is("会議室９９９９"));
            Assert.assertThat(meetingRoom.getCapacity(), Is.is(1000));
        }
    }


    @Test
    public void delete() {
        MeetingRoom meetingRoom = new MeetingRoom();
        meetingRoom.setRoomId("R999");
        meetingRoom.setRoomName("会議室９９９");
        meetingRoom.setCapacity(1000);
        meetingRoomMapper.create(meetingRoom);

        Assert.assertNotNull(meetingRoomMapper.findOne("R999"));

        meetingRoomMapper.delete("R999");

        Assert.assertNull(meetingRoomMapper.findOne("R999"));
    }


    @Test
    public void findAllOrderByColumn() {
        List<MeetingRoom> meetingRooms = meetingRoomMapper.findAllOrderByColumn("room_id DESC");
        Assert.assertThat(meetingRooms.size(), Is.is(3));
        Assert.assertThat(meetingRooms.get(0).getRoomId(), Is.is("R003"));
        Assert.assertThat(meetingRooms.get(0).getRoomName(), Is.is("会議室３"));
        Assert.assertThat(meetingRooms.get(0).getCapacity(), Is.is(100));
        Assert.assertThat(meetingRooms.get(1).getRoomId(), Is.is("R002"));
        Assert.assertThat(meetingRooms.get(1).getRoomName(), Is.is("会議室２"));
        Assert.assertThat(meetingRooms.get(1).getCapacity(), Is.is(50));
        Assert.assertThat(meetingRooms.get(2).getRoomId(), Is.is("R001"));
        Assert.assertThat(meetingRooms.get(2).getRoomName(), Is.is("会議室１"));
        Assert.assertThat(meetingRooms.get(2).getCapacity(), Is.is(10));
    }

    @Test
    public void findAllOrderBy() {
        List<MeetingRoom> meetingRooms = meetingRoomMapper.findAllOrderBy(new OrderBy("room_id", OrderBy.Direction.DESC));
        Assert.assertThat(meetingRooms.size(), Is.is(3));
        Assert.assertThat(meetingRooms.get(0).getRoomId(), Is.is("R003"));
        Assert.assertThat(meetingRooms.get(0).getRoomName(), Is.is("会議室３"));
        Assert.assertThat(meetingRooms.get(0).getCapacity(), Is.is(100));
        Assert.assertThat(meetingRooms.get(1).getRoomId(), Is.is("R002"));
        Assert.assertThat(meetingRooms.get(1).getRoomName(), Is.is("会議室２"));
        Assert.assertThat(meetingRooms.get(1).getCapacity(), Is.is(50));
        Assert.assertThat(meetingRooms.get(2).getRoomId(), Is.is("R001"));
        Assert.assertThat(meetingRooms.get(2).getRoomName(), Is.is("会議室１"));
        Assert.assertThat(meetingRooms.get(2).getCapacity(), Is.is(10));
    }


    @Test
    public void findAllByCriteria() {
        MeetingRoomCriteria criteria = new MeetingRoomCriteria();
        criteria.setCapacity(50);
        List<MeetingRoom> meetingRooms = meetingRoomMapper.findAllByCriteria(criteria, "room_id DESC");
        Assert.assertThat(meetingRooms.size(), Is.is(2));
        Assert.assertThat(meetingRooms.get(0).getRoomId(), Is.is("R003"));
        Assert.assertThat(meetingRooms.get(0).getRoomName(), Is.is("会議室３"));
        Assert.assertThat(meetingRooms.get(0).getCapacity(), Is.is(100));
        Assert.assertThat(meetingRooms.get(1).getRoomId(), Is.is("R002"));
        Assert.assertThat(meetingRooms.get(1).getRoomName(), Is.is("会議室２"));
        Assert.assertThat(meetingRooms.get(1).getCapacity(), Is.is(50));
    }

    @Test
    public void findAllByCriteriaIsEmpty() {
        List<MeetingRoom> meetingRooms = meetingRoomMapper.findAllByCriteria(null, "room_id DESC");
        Assert.assertThat(meetingRooms.size(), Is.is(3));
        Assert.assertThat(meetingRooms.get(0).getRoomId(), Is.is("R003"));
        Assert.assertThat(meetingRooms.get(0).getRoomName(), Is.is("会議室３"));
        Assert.assertThat(meetingRooms.get(0).getCapacity(), Is.is(100));
        Assert.assertThat(meetingRooms.get(1).getRoomId(), Is.is("R002"));
        Assert.assertThat(meetingRooms.get(1).getRoomName(), Is.is("会議室２"));
        Assert.assertThat(meetingRooms.get(1).getCapacity(), Is.is(50));
        Assert.assertThat(meetingRooms.get(2).getRoomId(), Is.is("R001"));
        Assert.assertThat(meetingRooms.get(2).getRoomName(), Is.is("会議室１"));
        Assert.assertThat(meetingRooms.get(2).getCapacity(), Is.is(10));
    }

    @Test
    public void findAllByCriteriaIsAll() {
        MeetingRoomCriteria criteria = new MeetingRoomCriteria();
        criteria.setRoomId("R00");
        criteria.setRoomName("会議室");
        criteria.setCapacity(0);
        List<MeetingRoom> meetingRooms = meetingRoomMapper.findAllByCriteria(criteria, "room_id DESC");
        Assert.assertThat(meetingRooms.size(), Is.is(3));
        Assert.assertThat(meetingRooms.get(0).getRoomId(), Is.is("R003"));
        Assert.assertThat(meetingRooms.get(0).getRoomName(), Is.is("会議室３"));
        Assert.assertThat(meetingRooms.get(0).getCapacity(), Is.is(100));
        Assert.assertThat(meetingRooms.get(1).getRoomId(), Is.is("R002"));
        Assert.assertThat(meetingRooms.get(1).getRoomName(), Is.is("会議室２"));
        Assert.assertThat(meetingRooms.get(1).getCapacity(), Is.is(50));
        Assert.assertThat(meetingRooms.get(2).getRoomId(), Is.is("R001"));
        Assert.assertThat(meetingRooms.get(2).getRoomName(), Is.is("会議室１"));
        Assert.assertThat(meetingRooms.get(2).getCapacity(), Is.is(10));
    }

    @Test
    public void findByCapacityClassWithSmall() {
        List<MeetingRoom> meetingRooms = meetingRoomMapper.findByCapacityClass("small");
        Assert.assertThat(meetingRooms.size(), Is.is(1));
        Assert.assertThat(meetingRooms.get(0).getRoomId(), Is.is("R001"));
        Assert.assertThat(meetingRooms.get(0).getRoomName(), Is.is("会議室１"));
        Assert.assertThat(meetingRooms.get(0).getCapacity(), Is.is(10));

    }

    @Test
    public void findByCapacityClassWithMiddle() {
        List<MeetingRoom> meetingRooms = meetingRoomMapper.findByCapacityClass("middle");
        Assert.assertThat(meetingRooms.size(), Is.is(1));
        Assert.assertThat(meetingRooms.get(0).getRoomId(), Is.is("R002"));
        Assert.assertThat(meetingRooms.get(0).getRoomName(), Is.is("会議室２"));
        Assert.assertThat(meetingRooms.get(0).getCapacity(), Is.is(50));

    }

    @Test
    public void findByCapacityClassWithLarge() {
        List<MeetingRoom> meetingRooms = meetingRoomMapper.findByCapacityClass("large");
        Assert.assertThat(meetingRooms.size(), Is.is(1));
        Assert.assertThat(meetingRooms.get(0).getRoomId(), Is.is("R003"));
        Assert.assertThat(meetingRooms.get(0).getRoomName(), Is.is("会議室３"));
        Assert.assertThat(meetingRooms.get(0).getCapacity(), Is.is(100));

    }

    @Test
    public void findByRoomIdsIsNull() {
        List<MeetingRoom> meetingRooms = meetingRoomMapper.findByRoomIds(null);
        Assert.assertThat(meetingRooms.size(), Is.is(3));

    }

    @Test
    public void findByRoomIdsIsEmpty() {
        List<MeetingRoom> meetingRooms = meetingRoomMapper.findByRoomIds(Collections.emptyList());
        Assert.assertThat(meetingRooms.size(), Is.is(3));

    }

    @Test
    public void findByRoomIdsIsOne() {
        List<MeetingRoom> meetingRooms = meetingRoomMapper.findByRoomIds(Collections.singletonList("R001"));
        Assert.assertThat(meetingRooms.size(), Is.is(1));

    }

    @Test
    public void findByRoomIdsIsAll() {
        List<MeetingRoom> meetingRooms = meetingRoomMapper.findByRoomIds(Arrays.asList("R001", "R002", "R003"));
        Assert.assertThat(meetingRooms.size(), Is.is(3));

    }

    @Test
    public void collectAll() {
        meetingRoomMapper.collectAll(context -> {
            int position = context.getResultCount();
            MeetingRoom meetingRoom = context.getResultObject();
            // ...
        });
    }

    @Test
    public void findAllWithRawBounds() {
        List<MeetingRoom> meetingRooms = meetingRoomMapper.findAll(new RowBounds(0,1));
        System.out.println(meetingRooms.get(0).getRoomName());
    }

}
