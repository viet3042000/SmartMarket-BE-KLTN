package com.example.authserver.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "user_role")
@Getter
@Setter
public class UserRole implements Serializable {

    @Column(name = "id")
    private Long id;

    @Id
    @Column(name = "user_name")
    private String userName;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "create_date" ,  columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "enabled")
    private Integer enabled;

}