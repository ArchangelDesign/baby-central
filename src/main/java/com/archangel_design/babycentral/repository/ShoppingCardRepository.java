package com.archangel_design.babycentral.repository;

import com.archangel_design.babycentral.entity.ShoppingCardEntity;
import com.archangel_design.babycentral.entity.ShoppingCardEntryEntity;
import com.archangel_design.babycentral.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.List;

@Repository
public class ShoppingCardRepository extends GenericRepository {

    public ShoppingCardEntity fetch(@NotNull final String shoppingCardId) {
        TypedQuery<ShoppingCardEntity> query = em.createQuery(
                "select s from ShoppingCardEntity s "
                        + "where s.uuid = :id", ShoppingCardEntity.class
        );

        query.setParameter("id", shoppingCardId);

        return query.getResultList().stream().findFirst().orElse(null);
    }

    public List<ShoppingCardEntity> fetchList(UserEntity user) {
        TypedQuery<ShoppingCardEntity> query = em.createQuery(
                "select s from ShoppingCardEntity s "
                        + "where (s.user = :user and not s.status = 'FINISHED') "
                        + "or ("
                            + "s.status = 'PUBLISHED' "
                            + "and :user member of s.assignedUsers"
                        + ")",
                ShoppingCardEntity.class
        );

        query.setParameter("user", user);

        return query.getResultList();
    }

    public List<ShoppingCardEntity> fetchList(UserEntity user, String uuid) {
        TypedQuery<ShoppingCardEntity> query = em.createQuery(
                "select s from ShoppingCardEntity s "
                        + "where s.user.id = :uid "
                        + "and s.uuid = :uuid", ShoppingCardEntity.class
        );

        query.setParameter("uid", user.getId());
        query.setParameter("uuid", uuid);

        return query.getResultList();
    }

    public ShoppingCardEntryEntity fetchEntry(String uuid) {
        TypedQuery<ShoppingCardEntryEntity> query = em.createQuery(
                "select s from ShoppingCardEntryEntity s "
                        + "where s.uuid = :uuid ", ShoppingCardEntryEntity.class
        );
        query.setParameter("uuid", uuid);

        return query.getResultList().stream().findFirst().orElse(null);
    }
}
