package io.sciota.demo.alarmservice.api;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.sciota.demo.alarmservice.dtos.EventDto;
import io.sciota.demo.alarmservice.mapper.DtoMapper;
import io.sciota.demo.alarmservice.persistence.Alarm;
import io.sciota.demo.alarmservice.persistence.AlarmRepository;
import io.sciota.demo.alarmservice.persistence.RoomRepository;
import io.sciota.demo.alarmservice.persistence.ScheduleRepository;
import jakarta.annotation.Resource;

@RestController
public class AlarmController {

	private final AtomicLong counter = new AtomicLong();

	@Resource
	AlarmRepository alarmRepository;

	@Resource
	ScheduleRepository scheduleRepository;

	@Resource
	RoomRepository roomRepository;

	@GetMapping("/hello")
	public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Ok #" + counter.getAndIncrement() + " from '" + name + "'";
	}

	@GetMapping("/room")
	public List<RoomDto> getRooms() {
		return StreamSupport.stream(roomRepository.findAll().spliterator(), false)
				.map(DtoMapper::from)
				.collect(Collectors.toList());
	}

	@GetMapping("/schedule")
	public List<ScheduleDto> getSchedules(@RequestParam(value = "roomId", required = true) long roomId) {
		if (!scheduleRepository.existsById(roomId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					String.format("room with id '%d' does not exist.", roomId));
		}

		// query db
		var scheds = scheduleRepository.findByRoomId(roomId);

		// map to dto
		var res = scheds
				.stream()
				.map(DtoMapper::from)
				.collect(Collectors.toList());

		return res;
	}

	@PostMapping("/schedule")
	public ResponseEntity<String> postSchedule(@RequestBody ScheduleDto dto) {
		var room = roomRepository.findById(dto.roomId);
		if (!room.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					String.format("room with id '%d' does not exist.", dto.roomId));
		}

		// insert in DB
		scheduleRepository.save(DtoMapper.from(dto, room.get()));

		return new ResponseEntity<String>(HttpStatus.CREATED);
	}

	@GetMapping("/alarm")
	public List<AlarmDto> getAlarms(@RequestParam(value = "roomId", required = true) long roomId) {
		if (!roomRepository.existsById(roomId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					String.format("room with id '%d' does not exist.", roomId));
		}

		return alarmRepository.findByRoomId(roomId)
				.stream()
				.map(DtoMapper::from)
				.toList();
	}

	// @PostMapping("/alarm")
	// public ResponseEntity<String> postAlarm(@RequestBody AlarmDto alarm) {
	// var room = roomRepository.findById(alarm.roomId);
	// if (!room.isPresent()) {
	// throw new ResponseStatusException(HttpStatus.NOT_FOUND,
	// String.format("room with id '%d' does not exist.", alarm.roomId));
	// }
	//
	// alarmRepository.save(DtoMapper.from(alarm, room.get()));
	//
	// return new ResponseEntity<String>(HttpStatus.CREATED);
	// }

	@PostMapping("/event")
	public ResponseEntity<String> postAlarm(@RequestBody EventDto event) {
		var room = roomRepository.findById(event.getRoomId());
		if (!room.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					String.format("room with id '%d' does not exist.", event.getRoomId()));
		}

		Alarm alarm = DtoMapper.from(event, room.get());
		alarmRepository.save(alarm);

		return new ResponseEntity<String>(HttpStatus.CREATED);
	}

	@PostMapping("/alarm/{id}/acknowledge")
	public ResponseEntity<String> postAlarm(@PathVariable Long id) {
		var alarm = alarmRepository.findById(id);
		if (!alarm.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					String.format("alarm with id '%d' does not exist.", id));
		}

		var _alarm = alarm.get();
		_alarm.setAcknowledged(true);
		alarmRepository.save(_alarm);

		return new ResponseEntity<String>(HttpStatus.CREATED);
	}

}