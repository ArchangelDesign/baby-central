/*
 * Baby Central
 * Copyright (c) 2018.
 * Rafal Martinez-Marjanski
 */

package com.archangel_design.babycentral.controller;

import com.archangel_design.babycentral.entity.ScheduleEntity;
import com.archangel_design.babycentral.entity.ScheduleEntryEntity;
import com.archangel_design.babycentral.request.ScheduleEntryRequest;
import com.archangel_design.babycentral.request.CreateScheduleRequest;
import com.archangel_design.babycentral.request.ScheduleEntryAlertAnswerRequest;
import com.archangel_design.babycentral.service.ScheduleService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
@Api(tags = "Schedule management")
public class ScheduleController {

    @Autowired
    ScheduleService scheduleService;

    @PostMapping("/create-schedule")
    public ScheduleEntity addSchedule(
            @RequestBody CreateScheduleRequest request
    ) {
        return scheduleService.createSchedule(
                request.getBabyUuid(), request.getName()
        );
    }

    @GetMapping("/list")
    public List<ScheduleEntity> getList() {
        return scheduleService.getList();
    }

    @GetMapping("/list/{babyUuid}")
    public List<ScheduleEntity> getListForBaby(
            @PathVariable String babyUuid
    ) {
        return scheduleService.getList(babyUuid);
    }

    @PostMapping("/entry/{scheduleId}")
    public ScheduleEntity createEntry(
            @PathVariable String scheduleId,
            @RequestBody ScheduleEntryRequest request
    ) {
        return scheduleService.createEntry(
                scheduleId,
                request.getType(),
                request.getStart(),
                request.getStop(),
                request.getPriority(),
                request.getRepeatType(),
                request.getStartDate(),
                request.getEndDate(),
                request.getTitle()
        );
    }

    @GetMapping("/fetch/{uuid}")
    public ScheduleEntity fetchSchedule(
            @PathVariable String uuid
    ) {
        return scheduleService.fetch(uuid);
    }

    @DeleteMapping("/{uuid}")
    public void removeScheduleEntity(
            @PathVariable String uuid
    ) {
        scheduleService.removeScheduleEntity(uuid);
    }

    @DeleteMapping("/entry/{uuid}")
    public void removeScheduleEntryEntity(
            @PathVariable String uuid
    ) {
        scheduleService.removeScheduleEntryEntity(uuid);
    }

    @PostMapping("/entry/{uuid}/alert-answer")
    public void recordAnswerForScheduleEntryAlert(
            @PathVariable final String uuid,
            @RequestBody final ScheduleEntryAlertAnswerRequest request
    ) {
        scheduleService.recordAnswerForScheduleEntryAlert(
                uuid,
                request.getUserUuid()
        );
    }

    @PutMapping("/entry/{uuid}")
    public ScheduleEntryEntity updateScheduleEntryEntity(
            @PathVariable final String uuid,
            @RequestBody final ScheduleEntryRequest scheduleEntryRequest
    ) {
        return scheduleService.updateScheduleEntryEntity(
                uuid,
                scheduleEntryRequest
        );
    }
}
