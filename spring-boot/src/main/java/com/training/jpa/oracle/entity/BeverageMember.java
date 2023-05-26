package com.training.jpa.oracle.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
@ToString
@Entity
@Table(name = "BEVERAGE_MEMBER")
public class BeverageMember {

	
		@Id	
		@Column(name = "IDENTIFICATION_NO")
		private String id;	
	
		@Column(name = "CUSTOMER_NAME")
		private String name;
		
		@Column(name = "PASSWORD")
		private String pwd;
	
}
