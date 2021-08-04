package com.smartmarket.code.model;

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

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_role_seq")
	@SequenceGenerator(sequenceName = "user_role_sequence", allocationSize = 1, name = "user_role_seq")
	private Long id;

	//@Id la primary key

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "role_id")
	private Long roleId;

	@Column(name = "create_date" ,  columnDefinition= "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	@Column(name = "enabled")
	private Long enabled;

}