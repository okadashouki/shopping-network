package com.training.jpa.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.training.jpa.oracle.entity.OrderCustomer;
import com.training.jpa.dao.FrontendDao;
import com.training.jpa.dao.OrderCustomerRepository;
import com.training.jpa.oracle.entity.BeverageGoods;
import com.training.jpa.vo.CheckoutCompleteInfo;
import com.training.jpa.vo.GenericPageable;
import com.training.jpa.vo.GoodsVo;
import com.training.jpa.vo.MemberInfo;
import com.training.jpa.vo.ProductGoodsInfo;

@Service
public class FrontendService {
	
	@Autowired
	private FrontendDao frontendDao;
	@Autowired
	private HttpSession httpSession;
	@Autowired
	private OrderCustomerRepository orderCustomerRepository;
	@Resource
	private MemberInfo sessionMemberInfo;
//	currentPageNo 當前頁碼
//	pageDataSize 頁碼資料數
//	pagesIconSize 顯示頁數
	 public void saveAll(List<OrderCustomer> orderCustomers) {
	        orderCustomerRepository.saveAll(orderCustomers);
	    }

	public ProductGoodsInfo queryGoodsData(String searchKeyword, Pageable pageable,GenericPageable genericPageable) {
		List<BeverageGoods> goodsList = new ArrayList<>();
	    Page<BeverageGoods> response = frontendDao.searchBeverageGoods(searchKeyword, pageable);
	    goodsList.addAll(response.getContent());
	    // 取得查詢結果中的商品列表
	    Pageable page = PageRequest.of(response.getNumber()+1, response.getSize());
	    
	    
	 
	    int totalPages = response.getTotalPages();//總頁數
	    long totalDataSize = response.getTotalElements();//總資料數
	    int pageNo = response.getNumber();//當前頁數
	    int displayPages =genericPageable.getPagesIconSize();//需要顯示的頁碼數
	    
	  
	    int startPage, endPage;
	    if (totalPages <= displayPages) { // 如果總頁數小於等於需要顯示的頁碼數
	        startPage = 1;
	        endPage = totalPages;
	    } else { // 如果總頁數大於需要顯示的頁碼數
	        startPage = Math.max(1,pageNo - (displayPages - 1) / 2);
	        endPage = Math.min(totalPages, startPage + displayPages - 1);
	        if (endPage - startPage < displayPages - 1) { // 如果起始頁和結束頁之差小於顯示頁碼數-1
	            startPage = Math.max(1, endPage - displayPages + 1);
	        }
	    }
	    List<Integer> pages = new ArrayList<>();
	    for (int i = startPage; i <= endPage; i++) {
	        pages.add(i);
	    }
	    genericPageable.setTotalDataSize(totalDataSize);
	    genericPageable.setTotalPages(totalPages);
	    genericPageable.setPages(pages);
	    genericPageable.setStartPage(startPage);
	    genericPageable.setEndPage(endPage);
	 // 將商品列表及分頁資訊封裝到 ProductGoodsInfo 物件中並返回
		return  ProductGoodsInfo.builder()
				.goodsList(goodsList)
				.genericPageable(genericPageable)
				.build();
	}

	
	public ResponseEntity<List<BeverageGoods>> queryAllGoods() {
		 List<BeverageGoods> response = frontendDao.queryAllBeverageGoods();
		    return ResponseEntity.ok(response);
	}

	@Transactional(rollbackOn = Exception.class)
	public CheckoutCompleteInfo checkoutGoods(MemberInfo sessionMemberInfo,
			List<GoodsVo> cartGoods,int inputMoney) {
		
		ArrayList<String> buyGoodsMsg = new ArrayList<String>();
		
		// 建立一個HashMap來儲存商品，Key為商品編號，Value為商品對應的GoodsVo物件
		Map<Long, GoodsVo> goodsMap = new HashMap<>();
		// 將購物車中的商品一個一個取出來進行處理
		for (GoodsVo cartGood : cartGoods) {

		        goodsMap.put(cartGood.getGoodsID(), cartGood);

		}
		// 遍歷 Map 中的商品，計算總價格
		int totalPrice = 0;
		for (GoodsVo goods :cartGoods) {
		int price = goods.getPrice() * goods.getBuygoodquantity(); // 計算該商品的價格
		totalPrice += price; // 將該商品價格累加到總價格中
		}
		//判斷是否購買成功
		boolean buyGood = (inputMoney >= totalPrice);
		 if (buyGood == false) {

				buyGoodsMsg.add("投入金額:" + inputMoney);
				buyGoodsMsg.add("購買金額:" + totalPrice);
				buyGoodsMsg.add("找零金額:" + inputMoney);
				buyGoodsMsg.add("----購買失敗----");
				
				return CheckoutCompleteInfo.builder()
						 .memberName(sessionMemberInfo.getName())
						 .buyGoodsMsg(buyGoodsMsg)
						 .success(false)
						 .build();
			}else {

				buyGoodsMsg.add("投入金額:" + inputMoney);
				buyGoodsMsg.add("購買金額:" + totalPrice);
				buyGoodsMsg.add("找零金額:" + (inputMoney - totalPrice));
				
				return CheckoutCompleteInfo.builder()
						 .buyGoodsMsg(buyGoodsMsg)
						 .goodsMap(goodsMap)
						 .memberName(sessionMemberInfo.getName())
						 .success(true)
						 .build();
			}
	 }

	@Transactional(rollbackOn = Exception.class)
	public boolean upDateGoodsQuantity(Map<Long, GoodsVo> goodsMap) {
	    Map<Long, Integer> quantityMap = goodsMap.entrySet().stream()
	        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getBuygoodquantity()));
	    return frontendDao.batchUpdateGoodsQuantity(quantityMap);
	}

	@Transactional(rollbackOn = Exception.class)
	public List<OrderCustomer> createOrder(Map<Long, GoodsVo> goodsMap, MemberInfo sessionMemberInfo) {
		List<OrderCustomer> orderCustomers = new ArrayList<>();
		goodsMap.entrySet().stream().forEach(entry -> {
		    Long goodsId = entry.getKey();
		    GoodsVo goodsVo = entry.getValue();
		   
		    OrderCustomer orderCustomer = OrderCustomer.builder()
					.orderDate(LocalDateTime.now())
					.customerID(sessionMemberInfo.getIdentificationNo())
					.goodsID(goodsId)
					.goodsBuyPrice(goodsVo.getPrice())
					.buyQuantity(goodsVo.getBuygoodquantity())
					.build();
		    orderCustomers.add(orderCustomer);
		});
		 // 使用saveAll方法一次性儲存多筆訂單資料
		 List<OrderCustomer> savedOrderCustomers = orderCustomerRepository.saveAll(orderCustomers);

		    return savedOrderCustomers;
		
	}

	public CheckoutCompleteInfo calculateTotalAmount(MemberInfo sessionMemberInfo, List<GoodsVo> cartGoods) {
		
		ArrayList<String> buyGoodsMsg = new ArrayList<String>();
		// 建立一個HashMap來儲存商品，Key為商品編號，Value為商品對應的GoodsVo物件
				Map<Long, GoodsVo> goodsMap = new HashMap<>();
				// 將購物車中的商品一個一個取出來進行處理
				for (GoodsVo cartGood : cartGoods) {

				        goodsMap.put(cartGood.getGoodsID(), cartGood);

				}
				// 遍歷 Map 中的商品，計算總價格
				int totalPrice = 0;
				for (GoodsVo goods :cartGoods) {
				int price = goods.getPrice() * goods.getBuygoodquantity(); // 計算該商品的價格
				totalPrice += price; // 將該商品價格累加到總價格中
				}
				buyGoodsMsg.add("購買金額:" + totalPrice);

				
				return CheckoutCompleteInfo.builder()
						 .buyGoodsMsg(buyGoodsMsg)
						 .goodsMap(goodsMap)
						 .memberName(sessionMemberInfo.getName())
						 .success(true)
						 .build();
	}

}
