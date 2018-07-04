package com.archangel_design.babycentral.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Accessors(chain = true)
@Entity
@Getter
@JsonIgnoreProperties(value = {"id", "deleted", "password", "avatar"})
@Setter
@Table(name = "users")
public class UserEntity {

    @GeneratedValue
    @Id
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(length = 36)
    @Setter(AccessLevel.NONE)
    private String uuid = UUID.randomUUID().toString();

    @Column(length = 150)
    private String email;

    @Column(length = 150)
    private String password;

    private Instant registration;

    private Instant lastUsage;

    private Boolean deleted = false;

    private Boolean invitationPending = false;

    @JoinColumn(name = "parent_id")
    @OneToMany(targetEntity = BabyEntity.class, cascade = CascadeType.ALL)
    private List<BabyEntity> babies = new ArrayList<>();

    @JoinColumn(name = "organization_id")
    @ManyToOne(
            targetEntity = OrganizationEntity.class,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.REFRESH
            })
    private OrganizationEntity organization;

    @JoinColumn(name = "profile_id")
    @ManyToOne(targetEntity = ProfileEntity.class, cascade = CascadeType.ALL)
    private ProfileEntity profile;

    @Column(columnDefinition = "mediumblob")
    @Lob
    private byte[] avatar = new byte[0];
}
