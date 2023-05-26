package com.training.jpa.dao;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import com.training.jpa.oracle.entity.BeverageGoods;
import com.training.jpa.oracle.entity.OrderCustomer;
import com.training.jpa.vo.GoodsVo;



@Repository
public interface FrontendDao extends JpaRepository<BeverageGoods, Long>{

	Page<BeverageGoods> findAll(Pageable pageable);
	
	@Query("SELECT s FROM BeverageGoods s WHERE s.goodsID IS NOT NULL")
	List<BeverageGoods> queryAllBeverageGoods();
	
	
	@Query("SELECT g FROM BeverageGoods g WHERE g.goodsID IS NOT NULL " +
	        "AND (:searchKeyword IS NULL OR LOWER(g.goodsName) LIKE %:searchKeyword% OR LOWER(g.description) LIKE %:searchKeyword%) " +
	        "AND g.status = 1 " +
	        "ORDER BY g.goodsID DESC")
	Page<BeverageGoods> searchBeverageGoods(@Param("searchKeyword") String searchKeyword, Pageable pageable);


	@Modifying
	@Query("UPDATE BeverageGoods g SET g.quantity = g.quantity - :quantity WHERE g.goodsID = :id")
	int updateGoodsQuantity(@Param("id") Long id, @Param("quantity") int quantity);


	default boolean batchUpdateGoodsQuantity(Map<Long, Integer> quantityMap) {
	    for (Entry<Long, Integer> entry : quantityMap.entrySet()) {
	        int result = updateGoodsQuantity(entry.getKey(), entry.getValue());
	        if (result != 1) { // 若更新結果不是 1，表示更新失敗，回傳 false
	            return false;
	        }
	    }
	    return true; // 若都更新成功，回傳 true
	}

	
}
