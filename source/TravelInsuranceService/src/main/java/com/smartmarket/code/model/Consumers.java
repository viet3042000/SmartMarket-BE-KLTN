package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "consumers")
@Getter
@Setter
public class Consumers implements Serializable {

	@Id
	@Column(name = "consumer_id")
	private String id;

	@Column(name = "created_at", columnDefinition= "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createAt;

}