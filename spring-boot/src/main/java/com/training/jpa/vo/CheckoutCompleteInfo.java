package com.training.jpa.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CheckoutCompleteInfo {
	
	private String memberName;
	
	private  ArrayList buyGoodsMsg;
	
	private Map<Long, GoodsVo> goodsMap;
	
	private MemberInfo sessionMemberInfo;
	
	private boolean success;
	
	public CheckoutCompleteInfo(MemberInfo memberInfo) {
		this.memberName = memberInfo.getName();  // 將 MemberInfo 的 name 屬性賦值給 memberName 屬性
	}

	public String getMemberName() {
		return memberName;
	}

}
