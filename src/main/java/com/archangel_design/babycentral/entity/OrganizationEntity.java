package com.archangel_design.babycentral.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "organizations")
@JsonIgnoreProperties(value = {"id"})
public class OrganizationEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 36)
    private String uuid = UUID.randomUUID().toString();

    private String name;

    private Date creationDate = new Date();

    public Long getId() {
        return id;
    }

    public OrganizationEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public OrganizationEntity setName(String name) {
        this.name = name;
        return this;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public OrganizationEntity setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }
}
