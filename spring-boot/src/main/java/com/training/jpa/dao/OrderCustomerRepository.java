package com.training.jpa.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.training.jpa.oracle.entity.OrderCustomer;
import com.training.jpa.vo.GenericPageable;
import com.training.jpa.vo.OrderVo;
import com.training.jpa.vo.SalesReportCondition;

@Repository
public interface OrderCustomerRepository extends JpaRepository<OrderCustomer, Long> {
	
	
	@Query(value = "SELECT o.order_id, m.customer_name, o.order_date, g.goods_name, o.goods_buy_price, o.buy_quantity, (o.goods_buy_price * o.buy_quantity) as buy_amount FROM beverage_order o JOIN beverage_member m ON o.customer_id = m.identification_no JOIN beverage_goods g ON o.goods_id = g.goods_id WHERE o.order_date BETWEEN TO_TIMESTAMP(?1, 'yyyy/MM/dd HH24:MI:SS') AND TO_TIMESTAMP(?2, 'yyyy/MM/dd HH24:MI:SS') ORDER BY o.order_id DESC",
		       countQuery = "SELECT COUNT(*) FROM beverage_order WHERE order_date BETWEEN TO_TIMESTAMP(?1, 'yyyy/MM/dd HH24:MI:SS') AND TO_TIMESTAMP(?2, 'yyyy/MM/dd HH24:MI:SS')",
		       nativeQuery = true)
		Page<Object[]> findOrdersWithGoodsAndCustomer(String startDate, String endDate, Pageable pageable);


}

