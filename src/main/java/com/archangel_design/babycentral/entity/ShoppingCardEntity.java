package com.archangel_design.babycentral.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shopping_card")
public class ShoppingCardEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 120)
    private String name;

    @Column(length = 36)
    private String uuid = UUID.randomUUID().toString();

    @CreationTimestamp
    @Column(updatable=false)
    private Date creationDate;

    @OneToMany(targetEntity = ShoppingCardEntryEntity.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "shopping_card_id")
    private List<ShoppingCardEntryEntity> entries = new ArrayList<>();

    @ManyToOne(targetEntity = UserEntity.class, optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public Long getId() {
        return id;
    }

    public ShoppingCardEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public UserEntity getUser() {
        return user;
    }

    public ShoppingCardEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public String getName() {
        return name;
    }

    public ShoppingCardEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public ShoppingCardEntity setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public List<ShoppingCardEntryEntity> getEntries() {
        return entries;
    }

    public ShoppingCardEntity setEntries(List<ShoppingCardEntryEntity> entries) {
        this.entries = entries;
        return this;
    }
}
