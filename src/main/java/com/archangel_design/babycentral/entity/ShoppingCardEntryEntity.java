package com.archangel_design.babycentral.entity;

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
public class ShoppingCardEntryEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 36)
    private String uuid = UUID.randomUUID().toString();

    @CreationTimestamp
    @Column(updatable=false)
    private Date creationDate;

    @Column(length = 120)
    private String articleName;

    private Integer quantity;

    private Boolean isPurchased = false;

}
