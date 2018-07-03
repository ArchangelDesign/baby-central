/*
 * Baby Central
 * Copyright (c) 2018.
 * Rafal Martinez-Marjanski
 */

package com.archangel_design.babycentral.service;

import com.archangel_design.babycentral.entity.*;
import com.archangel_design.babycentral.enums.ScheduleEntryPriority;
import com.archangel_design.babycentral.enums.ScheduleEntryRepeatType;
import com.archangel_design.babycentral.enums.ScheduleEntryType;
import com.archangel_design.babycentral.exception.InvalidArgumentException;
import com.archangel_design.babycentral.repository.ScheduleRepository;
import com.archangel_design.babycentral.repository.UserRepository;
import com.archangel_design.babycentral.request.ScheduleEntryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.Instant;
import static java.time.temporal.ChronoField.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    SessionService sessionService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    public ScheduleEntity createSchedule(
            @NotNull final String babyId,
            @NotNull final String name
            ) {
        System.out.println(babyId);
        BabyEntity baby = userRepository.fetchBaby(babyId);

        if (baby == null) {
            throw new InvalidArgumentException("Baby does not exist.");
        }
        ScheduleEntity scheduleEntity = new ScheduleEntity();
        UserEntity user = sessionService.getCurrentSession().getUser();

        scheduleEntity
                .setName(name)
                .setBaby(baby)
                .setUser(user);

        return scheduleRepository.save(scheduleEntity);
    }

    public ScheduleEntity createEntry(
            @NotNull final String scheduleId,
            @NotNull final ScheduleEntryType entryType,
            @NotNull final Time start,
            @NotNull final Time stop,
            @NotNull final ScheduleEntryPriority priority,
            @NotNull final ScheduleEntryRepeatType repeatType,
            final Date dateStart,
            final Date dateStop,
            final String title
    ) {
        ScheduleEntity scheduleEntity = scheduleRepository.fetch(scheduleId);
        if (scheduleEntity == null)
            throw new InvalidArgumentException("Schedule does not exist. " + scheduleId);

        ScheduleEntryEntity entry = new ScheduleEntryEntity();
        entry.setPriority(priority);
        entry.setStart(start);
        entry.setType(entryType);
        entry.setStop(stop);
        entry.setRepeatType(repeatType);
        entry.setStartDate(dateStart);
        entry.setEndDate(dateStop);
        entry.setTitle(title);
        entry.setSchedule(scheduleEntity);

        scheduleEntity.getEntries().add(entry);

        return scheduleRepository.save(scheduleEntity);
    }

    public List<ScheduleEntity> getList() {
        UserEntity user = sessionService.getCurrentSession().getUser();

        return scheduleRepository.fetchList(user);
    }

    public ScheduleEntity fetch(String uuid) {
        return scheduleRepository.fetch(uuid);
    }

    public List<ScheduleEntity> getList(String uuid) {
        UserEntity user = sessionService.getCurrentSession().getUser();
        return scheduleRepository.fetchList(user, uuid);
    }

    // TODO NAZWA
    public List<ScheduleEntryEntity> getHighPriorityEventsForAlertsResending() {
        return scheduleRepository.fetchScheduleEntriesForAlertResending();
    }

    // TODO NAZWA
    public List<ScheduleEntryEntity> getEventsForNotificationSending() {
        return scheduleRepository.fetchPrefiltredScheduleEntriesForNotificationSending()
                .stream()
                .filter(this::isValidForSend)
                .collect(Collectors.toList());
    }

    // TODO split to methods? NAZWA METODY
    private boolean isValidForSend(ScheduleEntryEntity prefiltredScheduleEntry) {
        Instant startDateAsInstant = prefiltredScheduleEntry.getStartDate().toInstant();
        // TODO /60/60/24? :O
        LocalDate scheduleEntryStartDate = LocalDate.ofEpochDay(
                startDateAsInstant.getEpochSecond()/60/60/24
        );

        switch(prefiltredScheduleEntry.getRepeatType()) {
            case SINGLE:
                return Objects.isNull(
                        prefiltredScheduleEntry.getLastNotificationDate()
                );
            case DAILY:
                return true;
            case WEEKLY:
                return Objects.equals(
                        scheduleEntryStartDate.get(DAY_OF_WEEK),
                        LocalDate.now().get(DAY_OF_WEEK)
                );
            case MONTHLY:
                return Objects.equals(
                        scheduleEntryStartDate.get(DAY_OF_MONTH),
                        LocalDate.now().get(DAY_OF_MONTH)
                );
            case YEARLY:
                return Objects.equals(
                        scheduleEntryStartDate.get(DAY_OF_YEAR),
                        LocalDate.now().get(DAY_OF_YEAR)
                );
            case WORKDAYS:
                return isWorkDay(
                        DayOfWeek.of(scheduleEntryStartDate.get(DAY_OF_WEEK))
                );
        }

        // LOGGER nieobsługiwany event
        return false;
    }

    // TODO tutaj?
    private boolean isWorkDay(DayOfWeek dayOfWeek) {
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    public void recordAnswerForScheduleEntryAlert(
            final String scheduleEntryUuid,
            final String userUuid
    ) {
        ScheduleEntryEntity scheduleEntry = scheduleRepository.fetchEntry(scheduleEntryUuid);
        if (Objects.isNull(scheduleEntry)) {
            // TODO Exception
        }

        UserEntity user = userRepository.fetchByUuid(userUuid);
        if (Objects.isNull(user)) {
            // TODO Exception
        }

        scheduleRepository.save(new HighPriorityAlertResponseEntity(scheduleEntry, user));
    }

    public void removeScheduleEntryEntity(String uuid) {
        ScheduleEntryEntity scheduleEntryEntity = scheduleRepository.fetchEntry(uuid);

        if (scheduleEntryEntity == null)
            throw new InvalidArgumentException("scheduleEntryEntity does not exist.");

        scheduleRepository.delete(scheduleEntryEntity);
    }

    public void removeScheduleEntity(String uuid) {
        ScheduleEntity scheduleEntity = scheduleRepository.fetch(uuid);

        if (scheduleEntity == null)
            throw new InvalidArgumentException("scheduleEntity does not exist.");

        scheduleRepository.delete(scheduleEntity);
    }

    public ScheduleEntryEntity updateScheduleEntryEntity(
            final String uuid,
            final ScheduleEntryRequest updateRequest
    ) {
        ScheduleEntryEntity scheduleEntry = scheduleRepository.fetchEntry(uuid);

        if (Objects.isNull(scheduleEntry)) {
            // TODO Exception
        }

        if (Objects.nonNull(scheduleEntry.getLastNotificationDate())) {
            // TODO Exception
        }

        if (Objects.nonNull(updateRequest.getTitle()))
            scheduleEntry.setTitle(updateRequest.getTitle());
        if (Objects.nonNull(updateRequest.getType()))
            scheduleEntry.setType(updateRequest.getType());
        if (Objects.nonNull(updateRequest.getStart()))
            scheduleEntry.setStart(updateRequest.getStart());
        if (Objects.nonNull(updateRequest.getStop()))
            scheduleEntry.setStop(updateRequest.getStop());
        if (Objects.nonNull(updateRequest.getPriority()))
            scheduleEntry.setPriority(updateRequest.getPriority());
        if (Objects.nonNull(updateRequest.getRepeatType()))
            scheduleEntry.setRepeatType(updateRequest.getRepeatType());
        if (Objects.nonNull(updateRequest.getStartDate()))
            scheduleEntry.setStartDate(updateRequest.getStartDate());
        if (Objects.nonNull(updateRequest.getEndDate()))
            scheduleEntry.setEndDate(updateRequest.getEndDate());

        return scheduleRepository.save(scheduleEntry);
    }
}
