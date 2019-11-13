package com.metoo.metooapi.manage.admin.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.tools.CommUtil;
import com.metoo.buyer.domain.Result;
@Controller
public class OrderFormMetooTool {
	
	public boolean indexof(String goods_info, String name){
		List<Map> map_list = new ArrayList<Map>();
		if(goods_info != null && !goods_info.equals("")){
			map_list = Json.fromJson(ArrayList.class, goods_info);
		}
		for(Map map : map_list){
			String goodsName = (String) map.get("goods_name");
			if(goodsName.toLowerCase().contains(name.toLowerCase())){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
}
