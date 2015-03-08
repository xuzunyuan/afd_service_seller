/**
 * Copyright (c)2015-? by www.afd.com. All rights reserved.
 * 
 */
package com.afd.seller.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.afd.model.seller.SellerRetAddress;
import com.afd.seller.dao.SellerRetAddressMapper;
import com.afd.service.seller.ISellerRetAddrService;

/**
 * 卖家退货地址服务
 * 
 * @author xuzunyuan
 * @date 2015年2月28日
 */
@Service("sellerRetAddressService")
public class SellerRetAddressServiceImpl implements ISellerRetAddrService {
	@Autowired
	SellerRetAddressMapper mapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.afd.service.seller.ISellerRetAddrService#getValidAddrListOfSeller
	 * (int)
	 */
	@Override
	public List<SellerRetAddress> getValidAddrListOfSeller(int sellerId) {
		return mapper.selectValidBySellerId(sellerId);
	}

	@Override
	public SellerRetAddress getAddrById(int id) {
		return mapper.selectByPrimaryKey(id);
	}

}
