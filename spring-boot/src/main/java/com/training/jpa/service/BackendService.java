package com.training.jpa.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.training.jpa.dao.BackendDao;
import com.training.jpa.dao.OrderCustomerRepository;
import com.training.jpa.oracle.entity.BeverageGoods;
import com.training.jpa.oracle.entity.BeverageGoods.BeverageGoodsBuilder;
import com.training.jpa.oracle.entity.OrderCustomer;
import com.training.jpa.vo.GenericPageable;
import com.training.jpa.vo.GoodsDataCondition;
import com.training.jpa.vo.GoodsDataInfo;
import com.training.jpa.vo.GoodsVo;
import com.training.jpa.vo.OrderVo;
import com.training.jpa.vo.PageResult;
import com.training.jpa.vo.ProductGoodsInfo;
import com.training.jpa.vo.SalesReportCondition;



@Service
public class BackendService {
	
	@Autowired
	private BackendDao backendDao; 
	
	@Autowired
	private OrderCustomerRepository orderCustomerRepository;
	
	@PersistenceContext
    private EntityManager entityManager;
	
	private static Logger logger = LoggerFactory.getLogger(BackendService.class);

	@Transactional(rollbackOn = Exception.class)
	public BeverageGoods createGoods(GoodsVo goodsVo) throws IOException {
		

		BeverageGoods beverageGoods = BeverageGoods.builder()
				.description(goodsVo.getDescription())
				.goodsName(goodsVo.getGoodsName())
				.price(goodsVo.getPrice())
				.quantity(goodsVo.getQuantity())
				.imageName(goodsVo.getImageName())
				.status(goodsVo.getStatus())
				.build();
				// 複制檔案
				MultipartFile file = goodsVo.getFile();
				// 前端傳入檔案名稱
				String fileName = goodsVo.getImageName();
				Files.copy(file.getInputStream(), Paths.get("/home/VendingMachine/DrinksImage").resolve(fileName));
		return backendDao.save(beverageGoods);
	}
	
	@Transactional(rollbackOn = Exception.class)
	public BeverageGoods updateGoods(GoodsVo goodsVo) throws IOException {
		Optional<BeverageGoods>optGoods = backendDao.findById(goodsVo.getGoodsID());
		BeverageGoods updateGoods = null;
		if(optGoods.isPresent()) {
			updateGoods=optGoods.get();
			if((goodsVo.getGoodsName()!=null)){
				updateGoods.setGoodsName(goodsVo.getGoodsName());
			}
			if((goodsVo.getDescription()!=null)){
				updateGoods.setDescription(goodsVo.getDescription());
			}
			if(goodsVo.getPrice()!=0){
				updateGoods.setPrice(goodsVo.getPrice());	
			}
			if(goodsVo.getQuantity()!=0){
				updateGoods.setQuantity(goodsVo.getQuantity());	
			}
			if((goodsVo.getStatus()!=null)){
				updateGoods.setStatus(goodsVo.getStatus());
			}
			
			MultipartFile file = goodsVo.getFile();
			if(file != null && file.getSize()>0) {
				//刪除舊圖檔
				Files.delete(Paths.get("/home/VendingMachine/DrinksImage").resolve(updateGoods.getImageName()));
				//複製商品新圖檔
				Files.copy(file.getInputStream(), Paths.get("/home/VendingMachine/DrinksImage").resolve(file.getOriginalFilename()));
				updateGoods.setImageName(file.getOriginalFilename());
			}
		}
		return updateGoods;
	}


	
	public ProductGoodsInfo querySalesReport(Pageable pageable, GenericPageable genericPageable,String startDate ,String endDate) {
	
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		LocalDateTime startLocalDateTime = LocalDate.parse(startDate, inputFormatter).atStartOfDay();
		LocalDateTime endLocalDateTime = LocalDate.parse(endDate, inputFormatter).atTime(LocalTime.MAX);

		String formattedStartDate = startLocalDateTime.format(outputFormatter);
		String formattedEndDate = endLocalDateTime.format(outputFormatter);
		
		Page<Object[]> tuples = orderCustomerRepository.findOrdersWithGoodsAndCustomer(formattedStartDate, formattedEndDate,pageable);
		List<OrderVo> orderVos = new ArrayList<>();
		
		for (Object[] tuple : tuples) {
		    OrderVo orderVo = buildOrderVo(tuple);
		    orderVos.add(orderVo);
		}
		 
		    int totalPages = tuples.getTotalPages();//總頁數
		    long totalDataSize = tuples.getTotalElements();//總資料數
		    int pageNo = tuples.getNumber()+1;//當前頁數
		    int displayPages = genericPageable.getPagesIconSize();//需要顯示的頁碼數
		    
		  
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
					.orderList(orderVos)
					.genericPageable(genericPageable)
					.build();
	}

	

	public ResponseEntity<PageResult<BeverageGoods>> queryGoods(GenericPageable genericPageable, GoodsDataCondition condition) {
		//創建 CriteriaBuilder 物件，用於構建查詢條件
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    //創建 CriteriaQuery 物件，指定查詢的返回類型
		CriteriaQuery<BeverageGoods> cq = cb.createQuery(BeverageGoods.class);
	    //創建 Root 物件，指定查詢的根實體
		Root<BeverageGoods> root = cq.from(BeverageGoods.class);
	    List<Predicate> predicates = new ArrayList<>();
	    if (condition.getGoodsID() != null) {
	        predicates.add(cb.equal(root.get("goodsID"), condition.getGoodsID()));
	    }
	    if (condition.getGoodsName() != null) {
	        predicates.add(cb.like(cb.lower(root.get("goodsName")), "%" + condition.getGoodsName().toLowerCase() + "%"));
	    }
	    if (condition.getMinPrice() != 0) {
	        predicates.add(cb.greaterThanOrEqualTo(root.get("price"), condition.getMinPrice()));
	    }
	    if (condition.getMaxPrice() != 0) {
	        predicates.add(cb.lessThanOrEqualTo(root.get("price"), condition.getMaxPrice()));
	    }
	    if (condition.getGoodsQuantity() > 0) {
	        predicates.add(cb.greaterThanOrEqualTo(root.get("quantity"), condition.getGoodsQuantity()));
	    }
	    if (condition.getstatus() != null) {
	        if (condition.getstatus() == GoodsDataCondition.StatusEnum.ON_SALE) {
	            predicates.add(cb.equal(root.get("status"), "1"));
	        } else if (condition.getstatus() == GoodsDataCondition.StatusEnum.OFF_SALE) {
	            predicates.add(cb.equal(root.get("status"), "0"));
	        }
	    }
	 // 排序
	    if (condition.getSort() != null) {
	        if (condition.getSort() == GoodsDataCondition.SortEnum.ASC) {
	            cq.orderBy(cb.asc(root.get("price")));
	        } else if (condition.getSort() == GoodsDataCondition.SortEnum.DESC) {
	            cq.orderBy(cb.desc(root.get("price")));
	        }
	    }
	   //設置查詢的條件，將 predicates 列表轉換為 Predicate 數組並設置到查詢中。
	    cq.where(predicates.toArray(new Predicate[0]));
	    //創建 TypedQuery 物件，使用 EntityManager 執行查詢
	    TypedQuery<BeverageGoods> query = entityManager.createQuery(cq);
	    
	    //創建另一個查詢用於計算符合條件的總數量
	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	    countQuery.select(cb.count(countQuery.from(BeverageGoods.class)));
	    countQuery.where(predicates.toArray(new Predicate[0]));
	   
	    Long totalDataSize = entityManager.createQuery(countQuery).getSingleResult();
	    int totalPages = (int) Math.ceil((double) totalDataSize / genericPageable.getPageDataSize());
	    int displayPages = genericPageable.getPagesIconSize();
	    int pageNo = genericPageable.getCurrentPageNo();
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
	    query.setFirstResult((genericPageable.getCurrentPageNo() - 1) * genericPageable.getPageDataSize());
	    query.setMaxResults(genericPageable.getPageDataSize());
	    List<BeverageGoods> results = query.getResultList();
	   //創建一個 PageResult 物件，將結果列表和 genericPageable 封裝起來
	    PageResult<BeverageGoods> pageResult = new PageResult<>(results, genericPageable);
	   
	    return ResponseEntity.ok(pageResult);
	}

	private OrderVo buildOrderVo(Object[] tuple) {
	    OrderVo orderVo = new OrderVo();
	    orderVo.setOrderID((BigDecimal) tuple[0]);
	    orderVo.setCustomerName((String) tuple[1]);
	    orderVo.setOrderDate(tuple[2].toString());
	    orderVo.setGoodsName((String) tuple[3]);
	    orderVo.setGoodsBuyPrice((BigDecimal) tuple[4]);
	    orderVo.setBuyQuantity((BigDecimal) tuple[5]);
	    orderVo.setBuyAmount((BigDecimal) tuple[6]);
	    return orderVo;
	}

	
	
	
}
