package com.training.jpa.vo;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class GoodsVo {
	
	private Long goodsID;
	
	private String goodsName;
	
	private String description;
	
	private int price;
	
	private int quantity;
	
	private MultipartFile file;
	
	private String imageName;
	
	private String status;
	
	private int buygoodquantity;
	
	
}
