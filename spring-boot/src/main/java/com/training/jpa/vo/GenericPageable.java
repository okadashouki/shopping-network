package com.training.jpa.vo;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.training.jpa.oracle.entity.BeverageGoods;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@ToString
public class GenericPageable {


		// 當前頁碼
		private int currentPageNo;

		// 每頁顯示資料筆數
		private int pageDataSize;

		// 顯示頁數數量
		private int pagesIconSize;

		// 總頁數
		private int totalPages;

		// 總資料筆數
		private long totalDataSize;

		// 起始顯示頁數
		private int startPage;

		// 結束顯示頁數
		private int endPage;
		
		private	List<Integer> pages;
	
}
