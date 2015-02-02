package com.afd.seller.dao;

import com.afd.model.seller.SellerApply;

public interface SellerApplyMapper {
	int deleteByPrimaryKey(Integer appId);

	int insert(SellerApply record);

	int insertSelective(SellerApply record);

	SellerApply selectByPrimaryKey(Integer appId);

	int updateByPrimaryKeySelective(SellerApply record);

	int updateByPrimaryKey(SellerApply record);

}