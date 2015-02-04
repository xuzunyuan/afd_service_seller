package com.afd.seller.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.afd.model.seller.SellerApply;

public interface SellerApplyMapper {
	int deleteByPrimaryKey(Integer appId);

	int insert(SellerApply record);

	int insertSelective(SellerApply record);

	SellerApply selectByPrimaryKey(Integer appId);

	int updateByPrimaryKeySelective(SellerApply record);

	int updateByPrimaryKey(SellerApply record);

	// 扩展
	SellerApply selectByLoginId(Integer sellerLoginId);

	// 修改申请状态
	@Update("update t_seller_apply set status = #{1} where apply_id = #{0}")
	int updateStatus(Integer appId, String status);

	List<SellerApply> selectWaitAuditApplyByPage(
			@Param("cond") Map<String, Object> map,
			@Param("page") com.afd.common.mybatis.Page<SellerApply> page);
}