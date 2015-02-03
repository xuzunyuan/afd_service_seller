package com.afd.seller.dao;

import java.util.List;

import com.afd.model.seller.SellerAudit;

public interface SellerAuditMapper {
	int deleteByPrimaryKey(Integer auditId);

	int insert(SellerAudit record);

	int insertSelective(SellerAudit record);

	SellerAudit selectByPrimaryKey(Integer auditId);

	int updateByPrimaryKeySelective(SellerAudit record);

	int updateByPrimaryKey(SellerAudit record);

	/** 扩展 **/
	// 获取最后一次审批信息
	SellerAudit selectRecentAudit(Integer applyId);

	// 获取所有审批信息，以时间倒叙
	List<SellerAudit> selectByApplyId(Integer applyId);

}