package com.smartmarket.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements Serializable {

	@Id
	@Column(name = "user_id")
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
//	@SequenceGenerator(sequenceName = "user_id_sequence", allocationSize = 1, name = "user_id_seq")
	private Long id;

	@Column(name = "user_name")
	private String username;

	@Column(name = "password")
	@JsonIgnore
	private String password;

	@Column(name = "user_id_sync")
	private Integer userIdSync;

	@Column(name = "enabled")
	private Integer enabled;

}