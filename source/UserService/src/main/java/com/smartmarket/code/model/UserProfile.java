package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserProfile implements Serializable {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
	@SequenceGenerator(sequenceName = "user_id_sequence", allocationSize = 1, name = "user_id_seq")
	private Long id;

	//@Id la primary key
	@Column(name = "user_name")
	private String username;

	@Column(name = "email")
	private String email;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "address")
	private String address;

	@Column(name = "gender")
	private Long gender;

	@Column(name = "identify_number")
	private String identifyNumber;

	@Column(name = "birth_date")
	private String birth_date;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "create_date")
	private String createDate;

	@Column(name = "enabled")
	private Long enabled;

}