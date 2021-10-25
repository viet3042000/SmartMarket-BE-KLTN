package com.smartmarket.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role implements Serializable {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
	@SequenceGenerator(sequenceName = "role_sequence", allocationSize = 1, name = "role_seq")
	private Long id;

	@Column(name = "role_name")
	private String roleName;

	@Column(name = "enabled")
	private int enabled;

	@Column(name = "description")
	private String desc;

	@Column(name = "created_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdLogtimestamp;

}