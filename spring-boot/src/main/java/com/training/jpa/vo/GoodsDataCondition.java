package com.training.jpa.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "商品數據查詢條件")
public class GoodsDataCondition {

	private Long goodsID;
	private String goodsName;
	private int minPrice;
	private int maxPrice;
	 @ApiModelProperty(value = "排序方式", allowableValues = "ASC, DESC", example = "ASC")
	    private SortEnum sort;
	private int goodsQuantity;
	  @ApiModelProperty(value = "狀態", allowableValues = "ON_SALE, OFF_SALE", example = "ON_SALE")
	    private StatusEnum status;
	
	  public enum SortEnum {
        ASC,
        DESC,
    }
	  public enum StatusEnum {
	        ON_SALE, 
	        OFF_SALE
	    }

    public SortEnum getSort() {
        return sort;
    }

    public void setSort(SortEnum sort) {
        this.sort = sort;
    }
    
    public StatusEnum getstatus() {
        return status;
    }
    
    public void setstatus(StatusEnum status) {
        this.status = status;
    }
}
