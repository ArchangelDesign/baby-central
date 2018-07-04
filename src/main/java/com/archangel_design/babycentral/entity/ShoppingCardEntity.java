package com.archangel_design.babycentral.entity;

import com.archangel_design.babycentral.enums.ShoppingCardStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Accessors(chain = true)
@Entity
@Getter
@JsonIgnoreProperties(value = {"id"})
@Setter
@Table(name = "shopping_card")
public class ShoppingCardEntity {

    @GeneratedValue
    @Id
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(length = 36)
    @Setter(AccessLevel.NONE)
    private String uuid = UUID.randomUUID().toString();

    @Column(length = 120)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(updatable = false)
    @CreationTimestamp
    private Date creationDate;

    @Enumerated(value = EnumType.STRING)
    private ShoppingCardStatus status = ShoppingCardStatus.DRAFT;

    @JoinColumn(name = "shopping_card_id")
    @OneToMany(
            targetEntity = ShoppingCardEntryEntity.class,
            cascade = CascadeType.ALL
    )
    private List<ShoppingCardEntryEntity> entries = new ArrayList<>();

    @JoinColumn(name = "user_id")
    @ManyToOne(targetEntity = UserEntity.class, optional = false)
    private UserEntity user;

    @ManyToMany(targetEntity = UserEntity.class)
    private List<UserEntity> assignedUsers = new ArrayList<>();
}
