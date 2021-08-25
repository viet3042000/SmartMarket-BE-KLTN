package com.smartmarket.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "travel_insurance")
@Getter
@Setter
public class TravelInsurance implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "state")
    @JsonIgnore
    private String state;
}
