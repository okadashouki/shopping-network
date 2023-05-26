package com.training.jpa.oracle.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
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
@Table(name = "BEVERAGE_GOODS")
public class BeverageGoods {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BEVERAGE_GOODS_SEQ_GEN")
    @SequenceGenerator(name = "BEVERAGE_GOODS_SEQ_GEN", sequenceName = "BEVERAGE_GOODS_SEQ", allocationSize = 1)
	@Column(name = "GOODS_ID")
	private Long goodsID;
	
	@Column(name = "GOODS_NAME")
	private String goodsName;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name = "PRICE")
	private int price;
	
	@Column(name = "QUANTITY")
	private int quantity;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "IMAGE_NAME")
	private String imageName;
	
	

	
}
