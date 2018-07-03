/*
 * Baby Central
 * Copyright (c) 2018.
 * Rafal Martinez-Marjanski
 */

package com.archangel_design.babycentral.repository;

import com.archangel_design.babycentral.entity.ScheduleEntity;
import com.archangel_design.babycentral.entity.ScheduleEntryEntity;
import com.archangel_design.babycentral.entity.UserEntity;
import com.archangel_design.babycentral.enums.ScheduleEntryPriority;
import org.springframework.stereotype.Repository;

import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Repository
public class ScheduleRepository extends GenericRepository {

    public ScheduleEntity fetch(@NotNull final String scheduleId) {
        TypedQuery<ScheduleEntity> query = em.createQuery(
                "select s from ScheduleEntity s "
                        + "where s.uuid = :id", ScheduleEntity.class
        );

        query.setParameter("id", scheduleId);

        return query.getResultList().stream().findFirst().orElse(null);
    }

    public ScheduleEntryEntity fetchEntry(@NotNull final String entryUuid) {
        TypedQuery<ScheduleEntryEntity> query = em.createQuery(
                "select s from ScheduleEntryEntity s "
                        + "where s.uuid = :id", ScheduleEntryEntity.class
        );

        query.setParameter("id", entryUuid);

        return query.getResultList().stream().findFirst().orElse(null);
    }

    public List<ScheduleEntity> fetchList(UserEntity user) {
        TypedQuery<ScheduleEntity> query = em.createQuery(
                "select s from ScheduleEntity s "
                        + "where s.user.id = :uid", ScheduleEntity.class
        );

        query.setParameter("uid", user.getId());

        return query.getResultList();
    }

    public List<ScheduleEntity> fetchList(UserEntity user, String uuid) {
        TypedQuery<ScheduleEntity> query = em.createQuery(
                "select s from ScheduleEntity s "
                        + "where s.user.id = :uid "
                        + "and s.baby.uuid = :bid", ScheduleEntity.class
        );

        query.setParameter("uid", user.getId());
        query.setParameter("bid", uuid);

        return query.getResultList();
    }

    // TODO NAZWA
    public List<ScheduleEntryEntity> fetchPrefiltredScheduleEntriesForNotificationSending() {
        Instant d1 = Instant.now().minus(Duration.ofMinutes(20));
        Instant d2 = Instant.now().plus(Duration.ofMinutes(20));

        TypedQuery<ScheduleEntryEntity> query = em.createQuery(
                "select s from ScheduleEntryEntity s " +
                        "where s.isHighPriorityAlertActive = false " +
                        "and DATE(SYSDATE()) between DATE(s.startDate) and DATE(s.endDate) " +
                        "and s.start between :d1 and :d2 " +
                        "and (s.lastNotificationDate is null or DATE(s.lastNotificationDate) < DATE(SYSDATE()))",
                ScheduleEntryEntity.class
        );

        query.setParameter("d1", Date.from(d1), TemporalType.TIME);
        query.setParameter("d2", Date.from(d2), TemporalType.TIME);

        return query.getResultList();
    }

    // TODO NAZWA
    public List<ScheduleEntryEntity> fetchScheduleEntriesForAlertResending() {
        TypedQuery<ScheduleEntryEntity> query = em.createQuery(
                "select s from ScheduleEntryEntity s " +
                        "where s.isHighPriorityAlertActive = true " +
                        "and s.priority = :priority " +
                        "and (" +
                            "select count(a.id) from HighPriorityAlertResponseEntity a " +
                            "where a.scheduleEntry.uuid = s.uuid " +
                            "and s.lastNotificationDate < a.responseDate " +
                        ") = 0",
                ScheduleEntryEntity.class
        );

        query.setParameter("priority", ScheduleEntryPriority.HIGH);

        return query.getResultList();
    }
}
