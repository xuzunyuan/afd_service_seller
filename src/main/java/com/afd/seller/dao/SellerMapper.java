package com.afd.seller.dao;

import com.afd.model.seller.Seller;

public interface SellerMapper {
	int deleteByPrimaryKey(Integer sellerId);

	int insert(Seller record);

	int insertSelective(Seller record);

	Seller selectByPrimaryKey(Integer sellerId);

	int updateByPrimaryKeySelective(Seller record);

	int updateByPrimaryKey(Seller record);

	// 扩展
	Seller selectByLoginId(Integer loginId);
}