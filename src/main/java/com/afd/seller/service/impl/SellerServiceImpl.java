/**
 * Copyright (c)2013-2014 by www.afd.com. All rights reserved.
 * 
 */
package com.afd.seller.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.afd.model.seller.Seller;
import com.afd.seller.dao.SellerMapper;
import com.afd.service.seller.ISellerService;

/**
 * 卖家服务
 * 
 * @author xuzunyuan
 * @date 2013年12月26日
 */

@Service("sellerService")
public class SellerServiceImpl implements ISellerService {
	@Autowired
	private SellerMapper sellerMapper;

	@Override
	public Seller getSellerByLoginId(int loginId) {
		return sellerMapper.selectByLoginId(loginId);
	}

	@Override
	public Seller getSellerById(int sellerId) {
		return sellerMapper.selectByPrimaryKey(sellerId);
	}

	@Override
	public int updateSeller(Seller seller) {
		return sellerMapper.updateByPrimaryKeySelective(seller);
	}

}