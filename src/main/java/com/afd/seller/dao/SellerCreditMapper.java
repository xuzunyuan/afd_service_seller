package com.afd.seller.dao;

import com.afd.model.seller.SellerCredit;

public interface SellerCreditMapper {
	int deleteByPrimaryKey(Integer sellerCreditId);

	int insert(SellerCredit record);

	int insertSelective(SellerCredit record);

	SellerCredit selectByPrimaryKey(Integer sellerCreditId);

	int updateByPrimaryKeySelective(SellerCredit record);

	int updateByPrimaryKey(SellerCredit record);

}