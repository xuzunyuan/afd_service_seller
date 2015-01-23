package com.afd.seller.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.afd.model.seller.SellerLogin;

public interface SellerLoginMapper {
	int deleteByPrimaryKey(Integer sellerLoginId);

	int insert(SellerLogin record);

	int insertSelective(SellerLogin record);

	SellerLogin selectByPrimaryKey(Integer sellerLoginId);

	int updateByPrimaryKeySelective(SellerLogin record);

	int updateByPrimaryKey(SellerLogin record);

	/** 扩展方法 **/

	// 根据账户名查找账号
	SellerLogin selectByLoginName(String loginName);

	// 根据卖家ID查询账号
	SellerLogin selectBySellerId(Integer sellerId);

	// 登录名是否已存在
	@Select("select 1 from t_seller_login where login_name = #{0} limit 1")
	Integer existLoginName(String loginName);

	// 卖家昵称是否已使用
	@Select("select 1 from t_seller_login where nickname = #{0} limit 1")
	Integer existNickname(String nickname);

	// 记录登录时间、IP
	@Update("update t_seller_login set last_login_date = #{1}, last_login_ip = #{2} where seller_login_id = #{0}")
	int updateLoginInfo(Integer sellerLoginId, Date date, String ip);

	// 修改登录密码
	@Update("update t_seller_login set login_pwd = #{1} where seller_login_id = #{0}")
	int updateLoginPwd(Integer sellerLoginId, String encryptPassword);
}