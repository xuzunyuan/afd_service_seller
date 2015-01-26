/**
 * Copyright (c)2013-2014 by www.afd.com. All rights reserved.
 * 
 */
package com.afd.seller.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.afd.common.encrypt.MD5Encrypt;
import com.afd.common.generator.UUIDGenerator;
import com.afd.common.util.DateUtils;
import com.afd.common.util.ThreadPoolUtils;
import com.afd.constants.seller.SellerConstants.SellerLogin$Status;
import com.afd.model.seller.SellerLogin;
import com.afd.seller.dao.SellerLoginMapper;
import com.afd.seller.dao.SellerMapper;
import com.afd.service.seller.ISellerLoginService;
import com.alibaba.dubbo.common.utils.StringUtils;

/**
 * 卖家账号服务
 * 
 * @author xuzunyuan
 * @date 2013年12月28日
 */
@Service("sellerLoginService")
public class SellerLoginServiceImpl implements ISellerLoginService {
	@Autowired
	private SellerLoginMapper sellerLoginMapper;

	@Autowired
	private SellerMapper sellerMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yiwang.service.seller.ISellerLoginService#getLoginByLoginName(java
	 * .lang.String)
	 */
	@Override
	public SellerLogin getLoginByLoginName(String loginName) {
		return sellerLoginMapper.selectByLoginName(loginName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yiwang.service.seller.ISellerLoginService#getLoginById(long)
	 */
	@Override
	public SellerLogin getLoginById(int loginId) {
		return sellerLoginMapper.selectByPrimaryKey(loginId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yiwang.service.seller.ISellerLoginService#getLoginBySellerId(long)
	 */
	@Override
	public SellerLogin getLoginBySellerId(int sellerId) {
		return sellerLoginMapper.selectBySellerId(sellerId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yiwang.service.seller.ISellerLoginService#newLogin(com.yiwang.model
	 * .seller.SellerLogin)
	 */
	@Override
	public int newLogin(SellerLogin login) {
		// 账户名不能重复，包括已经存在的非正常的账号和手机号
		assert (login.getLoginName() != null && login.getMobile() != null && login
				.getNickname() != null);
		if (existLoginName(login.getLoginName()))
			return 0;

		if (existNickname(login.getNickname()))
			return -1;

		login.setStatus(SellerLogin$Status.INIT);
		login.setRegDate(DateUtils.currentDate()); // 注册日期为当前日期

		// 密码加密
		assert (login.getLoginPwd() != null);
		String privateKey = UUIDGenerator.getUUID32(); // 获得一个私钥
		String encrypt = MD5Encrypt.md5(privateKey, login.getLoginPwd());

		login.setPwdKey(privateKey);
		login.setLoginPwd(encrypt);

		sellerLoginMapper.insert(login);

		return login.getSellerLoginId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yiwang.service.seller.ISellerLoginService#freezeLogin(long,
	 * java.lang.String)
	 */
	@Override
	public int freezeLogin(int loginId, String staffName) {
		SellerLogin login = new SellerLogin();
		login.setSellerLoginId(loginId);
		login.setStatus(SellerLogin$Status.FREEZED);
		login.setFreezeByName(staffName);
		login.setFreezeDate(DateUtils.currentDate());

		return sellerLoginMapper.updateByPrimaryKeySelective(login);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yiwang.service.seller.ISellerLoginService#unfreezeLogin(long,
	 * java.lang.String)
	 */
	@Override
	public int unfreezeLogin(int loginId, String staffName) {
		SellerLogin login = new SellerLogin();
		login.setSellerLoginId(loginId);
		login.setStatus(SellerLogin$Status.NORAML);
		login.setUnfreezeByName(staffName);
		login.setUnfreezeDate(DateUtils.currentDate());

		return sellerLoginMapper.updateByPrimaryKeySelective(login);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yiwang.service.seller.ISellerLoginService#isExistLoginName(java.lang
	 * .String)
	 */
	@Override
	public boolean existLoginName(String loginName) {
		return sellerLoginMapper.existLoginName(loginName) != null;
	}

	/**
	 * 
	 */
	@Override
	public boolean existNickname(String nickname) {
		return sellerLoginMapper.existNickname(nickname) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yiwang.service.seller.ISellerLoginService#loginByLoginName(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	@Override
	public SellerLoginValidate loginByLoginName(String loginName,
			String password, String ip) {
		return loginByLogin(getLoginByLoginName(loginName), password, ip);
	}

	// 验证卖家登录
	private SellerLoginValidate loginByLogin(final SellerLogin login,
			String password, final String ip) {
		SellerLoginValidate validate = new SellerLoginValidate();
		validate.setSellerLogin(login);

		if (login == null)
			validate.setValidateStatus(SellerLoginValidate.NOT_EXIST);
		else if (login.getStatus() == SellerLogin$Status.FREEZED)
			validate.setValidateStatus(SellerLoginValidate.FREEZED);
		else {
			// 密码校验
			assert (password != null && login.getPwdKey() != null && login
					.getLoginPwd() != null);
			String encrypt = MD5Encrypt.md5(login.getPwdKey(), password);

			if (!encrypt.equals(login.getLoginPwd()))
				validate.setValidateStatus(SellerLoginValidate.INCORRECT_PASSWORD);
			else {
				// 通过验证
				validate.setValidateStatus(SellerLoginValidate.PASSED);

				if (login.getStatus() == SellerLogin$Status.NORAML) {
					validate.setSeller(sellerMapper.selectByPrimaryKey(login
							.getSellerId()));
				}

				ThreadPoolUtils.execute(new Runnable() {
					@Override
					public void run() {
						registerLoginInfo(login.getSellerLoginId(),
								DateUtils.currentDate(), ip);
					}
				});
			}
		}

		return validate;
	}

	@Override
	public void registerLoginInfo(int loginId, Date date, String ip) {
		sellerLoginMapper.updateLoginInfo(loginId, date, ip);
	}

	@Override
	public int changePassword(int loginId, String newPassword,
			String oldPassword) {

		SellerLogin login = sellerLoginMapper.selectByPrimaryKey(loginId);
		if (login == null)
			return 0;

		assert (login.getPwdKey() != null && login.getLoginPwd() != null);

		// 旧密码校验
		if (!StringUtils.isBlank(oldPassword)) {
			String oldEncrypt = MD5Encrypt.md5(login.getPwdKey(), oldPassword);
			if (!oldEncrypt.equals(login.getLoginPwd()))
				return -1;
		}

		// 新密码加密并保存
		String newEncrypt = MD5Encrypt.md5(login.getPwdKey(), newPassword);

		return sellerLoginMapper.updateLoginPwd(loginId, newEncrypt);
	}

	@Override
	public int changePassword(int loginId, String newPassword) {
		return changePassword(loginId, newPassword, null);
	}
}
