package com.afd.seller.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.afd.model.seller.Seller;

public interface SellerMapper {
	int deleteByPrimaryKey(Integer sellerId);

	int insert(Seller record);

	int insertSelective(Seller record);

	Seller selectByPrimaryKey(Integer sellerId);

	int updateByPrimaryKeySelective(Seller record);

	int updateByPrimaryKey(Seller record);

	// 扩展
	Seller selectByLoginId(Integer loginId);

	// 记录登录时间、IP
	@Update("update t_seller set is_paid_deposit = #{1}, deposit_date = #{2}, deposit_auditor = #{3}, deposit_audit_date = #{4} where seller_id = #{0}")
	int updateDeposit(Integer sellerId, String isPaidDeposit, Date depositDate,
			String depositAuditor, Date depositAuditDate);

	List<Seller> getSellersByIds(@Param("sellerIds")Set<Long> sellerIds);
}