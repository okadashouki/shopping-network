package com.training.jpa.vo;


import java.util.List;

import javax.persistence.Entity;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.training.jpa.oracle.entity.BeverageGoods;
import com.training.jpa.oracle.entity.OrderCustomer;

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
public class ProductGoodsInfo {

    private List<BeverageGoods> goodsList;
    private Pageable pageable;
    private List<GenericPageable> pageList;
    private List<OrderVo> orderList;
    private GenericPageable genericPageable;
 		// 總頁數
 		private int totalPages;

 		// 總資料筆數
 		private long totalDataSize;

 		// 起始顯示頁數
 		private int startPage;

 		// 結束顯示頁數
 		private int endPage;
 		
 		private	List<Integer> pages;
 		private static Pageable buildPageable(Page<?> page) {
 		    return PageRequest.of(page.getNumber(), page.getSize(), page.getSort());
 		}
 		public static ProductGoodsInfo fromPage(Page<BeverageGoods> page) {
 		    List<BeverageGoods> goodsList = page.getContent();
 		    int totalPages = page.getTotalPages();
 		    long totalDataSize = page.getTotalElements();
 		    int startPage = Math.max(1, page.getNumber() - (page.getSize() / 2));
 		    int endPage = Math.min(totalPages, page.getNumber() + (page.getSize() / 2));
 		    
 		    return ProductGoodsInfo.builder()
 		             .goodsList(goodsList)
 		             .pageable(buildPageable(page))
 		             .totalPages(totalPages)
 		             .totalDataSize(totalDataSize)
 		             .startPage(startPage)
 		             .endPage(endPage)
 		             .build();
 		}

// 		public static ProductGoodsInfo orderPage(Page<OrderVo> page) {
// 		    List<OrderVo> orderList = page.getContent();
// 		    int totalPages = page.getTotalPages();
// 		    long totalDataSize = page.getTotalElements();
// 		    int startPage = Math.max(1, page.getNumber() - (page.getSize() / 2));
// 		    int endPage = Math.min(totalPages, page.getNumber() + (page.getSize() / 2));
// 		    
// 		    return ProductGoodsInfo.builder()
// 		             .orderList(orderList)
// 		             .genericPageable
// 		             .build();
// 		}
}
