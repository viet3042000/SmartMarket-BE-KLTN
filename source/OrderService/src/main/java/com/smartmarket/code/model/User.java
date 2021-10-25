package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements Serializable {

//	@Id
	@Column(name = "id")
	private Long id;

	@Id
	@Column(name = "user_name")
	private String userName;

	@Column(name = "user_password")
	private String password;

	@Column(name = "enabled")
	private Integer enabled;

	@Column(name = "provider")
	private String provider;

	@Column(name = "email")
	private String email;

}