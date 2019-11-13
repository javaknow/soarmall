package com.metoo.metooapi.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.buyer.domain.Result;
import com.metoo.core.annotation.SecurityMapping;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.FootPoint;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.query.FootPointQueryObject;
import com.metoo.foundation.domain.virtual.FootPointView;
import com.metoo.foundation.service.IFootPointService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.manage.buyer.tools.FootPointTools;

@Controller
public class MetooFootPointBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IFootPointService footPointService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private FootPointTools footPointTools;
	@Autowired
	private IUserService userService;

	@SecurityMapping(title = "用户足迹记录", value = "/buyer/foot_point.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer_foot_point.json")
	public void metoo_foot_point(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String token) {
		Result result = null;
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/foot_point.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if(token.equals("")){
			result = new Result(-100,"token Invalidation");
		}else{
			Map params = new HashMap();
			params.put("app_login_token", token);
			List<User> users =  this.userService.query("select obj from User obj where obj.app_login_token=:app_login_token order by obj.addTime desc",
					params, -1, -1);
			if(users.isEmpty()){
				result = new Result(-100,"token Invalidation");
			}else{
				User user = users.get(0);
				FootPointQueryObject qo = new FootPointQueryObject();
				qo.setCurrentPage(CommUtil.null2Int(currentPage));
				qo.addQuery("obj.fp_user_id", new SysMap("fp_user_id", users.get(0).getId()),
						"=");
				qo.setOrderBy("addTime");
				qo.setOrderType("desc");
				IPageList pList = this.footPointService.list(qo);
				
				List<FootPoint> footPoints = pList.getResult();
				List<Map> footlist = new ArrayList<Map>();
				for(FootPoint footPoint : footPoints){
					Map footpointmap = new HashMap();   
					footpointmap.put("fp_date", footPoint.getFp_date());
					footpointmap.put("fp_goods_count", footPoint.getFp_goods_count());
					List<FootPointView> footGoodsinfos = footPointTools.generic_fpv(footPoint.getFp_goods_content());
					List<Map> footgoods = new ArrayList<Map>();
					for(FootPointView footPointView : footGoodsinfos){
						Map footGoodsMap = new HashMap();
						footGoodsMap.put("goods_id", footPointView.getFpv_goods_id());
						footGoodsMap.put("goods_name", footPointView.getFpv_goods_name());
						footGoodsMap.put("goods_price", footPointView.getFpv_goods_price());
						footGoodsMap.put("goods_sale", footPointView.getFpv_goods_sale());
						footGoodsMap.put("goods_photo", footPointView.getFpv_goods_img_path());
						footgoods.add(footGoodsMap);
					}
					footpointmap.put("footgoods", footgoods);
					footlist.add(footpointmap);
				}
				if(CommUtil.isNotNull(footlist)){
					result = new Result(0,"查询成功",footlist);
				}else{
					result = new Result(1,"查询失败");
				}
			}
		}
		try {
			response.getWriter().print(Json.toJson(result, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SecurityMapping(title = "用户足迹记录删除", value = "/buyer/foot_point_remove.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer_foot_point_remove.json")
	public void foot_point_remove(HttpServletRequest request,
			HttpServletResponse response, String date, String goods_id, String token) {
		Result result = null;
		if(token.equals("")){
			result = new Result(-100,"token Invalidation");
		}else{
			Map params = new HashMap();
			params.put("app_login_token", token);
			List<User> users =  this.userService.query("select obj from User obj where obj.app_login_token=:app_login_token order by obj.addTime desc",
					params, -1, -1);
			if(users.isEmpty()){
				result = new Result(-100,"token Invalidation");
			}else{
				User user = users.get(0);
				boolean ret = true;
				if (!CommUtil.null2String(date).equals("")
						&& CommUtil.null2String(goods_id).equals("")) {// 删除当日所有足迹
					params.clear();
					params.put("fp_date", CommUtil.formatDate(date));
					params.put("fp_user_id", user.getId());
					List<FootPoint> fps = this.footPointService
							.query("select obj from FootPoint obj where obj.fp_date=:fp_date and obj.fp_user_id=:fp_user_id",
									params, -1, -1);
					for (FootPoint fp : fps) {
						this.footPointService.delete(fp.getId());
					}
				}
		
				if (!CommUtil.null2String(date).equals("")
						&& !CommUtil.null2String(goods_id).equals("")) {// 删除某一个足迹
					params.clear();
					params.put("fp_date", CommUtil.formatDate(date));
					params.put("fp_user_id",  user.getId());
					List<FootPoint> fps = this.footPointService
							.query("select obj from FootPoint obj where obj.fp_date=:fp_date and obj.fp_user_id=:fp_user_id",
									params, -1, -1);
					for (FootPoint fp : fps) {
						List<Map> list = Json.fromJson(List.class,
								fp.getFp_goods_content());
						for (Map map : list) {
							if (CommUtil.null2String(map.get("goods_id")).equals(
									goods_id)) {
								list.remove(map);
								break;
							}
						}
						fp.setFp_goods_content(Json.toJson(list, JsonFormat.compact()));
						fp.setFp_goods_count(list.size());
						this.footPointService.update(fp);
					}
				}
				result = new Result(0,"succes");
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(result,  JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
