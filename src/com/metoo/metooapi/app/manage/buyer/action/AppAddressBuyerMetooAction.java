package com.metoo.metooapi.app.manage.buyer.action;

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

import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.Address;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.IAddressService;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.IUserService;
import com.metoo.metooapi.foundation.service.IAddressMetooService;
import com.metoo.metooapi.foundation.service.IAreaMetooService;

@Controller
public class AppAddressBuyerMetooAction {
	
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IUserService userService;
	
	@RequestMapping("/app_buyer_address.json")
	public void buyer_address(HttpServletRequest request,
			HttpServletResponse response, String token) {
		Map params = new HashMap();
		params.put("app_login_token", token);
		List<User> users =  this.userService.query("select obj from User obj where obj.app_login_token=:app_login_token order by obj.addTime desc",
				params, -1, -1);
		String user_id = "";
		for(User obj : users){
			user_id = CommUtil.null2String(obj.getId());
		}
		
		Map json_map = new HashMap();
		List map_list = new ArrayList();
		params.clear();
		params.put("user_id", CommUtil.null2Long(user_id));
		List<Address> addrs = this.addressService
				.query("select obj from Address obj where obj.user.id=:user_id order by obj.addTime desc",
						params, -1, -1);
		int default_mark = 0;
		for (Address addr : addrs) {
			if (addr.getDefault_val() == 1) {
				default_mark++;
			}
			Map map = new HashMap();
			map.put("addr_id", addr.getId());
			if (addr.getDefault_val() == 1) {
				map.put("default", true);
			}
			map.put("trueName", addr.getTrueName());
			map.put("areaInfo", addr.getArea_info());
			map.put("zip", addr.getZip());
			map.put("mobile", addr.getMobile());
			map.put("telephone", addr.getTelephone());
			map.put("area", addr.getArea().getParent().getParent()
					.getAreaName()
					+ ","
					+ addr.getArea().getParent().getAreaName()
					+ ","
					+ addr.getArea().getAreaName());
			map_list.add(map);
		}
		if (default_mark == 0 && addrs.size() > 0) {
			addrs.get(0).setDefault_val(1);
			this.addressService.update(addrs.get(0));
		}
		json_map.put("address_list", map_list);
		json_map.put("verify", "true");
		String json = Json.toJson(json_map, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}