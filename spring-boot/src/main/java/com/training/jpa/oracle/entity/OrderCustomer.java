package com.training.jpa.oracle.entity;

import java.time.LocalDateTime;

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
@Table(name = "BEVERAGE_ORDER")
public class OrderCustomer {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BEVERAGE_ORDER_SEQ_GEN")
    @SequenceGenerator(name = "BEVERAGE_ORDER_SEQ_GEN", sequenceName = "BEVERAGE_ORDER_SEQ", allocationSize = 1)
	@Column(name = "ORDER_ID")
	private long orderID;
	
	@Column(name = "ORDER_DATE")
	private LocalDateTime orderDate;

	@Column(name = "CUSTOMER_ID")
	private String customerID;

	@Column(name = "GOODS_ID")
	private long goodsID;

	@Column(name = "GOODS_BUY_PRICE")
	private long goodsBuyPrice;

	@Column(name = "BUY_QUANTITY")
	private long buyQuantity;

}
