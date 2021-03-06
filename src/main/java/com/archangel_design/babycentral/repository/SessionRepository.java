package com.archangel_design.babycentral.repository;

import com.archangel_design.babycentral.entity.SessionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;

@Repository
public class SessionRepository extends GenericRepository {

    @Transactional()
    public void removeSessions(Long userId, String deviceId) {
        Query q = em.createQuery(
                "delete from SessionEntity s "
                        + "where s.user.id = :id and s.deviceId = :deviceId"
        );

        q.setParameter("id", userId);
        q.setParameter("deviceId", deviceId);

        q.executeUpdate();
    }

    public SessionEntity fetch(String token) {
        TypedQuery<SessionEntity> q = em.createQuery(
                "select s from SessionEntity s "
                        + "where s.sessionId = :token",
                SessionEntity.class
        );
        q.setParameter("token", token);

        return q.getResultList().stream().findFirst().orElse(null);
    }

    @Transactional
    public void remove(String token) {
        Query q = em.createQuery(
                "delete from SessionEntity s "
                + "where s.sessionId = :token"
        );
        q.setParameter("token", token);

        q.executeUpdate();
    }
}
