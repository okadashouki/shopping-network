package com.training.jpa.controller;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.training.jpa.dao.BeverageMemberDao;
import com.training.jpa.oracle.entity.BeverageGoods;
import com.training.jpa.oracle.entity.BeverageMember;
import com.training.jpa.service.AuthenticationService;
import com.training.jpa.vo.GoodsOrderVo;
import com.training.jpa.vo.GoodsVo;
import com.training.jpa.vo.MemberInfo;
import com.training.jpa.vo.UpdateCartItemRequest;
import com.training.spring.aop.MemberLoginCheck;

import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = {"http://localhost:3000","http://localhost:8085"}, allowCredentials = "true")
@RestController
@RequestMapping("/ecommerce/MemberController")
public class MemberController {
	
	private static Logger logger = LoggerFactory.getLogger(MemberController.class);
	
	@Autowired
    private AuthenticationService authService;
	 
	@Resource 
	private MemberInfo sessionMemberInfo;
	
	
	@Resource(name = "sessionCartGoods")
	private List<GoodsVo> cartGoods;
	
	@Autowired
	private HttpSession httpSession; 
	
	@Autowired
	private BeverageMemberDao memberDao;
	
	
	
	@ApiOperation(value = "購物網-會員-登入")
	@PostMapping(value = "/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody MemberInfo member) {
		/*
			{
			  "identificationNo": "A124243295",
			  "cusPassword": "123"
			}
			{
			  "identificationNo": "G436565447",
			  "cusPassword": "123"
			}
		 */
		Map<String, Object> responseMap = new HashMap<>();
		logger.info("HttpSession Login:" + httpSession.getId());
		logger.info("Before:" + sessionMemberInfo.toString());		
        // 檢查帳號密碼是否正確
        if (authService.checkLogin(member.getIdentificationNo(), member.getCusPassword())) {
        	//依使用者所輸入的帳戶名稱取得 Member
            MemberInfo memberInfo = memberDao.queryAccountById(member.getIdentificationNo());
            
            responseMap.put("message", "登入成功");
            responseMap.put("memberInfo", memberInfo);
        
         // 設定 session
           
            sessionMemberInfo = memberInfo;
            httpSession.setAttribute("sessionMemberInfo", sessionMemberInfo);
            logger.info("After:" + sessionMemberInfo.toString());
            // 回傳成功訊息與會員資訊
            return ResponseEntity.ok(responseMap);
        } else {
        	MemberInfo memberInfo = null;
        	responseMap.put("message", "登入失敗");
            responseMap.put("memberInfo", memberInfo);
            // 回傳錯誤訊息
            return ResponseEntity.ok(responseMap);
        }
	}
	@ApiOperation(value = "查詢登入")
	@GetMapping(value = "/checkLogin")
	public boolean checkLogin(HttpServletRequest request) {
		MemberInfo sessionMemberInfo = (MemberInfo) httpSession.getAttribute("sessionMemberInfo");
	   
	    return sessionMemberInfo != null;
	}

	
	@ApiOperation(value = "購物網-會員-登出")
	@GetMapping(value = "/logout")
	public ResponseEntity<String> logout() {
		
		
		logger.info("HttpSession logout:" + httpSession.getId());
		 httpSession.removeAttribute("sessionMemberInfo");
		
		return ResponseEntity.ok("Logout success!");
	}
	
//	@MemberLoginCheck //須受登入檢查
	@ApiOperation(value = "商品加入購物車")
	@PostMapping(value = "/addCartGoods")
	public ResponseEntity<List<GoodsVo>> addCartGoods(@RequestBody GoodsVo goodsVo) {
		/*
			{
			  "goodsID": 28,
			  "goodsName": "Java Chip",
			  "description": "暢銷口味之一，以摩卡醬、乳品及可可碎片調製，加上細緻鮮奶油及摩卡醬，濃厚的巧克力風味。",
			  "imageName": "20130813154445805.jpg",
			  "price": 145,
			  "quantity": 17
			}

			{
			  "goodsID": 3,
			  "goodsName": "柳橙檸檬蜂蜜水",
			  "description": "廣受喜愛的蜂蜜水，搭配柳橙與檸檬汁，酸甜的好滋味，尾韻更帶有柑橘清香。",
			  "imageName": "2021110210202761.jpg",
			  "price": 20,
			  "quantity": 16
			}
		 */
		 Long goodsId = goodsVo.getGoodsID();
//		 List<BeverageGoods> good = memberDao.queryBeverageGoods(goodsId);
		 boolean isGoodsFound = false;
		 for (GoodsVo cartItem : cartGoods) {
		        if (cartItem.getGoodsID() == goodsId) {
		            // 如果商品已存在，則更新商品數量
		        	cartGoods.add(goodsVo);
		            isGoodsFound = true;
		            break;
		        }
		    }
		 if (!isGoodsFound) {
		        // 如果商品不存在，則建立一個新的購物車項目
		        GoodsVo cartItem = new GoodsVo();
		        cartItem.setGoodsID(goodsId);
		        cartItem.setGoodsName(goodsVo.getGoodsName());
		        cartItem.setDescription(goodsVo.getDescription());
		        cartItem.setImageName(goodsVo.getImageName());
		        cartItem.setPrice(goodsVo.getPrice());
		        cartItem.setQuantity(goodsVo.getQuantity());
		        cartItem.setBuygoodquantity(goodsVo.getBuygoodquantity() + cartItem.getBuygoodquantity());
		        cartGoods.add(cartItem);
		    }

		 
		return ResponseEntity.ok(cartGoods);
	}
//	@MemberLoginCheck //須受登入檢查
	@ApiOperation(value = "查尋購物車商品")
	@GetMapping(value = "/queryCartGoods")
	public ResponseEntity<List<GoodsVo>> queryCartGoods() {
		 Map<Long, Integer> quantityMap = new HashMap<>(); // 用於記錄商品ID和數量的映射
		    List<GoodsVo> uniqueGoodsList = new ArrayList<>(); // 儲存唯一的商品列表

		    for (GoodsVo goods : cartGoods) {
		        Long goodsID = goods.getGoodsID();
		        // 檢查商品ID是否已經存在於quantityMap中，若存在則累加數量，若不存在則新增映射
		        if (quantityMap.containsKey(goodsID)) {
		            int totalQuantity = quantityMap.get(goodsID) + goods.getBuygoodquantity();
		            quantityMap.put(goodsID, totalQuantity);
		        } else {
		            quantityMap.put(goodsID, goods.getBuygoodquantity());
		            uniqueGoodsList.add(goods); // 將唯一的商品加入列表
		        }
		    }

		    // 更新uniqueGoodsList中的商品數量和最新的庫存和金額資訊
		    for (GoodsVo goods : uniqueGoodsList) {
		        Long goodsID = goods.getGoodsID();
		        int totalQuantity = quantityMap.get(goodsID);
		        // 從資料庫中重新獲取最新的商品資訊
		        List<BeverageGoods> updatedGoods = memberDao.queryBeverageGoods(goodsID);
		        if (updatedGoods != null) {
		            goods.setBuygoodquantity(totalQuantity);
		            goods.setQuantity(updatedGoods.get(0).getQuantity());
		            goods.setPrice(updatedGoods.get(0).getPrice());
		        }
		    }


	    cartGoods = uniqueGoodsList;
	    return ResponseEntity.ok(cartGoods);

	 
	}
//	@MemberLoginCheck //須受登入檢查
	@ApiOperation(value = "清空購物車商品")
	@DeleteMapping(value = "/clearCartGoods")
	public ResponseEntity<List<GoodsVo>> clearCartGoods() {
		cartGoods.clear();

		return ResponseEntity.ok(cartGoods);
	}
	
//	@MemberLoginCheck //須受登入檢查
	@ApiOperation(value = "更新購物車商品數量")
	@PostMapping(value = "/updateItem")
	public ResponseEntity<List<GoodsVo>> updateCartItem(@RequestBody UpdateCartItemRequest request) {
	    Long goodsId = request.getGoodsId();
	    int newQuantity = request.getNewQuantity();

	    for (GoodsVo cartItem : cartGoods) {
	        if (cartItem.getGoodsID() == goodsId) {
	            // 更新商品數量
	            cartItem.setBuygoodquantity(newQuantity);
	            break;
	        }
	    }

	    return ResponseEntity.ok(cartGoods);
	}
	
//	@MemberLoginCheck // 須受登入檢查
	@ApiOperation(value = "刪除購物車商品")
	@DeleteMapping(value = "/removeItem")
	public ResponseEntity<List<GoodsVo>> removeCartItem(@RequestParam("goodsID") Long goodsId) {
	    for (Iterator<GoodsVo> iterator = cartGoods.iterator(); iterator.hasNext();) {
	        GoodsVo cartItem = iterator.next();
	        if (cartItem.getGoodsID() == goodsId) {
	            iterator.remove();
	            break;
	        }
	    }

	    return ResponseEntity.ok(cartGoods);
	}
}
