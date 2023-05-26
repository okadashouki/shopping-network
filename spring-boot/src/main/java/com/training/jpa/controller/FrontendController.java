package com.training.jpa.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.training.jpa.dao.FrontendDao;
import com.training.jpa.oracle.entity.BeverageGoods;
import com.training.jpa.oracle.entity.OrderCustomer;
import com.training.jpa.service.FrontendService;
import com.training.jpa.vo.CheckoutCompleteInfo;
import com.training.jpa.vo.GenericPageable;
import com.training.jpa.vo.GoodsVo;
import com.training.jpa.vo.MemberInfo;

import com.training.jpa.vo.ProductGoodsInfo;
import com.training.spring.aop.MemberLoginCheck;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:8085"}, allowCredentials = "true")
@RequestMapping("/ecommerce/FrontendController")
public class FrontendController {
	
	private static Logger logger = LoggerFactory.getLogger(FrontendController.class);
	
	@Autowired
	private HttpSession httpSession;
	
	@Resource
	private MemberInfo sessionMemberInfo;
	
	@Resource(name = "sessionCartGoods")
	private List<GoodsVo> cartGoods;
	
	@Autowired
	MemberController memberController ;
	
	@Autowired
	private FrontendService frontendService;
	@CrossOrigin
	@ApiOperation(value = "購物網-前臺-查詢商品列表")
	@GetMapping(value = "/queryGoodsData")
	public ResponseEntity<ProductGoodsInfo> queryGoodsData(@RequestParam(required = false) String searchKeyword,
			 @RequestParam int currentPageNo, @RequestParam int pageDataSize, @RequestParam int pagesIconSize) {
	
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
		ProductGoodsInfo goodsDataInfo = frontendService.queryGoodsData(searchKeyword,  pageable,genericPageable);		
		
		
		
		
		return ResponseEntity.ok(goodsDataInfo);
	}
	
	@CrossOrigin
	@ApiOperation(value = "查詢全部商品列表")
	@GetMapping(value = "/queryAllGoodsData")
	public ResponseEntity<List<BeverageGoods>> allGoods(){
		ResponseEntity<List<BeverageGoods>> allGoods = frontendService.queryAllGoods();
		return allGoods;	
	}
	
//	@MemberLoginCheck //須受登入檢查
	@ApiOperation(value = "購物網-前臺-現金結帳購物車商品")
	@PostMapping(value = "/checkoutGoods")
	@Transactional(rollbackOn = Exception.class)
	public ResponseEntity<CheckoutCompleteInfo> checkoutGoods(@RequestParam int inputMoney) {
		MemberInfo sessionMemberInfo = (MemberInfo) httpSession.getAttribute("sessionMemberInfo");
		 

		logger.info("HttpSession checkoutGoods:" + httpSession.getId());
		// 調用查詢購物車商品的方法獲取最新的購物車商品列表
		 ResponseEntity<List<GoodsVo>> queryResponse = memberController.queryCartGoods(); 
		 // 使用最新的購物車商品列表進行後續操作
		    List<GoodsVo> cartGoods = queryResponse.getBody(); 
		    logger.info("CheckoutGoods:" + cartGoods);
		
		
		//把資料傳進Service去做邏輯運算，判斷購買是否成功，庫存是否足夠，算出總金額跟找零金額並回傳
		CheckoutCompleteInfo checkoutCompleteInfo = frontendService.checkoutGoods(sessionMemberInfo, cartGoods, inputMoney);
		
		boolean success = checkoutCompleteInfo.isSuccess();
	    
	    // 扣除資料庫數量的邏輯判斷，購買成功就要扣庫存和製作銷售報表
	    if (success) {
	        // 扣除資料庫數量
	    	boolean updateSuccess = frontendService.upDateGoodsQuantity(checkoutCompleteInfo.getGoodsMap());

	    	List<OrderCustomer> order = frontendService.createOrder(checkoutCompleteInfo.getGoodsMap(),sessionMemberInfo);
	    } 
		
		return ResponseEntity.ok(checkoutCompleteInfo);
	}
	
//	@MemberLoginCheck // 須受登入檢查
	@ApiOperation(value = "購物網-前臺-信用卡結帳購物車商品")
	@PostMapping(value = "/checkoutCreditCardGoods")
	@Transactional(rollbackOn = Exception.class)
	public ResponseEntity<CheckoutCompleteInfo> checkoutGoods() {
	    MemberInfo sessionMemberInfo = (MemberInfo) httpSession.getAttribute("sessionMemberInfo");
	    
	    logger.info("HttpSession checkoutGoods:" + httpSession.getId());
	    
	    // 調用查詢購物車商品的方法獲取最新的購物車商品列表
	    ResponseEntity<List<GoodsVo>> queryResponse = memberController.queryCartGoods();
	    
	    // 使用最新的購物車商品列表進行後續操作
	    List<GoodsVo> cartGoods = queryResponse.getBody();
	    logger.info("CheckoutGoods:" + cartGoods);
	    
	    // 把資料傳進Service去做邏輯運算，計算總金額並回傳
	    CheckoutCompleteInfo checkoutCompleteInfo = frontendService.calculateTotalAmount(sessionMemberInfo,cartGoods);
	    
	    boolean updateSuccess = frontendService.upDateGoodsQuantity(checkoutCompleteInfo.getGoodsMap());

    	List<OrderCustomer> order = frontendService.createOrder(checkoutCompleteInfo.getGoodsMap(),sessionMemberInfo);
	    
	    return ResponseEntity.ok(checkoutCompleteInfo);
	}


}
