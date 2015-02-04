/**
 * Copyright (c)2013-2014 by www.yiwang.com. All rights reserved.
 * 
 */
package com.afd.seller.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.afd.common.mybatis.Page;
import com.afd.common.util.DateUtils;
import com.afd.common.util.ThreadPoolUtils;
import com.afd.constants.seller.SellerConstants.Seller$Status;
import com.afd.constants.seller.SellerConstants.SellerApply$Status;
import com.afd.constants.seller.SellerConstants.SellerAudit$AuditResult;
import com.afd.constants.seller.SellerConstants.SellerLogin$Status;
import com.afd.model.seller.Seller;
import com.afd.model.seller.SellerApply;
import com.afd.model.seller.SellerAudit;
import com.afd.model.seller.SellerLogin;
import com.afd.seller.dao.SellerApplyMapper;
import com.afd.seller.dao.SellerAuditMapper;
import com.afd.seller.dao.SellerLoginMapper;
import com.afd.seller.dao.SellerMapper;
import com.afd.service.seller.ISellerApplyService;

/**
 * 卖家申请服务
 * 
 * @author xuzunyuan
 * @date 2013年12月28日
 */
@Service("sellerApplyService")
public class SellerApplyServiceImpl implements ISellerApplyService {
	private static final Logger logger = LoggerFactory
			.getLogger(SellerApplyServiceImpl.class);

	@Autowired
	private SellerApplyMapper sellerApplyMapper;

	@Autowired
	private SellerAuditMapper sellerAuditMapper;

	@Autowired
	private SellerLoginMapper sellerLoginMapper;

	@Autowired
	private SellerMapper sellerMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yiwang.service.seller.ISellerApplyService#getSellerApplyById(long)
	 */
	@Override
	public SellerApply getSellerApplyById(int id) {
		return sellerApplyMapper.selectByPrimaryKey(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yiwang.service.seller.ISellerApplyService#getSellerApplyByLoginId
	 * (long)
	 */
	@Override
	public SellerApply getSellerApplyByLoginId(int loginId) {
		return sellerApplyMapper.selectByLoginId(loginId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yiwang.service.seller.ISellerApplyService#commitNewSellerApply(com
	 * .yiwang.model.seller.SellerApply)
	 */
	@Override
	public long commitNewSellerApply(SellerApply sellerApply) {
		assert (sellerApply.getSellerLoginId() != null);

		sellerApply.setStatus(SellerApply$Status.WAIT_AUDIT);
		sellerApply.setApplyDate(DateUtils.currentDate());

		int result = sellerApplyMapper.insert(sellerApply);
		if (result != 0) {
			sellerLoginMapper.updateSellerApplyId(
					sellerApply.getSellerLoginId(), sellerApply.getAppId());
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yiwang.service.seller.ISellerApplyService#commitUpdatedSellerApply
	 * (com.yiwang.model.seller.SellerApply)
	 */
	@Override
	public int commitUpdatedSellerApply(SellerApply sellerApply) {
		assert (sellerApply.getAppId() != null);

		sellerApply.setStatus(SellerApply$Status.WAIT_AUDIT);
		sellerApply.setApplyDate(DateUtils.currentDate());

		return sellerApplyMapper.updateByPrimaryKey(sellerApply);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yiwang.service.seller.ISellerApplyService#passSellerApply(long,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public int passSellerApply(int appId, String auditType, String auditor,
			String ip, String opinion) {
		// 当前状态必须为待审批
		SellerApply apply = sellerApplyMapper.selectByPrimaryKey(appId);

		if (apply == null)
			return 0;

		if (!SellerApply$Status.WAIT_AUDIT.equals(apply.getStatus())) {
			return -1;
		}

		// 更新申请状态
		if (sellerApplyMapper.updateStatus(appId, SellerApply$Status.PASSED) == 0)
			return -2;

		// 增加审批流水信息
		SellerAudit audit = new SellerAudit();

		audit.setApplyId(appId);
		audit.setAuditDate(DateUtils.currentDate());
		audit.setAuditIp(ip);
		audit.setAuditOpinion(opinion);
		audit.setAuditor(auditor);
		audit.setAuditResult(SellerAudit$AuditResult.PASSED);
		audit.setAuditType(auditType);

		if (sellerAuditMapper.insert(audit) == 0)
			return -3;

		// 生成卖家信息
		Seller seller = new Seller();

		try {
			BeanUtils.copyProperties(seller, apply);
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		seller.setCreateDate(DateUtils.currentDate());
		seller.setStatus(Seller$Status.NORAML);

		if (sellerMapper.insert(seller) == 0)
			return -4;

		// 更新账号状态为正常，并补充卖家信息
		SellerLogin login = new SellerLogin();

		login.setSellerLoginId(apply.getSellerLoginId());
		login.setSellerId(seller.getSellerId());
		login.setStatus(SellerLogin$Status.NORAML);

		if (sellerLoginMapper.updateByPrimaryKeySelective(login) == 0)
			return -5;

		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yiwang.service.seller.ISellerApplyService#rejectSellerApply(long,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public int rejectSellerApply(int appId, String auditType, String auditor,
			String ip, String opinion) {

		// 当前状态必须为待审批
		SellerApply apply = sellerApplyMapper.selectByPrimaryKey(appId);

		if (apply == null)
			return 0;

		if (!SellerApply$Status.WAIT_AUDIT.equals(apply.getStatus())) {
			return -1;
		}

		// 更新申请状态
		if (sellerApplyMapper.updateStatus(appId, SellerApply$Status.REJECTED) == 0)
			return -2;

		// 增加审批流水信息
		SellerAudit audit = new SellerAudit();

		audit.setApplyId(appId);
		audit.setAuditDate(DateUtils.currentDate());
		audit.setAuditIp(ip);
		audit.setAuditOpinion(opinion);
		audit.setAuditor(auditor);
		audit.setAuditResult(SellerAudit$AuditResult.REJECTED);
		audit.setAuditType(auditType);

		if (sellerAuditMapper.insert(audit) == 0)
			return -3;

		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yiwang.service.seller.ISellerApplyService#batchPassSellerApply(long
	 * [], java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void batchPassSellerApply(final int[] appIds,
			final String auditorType, final String auditor, final String ip,
			final String opinion) {

		ThreadPoolUtils.execute(new Runnable() {
			@Override
			public void run() {
				for (int appId : appIds)
					passSellerApply(appId, auditorType, auditor, ip, opinion);
			}

		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yiwang.service.seller.ISellerApplyService#batchRejectSellerApply(
	 * long[], java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void batchRejectSellerApply(final int[] appIds,
			final String auditorType, final String auditor, final String ip,
			final String opinion) {

		ThreadPoolUtils.execute(new Runnable() {
			@Override
			public void run() {
				for (int appId : appIds)
					rejectSellerApply(appId, auditorType, auditor, ip, opinion);
			}

		});
	}

	@Override
	public SellerAudit getRecentAudit(int appId) {
		return sellerAuditMapper.selectRecentAudit(appId);
	}

	@Override
	public List<SellerAudit> getAuditList(int appId) {
		return sellerAuditMapper.selectByApplyId(appId);
	}

	@Override
	public SellerAudit getAudit(int auditId) {
		return sellerAuditMapper.selectByPrimaryKey(auditId);
	}

	@Override
	public Page<SellerApply> queryWaitAuditApply(Map<String, Object> queryCond,
			int... page) {

		Page<SellerApply> p = new Page<SellerApply>();

		if (ArrayUtils.isNotEmpty(page)) {
			p.setCurrentPageNo(page[0]);
			if (page.length > 1)
				p.setPageSize(page[1]);
		}

		p.setResult(sellerApplyMapper.selectWaitAuditApplyByPage(queryCond, p));

		return p;
	}
}
