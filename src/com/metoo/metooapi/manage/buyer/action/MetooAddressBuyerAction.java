package com.metoo.metooapi.manage.buyer.action;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.metoo.buyer.domain.Result;
import com.metoo.core.annotation.SecurityMapping;
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.Area;
import com.metoo.foundation.service.IAreaService;
import com.metoo.metooapi.foundation.service.IAddressMetooService;
import com.metoo.metooapi.foundation.service.IAreaMetooService;

@Controller
public class MetooAddressBuyerAction {
	@Autowired
	private IAddressMetooService  addressMetooService;
	@Autowired
	private IAreaMetooService areaMetooService;
	@Autowired
	private IAreaService areaService;
	
	/**
	 * Address列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "收货地址列表", value = "/buyer/address.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer_address.json")
	public void address(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String token) {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		String address = this.addressMetooService.address_metoo_list(request,response,currentPage,token);
		try {
			response.getWriter().print(address);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SecurityMapping(title = "新增收货地址", value = "/buyer/address_add.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer_address_add.json")
	public void address_Add(HttpServletRequest request,
			HttpServletResponse response, String currentPage){
		//addressMetooService.address_Metoo_Add();
		String areaParent = this.areaMetooService.areaParent(request, response, currentPage);
		try {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().print(areaParent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@SecurityMapping(title = "编辑收货地址", value = "/buyer/address_edit.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer_address_edit.json")
	public void address_edit(HttpServletRequest request, HttpServletResponse response,
			String id, String currentPage, String token) {
		String address_edit = this.addressMetooService.address_metoo_edit(request, response, id, currentPage,token);
		try {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().print(address_edit);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据父id加载下级区域，返回json格式数据，这里只返回id和areaName，根据需要可以修改返回数据
	 * 过滤掉有关联的属性关联对象
	 * @param request
	 * @param response
	 * @param pid
	 */
	@RequestMapping("/load_area.json")
	public void load_area(HttpServletRequest request,
			HttpServletResponse response, String pid) {
		Map params = new HashMap();
		Result result = null;
		params.put("pid", CommUtil.null2Long(pid));
			List<Area> areas = this.areaMetooService.query(
					"select obj from Area obj where obj.parent.id=:pid", params,
					-1, -1);
			List<Map> list = new ArrayList<Map>();
			for (Area area : areas) {
				Map map = new HashMap();
				map.put("id", area.getId());
				map.put("areaName", area.getAreaName());
				list.add(map);
			}
			if(!list.isEmpty()){
				result = new Result(0,"查询成功",list);
			}else{
				result = new Result(1,"查询失败");
			}
		String temp = Json.toJson(result, JsonFormat.compact());
		PrintWriter writer;
		try {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param id 地址
	 * @param area_id 区域
	 * @param currentPage 当前页
	 * @return
	 */
	@SecurityMapping(title = "收货地址保存", value = "/buyer/address_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer_address_save.json")
	public void address_save(HttpServletRequest request,
			HttpServletResponse response, String id, String area_id,String flag,
			String currentPage, String token) {
		String save_temp = this.addressMetooService.address_metoo_save(request, response, id, area_id, flag, currentPage,token);
		try {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().print(save_temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 */
	@SecurityMapping(title = "收货地址删除", value = "/buyer/address_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer_address_del.json")
	public void address_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage, String token) {
	String deltetemp = this.addressMetooService.address_metoo_del(request, response, mulitId, currentPage,token);
	try {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(deltetemp);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 */
	@SecurityMapping(title = "收货地址默认设置", value = "/buyer/address_default.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer_address_default.json")
	public void address_default(HttpServletRequest request, HttpServletResponse response,
			String mulitId,String currentPage, String token){
			String default_result = this.addressMetooService.address_metoo_default(request, response, mulitId, currentPage,token);
			try {
				response.setContentType("text/plain");
				response.setHeader("Cache-Control", "no-cache");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().println(default_result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	/**
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 */
	@SecurityMapping(title = "收货地址默认取消", value = "/buyer/address_default_cancle.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer_address_default_cancle.json")
	public void address__default_cancle(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage, String token) {
		String default_cel_result = this.addressMetooService.address__default_cancle(request, response, mulitId , currentPage,token );
		try {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(default_cel_result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
