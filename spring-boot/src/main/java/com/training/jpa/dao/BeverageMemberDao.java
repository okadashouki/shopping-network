package com.training.jpa.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.training.jpa.oracle.entity.BeverageGoods;
import com.training.jpa.vo.MemberInfo;

public interface BeverageMemberDao  extends JpaRepository<BeverageGoods, Long>{
	
	@Query(value ="SELECT * FROM BEVERAGE_GOODS  WHERE GOODS_ID = ?1",nativeQuery = true)
	List<BeverageGoods> queryBeverageGoods(Long goodsId);

	@Query("SELECT s FROM MemberInfo s WHERE s.identificationNo = ?1")
	MemberInfo queryAccountById(String inputID);
	

}
