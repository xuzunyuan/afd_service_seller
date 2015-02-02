package com.afd.seller.dao;

import com.afd.model.seller.SellerAudit;

public interface SellerAuditMapper {
	int deleteByPrimaryKey(Integer auditId);

	int insert(SellerAudit record);

	int insertSelective(SellerAudit record);

	SellerAudit selectByPrimaryKey(Integer auditId);

	int updateByPrimaryKeySelective(SellerAudit record);

	int updateByPrimaryKey(SellerAudit record);

}