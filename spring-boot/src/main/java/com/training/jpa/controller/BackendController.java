package com.training.jpa.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.training.jpa.dao.BackendDao;
import com.training.jpa.oracle.entity.BeverageGoods;
import com.training.jpa.oracle.entity.OrderCustomer;
import com.training.jpa.service.BackendService;
import com.training.jpa.vo.GenericPageable;
import com.training.jpa.vo.GoodsDataCondition;
import com.training.jpa.vo.GoodsDataCondition.SortEnum;
import com.training.jpa.vo.GoodsDataCondition.StatusEnum;
import com.training.jpa.vo.GoodsDataInfo;
import com.training.jpa.vo.GoodsVo;
import com.training.jpa.vo.PageResult;
import com.training.jpa.vo.ProductGoodsInfo;
import com.training.jpa.vo.SalesReportCondition;
import com.training.spring.aop.MemberLoginCheck;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
public class BackendController {
		
	@Autowired
	private BackendService backendService;
	@Autowired
	private GoodsDataCondition condition;
	@Autowired
	private BackendDao backendDao; 
	
	@CrossOrigin
//	@MemberLoginCheck //須受登入檢查
	@ApiOperation(value = "購物網-後臺-商品新增作業")
	@PostMapping(value = "/createGoods", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> createGoods(@ModelAttribute GoodsVo goodsVo) throws IOException {
		
		// 先查 Dao 有沒有這個檔案，如有則返回錯誤訊息
        if (backendDao.existsByImageName(goodsVo.getImageName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image name already exists");
        }
		BeverageGoods goods = backendService.createGoods(goodsVo);
		
		return ResponseEntity.ok(goods);
	}
	
	@CrossOrigin
	//@MemberLoginCheck //須受登入檢查
	@ApiOperation(value = "購物網-後臺-商品維護作業-更新商品資料")
	@PostMapping(value = "/updateGoods", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<BeverageGoods> updateGoods(@ModelAttribute GoodsVo goodsVo) throws IOException {
		
		BeverageGoods goods = backendService.updateGoods(goodsVo);
		
		return ResponseEntity.ok(goods);
	}
	
	@CrossOrigin
//	@MemberLoginCheck //須受登入檢查
	@ApiOperation(value = "購物網-後臺-查詢銷售報表")
	@GetMapping(value = "/querySalesReport")
	public ResponseEntity<ProductGoodsInfo> querySalesReport( @RequestParam int currentPageNo, @RequestParam int pageDataSize, @RequestParam int pagesIconSize,
			@RequestParam String startDate,@RequestParam String endDate) throws IOException {
		

		
		GenericPageable genericPageable = GenericPageable.builder()
				.currentPageNo(currentPageNo)
				.pageDataSize(pageDataSize)
				.pagesIconSize(pagesIconSize)
				.totalPages(0)
				.totalDataSize(0)
				.pages(null)
				.build();
		int PageNo = genericPageable.getCurrentPageNo();
	    int DataSize = genericPageable.getPageDataSize();
	    Pageable pageable = PageRequest.of(PageNo-1, DataSize);
	    ProductGoodsInfo reportDataInfo = backendService.querySalesReport(pageable,genericPageable,startDate,endDate);
		
		return ResponseEntity.ok(reportDataInfo);
	}
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "sortType", value = "排序方式，DESC為價格高到低，ASC為價格低到高", dataType = "int", allowableValues = "ASC,DESC", paramType = "query"),
	    @ApiImplicitParam(name = "status", value = "上架狀態", dataType = "string", allowableValues = "OFF_SALE,ON_SALE", paramType = "query",defaultValue = "ON_SALE")
	})

	@CrossOrigin
//	@MemberLoginCheck //須受登入檢查
	@ApiOperation(value = "購物網-後臺-查詢商品")
	@GetMapping(value = "/queryGoods")
	public ResponseEntity<PageResult<BeverageGoods>> queryGoods(  
			@RequestParam( required = false) Long goodsID,
		    @RequestParam( required = false) String goodsName,
		    @RequestParam( required = false) Integer minPrice,
		    @RequestParam( required = false) Integer maxPrice,
		    @RequestParam( required = false) SortEnum sort,
		    @RequestParam(required = false) Integer goodsQuantity,
		    @RequestParam StatusEnum status,
		    @RequestParam Integer currentPageNo,
		    @RequestParam Integer pageDataSize,
		    @RequestParam Integer pagesIconSize) throws IOException {
		
	

		GenericPageable genericPageable = GenericPageable.builder()
				.currentPageNo(currentPageNo)
				.pageDataSize(pageDataSize)
				.pagesIconSize(pagesIconSize)
				.totalDataSize(0)
				.pages(null)
				.build();
		GoodsDataCondition condition = GoodsDataCondition.builder()
				.goodsID(goodsID)
				.goodsName("".equals(goodsName)?null:goodsName)
				.minPrice(minPrice == null ? 0 : minPrice)
				.maxPrice(maxPrice == null ? Integer.MAX_VALUE : maxPrice)
				.sort(sort)
				.goodsQuantity(goodsQuantity == null ? 0 : goodsQuantity)
				.status(status)
				.build();
		ResponseEntity<PageResult<BeverageGoods>> goods = backendService.queryGoods(genericPageable,condition);
		
		
		return goods;
	}
	
	
	
}
