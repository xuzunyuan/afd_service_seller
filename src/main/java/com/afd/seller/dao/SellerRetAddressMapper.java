package com.afd.seller.dao;

import java.util.List;

import com.afd.model.seller.SellerRetAddress;

public interface SellerRetAddressMapper {
	int deleteByPrimaryKey(Integer sRAId);

	int insert(SellerRetAddress record);

	int insertSelective(SellerRetAddress record);

	SellerRetAddress selectByPrimaryKey(Integer sRAId);

	int updateByPrimaryKeySelective(SellerRetAddress record);

	int updateByPrimaryKey(SellerRetAddress record);

	// 扩展
	List<SellerRetAddress> selectValidBySellerId(Integer sellerId);
}