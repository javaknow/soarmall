package com.metoo.metooapi.foundation.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.metoo.core.query.support.IPageList;
import com.metoo.core.query.support.IQueryObject;

public interface ICouponInfoMetooService {
	/**
	 * 通过一个查询对象得到CouponInfo
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	public String coupon_listimpl(HttpServletRequest request,
			HttpServletResponse response, String reply, String currentPage);
}
