package com.metoo.manage.seller.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.tools.CommUtil;
import com.metoo.buyer.domain.Result;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.query.support.IPageList;
import com.metoo.foundation.domain.EnoughFree;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.query.EnoughFreeQueryObject;
import com.metoo.foundation.service.IEnoughFreeService;
import com.metoo.foundation.service.IUserService;
/**
 *
 * @Title EnoughFreeSellerAction.java
 * 
 * Description: 卖家满包邮控制器
 * 
 * @author Administrator
 *
 */
@Controller
public class EnoughFreeSellerAction {
	
	@Autowired
	private IUserService userService;
	@Autowired
	private IEnoughFreeService enoughFreeService;
	
	/**
	 * 
	 * EnoughFree列表页
	 * @param request
	 * @param response
	 * @param currentPage 
	 * @param title
	 * @param begin_time
	 * @param end_time
	 * @param status
	 * @param uid
	 */
	@RequestMapping("/php/seller/enoughFress/post.json")
	public void query(HttpServletRequest request,
			HttpServletResponse response, String currentPage, 
			String title, String begin_time, String end_time, String status, String uid){
		Result result = null;
		Map map = new HashMap();
		if(uid != null && !"".equals(uid)){
			User user = this.userService.getObjById(CommUtil.null2Long(uid));
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			ModelAndView mv = new ModelAndView();
			EnoughFreeQueryObject qo = new EnoughFreeQueryObject(currentPage,
					mv,"addTime", "desc");
			qo.addQuery("obj.sotre_id", new SysMap("store_id", store.getId().toString()), "=");
			if(title != null && !"".equals(title)){
				qo.addQuery("obj.eftitle", new SysMap("ertitle", "%" + title + "%"), "like");
			}
			if (begin_time != null && !begin_time.equals("")
					&& end_time != null && !end_time.equals("")) {
				qo.addQuery("DATE_FORMAT(obj.efbegin_time,'%Y-%m-%d')", new SysMap(
						"efbegin_time", begin_time), ">=");
				qo.addQuery("DATE_FORMAT(obj.efend_time,'%Y-%m-%d')", new SysMap(
						"efend_time", end_time), "<=");
			}
			if (status != null && !"".equals(status)) {
				qo.addQuery("obj.efstatus",
						new SysMap("efstatus", CommUtil.null2Int(status)), "=");
			}
			IPageList pList = this.enoughFreeService.list(qo);
			List<EnoughFree> enoughFrees = pList.getResult();
			List<Map> efList = new ArrayList<Map>();
			for(EnoughFree ef : enoughFrees){
				Map efMap = new HashMap();
				efMap.put("title", ef.getEftitle());
				efMap.put("begin_time", ef.getEfbegin_time());
				efMap.put("end_time", ef.getEfend_time());
				efMap.put("content", ef.getEfcontent());
				efMap.put("tag", ef.getEftag());
				efList.add(efMap);
			}
			map.put("enoughFress", efList);
			map.put("currentpage", pList.getCurrentPage());
			map.put("psgeSize", pList.getPageSize());
			map.put("begin_time", begin_time);
			map.put("end_time", end_time);
			result = new Result(0,"success");
		}else{
			result = new Result(-100, "User information error");
		}
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		try {
			response.getWriter().print(Json.toJson(result, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/php/seller/enoughFress")
	public void POST(HttpServletRequest request, HttpServletResponse response){
		
	}
}
