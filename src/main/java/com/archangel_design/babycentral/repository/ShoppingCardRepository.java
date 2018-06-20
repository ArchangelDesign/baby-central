package com.archangel_design.babycentral.repository;

import com.archangel_design.babycentral.entity.ShoppingCardEntity;
import com.archangel_design.babycentral.entity.ShoppingCardEntryEntity;
import com.archangel_design.babycentral.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
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
                        + "where s.user.id = :uid", ShoppingCardEntity.class
        );

        query.setParameter("uid", user.getId());

        return query.getResultList();
    }

    public List<ShoppingCardEntity> fetchList(UserEntity user, String uuid) {
        TypedQuery<ShoppingCardEntity> query = em.createQuery(
                "select s from ShoppingCardEntity s "
                        + "where s.user.id = :uid "
                        + "and s.baby.uuid = :bid", ShoppingCardEntity.class
        );

        query.setParameter("uid", user.getId());
        query.setParameter("bid", uuid);

        return query.getResultList();
    }

    @Transactional
    public Boolean toogleIsPurchased(String uuid) {
        TypedQuery<ShoppingCardEntryEntity> query = em.createQuery(
                "select b from ShoppingCardEntryEntity b "
                        + "where b.uuid = :uuid", ShoppingCardEntryEntity.class
        );
        query.setParameter("uuid", uuid);

        ShoppingCardEntryEntity shoppingCardEntryEntity = query.getSingleResult();
        shoppingCardEntryEntity.setIsPurchased(shoppingCardEntryEntity.getIsPurchased() ? false : true);
        save(shoppingCardEntryEntity);

        return shoppingCardEntryEntity.getIsPurchased();
    }

}
