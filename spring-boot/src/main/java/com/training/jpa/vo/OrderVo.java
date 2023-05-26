package com.training.jpa.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;

import lombok.Data;

@Data
public class OrderVo {

	
	@Column(name = "ORDER_ID")
	private BigDecimal orderID;
	// 顧客姓名
	@Column(name = "CUSTOMER_NAME")
	private String customerName;
	// 購買日期
	@Column(name = "ORDER_DATE")
	private String orderDate;
	// 飲料名稱
	@Column(name = "GOODS_NAME")
	private String goodsName;
	// 商品金額(購買單價)
	@Column(name = "GOODS_BUY_PRICE")
	private BigDecimal goodsBuyPrice;
	// 購買數量
	@Column(name = "BUY_QUANTITY")
	private BigDecimal buyQuantity;	
	// 購買金額
	@Column(name = "buyAmount")
	private BigDecimal buyAmount;
	
	
	
	
}
