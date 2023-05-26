package com.training.jpa.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.training.jpa.oracle.entity.BeverageGoods;

@Repository
public interface BackendDao extends JpaRepository<BeverageGoods, Long>{

	 @Query("SELECT g FROM BeverageGoods g WHERE g.goodsID = :goodsID")
	    Optional<BeverageGoods> findById(@Param("goodsID") long goodsID);

	boolean existsByImageName(String imageName);

	
	
	
}
