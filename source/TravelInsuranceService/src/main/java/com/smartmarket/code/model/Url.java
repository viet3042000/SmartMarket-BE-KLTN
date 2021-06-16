package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "urls")
@Getter
@Setter
public class Url implements Serializable {

	@Id
	@Column(name = "url_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "url_name")
	private String urlName;

	@Column(name = "path")
	private String path;

	@Column(name = "is_active")
	private Long isActive;


}