package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "password_reset_token")
@Getter
@Setter
public class PasswordResetToken {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_reset_token_seq")
    @SequenceGenerator(sequenceName = "password_reset_token_sequence", allocationSize = 1, name = "password_reset_token_seq")
    private Long id;

    @Column(name = "username")
    private String userName;

    @Column(name = "token")
    private String token;

    @Column(name = "expired_time" ,  columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredTime;
}
