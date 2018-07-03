package com.archangel_design.babycentral.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@JsonIgnoreProperties(value = {"id", "deleted", "password", "avatar"})
public class UserEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 36)
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

    @ManyToOne(targetEntity = OrganizationEntity.class,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.REFRESH})
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organization;

    @ManyToOne(targetEntity = ProfileEntity.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;

    @Lob
    @Column(columnDefinition = "mediumblob")
    private byte[] avatar = new byte[0];

    public Long getId() {
        return id;
    }

    public UserEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public UserEntity setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserEntity setPassword(String password) {
        this.password = password;
        return this;
    }

    public Date getRegistration() {
        return registration;
    }

    public UserEntity setRegistration(Date registration) {
        this.registration = registration;
        return this;
    }

    public Date getLastUsage() {
        return lastUsage;
    }

    public UserEntity setLastUsage(Date lastUsage) {
        this.lastUsage = lastUsage;
        return this;
    }

    public List<BabyEntity> getBabies() {
        return babies;
    }

    public UserEntity setBabies(List<BabyEntity> babies) {
        this.babies = babies;
        return this;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public UserEntity setDeleted(Boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public OrganizationEntity getOrganization() {
        return organization;
    }

    public UserEntity setOrganization(OrganizationEntity organization) {
        this.organization = organization;
        return this;
    }

    public ProfileEntity getProfile() {
        return profile;
    }

    public UserEntity setProfile(ProfileEntity profile) {
        this.profile = profile;
        return this;
    }

    public Boolean getInvitationPending() {
        return invitationPending;
    }

    public UserEntity setInvitationPending(Boolean invitationPending) {
        this.invitationPending = invitationPending;
        return this;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }
}
