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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.Instant;
import static java.time.temporal.ChronoField.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
            final Instant startDate,
            final Instant stopDate,
            final String title
    ) {
        ScheduleEntity scheduleEntity = scheduleRepository.fetch(scheduleId);
        if (scheduleEntity == null)
            throw new InvalidArgumentException("Schedule does not exist. " + scheduleId);

        ScheduleEntryEntity entry = new ScheduleEntryEntity();
        entry.setPriority(priority);
        entry.setType(entryType);
        entry.setRepeatType(repeatType);
        entry.setTitle(title);
        entry.setSchedule(scheduleEntity);
        entry.updateTimestamp(
                Optional.ofNullable(start),
                Optional.ofNullable(stop),
                Optional.ofNullable(startDate),
                Optional.ofNullable(stopDate)
        );

        scheduleEntity.getEntries().add(entry);

        return scheduleRepository.save(scheduleEntity);
    }

    public List<ScheduleEntity> getList() {
        UserEntity user = sessionService.getCurrentSession().getUser();

        if (user.getOrganization() == null)
            return scheduleRepository.fetchList(user);

        return scheduleRepository.fetchList(user.getOrganization());
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
    private boolean isValidForSend(
            final ScheduleEntryEntity prefiltredScheduleEntry
    ) {
        // TODO /60/60/24? :O
        LocalDate scheduleEntryNotificationStartDate = LocalDate.ofEpochDay(
                prefiltredScheduleEntry
                        .getNotificationStartDate()
                        .getEpochSecond()/60/60/24
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
                        scheduleEntryNotificationStartDate.get(DAY_OF_WEEK),
                        LocalDate.now().get(DAY_OF_WEEK)
                );
            case MONTHLY:
                return Objects.equals(
                        scheduleEntryNotificationStartDate.get(DAY_OF_MONTH),
                        LocalDate.now().get(DAY_OF_MONTH)
                );
            case YEARLY:
                boolean validDay = Objects.equals(
                        scheduleEntryNotificationStartDate.get(DAY_OF_MONTH),
                        LocalDate.now().get(DAY_OF_MONTH));
                boolean validMonth = Objects.equals(
                        scheduleEntryNotificationStartDate.get(MONTH_OF_YEAR),
                        LocalDate.now().get(MONTH_OF_YEAR));
                return validDay && validMonth;
            case WORKDAYS:
                return isWorkDay(
                        DayOfWeek.of(
                                scheduleEntryNotificationStartDate.get(DAY_OF_WEEK)
                        )
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
        ScheduleEntryEntity scheduleEntry = scheduleRepository
                .fetchEntry(scheduleEntryUuid)
                .orElseThrow(() -> new InvalidArgumentException("")); // TODO Exception

        UserEntity user = userRepository
                .fetchByUuid(userUuid)
                .orElseThrow(() -> new InvalidArgumentException("")); // TODO Exception

        scheduleRepository.save(
                new HighPriorityAlertResponseEntity(scheduleEntry, user)
        );
    }

    public void removeScheduleEntryEntity(final String uuid) {
        ScheduleEntryEntity scheduleEntryEntity = scheduleRepository
                .fetchEntry(uuid)
                .orElseThrow(() -> new InvalidArgumentException("")); // TODO Exception

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
            final Optional<String> title,
            final Optional<ScheduleEntryType> type,
            final Optional<ScheduleEntryPriority> priority,
            final Optional<ScheduleEntryRepeatType> repeatType,
            final Optional<Time> start,
            final Optional<Time> stop,
            final Optional<Instant> startDate,
            final Optional<Instant> endDate
    ) {
        ScheduleEntryEntity scheduleEntry = scheduleRepository
                .fetchEntry(uuid)
                .orElseThrow(() -> new InvalidArgumentException("")); // TODO Exception

        if (Objects.nonNull(scheduleEntry.getLastNotificationDate())) {
            // TODO Exception
        }

        title.ifPresent(scheduleEntry::setTitle);
        type.ifPresent(scheduleEntry::setType);
        priority.ifPresent(scheduleEntry::setPriority);
        repeatType.ifPresent(scheduleEntry::setRepeatType);
        scheduleEntry.updateTimestamp(start, stop, startDate, endDate);

        return scheduleRepository.save(scheduleEntry);
    }
}
