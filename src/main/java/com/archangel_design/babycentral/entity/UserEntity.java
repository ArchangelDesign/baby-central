package com.archangel_design.babycentral.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "users")
@JsonIgnoreProperties(value = {"id", "deleted", "password", "avatar"})
public class UserEntity {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(length = 36)
    @Setter(AccessLevel.NONE)
    private String uuid = UUID.randomUUID().toString();

    @Column(length = 150)
    private String email;

    @Column(length = 150)
    private String password;

    private Date registration;

    private Date lastUsage;

    private Boolean deleted = false;

    private Boolean invitationPending = false;

    @OneToMany(targetEntity = BabyEntity.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    private List<BabyEntity> babies = new ArrayList<>();

    @ManyToOne(
            targetEntity = OrganizationEntity.class,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.REFRESH
            })
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organization;

    @ManyToOne(targetEntity = ProfileEntity.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;

    @Lob
    @Column(columnDefinition = "mediumblob")
    private byte[] avatar = new byte[0];
}
