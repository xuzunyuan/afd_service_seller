package com.afd.seller.dao;

import com.afd.model.seller.SellerReceipt;

public interface SellerReceiptMapper {
    int deleteByPrimaryKey(Integer receiptId);

    int insert(SellerReceipt record);

    int insertSelective(SellerReceipt record);

    SellerReceipt selectByPrimaryKey(Integer receiptId);

    int updateByPrimaryKeySelective(SellerReceipt record);

    int updateByPrimaryKey(SellerReceipt record);

	SellerReceipt getSellerReceiptBySellerId(int sellerId);
}