package com.afd.seller.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.afd.model.seller.SellerRetAddress;

public interface SellerRetAddressMapper {
	public int deleteByPrimaryKey(Integer sRAId);

	public int insert(SellerRetAddress record);

	public int insertSelective(SellerRetAddress record);

	public SellerRetAddress selectByPrimaryKey(Integer sRAId);

	public int updateByPrimaryKeySelective(SellerRetAddress record);

	public int updateByPrimaryKey(SellerRetAddress record);

	// 扩展
	public List<SellerRetAddress> selectValidBySellerId(Integer sellerId);
	
	/**
	 * @param sellerId　卖家Id
	 * @return 获取默认的退货地址
	 */
	public SellerRetAddress getDefaultRetAddressBySellerId(Integer sellerId);
	
	/**
	 * @param isDefault　0:否,1:是
	 * @param sRAId id
	 * @return
	 */
	public int setIsDefault(@Param("isDefault") Boolean isDefault, @Param("sRAId") Integer sRAId);
}