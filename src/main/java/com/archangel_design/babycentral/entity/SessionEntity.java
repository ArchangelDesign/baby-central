package com.archangel_design.babycentral.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Accessors(chain = true)
@Entity
@Getter
@Setter
@Table(name = "sessions")
public class SessionEntity {

    @GeneratedValue
    @Id
    @Setter(AccessLevel.NONE)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(targetEntity = UserEntity.class)
    private UserEntity user;

    private String sessionId = UUID.randomUUID().toString();

    private Date created;

    private Date expiration;

    private String deviceId;
}
