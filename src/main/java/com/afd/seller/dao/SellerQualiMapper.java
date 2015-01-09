package com.afd.seller.dao;

import com.afd.model.seller.SellerQuali;

public interface SellerQualiMapper {
    int deleteByPrimaryKey(Integer sellerQualiId);

    int insert(SellerQuali record);

    int insertSelective(SellerQuali record);

    SellerQuali selectByPrimaryKey(Integer sellerQualiId);

    int updateByPrimaryKeySelective(SellerQuali record);

    int updateByPrimaryKey(SellerQuali record);
}