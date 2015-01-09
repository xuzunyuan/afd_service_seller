package com.afd.seller.dao;

import com.afd.model.seller.SellerLogin;

public interface SellerLoginMapper {
    int deleteByPrimaryKey(Integer sellerLoginId);

    int insert(SellerLogin record);

    int insertSelective(SellerLogin record);

    SellerLogin selectByPrimaryKey(Integer sellerLoginId);

    int updateByPrimaryKeySelective(SellerLogin record);

    int updateByPrimaryKey(SellerLogin record);
}