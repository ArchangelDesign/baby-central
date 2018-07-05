/*
 * Baby Central
 * Copyright (c) 2018.
 * Rafal Martinez-Marjanski
 */

package com.archangel_design.babycentral.repository;

import com.archangel_design.babycentral.entity.BabyEntity;
import com.archangel_design.babycentral.entity.OrganizationEntity;
import com.archangel_design.babycentral.entity.UserEntity;
import org.springframework.stereotype.Repository;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends GenericRepository {

    /**
     * Returns UserEntity matching criteria
     * or null if not found
     *
     * @param email        user email
     * @param passwordHash hashed password
     * @return UserEntity|null
     */
    public UserEntity fetch(String email, String passwordHash) {
        TypedQuery<UserEntity> query = em.createQuery(
                "select u from UserEntity u "
                        + "where u.email = :email "
                        + "and u.password = :password",
                UserEntity.class
        );

        query.setParameter("email", email);
        query.setParameter("password", passwordHash);

        return query.getResultList().stream().findFirst().orElse(null);
    }

    /**
     * Returns UserEntity matching criteria
     * or null if not found
     *
     * @param email user email
     * @return UserEntity|null
     */
    public UserEntity fetch(String email) {
        TypedQuery<UserEntity> query = em.createQuery(
                "select u from UserEntity u "
                        + "where u.email = :email ",
                UserEntity.class
        );

        query.setParameter("email", email);

        return query.getResultList().stream().findFirst().orElse(null);
    }

    /**
     * Returns true if user identified by given email
     * is already registered. Does not include deleted accounts
     *
     * @param email user email
     * @return true|false
     */
    public Boolean userExists(String email) {
        TypedQuery<Long> query = em.createQuery(
                "select count(u) from UserEntity u "
                        + "where lower(u.email) = :email "
                        + "and u.deleted = false",
                Long.class
        );
        query.setParameter("email", email.toLowerCase());

        return query.getSingleResult() > 0;
    }

    /**
     * Returns user entity of invited user.
     * such user can create an account and become member of organization
     *
     * @param email
     * @return
     */
    public Optional<UserEntity> getUserWithPendingInvitation(
            final String email
    ) {
        TypedQuery<UserEntity> query = em.createQuery(
                "select u from UserEntity u "
                        + "where lower(u.email) = :email "
                        + "and u.deleted = false "
                        + "and u.invitationPending = true",
                UserEntity.class
        );

        query.setParameter("email", email.toLowerCase());

        return query.getResultList().stream().findFirst();
    }

    /**
     *
     * @param babyId uuid
     * @return BabyEntity|null
     */
    public BabyEntity fetchBaby(String babyId) {
        TypedQuery<BabyEntity> query = em.createQuery(
                "select b from BabyEntity b "
                + "where b.uuid = :uuid", BabyEntity.class
        );
        query.setParameter("uuid", babyId);

        return query.getResultList().stream().findFirst().orElse(null);
    }

    public List<UserEntity> fetchOrganizationMembers(OrganizationEntity organization) {
        TypedQuery<UserEntity> query = em.createQuery(
                "select u from UserEntity u "
                + "where u.deleted = false "
                + "and u.organization is not null "
                + "and u.organization.id = :oid",
                UserEntity.class
        );
        query.setParameter("oid", organization.getId());

        return query.getResultList();
    }

    public Optional<UserEntity> fetchByUuid(final String userUuid) {
        TypedQuery<UserEntity> query = em.createQuery(
                "select u from UserEntity u "
                        + "where u.uuid = :userUuid ",
                UserEntity.class
        );

        query.setParameter("userUuid", userUuid);

        return query.getResultList().stream().findFirst();
    }
}
