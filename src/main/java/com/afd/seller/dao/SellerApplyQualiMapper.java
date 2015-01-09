package com.afd.seller.dao;

import com.afd.model.seller.SellerApplyQuali;

public interface SellerApplyQualiMapper {
    int deleteByPrimaryKey(Integer applyQualiId);

    int insert(SellerApplyQuali record);

    int insertSelective(SellerApplyQuali record);

    SellerApplyQuali selectByPrimaryKey(Integer applyQualiId);

    int updateByPrimaryKeySelective(SellerApplyQuali record);

    int updateByPrimaryKey(SellerApplyQuali record);
}