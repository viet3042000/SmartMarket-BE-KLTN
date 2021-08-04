package com.smartmarket.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role implements Serializable {

	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
	@SequenceGenerator(sequenceName = "role_sequence", allocationSize = 1, name = "role_seq")
	private Long id;

	//@Id la primary key
	@Id
	@Column(name = "role_name")
	private String roleName;

	@Column(name = "enabled")
	private Long enabled;

}