package com.training.jpa.vo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.training.jpa.oracle.entity.BeverageGoods;

import lombok.Builder;
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
public class MemberInfo {
	
		@Id	
		@Column(name = "IDENTIFICATION_NO")
		private String identificationNo;	// 身份證字號
		
		
		@Column(name = "CUSTOMER_NAME")
		private String name; // 帳戶名稱
		
		
		@Column(name = "PASSWORD")
		private String cusPassword; // 帳戶密碼
		
		
}
