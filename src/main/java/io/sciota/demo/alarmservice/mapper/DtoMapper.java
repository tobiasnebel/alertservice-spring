package io.sciota.demo.alarmservice.mapper;

import java.time.Instant;
import java.util.Date;

import io.sciota.demo.alarmservice.dtos.AlarmDto;
import io.sciota.demo.alarmservice.dtos.EventDto;
import io.sciota.demo.alarmservice.dtos.RoomDto;
import io.sciota.demo.alarmservice.dtos.ScheduleDto;
import io.sciota.demo.alarmservice.persistence.Alarm;
import io.sciota.demo.alarmservice.persistence.Room;
import io.sciota.demo.alarmservice.persistence.Schedule;

public class DtoMapper {
    public static ScheduleDto from(Schedule dbSched) {
        ScheduleDto sched = new ScheduleDto();
        sched.setRoomId(dbSched.getRoom().getId());
        sched.setBegin(dbSched.getBeginMinsOfDay());
        sched.setEnd(dbSched.getEndMinsOfDay());
        sched.setDaysOfWeekMask(dbSched.getActiveDaysOfWeekMask());
        return sched;
    }

    public static Schedule from(ScheduleDto dto, Room dbRoom) {
        Schedule sched = new Schedule();
        sched.setRoom(dbRoom);
        sched.setBeginMinsOfDay(dto.getBegin());
        sched.setEndMinsOfDay(dto.getEnd());
        sched.setActiveDaysOfWeekMask(dto.getDaysOfWeekMask());
        return sched;
    }

    public static AlarmDto from(Alarm dbAlarm) {
        var alarm = new AlarmDto();
        alarm.setAlarmId(dbAlarm.getId());
        alarm.setRoomId(dbAlarm.getRoom().getId());
        alarm.setReason(dbAlarm.getReason());
        alarm.setTimestamp(DateUtils.asOffsetDateTime(dbAlarm.getTimestamp()));
        alarm.setAcknowledged(dbAlarm.isAcknowledged());
        return alarm;
    }

    public static Alarm from(AlarmDto dto, Room room) {
        var alarm = new Alarm();
        alarm.setRoom(room);
        alarm.setReason(dto.getReason());
        alarm.setTimestamp(DateUtils.asDate(dto.getTimestamp()));
        alarm.setAcknowledged(dto.getAcknowledged() == null ? false : dto.getAcknowledged());
        return alarm;
    }

    public static RoomDto from(Room dbRoom) {
        var room = new RoomDto();
        room.setRoomId(dbRoom.getId());
        room.setName(dbRoom.getName());
        return room;
    }

    public static Alarm from(EventDto event, Room dbRoom) {
        Alarm alarm = new Alarm();
        alarm.setAcknowledged(false);
        alarm.setRoom(dbRoom);
        alarm.setReason(event.getEventType());
        // alarm.setTimestamp(DateUtils.asDate(event.getTimestamp()));  // use event timestamp
        alarm.setTimestamp(Date.from(Instant.now()));  // overwrite timestamp with new alarm creation timestamp
        return alarm;
    }

}
