package io.sciota.demo.alarmservice.mapper;

import io.sciota.demo.alarmservice.dtos.ScheduleDto;

public class DtoValidation {
    public static void validate(ScheduleDto sched) {
        if (sched.getBegin() >= sched.getEnd()) {
            throw new IllegalArgumentException("'begin' must be before 'end'");
        }
    }

}
