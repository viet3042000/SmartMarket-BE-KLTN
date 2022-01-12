//package com.example.authserver.entities;
//
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.*;
//import java.io.Serializable;
//import java.util.Date;
//
//@Entity
//@Table(name = "roles")
//@Getter
//@Setter
//public class Role implements Serializable {
//
//    //    @Id
//    @Column(name = "id")
//    private Long id;
//
//    @Id
//    @Column(name = "role_name")
//    private String roleName;
//
//    @Column(name = "enabled")
//    private Integer enabled;
//
//    @Column(name = "description")
//    private String desc;
//
//    @Column(name = "created_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date createdLogtimestamp;
//
//}