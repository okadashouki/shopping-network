package com.training.jpa.vo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@ToString
public class GoodsDataInfo {

	private Long goodsID;
	private String goodsName;
	private int minPrice;
	private int maxPrice;
	private String sort; //價格排序
	private int goodsQuantity;
	private String status;
	
	
}
