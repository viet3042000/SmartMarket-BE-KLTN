package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "consumers")
@Getter
@Setter
public class Consumers implements Serializable {

	@Id
	@Column(name = "consumerId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "created_at")
	private String createAt;


}