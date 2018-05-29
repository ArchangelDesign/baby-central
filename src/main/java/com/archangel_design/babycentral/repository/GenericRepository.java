package com.archangel_design.babycentral.repository;

import com.archangel_design.babycentral.entity.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

@Repository
@Transactional(readOnly = true)
public abstract class GenericRepository {

    @PersistenceContext
    EntityManager em;

    @Transactional()
    public SessionEntity save(SessionEntity session) {
        return em.merge(session);
    }

    @Transactional()
    public UserEntity save(UserEntity userEntity) {
        return em.merge(userEntity);
    }

    @Transactional()
    public LocationEntity save(LocationEntity locationEntity) {
        return em.merge(locationEntity);
    }


    @Transactional()
    public ScheduleEntity save(@NotNull final ScheduleEntity scheduleEntity) {
        return em.merge(scheduleEntity);
    }

    @Transactional()
    public ScheduleEntryEntity save(@NotNull final ScheduleEntryEntity scheduleEntryEntity) {
        return em.merge(scheduleEntryEntity);
    }

    @Transactional()
    public Object save(Object object) {
        return em.merge(object);
    }
}
