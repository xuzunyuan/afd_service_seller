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
	
	@Override
	public int deleteSellerRetAddressById(Integer sRAId) {
		return this.mapper.deleteByPrimaryKey(sRAId);
	}

	@Override
	public int insertSellerRetAddress(SellerRetAddress retAddress) {
		//取消原默认值
		if(retAddress.getIsDefault()){
			this.cancelDefalutAddress(retAddress.getSellerId());
		}
		
		return this.mapper.insert(retAddress);
	}

	@Override
	public int updateSellerRetAddressById(SellerRetAddress retAddress) {
		//取消原默认值
		if(retAddress.getIsDefault()){
			this.cancelDefalutAddress(retAddress.getSellerId());
		}
				
		return this.mapper.updateByPrimaryKeySelective(retAddress);
	}

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

	@Override
	public int setIsDefault(Integer sRAId, Integer sellerId) {
		//取消原默认值
		this.cancelDefalutAddress(sellerId);
		
		return this.mapper.setIsDefault(true, sRAId);
	}

	/**取消原有默认收货地址
	 * @param sellerId
	 */
	private void cancelDefalutAddress(Integer sellerId){
		SellerRetAddress dra = this.mapper.getDefaultRetAddressBySellerId(sellerId);
		
		//取消原默认值
		if(dra != null){
			this.mapper.setIsDefault(false, dra.getsRAId());
		}
	}
}
