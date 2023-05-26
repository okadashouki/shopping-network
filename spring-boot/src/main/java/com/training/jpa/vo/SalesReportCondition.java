package com.training.jpa.vo;

import com.training.jpa.vo.GoodsDataCondition.SortEnum;
import com.training.jpa.vo.GoodsDataCondition.StatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesReportCondition {
	
	private String startDate;
	private String endDate;

}
