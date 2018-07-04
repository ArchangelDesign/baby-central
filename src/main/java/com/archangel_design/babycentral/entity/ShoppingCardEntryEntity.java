package com.archangel_design.babycentral.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@JsonIgnoreProperties(value = {"id"})
@Setter
@Table(name = "shopping_card_entries")
public class ShoppingCardEntryEntity {

    @GeneratedValue
    @Id
    private Long id;

    @Column(length = 36)
    private String uuid = UUID.randomUUID().toString();

    @Column(updatable = false)
    @CreationTimestamp
    private Date creationDate;

    @Column(length = 120)
    private String articleName;

    private Integer quantity;

    private Boolean isPurchased = false;
}
