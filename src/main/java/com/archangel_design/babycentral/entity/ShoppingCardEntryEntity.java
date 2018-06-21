package com.archangel_design.babycentral.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "shopping_card_entries")
@Getter
@Setter
@JsonIgnoreProperties(value = {"id"})
public class ShoppingCardEntryEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 36)
    private String uuid = UUID.randomUUID().toString();

    @ManyToOne(targetEntity = ShoppingCardEntity.class)
    @JoinColumn(name = "shopping_card_id")
    private ShoppingCardEntity shoppingCardEntity;

    @CreationTimestamp
    @Column(updatable=false)
    private Date creationDate;

    @Column(length = 120)
    private String articleName;

    private Integer quantity;

    private Boolean isPurchased = false;

}
