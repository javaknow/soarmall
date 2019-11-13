package com.metoo.metooapi.foundation.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.buyer.domain.Result;
import com.metoo.core.dao.IGenericDAO;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.GenericPageList;
import com.metoo.core.query.PageObject;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.query.support.IQueryObject;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.foundation.domain.CouponInfo;
import com.metoo.foundation.domain.query.CouponInfoQueryObject;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.metooapi.foundation.service.ICouponInfoMetooService;
@Service
@Transactional
public class CouponInfoMetooServiceImpl implements ICouponInfoMetooService{
	@Resource(name = "CouponMetooDAO")
	private IGenericDAO<CouponInfo> couponMetooDao;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		//得到一个查询语句
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		//[面向对象分页类，该类用来进行数据查询并分页返回数据信息 ]
		GenericPageList pList = new GenericPageList(CouponInfo.class,
				construct, query, params, this.couponMetooDao);
		if (properties != null) {
			//[获取一个分页信息getCurrentPage :-1 getPageSize :-1]
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(
						pageObj.getCurrentPage() == null ? 0 : pageObj
								.getCurrentPage(),
						pageObj.getPageSize() == null ? 0 : pageObj
								.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}
	//优惠券列表
	public String coupon_listimpl(HttpServletRequest request,
			HttpServletResponse response, String reply, String currentPage) {
		Result result = null;
		Map map = new HashMap();
		ModelAndView mv = new JModelAndView(
				"",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		//[构造查询对象]
		CouponInfoQueryObject qo = new CouponInfoQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder
				.getCurrentUser().getId()), "=");
		IPageList pList = this.list(qo);
		List<CouponInfo> couponInfos = pList.getResult();
		List<Map> couponList = new ArrayList<Map>();
		for(CouponInfo couponInfo:couponInfos){
			Map couponInfoMap = new HashMap();
			couponInfoMap.put("Coupon_sn", couponInfo.getCoupon_sn());
			couponInfoMap.put("Coupon_amount",couponInfo.getCoupon().getCoupon_amount());
			couponInfoMap.put("Coupon_order_amount", couponInfo.getCoupon().getCoupon_order_amount());
			couponInfoMap.put("Coupon_begin_time",couponInfo.getCoupon().getCoupon_begin_time());
			couponInfoMap.put("Coupon_end_time", couponInfo.getCoupon().getCoupon_end_time());
			couponInfoMap.put("Status", couponInfo.getStatus());
			couponList.add(couponInfoMap);
		}
		map.put("couponList", couponList);
		if(!map.isEmpty()){
			result = new Result(0,"查询成功",map);
		}else{
			result = new Result(1,"您目前还没有优惠券！");
		}
		String addressTemp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().println(addressTemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return addressTemp;
	}
}
