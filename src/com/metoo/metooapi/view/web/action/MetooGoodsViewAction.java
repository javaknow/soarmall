package com.metoo.metooapi.view.web.action;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.soap.SOAPBinding.Use;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.buyer.domain.Result;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.ip.IPSeeker;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.Accessory;
import com.metoo.foundation.domain.ActivityGoods;
import com.metoo.foundation.domain.BuyGift;
import com.metoo.foundation.domain.CGoods;
import com.metoo.foundation.domain.CombinPlan;
import com.metoo.foundation.domain.Consult;
import com.metoo.foundation.domain.EnoughReduce;
import com.metoo.foundation.domain.Evaluate;
import com.metoo.foundation.domain.Favorite;
import com.metoo.foundation.domain.FootPoint;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsClass;
import com.metoo.foundation.domain.GoodsLog;
import com.metoo.foundation.domain.Group;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.query.ConsultQueryObject;
import com.metoo.foundation.domain.query.EvaluateQueryObject;



import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.annotations.Parameter;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;


import com.metoo.buyer.domain.Result;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.ip.IPSeeker;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.JsonUtil;
import com.metoo.core.tools.WebForm;
import com.metoo.foundation.domain.Accessory;
import com.metoo.foundation.domain.ActivityGoods;
import com.metoo.foundation.domain.Address;
import com.metoo.foundation.domain.Album;
import com.metoo.foundation.domain.Area;
import com.metoo.foundation.domain.BuyGift;
import com.metoo.foundation.domain.CombinPlan;
import com.metoo.foundation.domain.Consult;
import com.metoo.foundation.domain.ConsultSatis;
import com.metoo.foundation.domain.EnoughReduce;
import com.metoo.foundation.domain.Evaluate;
import com.metoo.foundation.domain.FootPoint;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsBrand;
import com.metoo.foundation.domain.GoodsClass;
import com.metoo.foundation.domain.GoodsLog;
import com.metoo.foundation.domain.GoodsSpecProperty;
import com.metoo.foundation.domain.GoodsTest;
import com.metoo.foundation.domain.GoodsTypeProperty;
import com.metoo.foundation.domain.Group;
import com.metoo.foundation.domain.GroupGoods;
import com.metoo.foundation.domain.OrderFormLog;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.StoreNavigation;
import com.metoo.foundation.domain.SysConfig;
import com.metoo.foundation.domain.Transport;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.UserGoodsClass;
import com.metoo.foundation.domain.query.ConsultQueryObject;
import com.metoo.foundation.domain.query.EvaluateQueryObject;
import com.metoo.foundation.domain.query.GoodsQueryObject;
import com.metoo.foundation.domain.virtual.GoodsCompareView;
import com.metoo.foundation.service.IActivityGoodsService;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.IBuyGiftService;
import com.metoo.foundation.service.ICGoodsService;
import com.metoo.foundation.service.ICombinPlanService;
import com.metoo.foundation.service.IConsultSatisService;
import com.metoo.foundation.service.IConsultService;
import com.metoo.foundation.service.IEnoughReduceService;
import com.metoo.foundation.service.IEvaluateService;
import com.metoo.foundation.service.IFavoriteService;
import com.metoo.foundation.service.IFootPointService;
import com.metoo.foundation.service.IGoodsBrandService;
import com.metoo.foundation.service.IGoodsCartService;
import com.metoo.foundation.service.IGoodsClassService;
import com.metoo.foundation.service.IGoodsLogService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IGoodsSpecPropertyService;
import com.metoo.foundation.service.IGoodsTestService;
import com.metoo.foundation.service.IGoodsTypePropertyService;
import com.metoo.foundation.service.IOrderFormService;
import com.metoo.foundation.service.IStoreNavigationService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.ITransportService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserGoodsClassService;
import com.metoo.foundation.service.IUserService;
import com.metoo.manage.admin.tools.ImageTools;
import com.metoo.manage.admin.tools.OrderFormTools;
import com.metoo.manage.admin.tools.UserTools;
import com.metoo.manage.seller.tools.TransportTools;
import com.metoo.metooapi.foundation.service.IGoodsMetooService;
import com.metoo.metooapi.view.web.tool.MetooGoodsViewTools;
import com.metoo.view.web.tools.ActivityViewTools;
import com.metoo.view.web.tools.AreaViewTools;
import com.metoo.view.web.tools.ConsultViewTools;
import com.metoo.view.web.tools.EvaluateViewTools;
import com.metoo.view.web.tools.GoodsViewTools;
import com.metoo.view.web.tools.IntegralViewTools;
import com.metoo.view.web.tools.StoreViewTools;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
 * 
 * <p>
 * Title: GoodsViewAction.java
 * </p>
 * 
 * <p>
 * Description: 商品前台控制器,用来显示商品列表、商品详情、商品其他信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.metoo.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-4-28
 * 
 * @version metoo_b2b2c v2.0 2015版 
 */
@Controller
public class MetooGoodsViewAction {

	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IConsultService consultService;
	@Autowired
	private MetooGoodsViewTools metooGoodsViewTools;
	@Autowired
	private ICGoodsService cGoodsService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	private IGoodsTypePropertyService goodsTypePropertyService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private AreaViewTools areaViewTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private UserTools userTools;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private ConsultViewTools consultViewTools;
	@Autowired
	private EvaluateViewTools evaluateViewTools;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreNavigationService storenavigationService;
	@Autowired
	private IConsultSatisService consultsatisService;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private IEnoughReduceService enoughReduceService;
	@Autowired
	private IFootPointService footPointService;
	@Autowired
	private IActivityGoodsService actgoodsService;
	@Autowired
	private ActivityViewTools activityViewTools;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private ICombinPlanService combinplanService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IBuyGiftService buyGiftService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ImageTools imageTools;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private ITransportService transportService;
	@Autowired
	private IFavoriteService favoriteService;
	
	/**
	 * 商品详情信息
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/goods.json")
	public void goodsdetail(HttpServletRequest request, HttpServletResponse response,
			String id, String token){
		Result result = null;
		User user = null;
		if(token != null && token.equals("")){
			result = new Result(-100,"token Invalidation");
		}else{
			Map params = new HashMap();
			params.put("app_login_token", token);
			List<User> users =  this.userService.query("select obj from User obj where obj.app_login_token=:app_login_token order by obj.addTime desc",
					params, -1, -1);
			if(users.isEmpty()){
				result = new Result(-100,"token Invalidation");
			}else{
			 user = users.get(0);
			}
		}
		String imageWebServer = this.configService.getSysConfig().getImageWebServer();
		ModelAndView mv = null;
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
			Map statusmap = new HashMap(); 
			Map goodsmap = new HashMap();
			Map objmap = new HashMap();
			if(obj!=null ){
				if(this.configService.getSysConfig().isSecond_domain_open()){
					String serverName = request.getServerName().toLowerCase();
					String secondDomain = CommUtil.null2String(serverName
							.substring(0, serverName.indexOf(".")));
					if (serverName.indexOf(".") == serverName.lastIndexOf(".")) {
						secondDomain = "www";
					}
					// System.out.println("已经开启二级域名，二级域名为：" + secondDomain);
					if(!secondDomain.equals("")){
						//[0为自营商品，1为第三方经销商]
						if(obj.getGoods_type()==0){  //[自营商品禁止使用二级域名访问]
							if(!secondDomain.equals("www")){
								result = new Result(1,"自营商品禁止使用二级域名访问");
							}
						}else{
							if(!obj.getGoods_store().getStore_second_domain().equals(secondDomain)){
								result = new Result(1,"商品对应店铺二级与当前二级域名不符");
							}
						}
						
					}else{
						result = new Result(1,"域名为空");
					}
				}
				
				//2[利用cookie添加查看过的商品]
				Cookie[] cookies = request.getCookies();
				Cookie goodscookie = null;
				int k=0;
				if(cookies != null){
					for(Cookie cookie:cookies){
						if (cookie.getName().equals("goodscookie")) {
							String goods_ids = cookie.getValue();
							int m = 6;
							int n = goods_ids.split(",").length;
							if (m > n) {
								m = n + 1;
							}												  
							String[] new_goods_ids = goods_ids.split(",", m);
							for (int i = 0; i < new_goods_ids.length; i++) {
								if ("".equals(new_goods_ids[i])) {
									for (int j = i + 1; j < new_goods_ids.length; j++) {
										new_goods_ids[i] = new_goods_ids[j];
									}
								}
							}
							String[] new_ids = new String[6];
							for (int i = 0; i < m - 1; i++) {
								if (id.equals(new_goods_ids[i])) {
									k++;
								}
							}
							if (k == 0) {
								new_ids[0] = id;
								for (int j = 1; j < m; j++) {
									new_ids[j] = new_goods_ids[j - 1];
								}
								goods_ids = id + ",";
								if (m == 2) {
									for (int i = 1; i <= m - 1; i++) {
										goods_ids = goods_ids + new_ids[i] + ",";
									}
								} else {
									for (int i = 1; i < m; i++) {
										goods_ids = goods_ids + new_ids[i] + ",";
									}
								}
								goodscookie = new Cookie("goodscookie", goods_ids);
							} else {
								new_ids = new_goods_ids;
								goods_ids = "";
								for (int i = 0; i < m - 1; i++) {
									goods_ids += new_ids[i] + ",";
								}
								goodscookie = new Cookie("goodscookie", goods_ids);
							}
							goodscookie.setMaxAge(60 * 60 * 24 * 7);
							goodscookie.setDomain(CommUtil.generic_domain(request));
							response.addCookie(goodscookie);
							break;
						} else {
							goodscookie = new Cookie("goodscookie", id + ",");
							goodscookie.setMaxAge(60 * 60 * 24 * 7);
							goodscookie.setDomain(CommUtil.generic_domain(request));
							response.addCookie(goodscookie);
						}
					}
				}else{
					goodscookie = new Cookie("goodscookie", id + ",");
					goodscookie.setMaxAge(60 * 60 * 24 * 7);
					goodscookie.setDomain(CommUtil.generic_domain(request));
					response.addCookie(goodscookie);
				}
				
				//3[记录登陆用户浏览足迹]
				User current_user = user;
				boolean admin_view = false;// 超级管理员可以查看未审核得到商品信息
				if (current_user != null) {
					// 登录用户记录浏览足迹信息
					Map params = new HashMap();
					params.put("fp_date", CommUtil.formatDate(CommUtil
							.formatShortDate(new Date())));
					params.put("fp_user_id", current_user.getId());
					List<FootPoint> fps = this.footPointService
							.query("select obj from FootPoint obj where obj.fp_date=:fp_date and obj.fp_user_id=:fp_user_id",
									params, -1, -1);
					if (fps.size() == 0) {
						
						FootPoint fp = new FootPoint();
						fp.setAddTime(new Date());
						fp.setFp_date(new Date());
						fp.setFp_user_id(current_user.getId());
						fp.setFp_user_name(current_user.getUsername());
						fp.setFp_goods_count(1);
						Map map = new HashMap();
						map.put("goods_id", obj.getId());
						map.put("goods_name", obj.getGoods_name());
						map.put("goods_sale", obj.getGoods_salenum());
						map.put("goods_time", CommUtil.formatLongDate(new Date()));
						map.put("goods_img_path", obj.getGoods_main_photo() != null
								? imageWebServer + "/" + obj.getGoods_main_photo().getPath() + "/"
										+ obj.getGoods_main_photo().getName() +"_middle."+ obj.getGoods_main_photo().getExt()
								: imageWebServer
										+ "/"
										+ this.configService.getSysConfig()
												.getGoodsImage().getPath()
										+ "/"
										+ this.configService.getSysConfig()
												.getGoodsImage().getName()
										+ "_middle."
										+ this.configService.getSysConfig()
											.getGoodsImage().getExt());
						map.put("goods_price", obj.getGoods_current_price());
						map.put("goods_class_id",
								CommUtil.null2Long(obj.getGc().getId()));
						map.put("goods_class_name",
								CommUtil.null2String(obj.getGc().getClassName()));
						List<Map> list = new ArrayList<Map>();
						list.add(map);
						fp.setFp_goods_content(Json.toJson(list,
								JsonFormat.compact()));
						this.footPointService.save(fp);
					} else {
						FootPoint fp = fps.get(0);
						List<Map> list = Json.fromJson(List.class,
								fp.getFp_goods_content());
						boolean add = true;
						for (Map map : list) {// 排除重复的商品足迹
							if (CommUtil.null2Long(map.get("goods_id")).equals(
									obj.getId())) {
								add = false;
							}
						}
						if (add) {
							Map map = new HashMap();
							map.put("goods_id", obj.getId());
							map.put("goods_name", obj.getGoods_name());
							map.put("goods_sale", obj.getGoods_salenum());
							map.put("goods_time",
									CommUtil.formatLongDate(new Date()));
							map.put("goods_img_path",
									obj.getGoods_main_photo() != null
											? imageWebServer + "/"
													+ obj.getGoods_main_photo()
															.getPath()
													+ "/"
													+ obj.getGoods_main_photo()
															.getName() +"_middle." + obj.getGoods_main_photo().getExt() : imageWebServer
													+ "/"
													+ this.configService
															.getSysConfig()
															.getGoodsImage()
															.getPath()
													+ "/"
													+ this.configService
															.getSysConfig()
															.getGoodsImage()
															.getName()
													+ "_middle."
													+ this.configService
														.getSysConfig()
														.getGoodsImage().getExt());
							map.put("goods_price", obj.getGoods_current_price());
							map.put("goods_class_id",
									CommUtil.null2Long(obj.getGc().getId()));
							map.put("goods_class_name", CommUtil.null2String(obj
									.getGc().getClassName()));
							list.add(0, map);// 后浏览的总是插入最前面
							fp.setFp_goods_count(list.size());
							fp.setFp_goods_content(Json.toJson(list,
									JsonFormat.compact()));
							this.footPointService.update(fp);
						}
					}
			
					if (current_user.getUserRole().equals("ADMIN")) {
						admin_view = true;
					}
				}
				
				GoodsLog todayGoodsLog = this.goodsViewTools.getTodayGoodsLog(obj
						.getId());
				todayGoodsLog.setGoods_click(todayGoodsLog.getGoods_click() + 1);
				String click_from_str = todayGoodsLog.getGoods_click_from();
				Map<String, Integer> clickmap = (click_from_str != null && !click_from_str
						.equals("")) ? (Map<String, Integer>) Json
						.fromJson(click_from_str) : new HashMap<String, Integer>();
				String from = clickfrom_to_chinese(CommUtil.null2String(request
						.getParameter("from")));
				if (from != null && !from.equals("")) {
					if (clickmap.containsKey(from)) {
						clickmap.put(from, clickmap.get(from) + 1);
					} else {
						clickmap.put(from, 1);
					}
				} else {
					if (clickmap.containsKey("unknow")) {
						clickmap.put("unknow", clickmap.get("unknow") + 1);
					} else {
						clickmap.put("unknow", 1);
					}
				}
				todayGoodsLog.setGoods_click_from(Json.toJson(clickmap,
						JsonFormat.compact()));
				this.goodsLogService.update(todayGoodsLog);
				
				if(obj.getGoods_status() == 0 || admin_view){
					mv = new JModelAndView("default/store_goods.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					
					obj.setGoods_click(obj.getGoods_click() + 1);
					if(this.configService.getSysConfig().isZtc_status() && obj.getZtc_status() == 2){
						obj.setZtc_click_num(obj.getZtc_click_num() + 1);
					}
					
					if(obj.getActivity_status() == 1 
							|| obj.getActivity_status() == 2){
						if(!CommUtil.null2Boolean(obj.getActivity_goods_id().equals(""))){
							ActivityGoods ag =  this.actgoodsService.getObjById(obj.getActivity_goods_id());
							if(ag.getAct().getAc_end_time().before(new Date())){
								ag.setAg_status(-2);
								this.actgoodsService.update(ag);
								obj.setActivity_status(0);
								obj.setActivity_goods_id(null);
							}
						}
					}
					
					if(obj.getGroup() != null && obj.getGroup_buy() == 2){
						Group group = obj.getGroup();
						if(group.getEndTime().before(new Date())){
							obj.setGroup(null);
							obj.setGroup_buy(0);
							obj.setGoods_current_price(obj.getStore_price());
						}
					}
					
					if (obj.getCombin_status() == 1) {
						Map params = new HashMap();
						params.put("endTime", new Date());
						params.put("main_goods_id", obj.getId());
						List<CombinPlan> combins = this.combinplanService
								.query("select obj from CombinPlan obj where obj.endTime<=:endTime and obj.main_goods_id=:main_goods_id",
										params, -1, -1);
						if (combins.size() > 0) {
							for (CombinPlan com : combins) {
								if (com.getCombin_type() == 0) {
									if (obj.getCombin_suit_id().equals(com.getId())) {
										obj.setCombin_suit_id(null);
									}
								} else {
									if (obj.getCombin_parts_id()
											.equals(com.getId())) {
										obj.setCombin_parts_id(null);
									}
								}
								obj.setCombin_status(0);
							}
						}
					}
					if (obj.getOrder_enough_give_status() == 1) { 
						BuyGift bg = this.buyGiftService.getObjById(obj
								.getBuyGift_id());
						if (bg != null && bg.getEndTime().before(new Date())) {
							bg.setGift_status(20);
							List<Map> maps = Json.fromJson(List.class,
									bg.getGift_info());
							maps.addAll(Json.fromJson(List.class,
									bg.getGoods_info()));
							for (Map map : maps) {
								Goods goods = this.goodsService.getObjById(CommUtil
										.null2Long(map.get("goods_id")));
								if (goods != null) {
									goods.setOrder_enough_give_status(0);
									goods.setOrder_enough_if_give(0);
									goods.setBuyGift_id(null);
								}
							}
							this.buyGiftService.update(bg);
						}
						if (bg != null && bg.getGift_status() == 10) {
							objmap.put("isGift", true);
						}
					}
					if (obj.getOrder_enough_if_give() == 1) {
						BuyGift bg = this.buyGiftService.getObjById(obj
								.getBuyGift_id());
						if (bg != null && bg.getGift_status() == 10) {
							objmap.put("isGift", true);
						}
					}
					
					if (obj.getEnough_reduce() == 1) {
						EnoughReduce er = this.enoughReduceService
								.getObjById(CommUtil.null2Long(obj
										.getOrder_enough_reduce_id()));
						if (er.getErstatus() == 10
								&& er.getErbegin_time().before(new Date())
								&& er.getErend_time().after(new Date())) {// 正在进行
							Map ermap = new HashMap();
							ermap.put("ertag", er.getErtag());
							goodsmap.put("ertagmap", ermap);
						} else if (er.getErend_time().before(new Date())) {// 已过期
							er.setErstatus(20);
							this.enoughReduceService.update(er);
							String goods_json = er.getErgoods_ids_json();
							List<String> goods_id_list = (List) Json
									.fromJson(goods_json);
							for (String goods_id : goods_id_list) {
								Goods ergood = this.goodsService
										.getObjById(CommUtil.null2Long(goods_id));
								ergood.setEnough_reduce(0);
								ergood.setOrder_enough_reduce_id("");
								this.goodsService.update(ergood);
							}
						}
					}
					this.goodsService.update(obj);
					List<Map> goodsList = new ArrayList<Map>();
					
					List<Accessory> accessorys = obj.getGoods_photos();
					List<Map> acclist = new ArrayList<Map>();
					for(Accessory accessory:accessorys){
						Map accmap = new HashMap();
						accmap.put("photos", configService.getSysConfig().getImageWebServer()
								+ "/" 
								+ accessory.getPath()
								+ "/"
								+ accessory.getName()
								+ "_middle."
								+ accessory.getExt());
						acclist.add(accmap);
					}
					objmap.put("photos", acclist);
					objmap.put("goodsimg", obj.getGoods_main_photo() != null
							? imageWebServer + "/" + obj.getGoods_main_photo().getPath() + "/"
									+ obj.getGoods_main_photo().getName()
							: imageWebServer
									+ "/"
									+ this.configService.getSysConfig()
											.getGoodsImage().getPath()
									+ "/"
									+ this.configService.getSysConfig()
											.getGoodsImage().getName());
					objmap.put("goodsid", obj.getId());
					objmap.put("goodsname", obj.getGoods_name());
					objmap.put("goods_current_price", obj.getGoods_current_price());
					objmap.put("goods_evaluate_count", obj.getEvaluate_count());
					objmap.put("goods_price", obj.getGoods_price());
					objmap.put("well_evaluate", obj.getWell_evaluate()); 
					objmap.put("well_inventory", obj.getGoods_inventory()); 
					objmap.put("good_middle_evaluate", obj.getMiddle_evaluate());
					int all_eval = evaluateViewTools.queryByEva(CommUtil.null2String(obj.getId()),"all").size();
					int well_eval = evaluateViewTools.queryByEva(CommUtil.null2String(obj.getId()),"5").size();
					int middle_eval = evaluateViewTools.queryByEva(CommUtil.null2String(obj.getId()),"3").size();
					objmap.put("eva_all_count", all_eval);
					objmap.put("eva_well_count", well_eval);
					objmap.put("eva_middle_count", middle_eval);
					objmap.put("goods_bad_evaluate", obj.getBad_evaluate());
					objmap.put("goods_salenum", obj.getGoods_salenum());
					objmap.put("goods_cod", obj.getGoods_cod());
					objmap.put("goods_gc_id", obj.getGc().getParent().getParent().getId());
					objmap.put("act_status", obj.getActivity_status());
					objmap.put("group_buy", obj.getGroup_buy());
					objmap.put("give_status", obj.getOrder_enough_give_status());
					objmap.put("BuyGift_id", obj.getBuyGift_id());
					objmap.put("order_enough_give_status", obj.getOrder_enough_give_status()); 
					objmap.put("buyGift_amount", obj.getBuyGift_amount());
					objmap.put("order_enough_if_give", obj.getOrder_enough_if_give());
					objmap.put("goods_detail", obj.getGoods_details() == null ? "" : obj.getGoods_details());
					if(obj.getGoods_global() == 1){
						objmap.put("goods_arrival_time", "It is expected to be delivered in 8-15 working days");
					}else{
						if(obj.getGoods_global() == 2){
							objmap.put("goods_arrival_time", "It is expected to be delivered in 1-3 working days");	
						}
					}
					objmap.put("goods_discount_rate", obj.getGoods_discount_rate());
					objmap.put("advance_sale_type", obj.getAdvance_sale_type());
					if(user != null){
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("user_id", user.getId());
						params.put("goods_id", CommUtil.null2Long(id));
						List<Favorite> list1 = this.favoriteService
								.query("select obj from Favorite obj where obj.user_id=:user_id and obj.goods_id=:goods_id order by obj.goods_id ",
										params, -1, -1);
						if(list1.size() > 0){
							objmap.put("goods_favorite_goods", list1.size());
						}else{
							objmap.put("goods_favorite_goods", "0");
						}
					}
					objmap.put("advance_date", obj.getAdvance_date());
					objmap.put("features_one", obj.getFeatures_one() == null ? "" : obj.getFeatures_one());
					objmap.put("features_two", obj.getFeatures_two() == null ? "" : obj.getFeatures_two());
					objmap.put("features_three", obj.getFeatures_three() == null ? "" : obj.getFeatures_three());
					objmap.put("features_four", obj.getFeatures_four() == null ? "" : obj.getFeatures_four());
					objmap.put("features_five", obj.getFeatures_five() == null ? "" : obj.getFeatures_five());
					if(obj.getGoods_brand() == null){
						objmap.put("goods_brand_id", "");
						objmap.put("goods_brand_name", "");
					}else{
						objmap.put("goods_brand_id", obj.getGoods_brand().getId());
						objmap.put("goods_brand_name", obj.getGoods_brand().getName());
					}
					if(obj.getGoods_property() != null){
						List<Map> goods_property = this.orderFormTools.queryGoodsInfo(obj.getGoods_property());
						objmap.put("goods_property", goods_property);
					}else{
						objmap.put("goods_property", "");
					}
					
					if(obj.getGoods_store() != null && !obj.getGoods_store().equals("")){
						try {
							objmap.put("store_logo", this.configService.getSysConfig().getImageWebServer() +"/"+ obj.getGoods_store().getStore_logo().getPath() +"/"+ obj.getGoods_store().getStore_logo().getName());
					} catch (NullPointerException e) {
						// TODO Auto-generated catch block
						objmap.put("store_logo", "");
					}
				}
					goodsmap.put("goods", objmap);
					  
					//[计算档期访问用户IP地址，并计算对应的运费信息]
					String current_city = null;
					String current_ip = CommUtil.getIpAddr(request);// 获得本机IP
					if (CommUtil.isIp(current_ip)) {
						IPSeeker ip = new IPSeeker(null, null);
							current_city = ip.getIPLocation(current_ip)
								.getCountry();
						goodsmap.put("current_city", current_city);
					} else {
						goodsmap.put("current_city", "未知地区");
					}
					
					//[查询运费地区]
					List<Area> areas = this.areaService
							.query("select obj from Area obj where obj.parent.id is null order by obj.sequence asc",
									null, -1, -1);
					List<Map> arealist = new ArrayList<Map>();
					for(Area area:areas){
						Map areamap = new HashMap();
						areamap.put("areaid", area.getId());
						areamap.put("areaname", area.getAreaName());
						arealist.add(areamap);
					}
					goodsmap.put("area_info", arealist);

					Map storemap = new HashMap();
					//[查询商家评分信息 generic_evaluate ]
					if (obj.getGoods_type() == 0) {// 平台自营商品
						storemap.put("store_name", "self");
					} else {// 商家商品
						this.generic_evaluate(obj.getGoods_store(), mv);
						storemap.put("store_id", obj.getGoods_store().getId());
						storemap.put("store_name", obj.getGoods_store().getStore_name());
					} 
					goodsmap.put("storemap", storemap);
					
					//[根据区域查询运费]
					Map goods_transfee_map = new HashMap();
					if(obj.getGoods_transfee() == 1){
						goods_transfee_map.put("goods_transfee", "0");
						goods_transfee_map.put("trans", "seller");
					}else{
						String trans = null;
						float freight_price = 0;
						if(obj.getTransport().isTrans_mail()){
							trans = "mail";
							 freight_price = transportTools.cal_goods_trans_fee_metoo(CommUtil.null2String(obj.getTransport().getId()), "mail", obj, current_city);
						}
						if(obj.getTransport().isTrans_express()){
							trans = "express";
							 freight_price = transportTools.cal_goods_trans_fee_metoo(CommUtil.null2String(obj.getTransport().getId()), "express", obj, current_city);
						}
						if(obj.getTransport().isTrans_ems()){
							trans = "ems";
							 freight_price = transportTools.cal_goods_trans_fee_metoo(CommUtil.null2String(obj.getTransport().getId()), "ems", obj, current_city);
						}
						
						goods_transfee_map.put("goods_transfee", freight_price);
						goods_transfee_map.put("trans", trans);
					}
					goodsmap.put("goods_transfee_map", goods_transfee_map);
					
					//[商品规格]
					Map spec_map = this.metooGoodsViewTools.generic_spec(CommUtil.null2String(obj.getId()));
					spec_map.put("color", this.metooGoodsViewTools.color(CommUtil.null2String(obj.getId())));
					goodsmap.put("spec", spec_map);
					
					//[查询评价第一页]
					EvaluateQueryObject qo = new EvaluateQueryObject("1", mv,
							"addTime", "desc");
					qo.addQuery("obj.evaluate_goods.id", new SysMap("goods_id",
							CommUtil.null2Long(id)), "=");
					qo.addQuery("obj.evaluate_type", new SysMap("evaluate_type",
							"goods"), "=");
					qo.addQuery("obj.evaluate_status", new SysMap(
							"evaluate_status", 0), "=");
					qo.setPageSize(10);
					IPageList eva_pList = this.evaluateService.list(qo);
					objmap.put("currentpage", eva_pList.getCurrentPage());
					objmap.put("pages", eva_pList.getPages());
					List<Evaluate> evas = eva_pList.getResult();
					List<Map> eva_list = new ArrayList<Map>();
					for(Evaluate evaluate:evas){
						Map map = new HashMap();
						map.put("evaid", evaluate.getId());
						User eval_user = evaluate.getEvaluate_user();
						map.put("eva_userName", eval_user.getUserName());
						if(eval_user.getSex() == -1){
							map.put("user_photo", this.configService.getSysConfig().getImageWebServer()+"/"+"resources"+"/"+"style"+"/"+"common"+"/"+"images"+"/"+"member.png");
						}
						if(eval_user.getSex() == 0){
							map.put("user_photo", this.configService.getSysConfig().getImageWebServer()+"/"+"resources"+"/"+"style"+"/"+"common"+"/"+"images"+"/"+"member0.png");
						}
						if(eval_user.getSex() == 1){
							map.put("user_photo", this.configService.getSysConfig().getImageWebServer()+"/"+"resources"+"/"+"style"+"/"+"common"+"/"+"images"+"/"+"member1.png");
						}
						map.put("eva_addTime", evaluate.getAddTime());
						map.put("eva_description", evaluate.getDescription_evaluate());
						map.put("eva_service", evaluate.getService_evaluate());
						map.put("eva_ship", evaluate.getShip_evaluate());
						map.put("eva_add_info", evaluate.getAddeva_info());
						map.put("eva_reply_status", evaluate.getReply_status());
						map.put("eva_info", evaluate.getEvaluate_info() == null ? "" : evaluate.getEvaluate_info());
						map.put("eva_reply", evaluate.getReply());
						map.put("eva_add_status", evaluate.getAddeva_status());
						map.put("eva_buyer_val", evaluate.getEvaluate_buyer_val()); //买家评价，评价类型，1为好评，0为中评，-1为差评[1,2,3,4,5, 1-5星]
						Goods goods = evaluate.getEvaluate_goods();
						map.put("eva_well", goods.getWell_evaluate());// 商品好评率,例如：该值为0.96，好评率即为96%
						List<Accessory> eval_accessorys = this.evaluateViewTools.queryEvaImgSrc(evaluate.getEvaluate_photos());
						List<Map> eva_map_list = new ArrayList<Map>();
						if(!eval_accessorys.isEmpty()){
							for(Accessory accessory:eval_accessorys){
								Map photoMap = new HashMap();
								photoMap.put("Evaluate_photos", this.configService.getSysConfig().getImageWebServer()+"/"+accessory.getPath()+"/"+accessory.getName());
								eva_map_list.add(photoMap);
							}
							map.put("eva_photo", eva_map_list);
						}else{
							map.put("eva_photo", "");
						}
						List<Accessory> addAcc = imageTools.queryImgs(evaluate.getAddeva_photos());
						List<Map> add_eva_map_list = new ArrayList<Map>();
						if(CommUtil.isNotNull(add_eva_map_list)){
							for(Accessory accessory:addAcc){
								Map Addeva_photoMap = new HashMap();
								Addeva_photoMap.put("Evaluate_photos", this.configService.getSysConfig().getImageWebServer()+"/"+accessory.getPath()+"/"+accessory.getName());
								add_eva_map_list.add(Addeva_photoMap);
							}
							map.put("eva_add_photo", add_eva_map_list);
						}
						eva_list.add(map);
					}
					goodsmap.put("evalist", eva_list);
					objmap.put("eval_count", eva_list.size()); 
				}else{
					result = new Result(1,"Goods not on shelves");
				}
				if(!goodsmap.isEmpty()){
					result = new Result(0,"The query is successful",goodsmap);
				}else{
					result = new Result(-5,"Commodity audit",goodsmap);
				}
			}else{
				result = new Result(-1,"No item found");
			}

		String goodstemp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(goodstemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
     * 修改来源提示语言
     * @param key
     * @return
     */
	public String clickfrom_to_chinese(String key) {
		String str = "其它";
		if (key.equals("search")) {
			str = "搜索";
		}
		if (key.equals("floor")) {
			str = "首页楼层";
		}
		if (key.equals("gcase")) {
			str = "橱窗";
		}

		return str;
	}
	
	/**
	 * 查询运费地址的子集
	 * @param request
	 * @param response
	 * @param pid
	 */
	@RequestMapping("/query_areachilds.json")
	public void queryChild(HttpServletRequest request, HttpServletResponse response,String pid){
		Result result = null;
		List<Area> childlist = new ArrayList<Area>();
		Map childmap = new HashMap();
		if(!CommUtil.null2String(pid).equals("")){
			Map params = new HashMap();
			params.put("pid",CommUtil.null2Long(pid));
			childlist = this.areaService.query("select obj from Area obj where obj.parent.id=:pid order by obj.sequence asc", params, -1, -1);
			List<Map> areamaps = new ArrayList<Map>();
			for(Area area:childlist){
				Map areamap = new HashMap();
				areamap.put("areaid", area.getId());
				areamap.put("areaname", area.getAreaName());
				
				List<Area> areachildlist = area.getChilds();
				List<Map> areachildmaps = new ArrayList<Map>();
				for(Area areachild:areachildlist){
					Map areachildmap = new HashMap();
					areachildmap.put("areaid", areachild.getId());
					areachildmap.put("areaname", areachild.getAreaName());
					areachildmaps.add(areachildmap);
				}
				areamap.put("areachildmaps", areachildmaps);
				areamaps.add(areamap);
			}
			childmap.put("areamaps", areamaps);
			if(CommUtil.isNotNull(areamaps)){
				result = new Result(0,"查询成功",areamaps);
			}else{
				result = new Result(1,"查询失败");
			}
		}else{
			result = new Result(1,"参数错误");
		}
		String areachildtemp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(areachildtemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 加载店铺评分信息
	 * 
	 * @param store
	 * @param mv
	 */
	private void generic_evaluate(Store store, ModelAndView mv) {
		double description_result = 0;
		double service_result = 0;
		double ship_result = 0;
		GoodsClass gc = this.goodsClassService
				.getObjById(store.getGc_main_id());
		if (store != null && gc != null && store.getPoint() != null) {
			float description_evaluate = CommUtil.null2Float(gc
					.getDescription_evaluate());//[商品分类描述相符评分，同行业均分]
			float service_evaluate = CommUtil.null2Float(gc
					.getService_evaluate());//[商品分类服务态度评价，同行业均分]
			float ship_evaluate = CommUtil.null2Float(gc.getShip_evaluate());//[商品分类发货速度评价，同行业均分]

			float store_description_evaluate = CommUtil.null2Float(store
					.getPoint().getDescription_evaluate());//[描述相符评价]
			float store_service_evaluate = CommUtil.null2Float(store.getPoint()
					.getService_evaluate());//[服务态度评价]
			float store_ship_evaluate = CommUtil.null2Float(store.getPoint()
					.getShip_evaluate());//[发货速度评价]
			// 计算和同行比较结果
			description_result = CommUtil.div(store_description_evaluate
					- description_evaluate, description_evaluate);
			service_result = CommUtil.div(store_service_evaluate
					- service_evaluate, service_evaluate);
			ship_result = CommUtil.div(store_ship_evaluate - ship_evaluate,
					ship_evaluate);
		}
		if (description_result > 0) {
			mv.addObject("description_css", "value_strong");
			mv.addObject(
					"description_result",
					CommUtil.null2String(CommUtil.mul(description_result, 100) > 100
							? 100
							: CommUtil.mul(description_result, 100))
							+ "%");
		}
		if (description_result == 0) {
			mv.addObject("description_css", "value_normal");
			mv.addObject("description_result", "-----");
		}
		if (description_result < 0) {
			mv.addObject("description_css", "value_light");
			mv.addObject(
					"description_result",
					CommUtil.null2String(CommUtil.mul(-description_result, 100))
							+ "%");
		}
		if (service_result > 0) {
			mv.addObject("service_css", "value_strong");
			mv.addObject("service_result",
					CommUtil.null2String(CommUtil.mul(service_result, 100)>100?100
							:CommUtil.mul(service_result, 100))
							+ "%");
		}
		if (service_result == 0) {
			mv.addObject("service_css", "value_normal");
			mv.addObject("service_result", "-----");
		}
		if (service_result < 0) {
			mv.addObject("service_css", "value_light");
			mv.addObject("service_result",
					CommUtil.null2String(CommUtil.mul(-service_result, 100))
							+ "%");
		}
		if (ship_result > 0) {
			mv.addObject("ship_css", "value_strong");
			mv.addObject("ship_result",
					CommUtil.null2String(CommUtil.mul(ship_result, 100)>100?100
							:CommUtil.mul(ship_result, 100)) + "%");
		}
		if (ship_result == 0) {
			mv.addObject("ship_css", "value_normal");
			mv.addObject("ship_result", "-----");
		}
		if (ship_result < 0) {
			mv.addObject("ship_css", "value_light");
			mv.addObject("ship_result",
					CommUtil.null2String(CommUtil.mul(-ship_result, 100)) + "%");
		}
	}

	
	/**
	 * 查询用户评价 
	 */
	@RequestMapping("/goods_evaluation.json")
	public void goods_evaluation(HttpServletRequest request, HttpServletResponse response, String goods_id,
			String currentPage, String goods_eva){ 
		Map evaluatemap = new HashMap();
		Result result = null;
		ModelAndView mv = new JModelAndView("default/goods_evaluation.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.evaluate_goods.id",
				new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
		qo.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"),
				"=");
		qo.addQuery("obj.evaluate_status", new SysMap("evaluate_status", 0),
				"=");
		qo.setPageSize(10);
		qo.setCurrentPage(CommUtil.null2Int(currentPage));
		if (!CommUtil.null2String(goods_eva).equals("")) {
			if (goods_eva.equals("100")) {
				qo.addQuery("obj.evaluate_photos", new SysMap(
						"evaluate_photos", ""), "!=");
			} else {
				qo.addQuery("obj.evaluate_buyer_val", new SysMap(
						"evaluate_buyer_val", CommUtil.null2Int(goods_eva)),
						"=");
			}
		}
		IPageList pList = this.evaluateService.list(qo);
		List<Evaluate> evaluates = pList.getResult();		
		List<Map> evaMaps = new ArrayList<Map>();
		for(Evaluate evaluate:evaluates){
			Map evaMap = new HashMap();
			evaMap.put("evaid", evaluate.getId());
			User user = evaluate.getEvaluate_user();
			evaMap.put("eva_userName", user.getUserName());
			if(user.getSex() == -1){
				evaMap.put("user_photo", this.configService.getSysConfig().getImageWebServer()+"/"+"resources"+"/"+"style"+"/"+"common"+"/"+"images"+"/"+"member.png");
			}
			if(user.getSex() == 0){
				evaMap.put("user_photo", this.configService.getSysConfig().getImageWebServer()+"/"+"resources"+"/"+"style"+"/"+"common"+"/"+"images"+"/"+"member0.png");
			}
			if(user.getSex() == 1){
				evaMap.put("user_photo", this.configService.getSysConfig().getImageWebServer()+"/"+"resources"+"/"+"style"+"/"+"common"+"/"+"images"+"/"+"member1.png");
			}
			evaMap.put("eva_addTime", evaluate.getAddTime());
			evaMap.put("eva_description", evaluate.getDescription_evaluate());
			evaMap.put("eva_service", evaluate.getDescription_evaluate());
			evaMap.put("eva_service", evaluate.getService_evaluate());
			evaMap.put("eva_ship", evaluate.getShip_evaluate());
			evaMap.put("eva_add_info", evaluate.getAddeva_info());
			evaMap.put("eva_reply_status", evaluate.getReply_status());
			evaMap.put("eva_reply", evaluate.getReply());
			evaMap.put("eva_info", evaluate.getEvaluate_info() == null ? "" : evaluate.getEvaluate_info());
			evaMap.put("eva_add_status", evaluate.getAddeva_status());
			evaMap.put("eva_buyer_val", evaluate.getEvaluate_buyer_val()); //买家评价，评价类型，1为好评，0为中评，-1为差评[1,2,3,4,5, 1-5星]
			Goods goods = evaluate.getEvaluate_goods();
			evaMap.put("eva_well", goods.getWell_evaluate());// 商品好评率,例如：该值为0.96，好评率即为96%
			evaMap.put("eva_middle", goods.getMiddle_evaluate());
			int all_eval = evaluateViewTools.queryByEva(CommUtil.null2String(goods.getId()),"all").size();
			int well_eval = evaluateViewTools.queryByEva(CommUtil.null2String(goods.getId()),"5").size();
			int middle_eval = evaluateViewTools.queryByEva(CommUtil.null2String(goods.getId()),"3").size();
			evaMap.put("eva_all_count", all_eval);
			evaMap.put("eva_well_count", well_eval);
			evaMap.put("eva_middle_count", middle_eval);
			List<Accessory> Accessoryes = imageTools.queryImgs(evaluate.getEvaluate_photos());
			List<Map> photos = new ArrayList<Map>();
			if(!Accessoryes.isEmpty()){
				for(Accessory accessory:Accessoryes){
					Map map = new HashMap();
					map.put("Evaluate_photos", this.configService.getSysConfig().getImageWebServer()+"/"+accessory.getPath()+"/"+accessory.getName());
					photos.add(map);
				}
				evaMap.put("eva_photo", photos);
			}
			List<Accessory> addAccessoryes = imageTools.queryImgs(evaluate.getAddeva_photos());
			List<Map> addeva_photos = new ArrayList<Map>();
			if(CommUtil.isNotNull(addeva_photos)){
				for(Accessory accessory:addAccessoryes){
					Map map = new HashMap();
					map.put("Evaluate_photos", this.configService.getSysConfig().getImageWebServer()+"/"+accessory.getPath()+"/"+accessory.getName());
					addeva_photos.add(map);
				}
				evaMap.put("eva_add_photo", addeva_photos);
			}
			evaMaps.add(evaMap);
		}
		evaluatemap.put("evaluate", evaMaps);
		evaluatemap.put("currentpage", pList.getCurrentPage());
		evaluatemap.put("eval_count", evaMaps.size());
		evaluatemap.put("pages", pList.getPages());
		if(CommUtil.isNotNull(evaluatemap)){
			result = new Result(0,"查询成功",evaluatemap);
		}else{
			result = new Result(1,"查询失败");
		}
		String evaluatetemp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(evaluatetemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据用户浏览记录 猜你喜欢的商品列表
	 * 
	 * @param request
	 * 
	 * @param response
	 */
	@RequestMapping("/you_likegoods.json")
	public void yopu_like(HttpServletRequest request, HttpServletResponse response ){
		//[猜您喜欢，按cookie查询，没有cookie按销量查询]
		Map likemap = new HashMap();
		Result result = null;
		Map youlikemap = new HashMap();
		List<Goods> you_likegoods = new ArrayList<Goods>();
		Long your_like_GoodsClass = null;
		Cookie[] cookies = request.getCookies();
		if(cookies != null){
			for(Cookie cookie:cookies){
				if(cookie.getName().equals("goodscookie")){
					String[] like_gcid = cookie.getValue().split(",", 2);
					Goods obj = this.goodsService.getObjById(CommUtil
							.null2Long(like_gcid[0]));
					if (obj == null)
						break;
					your_like_GoodsClass = obj.getGc().getId();
					you_likegoods = this.goodsService
							.query("select obj from Goods obj where obj.goods_status=0 and obj.gc.id = "
									+ your_like_GoodsClass
									+ " and obj.id is not "
									+ obj.getId()
									+ " order by obj.goods_salenum desc", null,
									0, 20);
					int gcs_size = you_likegoods.size();
					if (gcs_size < 20) {
						List<Goods> like_goods = this.goodsService
								.query("select obj from Goods obj where obj.goods_status=0 and obj.id is not "
										+ obj.getId()
										+ " order by obj.goods_salenum desc",
										null, 0, 20 - gcs_size);
						for (int i = 0; i < like_goods.size(); i++) {
							// 去除重复商品
							int k = 0;
							for (int j = 0; j < you_likegoods.size(); j++) {
								if (like_goods.get(i).getId()
										.equals(you_likegoods.get(j).getId())) {
									k++;
								}
							}
							if (k == 0) {
								you_likegoods.add(like_goods.get(i));
							}
						}
					}
					List<Map> goodslist = new ArrayList<Map>();
					for(Goods goods:you_likegoods){
						Map goodsmap = new HashMap();
							goodsmap.put("goodsimg", this.configService.getSysConfig().getImageWebServer()+"/"+goods.getGoods_main_photo().getPath()+"/"+goods.getGoods_main_photo().getName());
							goodsmap.put("goodsid", goods.getId());
							goodsmap.put("goodsname", goods.getGoods_name());
							goodsmap.put("goodstype", goods.getGoods_type());
							goodsmap.put("goodswell_evaluate", goods.getWell_evaluate());
							goodsmap.put("goods_collect", goods.getGoods_collect());
							goodsmap.put("goodsprice", goods.getGoods_price());
							goodsmap.put("goods_current_price", goods.getGoods_current_price());
						goodslist.add(goodsmap);
					}
					youlikemap.put("goodslist", goodslist);
					break;
				}else{
					you_likegoods = this.goodsService
							.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
									null, 0, 20);
					List<Map> goodslist = new ArrayList<Map>();
					for(Goods goods:you_likegoods){
						Long id = goods.getId();
						Map goodsmap = new HashMap();
							goodsmap.put("goodsimg", this.configService.getSysConfig().getImageWebServer()+"/"+goods.getGoods_main_photo().getPath()+"/"+goods.getGoods_main_photo().getName());
							goodsmap.put("goodsid", goods.getId());
							goodsmap.put("goodsname", goods.getGoods_name());
							goodsmap.put("goodstype", goods.getGoods_type());
							goodsmap.put("goods_collect", goods.getGoods_collect());
							goodsmap.put("goodswell_evaluate", goods.getWell_evaluate());
							goodsmap.put("goodsprice", goods.getGoods_price());
							goodsmap.put("goods_current_price", goods.getGoods_current_price());
						
						if(!goodsmap.isEmpty()){
							goodslist.add(goodsmap);
						}
					}
					youlikemap.put("goodslist", goodslist);
				}
			}
		}else{
			you_likegoods = this.goodsService
					.query("select obj from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc ",
							null, 0, 80);
			List<Map> goodslist = new ArrayList<Map>();
			for(Goods obj:you_likegoods){
				Map goodsmap = new HashMap();
					goodsmap.put("goodsimg", this.configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName() +"_middle."+obj.getGoods_main_photo().getExt());
					goodsmap.put("goodsid", obj.getId());
					goodsmap.put("goodsname", obj.getGoods_name());
					goodsmap.put("goodstype", obj.getGoods_type());
					goodsmap.put("goods_collect", obj.getGoods_collect());
					goodsmap.put("goodswell_evaluate", obj.getWell_evaluate() == null ? 0 : obj.getWell_evaluate());
					goodsmap.put("goodsprice", obj.getGoods_price());
					goodsmap.put("goods_current_price", obj.getGoods_current_price());
					if(obj.getGoods_store() != null && !obj.getGoods_store().equals("")){
						try {
							goodsmap.put("store_logo", this.configService.getSysConfig().getImageWebServer() +"/"+ obj.getGoods_store().getStore_logo().getPath() +"/"+ obj.getGoods_store().getStore_logo().getName());
					} catch (NullPointerException e) {
						// TODO Auto-generated catch block
						goodsmap.put("store_logo", "");
					}
				}
				goodslist.add(goodsmap);
				
			}
			youlikemap.put("goodslist", goodslist);
	}
		if(CommUtil.isNotNull(youlikemap)){
			result = new Result(0,"success",youlikemap);
		}else{
			result = new Result(1,"error");
		}
		/*最近浏览手机端暂时不开放
		 * if (!ids.isEmpty()) {
			params.put("ids", ids);
			goods_last = this.goodsService
					.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.id in(:ids)",
							params, -1, -1);
		}*/
		String youliketemp = Json.toJson(result, JsonFormat.compact());
		try {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().print(youliketemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
	
	/**
	 * 根据用户浏览记录 猜你喜欢的商品列表
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/you_likegoods_repetition.json")
	public void yopu_like_repetition(HttpServletRequest request, HttpServletResponse response ){
		//[猜您喜欢，按cookie查询，没有cookie按销量查询]
		Map likemap = new HashMap();
		Result result = null;
		Map youlikemap = new HashMap();
		List<Map> configlist = new ArrayList<Map>();
		Map sysmap = new HashMap();
		SysConfig syscon = this.configService.getSysConfig();
		sysmap.put("imgwebServer", syscon.getImageWebServer());
		sysmap.put("imgpath", syscon.getGoodsImage().getPath());
		sysmap.put("imgname", syscon.getGoodsImage().getName());
		sysmap.put("Second_domain_open", syscon.getSecond_domain_open());
		configlist.add(sysmap);
		youlikemap.put("configlist", configlist);
		
		List<Goods> you_likegoods = new ArrayList<Goods>();
		Long your_like_GoodsClass = null;
		Cookie[] cookies = request.getCookies();
		if(cookies != null){
			for(Cookie cookie:cookies){
				if(cookie.getName().equals("goodscookie")){
					String[] like_gcid = cookie.getValue().split(",", 2);
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(like_gcid[0]));
					if (goods == null)
						break;
					your_like_GoodsClass = goods.getGc().getId();
					you_likegoods = this.goodsService
							.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.goods_status=0 and obj.gc.id = "
									+ your_like_GoodsClass
									+ " and obj.id is not "
									+ goods.getId()
									+ " order by obj.goods_salenum desc", null,
									0, 20);
					int gcs_size = you_likegoods.size();
					List<Goods> repetition = null;
					if (gcs_size < 20) {
						List<Goods> like_goods = this.goodsService
								.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.goods_status=0 and obj.id is not "
										+ goods.getId()
										+ " order by obj.goods_salenum desc",
										null, 0, 20 - gcs_size);
						/*for (int i = 0; i < like_goods.size(); i++) {
							// 去除重复商品
							int k = 0;
							for (int j = 0; j < you_likegoods.size(); j++) {
								if (like_goods.get(i).getId()
										.equals(you_likegoods.get(j).getId())) {
									k++;
								}
							}
							if (k == 0) {
								you_likegoods.add(like_goods.get(i));
							}
						}*/
						you_likegoods.addAll(like_goods);
						repetition = new ArrayList<Goods>(new HashSet(you_likegoods));
					}
					List<Map> goodslist = new ArrayList<Map>();
					for(Goods likegoods:repetition){
						Map goodsmap = new HashMap();
						goodsmap.put("goodsimg", syscon.getImageWebServer()+"/"+likegoods.getGoods_main_photo().getPath()+"/"+goods.getGoods_main_photo().getName());
						goodsmap.put("goodsid", likegoods.getId());
						goodsmap.put("goodsname", likegoods.getGoods_name());
						goodsmap.put("goodstype", likegoods.getGoods_type());
						goodsmap.put("goodswell_evaluate", likegoods.getWell_evaluate());
						goodsmap.put("goodsprice", likegoods.getGoods_price());
						goodsmap.put("goods_current_price", likegoods.getGoods_current_price());
						goodslist.add(goodsmap);
						
					}
					youlikemap.put("goodslist", goodslist);
					break;
				}else{
					you_likegoods = this.goodsService
							.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc",
									null, 0, 20);
					List<Map> goodslist = new ArrayList<Map>();
					for(Goods goods:you_likegoods){
						Map goodsmap = new HashMap();
						goodsmap.put("goodsimg", goods.getGoods_main_photo().getPath()+"/"+goods.getGoods_main_photo().getName());
						goodsmap.put("goodsid", goods.getId());
						goodsmap.put("goodsname", goods.getGoods_name());
						goodsmap.put("goodstype", goods.getGoods_type());
						goodsmap.put("goodswell_evaluate", goods.getWell_evaluate());
						goodsmap.put("goodsprice", goods.getGoods_price());
						goodsmap.put("goods_current_price", goods.getGoods_current_price());
						goodslist.add(goodsmap);
						
					}
					youlikemap.put("goodslist", goodslist);
					
				}
			}
		}else{
			you_likegoods = this.goodsService
					.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.goods_status=0 order by obj.goods_salenum desc ",
							null, 0, 20);
			List<Map> goodslist = new ArrayList<Map>();
			for(Goods goods:you_likegoods){
				Map goodsmap = new HashMap();
				goodsmap.put("goodsimg", goods.getGoods_main_photo().getPath()+"/"+goods.getGoods_main_photo().getName());
				goodsmap.put("goodsid", goods.getId());
				goodsmap.put("goodsname", goods.getGoods_name());
				goodsmap.put("goodstype", goods.getGoods_type());
				goodsmap.put("goodswell_evaluate", goods.getWell_evaluate());
				goodsmap.put("goodsprice", goods.getGoods_price());
				goodsmap.put("goods_current_price", goods.getGoods_current_price());
				goodslist.add(goodsmap);
				
			}
			youlikemap.put("goodslist", goodslist);
		}
		if(CommUtil.isNotNull(youlikemap)){
			result = new Result(0,"查询成功",youlikemap);
		}else{
			result = new Result(1,"查询失败");
		}
		String youliketemp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(youliketemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * 根据商城分类查看商品列表
	 * 
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping("/wap_store_goods_list.json")
	public void store_goods_list(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String currentPage,
			String orderBy, String orderType, String brand_ids, String gs_ids,
			String properties, String all_property_status,
			String detail_property_status, String goods_type,
			String goods_inventory, String goods_transfee, String goods_cod, String gc_ids) {
		Result result = null;
		String imageWebServer = this.configService.getSysConfig().getImageWebServer();
		Map wap_map = new HashMap();
		ModelAndView mv = new JModelAndView("store_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsClass gc = this.goodsClassService.getObjById(CommUtil
				.null2Long(gc_id));
		Set gc_list = new TreeSet();
		if (gc != null) {
			if (gc.getLevel() == 0) {
				gc_list = gc.getChilds();
			} else if (gc.getLevel() == 1) {
				gc_list = gc.getParent().getChilds();
			} else if (gc.getLevel() == 2) {
				gc_list = gc.getParent().getParent().getChilds();
			}
		
			List<Map> gc_list_map = new ArrayList<Map>();
			for(Iterator<GoodsClass> iterator = gc_list.iterator();iterator.hasNext();){
				GoodsClass goodsclass = iterator.next();
				Map gc_map = new HashMap();
				gc_map.put("gc_id", goodsclass.getId());
				gc_map.put("gc_name", goodsclass.getClassName());
				if(goodsclass.getChilds() != null && !goodsclass.getChilds().equals("")){
					List<Map> gcc_list_map = new ArrayList<Map>();
					for(Iterator<GoodsClass> iteratorc = goodsclass.getChilds().iterator();iterator.hasNext();){
						GoodsClass goodsclassc = iterator.next();
						Map gcc_map = new HashMap();
						gcc_map.put("gcc_name", goodsclassc.getClassName());
						gcc_map.put("gcc_id", goodsclassc.getId());
						gcc_list_map.add(gcc_map);
					}
					gc_map.put("gcc_list_map", gcc_list_map);
					gc_list_map.add(gc_map);
				}
			}
		}
		
		if (orderBy == null || orderBy.equals("")) {
			orderBy = "addTime";
		}
		if (orderType == null || orderType.equals("")) {
			orderType = "desc";
		}
		GoodsQueryObject gqo = new GoodsQueryObject(null, currentPage, mv,
				orderBy, orderType);
		Set<Long> ids = null;
		if (gc != null) {
			ids = this.genericIds(gc.getId());
		}
		if (ids != null && ids.size() > 0) {
			Map paras = new HashMap();
			paras.put("ids", ids);
			gqo.addQuery("obj.gc.id in (:ids)", paras);
		}else{
			Set<Long> goodsclass_id = null;
			goodsclass_id = this.genericGcIds(gc_ids);
			if (goodsclass_id != null && !goodsclass_id.equals("")) {
				Map paras = new HashMap();
				paras.put("ids", goodsclass_id);
				gqo.addQuery("obj.gc.id in (:ids)", paras);
			}
		}
		
		
		if (goods_cod != null && !goods_cod.equals("")) {
			gqo.addQuery("obj.goods_cod", new SysMap("goods_cod", 0), "=");
			mv.addObject("goods_cod", goods_cod);
		}
		if (goods_transfee != null && !goods_transfee.equals("")) {
			gqo.addQuery("obj.goods_transfee", new SysMap("goods_transfee", 1),
					"=");
			mv.addObject("goods_transfee", goods_transfee);
		}
		gqo.setPageSize(24);// 设定分页查询，每页24件商品
		gqo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		
		List<Map> goods_property = new ArrayList<Map>();
		
		if (!CommUtil.null2String(brand_ids).equals("")) {
			if (brand_ids.indexOf(",") < 0) {
				brand_ids = brand_ids + ",";
			}
			String[] brand_id_list = CommUtil.null2String(brand_ids).split(",");
			if (brand_id_list.length == 1) {
				String brand_id = brand_id_list[0];
				gqo.addQuery("obj.goods_brand.id", new SysMap("brand_id",
						CommUtil.null2Long(brand_id)), "=", "and");
				Map map = new HashMap();
				GoodsBrand brand = this.brandService.getObjById(CommUtil
						.null2Long(brand_id));
				if (brand != null) {
					map.put("name", "品牌");
					map.put("value", brand.getName());
					map.put("type", "brand");
					map.put("id", brand.getId());
					goods_property.add(map);
				}
			} else {
				for (int i = 0; i < brand_id_list.length; i++) {
					String brand_id = brand_id_list[i];
					if (i == 0) {
						gqo.addQuery(
								"and (obj.goods_brand.id="
										+ CommUtil.null2Long(brand_id), null);
						Map map = new HashMap();
						GoodsBrand brand = this.brandService
								.getObjById(CommUtil.null2Long(brand_id));
						map.put("name", "品牌");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					} else if (i == brand_id_list.length - 1) {
						gqo.addQuery(
								"or obj.goods_brand.id="
										+ CommUtil.null2Long(brand_id) + ")",
								null);
						Map map = new HashMap();
						GoodsBrand brand = this.brandService
								.getObjById(CommUtil.null2Long(brand_id));
						map.put("name", "品牌");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					} else {
						gqo.addQuery(
								"or obj.goods_brand.id="
										+ CommUtil.null2Long(brand_id), null);
						Map map = new HashMap();
						GoodsBrand brand = this.brandService
								.getObjById(CommUtil.null2Long(brand_id));
						map.put("name", "品牌");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					}
				}
			}
			if (brand_ids != null && !brand_ids.equals("")) {
				wap_map.put("brand_ids", brand_ids);
			}
		}
		
		if (!CommUtil.null2String(gs_ids).equals("")) {
			List<List<GoodsSpecProperty>> gsp_lists = this.generic_gsp(gs_ids);
			for (int j = 0; j < gsp_lists.size(); j++) {
				List<GoodsSpecProperty> gsp_list = gsp_lists.get(j);
				if (gsp_list.size() == 1) {
					GoodsSpecProperty gsp = gsp_list.get(0);
					gqo.addQuery("gsp" + j, gsp, "obj.goods_specs",
							"member of", "and");
					Map map = new HashMap();
					map.put("spec_name", gsp.getSpec().getName());
					map.put("value", gsp.getValue());
					map.put("type", "gs");
					map.put("id", gsp.getId());
					goods_property.add(map);
				} else {
					for (int i = 0; i < gsp_list.size(); i++) {
						if (i == 0) {
							GoodsSpecProperty gsp = gsp_list.get(i);
							gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs",
									"member of", "and(");
							Map map = new HashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						} else if (i == gsp_list.size() - 1) {
							GoodsSpecProperty gsp = gsp_list.get(i);
							gqo.addQuery("gsp" + j + i, gsp,
									"obj.goods_specs)", "member of", "or");
							Map map = new HashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						} else {
							GoodsSpecProperty gsp = gsp_list.get(i);
							gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs",
									"member of", "or");
							Map map = new HashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						}
					}
				}
			}
			
			wap_map.put("gs_ids", gs_ids);
		}
		
		
		List<Map> propertylist = null;
		if (!CommUtil.null2String(properties).equals("")) {
			String[] properties_list = properties.substring(1).split("\\|");
			for (int i = 0; i < properties_list.length; i++) {
				String property_info = CommUtil.null2String(properties_list[i]);
				String[] property_info_list = property_info.split(",");
				GoodsTypeProperty gtp = this.goodsTypePropertyService
						.getObjById(CommUtil.null2Long(property_info_list[0]));
				Map p_map = new HashMap();
				p_map.put("gtp_name" + i, "%" + gtp.getName().trim() + "%");
				p_map.put("gtp_value" + i, "%" + property_info_list[1].trim()
						+ "%");
				gqo.addQuery("and (obj.goods_property like :gtp_name" + i
						+ " and obj.goods_property like :gtp_value" + i + ")",
						p_map);
				Map map = new HashMap();
				map.put("name", gtp.getName());
				map.put("value", property_info_list[1]);
				map.put("type", "properties");
				map.put("id", gtp.getId());
				goods_property.add(map);
			}
			wap_map.put("properties", properties);
			
			// 处理筛选类型互斥,|1,超短裙（小于75cm）|2,纯色
			List<GoodsTypeProperty> filter_properties = new ArrayList<GoodsTypeProperty>();
			List<String> hc_property_list = new ArrayList<String>();// 已经互斥处理过的属性值，在循环中不再处理
			if (gc.getGoodsType() != null) {
				for (GoodsTypeProperty gtp : gc.getGoodsType().getProperties()) {
					// System.out.println(gtp.getName() + "," + gtp.getValue());
					boolean flag = true;
					GoodsTypeProperty gtp1 = new GoodsTypeProperty();
					gtp1.setDisplay(gtp.isDisplay());
					gtp1.setGoodsType(gtp.getGoodsType());
					gtp1.setHc_value(gtp.getHc_value());
					gtp1.setId(gtp.getId());
					gtp1.setName(gtp.getName());
					gtp1.setSequence(gtp.getSequence());
					gtp1.setValue(gtp.getValue());
					for (String hc_property : hc_property_list) {
						String[] hc_list = hc_property.split(":");
						if (hc_list[0].equals(gtp.getName())) {
							String[] hc_temp_list = hc_list[1].split(",");
							String[] defalut_list_value = gtp1.getValue()
									.split(",");
							ArrayList<String> defalut_list = new ArrayList<String>(
									Arrays.asList(defalut_list_value));
							for (String hc_temp : hc_temp_list) {
								defalut_list.remove(hc_temp);
							}
							String value = "";
							for (int i = defalut_list.size() - 1; i >= 0; i--) {
								value = defalut_list.get(i) + "," + value;
							}
							gtp1.setValue(value.substring(0, value.length() - 1));
							flag = false;
							break;
						}

					}
					if (flag) {
						if (!CommUtil.null2String(gtp.getHc_value()).equals("")) {// 取消互斥类型
							// System.out.println(gtp.getHc_value());
							String[] list1 = gtp.getHc_value().split("#");
							for (int i = 0; i < properties_list.length; i++) {
								String property_info = CommUtil
										.null2String(properties_list[i]);
								String[] property_info_list = property_info
										.split(",");
								if (property_info_list[1].equals(list1[0])) {// 存在该互斥，则需要进行处理
									hc_property_list.add(list1[1]);
								}
							}

						}
						filter_properties.add(gtp);
					} else {
						filter_properties.add(gtp1);
					}
				}
				Map goodsTypemap = new HashMap();
				 propertylist = new ArrayList<Map>();
				for(GoodsTypeProperty goodsType:filter_properties){
					goodsTypemap.put("id", goodsType.getId());
					goodsTypemap.put("name", goodsType.getName());
					//[属性可选值]
					goodsTypemap.put("value", CommUtil.splitByChar(goodsType.getValue(),","));
					propertylist.add(goodsTypemap);
				}
				wap_map.put("propertylist",propertylist);
			}
		} else {
			// 处理筛选类型互斥
			try {
				if(gc.getGoodsType() != null){
					List<GoodsTypeProperty> goodstype_property = gc.getGoodsType().getProperties();
					

					List<Map> goodsTypeProperty_list = new ArrayList<Map>(); 
					
					for(GoodsTypeProperty goodsTypeProperty : goodstype_property){
						Map goodsTypePropertyMap = new HashMap();
						goodsTypePropertyMap.put("Property_id", goodsTypeProperty.getId());
						goodsTypePropertyMap.put("Property_name", goodsTypeProperty.getName());
						
						List<Map> goodsTypeProperty_v_list = new ArrayList<Map>(); 
						for(String s : CommUtil.splitByChar(goodsTypeProperty.getValue(),",")){
							Map goodsTypeProperty_v_Map = new HashMap();
							goodsTypeProperty_v_Map.put("v_info", s);
							goodsTypeProperty_v_list.add(goodsTypeProperty_v_Map);
						}
						goodsTypePropertyMap.put("v_info_value", goodsTypeProperty_v_list);
						goodsTypeProperty_list.add(goodsTypePropertyMap);
						}
						wap_map.put("propertylist",goodsTypeProperty_list);
					}else{
						wap_map.put("propertylist","");
					}
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				wap_map.put("propertylist","");
			}
			}
		
			try {
				if(gc.getGoodsType().getGbs().size()>0){
					List<Map> gb_list = new ArrayList<Map>();
					for(GoodsBrand gb : gc.getGoodsType().getGbs()){
						Map gbmap = new HashMap();
						gbmap.put("gb_id", gb.getId());
						gbmap.put("gb_name", gb.getName());
						gbmap.put("gb_photo", this.configService.getSysConfig().getImageWebServer()+"/"+gb.getBrandLogo().getPath()+"/"+gb.getBrandLogo().getName());
						gb_list.add(gbmap);
					}
					wap_map.put("gb_list", gb_list);
				}
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				wap_map.put("gb_list", "");
			}
		
		
		/*if (CommUtil.null2Int(goods_inventory) == 0) {// 查询库存大于0
			gqo.addQuery("obj.goods_inventory",
					new SysMap("goods_inventory", 0), ">");
		}
*/		if (!CommUtil.null2String(goods_type).equals("")
				&& CommUtil.null2Int(goods_type) != -1) {// 查询自营或者第三方经销商商品
			gqo.addQuery("obj.goods_type",
					new SysMap("goods_type", CommUtil.null2Int(goods_type)),
					"=");
		}
		IPageList pList = this.goodsService.list(gqo);
		wap_map.put("goods_Pages", pList.getPages());
		//[商品信息]
		List<Goods> obj = pList.getResult();
		List<Map> goodslist = new ArrayList<Map>();
		Set<Map> goodsset = new HashSet<Map>();
 		for(Goods goods:obj){
 			Map goodsmap = new HashMap();
 	/*原用于查询子商品的第一个商品	if(goods.getCgoods().size() > 0){
				for(CGoods cgs : goods.getCgoods()){
					if(cgs.getGoods_disabled().equals("0")){
						goodsmap.put("cgoodsId", cgs.getId());
						goodsmap.put("goodsid", goods.getId());
						goodsmap.put("goodsType", goods.getGoods_type());
						goodsmap.put("goods_name", goods.getGoods_name());
						goodsmap.put("well_evaluate", goods.getWell_evaluate());
						goodsmap.put("goods_price", cgs.getGoods_price());
						goodsmap.put("goods_current_price", cgs.getDiscount_price());
						goodsmap.put("goods_inventory", cgs.getGoods_inventory());
						if(CommUtil.isNotNull(cgs.getC_goods_photos()) && CommUtil.isNotNull(goods.getGoods_main_photo())){
							goodsmap.put("goods_main_photo", this.configService.getSysConfig().getImageWebServer()+"/"+goods.getGoods_main_photo().getPath()+"/"+goods.getGoods_main_photo().getName());
						}else{
							goodsmap.put("goods_main_photo", this.configService.getSysConfig().getImageWebServer()+"/"+cgs.getC_goods_photos().get(0).getPath()+"/"+cgs.getC_goods_photos().get(0).getName());
						}
						if(goods.getGoods_store() != null && !goods.getGoods_store().equals("")){
							try {
								goodsmap.put("store_logo", this.configService.getSysConfig().getImageWebServer() +"/"+ goods.getGoods_store().getStore_logo().getPath() +"/"+ goods.getGoods_store().getStore_logo().getName());
						} catch (NullPointerException e) {
							// TODO Auto-generated catch block
							goodsmap.put("store_logo", "");
						}
					}
					}
					break;
				}
			}else{*/
				goodsmap.put("goodsid", goods.getId());
				goodsmap.put("goods_name", goods.getGoods_name());
				goodsmap.put("goods_price", goods.getGoods_price());
				goodsmap.put("goods_current_price", goods.getGoods_current_price());
				goodsmap.put("well_evaluate", goods.getWell_evaluate() == null ? 0 : goods.getWell_evaluate());	
				goodsmap.put("goods_main_photo", goods.getGoods_main_photo() != null
						? imageWebServer + "/" + goods.getGoods_main_photo().getPath() + "/"
								+ goods.getGoods_main_photo().getName() +"_middle."+ goods.getGoods_main_photo().getExt()
						: imageWebServer
								+ "/"
								+ this.configService.getSysConfig()
										.getGoodsImage().getPath()
								+ "/"
								+ this.configService.getSysConfig()
										.getGoodsImage().getName()
								+ "_middle."
								+this.configService.getSysConfig()
								.getGoodsImage().getExt());
					goodsmap.put("goodsext", goods.getAccessory() != null
							 ? goods.getAccessory().getExt() : null);
				List<Accessory> acc = goods.getGoods_photos();
				
				List<Map> acclist = new ArrayList<Map>();
				for(Accessory accessory:acc){
					Map accmap = new HashMap();
					accmap.put("photos", configService.getSysConfig().getImageWebServer() +"/"+ accessory.getPath() +"/"+ accessory.getName() +"_middle."+ accessory.getExt());
					acclist.add(accmap);
				}
				
				goodsmap.put("goods_photo", acclist);
				if(goods.getGoods_store() != null && !goods.getGoods_store().equals("")){
					try {
					goodsmap.put("store_logo", this.configService.getSysConfig().getImageWebServer() +"/"+ goods.getGoods_store().getStore_logo().getPath() +"/"+ goods.getGoods_store().getStore_logo().getName());
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					goodsmap.put("store_logo", "");
				}
			}
		//}
		goodslist.add(goodsmap);
		}
 		wap_map.put("goodslist", goodslist);
 		//[已选择商品属性]
		Map goods_property_map = new HashMap();
		List<Map> goods_property_list = new ArrayList<Map>();
		for(Map map : goods_property){
			goods_property_map.put("name", map.get("name"));
			goods_property_map.put("value", map.get("value"));
			goods_property_map.put("id", map.get("id"));
			goods_property_map.put("type", map.get("type"));
			goods_property_list.add(goods_property_map);
		}
		wap_map.put("goods_property_list", goods_property_list);
		wap_map.put("orderBy", orderBy);
		wap_map.put("goods_property", goods_property);
		wap_map.put("allCount", pList.getRowCount());
		if (detail_property_status != null
				&& !detail_property_status.equals("")) {
			mv.addObject("detail_property_status", detail_property_status);
			String temp_str[] = detail_property_status.split(",");
			Map pro_map = new HashMap();
			List pro_list = new ArrayList();
			for (String property_status : temp_str) {
				if (property_status != null && !property_status.equals("")) {
					String mark[] = property_status.split("_");
					pro_map.put(mark[0], mark[1]);
					
					pro_list.add(mark[0]);
				}
			}
			/*mv.addObject("pro_list", pro_list);
			mv.addObject("pro_map", pro_map);*/
		}
		wap_map.put("all_property_status", all_property_status);
		
		// 计算当期访问用户的IP地址，并计算对应的运费信息
		String current_ip = CommUtil.getIpAddr(request);// 获得本机IP
		if (CommUtil.isIp(current_ip)) {
			IPSeeker ip = new IPSeeker(null, null);
			String current_city = ip.getIPLocation(current_ip).getCountry();
			wap_map.put("current_city", current_city);
			//mv.addObject("current_city", current_city);
		} else {
			wap_map.put("current_city", "未知地区");
		}
		wap_map.put("goods_inventory", CommUtil.null2Int(goods_inventory));
		wap_map.put("goods_type",CommUtil.null2String(goods_type).equals("")
				? -1
				: CommUtil.null2Int(goods_type));
		if(CommUtil.isNotNull(wap_map)){
			result = new Result(0,"查询成功",wap_map);
		}else{
			result = new Result(1,"查询失败");
		}
		String gctemp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(gctemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Set<Long> genericIds(Long id) {
		Set<Long> ids = new HashSet<Long>();
		if (id != null) {
			ids.add(id);
			Map params = new HashMap();
			params.put("pid", id);
			List id_list = this.goodsClassService
					.query("select obj.id from GoodsClass obj where obj.parent.id=:pid",
							params, -1, -1);
			ids.addAll(id_list);
			for (int i = 0; i < id_list.size(); i++) {
				Long cid = CommUtil.null2Long(id_list.get(i));
				Set<Long> cids = genericIds(cid);
				ids.add(cid);
				ids.addAll(cids);
			}
		}
		return ids;
	}
	
	/**
	 * 查询多个一级类目的子类目
	 * @param gclass_ids
	 * @return
	 */
	private Set<Long> genericGcIds(String gclass_ids) {
		String[] gc_ids = gclass_ids.split(",");
		Set<Long> ids = new HashSet<Long>();
		for(String id : gc_ids){
			if (id != null) {
				Long lid = Long.parseLong(id);
				ids.add(lid);
				Map params = new HashMap();
				params.put("pid", lid);
				List id_list = this.goodsClassService
						.query("select obj.id from GoodsClass obj where obj.parent.id=:pid",
								params, -1, -1);
				ids.addAll(id_list);
				for (int i = 0; i < id_list.size(); i++) {
					Long cid = CommUtil.null2Long(id_list.get(i));
					Set<Long> cids = genericIds(cid);
					ids.add(cid);
					ids.addAll(cids);
				}
			}
		}
		return ids;
	}
	private List<List<GoodsSpecProperty>> generic_gsp(String gs_ids) {
		List<List<GoodsSpecProperty>> list = new ArrayList<List<GoodsSpecProperty>>();
		String[] gs_id_list = gs_ids.substring(1).split("\\|");
		for (String gd_id_info : gs_id_list) {
			String[] gs_info_list = gd_id_info.split(",");
			GoodsSpecProperty gsp = this.goodsSpecPropertyService
					.getObjById(CommUtil.null2Long(gs_info_list[0]));
			boolean create = true;
			for (List<GoodsSpecProperty> gsp_list : list) {
				for (GoodsSpecProperty gsp_temp : gsp_list) {
					if (gsp_temp.getSpec().getId()
							.equals(gsp.getSpec().getId())) {
						gsp_list.add(gsp);
						create = false;
						break;
					}
				}
			}
			if (create) {
				List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();
				gsps.add(gsp);
				list.add(gsps);
			}
		}
		return list;
	}
	
	
	/**
	 * wap根据商品显示同类商品最热商品
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param gs_ids
	 * @param goods_type
	 */
	@RequestMapping("/wap_hot_goods.json")
	public void gc_hot_goods(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String currentPage,
			String orderBy, String orderType, String gs_ids, String goods_type){
		
		Result result = null;
		String imageWebServer = this.configService.getSysConfig().getImageWebServer();
		if(gc_id == null && gc_id.equals("")){
			result = new Result(1,"参数错误");
		}else{
		
		GoodsClass gc = this.goodsClassService.getObjById(CommUtil
				.null2Long(gc_id));
		ModelAndView mv = new JModelAndView("",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("gc", gc);
		Set gc_list = new TreeSet();
		if (gc != null) {
			if (gc.getLevel() == 0) {
				gc_list = gc.getChilds();
			} else if (gc.getLevel() == 1) {
				gc_list = gc.getParent().getChilds();
			} else if (gc.getLevel() == 2) {
				gc_list = gc.getParent().getParent().getChilds();
			}
		}
		mv.addObject("gc_list", gc_list);
		if (orderBy == null || orderBy.equals("")) {
			orderBy = "goods_salenum";
		}
		if (orderType == null || orderType.equals("")) {
			orderType = "desc";
		}
		GoodsQueryObject gqo = new GoodsQueryObject(null, currentPage, mv,
				orderBy, orderType);
		Set<Long> ids = null;
		if (gc != null) {
			ids = this.genericIds(gc.getId());
		}
		if (ids != null && ids.size() > 0) {
			Map paras = new HashMap();
			paras.put("ids", ids);
			gqo.addQuery("obj.gc.id in (:ids)", paras);
		}
		gqo.setPageSize(1);// 设定分页查询，每页24件商品
		gqo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		if (!CommUtil.null2String(goods_type).equals("")
				&& CommUtil.null2Int(goods_type) != -1) {// 查询自营或者第三方经销商商品
			gqo.addQuery("obj.goods_type",
					new SysMap("goods_type", CommUtil.null2Int(goods_type)),
					"=");
		}
		IPageList pList = this.goodsService.list(gqo);
		List<Goods> obj = pList.getResult();
		List<Map> goodslist = new ArrayList<Map>();
 		for(Goods goods:obj){
			Map goodsmap = new HashMap();
			goodsmap.put("goodsid", goods.getId());
			goodsmap.put("goods_name", goods.getGoods_name());
			goodsmap.put("well_evaluate", goods.getWell_evaluate());
			goodsmap.put("goods_img_path", goods.getGoods_main_photo() != null
					? imageWebServer + "/" + goods.getGoods_main_photo().getPath() + "/"
							+ goods.getGoods_main_photo().getName()
					: imageWebServer
							+ "/"
							+ this.configService.getSysConfig()
									.getGoodsImage().getPath()
							+ "/"
							+ this.configService.getSysConfig()
									.getGoodsImage().getName());
			goodsmap.put("goodsext", goods.getAccessory() != null
					 ? goods.getAccessory().getExt() : null);
			goodslist.add(goodsmap);
			
		}
 		if(CommUtil.isNotNull(goodslist)){
 			result = new Result(0,"查询成功",goodslist);
 		}else{
 			result = new Result(1,"查询失败");
 		}
 		
	}
		String goodslisttemp = Json.toJson(result, JsonFormat.compact());
 		try {
			response.getWriter().print(goodslisttemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		
	}

	/**
	 * @methodsName index_discount
	 * @description 根据store id 查询商品列表
	 * @param request
	 * @param response
	 * @param orderBy 排序字段
	 * @param orderType	排序类型
	 * @param currentPage 当前页数
	 * @param store_recommend 是否被推荐
	 * @param store_creativity 商城精选
	 * @param goods_global 国际直邮 普通商品为2 国际直邮为1
	 * @param goods_type 商品类型
	 * @param goods_store 店铺id
	 * @param token 用户token
	 * 
	 */
	@RequestMapping("/store_goods_info.json")
	public void index_discount(HttpServletRequest request,
			HttpServletResponse response,String orderBy, String orderType,String currentPage,
				String store_recommend, String store_creativity,
					String goods_global, String goods_type, String goods_store, String token){
		ModelAndView mv = new JModelAndView("",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Result result = null;
		Store store = new Store();
		User user = new User();
		Map discountmap = new HashMap();
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
			 user = users.get(0);
			}
		}
		if (orderBy == null || orderBy.equals("")) {
			orderBy = "addTime";
		}
		if (orderType == null || orderType.equals("")) {
			orderType = "desc";
		}
		GoodsQueryObject gqo = new GoodsQueryObject(null, currentPage, mv,
				orderBy, orderType);
		if (store_recommend != null && !store_recommend.equals("")) {
			gqo.addQuery(
					"obj.store_recommend",
					new SysMap("goods_store_recommend", CommUtil
							.null2Boolean(store_recommend)), "=");
			mv.addObject("store_recommend", store_recommend);
		}
		if (store_creativity != null && !store_creativity.equals("")) {
			gqo.addQuery(
					"obj.store_creativity",
					new SysMap("store_creativity", CommUtil
							.null2Boolean(store_creativity)), "=");
			mv.addObject("store_creativity", store_creativity);
		}
		if(goods_global != null && !goods_global.equals("")){
			gqo.addQuery("obj.goods_global", 
						new SysMap("goods_global", CommUtil.null2Int(goods_global)), "=");
			mv.addObject("goods_global", goods_global);
		}
		if(goods_type != null && !goods_type.equals("")){
			gqo.addQuery("obj.goods_type", 
					new SysMap("goods_type", CommUtil.null2Int(goods_type)),  "=");
		}
		if(goods_store != null && !goods_store.equals("")){
			store = this.storeService.getObjById(CommUtil.null2Long(goods_store));
			gqo.addQuery("obj.goods_store.id", 
					new SysMap("store_id", CommUtil.null2Long(goods_store)),  "=");
		}
		gqo.setPageSize(30);
		gqo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		IPageList pList = this.goodsService.list(gqo);
		List<Goods> goods = pList.getResult();
		List goodslist = new ArrayList();
		for(Goods obj : goods){
			Map goodsmap = new HashMap();
			if(CommUtil.isNotNull(obj.getGoods_main_photo())){
					goodsmap.put("goodsimg", this.configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName() +"_middle."+ obj.getGoods_main_photo().getExt());
				}
				try {
					goodsmap.put("Store_second_domain", obj.getGoods_store().getStore_second_domain());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					goodsmap.put("Store_second_domain", "");
				}
				goodsmap.put("goodsid", obj.getId());
				goodsmap.put("goodsType", obj.getGoods_type());
				goodsmap.put("goodsname", obj.getGoods_name());
				goodsmap.put("goods_discount_rate", obj.getGoods_discount_rate());
				goodsmap.put("goods_price", obj.getGoods_price());
				goodsmap.put("goods_current_price", obj.getGoods_current_price());
				goodsmap.put("well_evaluate", obj.getWell_evaluate() == null ? 0 : obj.getWell_evaluate());
				goodsmap.put("goods_inventory", obj.getGoods_inventory());
					if(obj.getGoods_store() != null && !obj.getGoods_store().equals("")){
						try {
						goodsmap.put("store_logo", this.configService.getSysConfig().getImageWebServer() +"/"+ obj.getGoods_store().getStore_logo().getPath() +"/"+ obj.getGoods_store().getStore_logo().getName());
					} catch (NullPointerException e) {
						// TODO Auto-generated catch block
						goodsmap.put("store_logo", "");
					}
				}
			for(Evaluate eva:obj.getEvaluates()){
						goodsmap.put("evaluate_status", eva.getEvaluate_status());
					}
			goodslist.add(goodsmap);
		}
		discountmap.put("goods_info", goodslist);
		if(user != null){
			Map params = new HashMap();
			params.put("user_id", user.getId());
			params.put("store_id", CommUtil.null2Long(goods_store));
			List<Favorite> list = this.favoriteService
					.query("select obj from Favorite obj where obj.user_id=:user_id and obj.store_id=:store_id",
							params, -1, -1);
			if(!list.isEmpty()){
				discountmap.put("favorite_store", list.size());
			}else{
				discountmap.put("favorite_store", 0);
			}
		}
		discountmap.put("store_id", store.getId());
		discountmap.put("store_name", store.getStore_name());
		discountmap.put("goods_Pages", pList.getPages());
		if(goodslist.isEmpty()){
			result = new Result(1,"该分类没有商品");
		}else{
			result = new Result(0,"success",discountmap);
		}
		try {
			response.getWriter().print(Json.toJson(result, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * 商家商品信息
	 * @param mv
	 * @param store
	 */
/*	@RequestMapping("/store_goods_info.json")
	private void store_goods_sale(HttpServletRequest request, HttpServletResponse response, String store_id, String token) {
		Result result = null;
		Map storemap = new HashMap();
		User user = new User();
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
			 user = users.get(0);
			}
		}
		if(store_id == null && store_id.equals("")){
			result = new Result(1,"参数错误");
		}else{
		Store store = this.storeService.getObjById(CommUtil.null2Long(store_id));
		Map params = new HashMap();
		params.put("store_id", store.getId());
		params.put("goods_status", 0);
		List<Goods> store_obj = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
						params, -1, -1);
		List<Map> objlist = new ArrayList<Map>();
		for(Goods obj : store_obj){
			Map goodsmap = new HashMap();
			if(obj.getCgoods().size() > 0){
				for(CGoods cgs : obj.getCgoods()){
					if(cgs.getGoods_disabled().equals("0")){
						goodsmap.put("cgoodsId", cgs.getId());
						goodsmap.put("goodsid", obj.getId());
						goodsmap.put("goodsType", obj.getGoods_type());
						goodsmap.put("goodsname", obj.getGoods_name());
						goodsmap.put("well_evaluate", obj.getWell_evaluate());
						goodsmap.put("goods_price", cgs.getGoods_price());
						goodsmap.put("goods_current_price", cgs.getDiscount_price());
						goodsmap.put("evaluate_count", obj.getEvaluate_count());
						goodsmap.put("goods_inventory", cgs.getGoods_inventory());
						goodsmap.put("goods_details", obj.getGoods_details());
						goodsmap.put("goods_salenum", obj.getGoods_salenum());
						if(CommUtil.isNotNull(cgs.getC_goods_photos()) && CommUtil.isNotNull(obj.getGoods_main_photo())){
							goodsmap.put("goodsimg", this.configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName());
						}else{
							goodsmap.put("goodsimg", this.configService.getSysConfig().getImageWebServer()+"/"+cgs.getC_goods_photos().get(0).getPath()+"/"+cgs.getC_goods_photos().get(0).getName());
						}
						if(obj.getGoods_store() != null && !obj.getGoods_store().equals("")){
							try {
								goodsmap.put("store_logo", this.configService.getSysConfig().getImageWebServer() +"/"+ obj.getGoods_store().getStore_logo().getPath() +"/"+ obj.getGoods_store().getStore_logo().getName());
						} catch (NullPointerException e) {
							// TODO Auto-generated catch block
							goodsmap.put("store_logo", "");
						}
					}
					}
					break;
				}
			}else{
				List<Accessory> acc = obj.getGoods_photos();
				List<Map> acclist = new ArrayList<Map>();
				for(Accessory accessory:acc){
					Map accmap = new HashMap();
					accmap.put("photos", configService.getSysConfig().getImageWebServer()+"/"+accessory.getPath()+"/"+accessory.getName());
					acclist.add(accmap);
				}
				goodsmap.put("photos", acclist);
				goodsmap.put("goodsimg", this.configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName());
				//goodsmap.put(key,  configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName());
				goodsmap.put("goodsid", obj.getId());
				goodsmap.put("goodsname", obj.getGoods_name());
				goodsmap.put("goods_current_price", obj.getGoods_current_price());
				goodsmap.put("goods_price", obj.getGoods_price());
				goodsmap.put("evaluate_count", obj.getEvaluate_count());
				goodsmap.put("well_evaluate", obj.getWell_evaluate()); 
				goodsmap.put("middle_evaluate", obj.getMiddle_evaluate());
				goodsmap.put("middle_evaluate", obj.getMiddle_evaluate());
				goodsmap.put("goods_salenum", obj.getGoods_salenum());
				goodsmap.put("goods_cod", obj.getGoods_cod());
				goodsmap.put("goods_gc_id", obj.getGc().getParent().getParent().getId());
				goodsmap.put("act_status", obj.getActivity_status());
				goodsmap.put("group_buy", obj.getGroup_buy());
				goodsmap.put("BuyGift_id", obj.getBuyGift_id());
				goodsmap.put("order_enough_give_status", obj.getOrder_enough_give_status()); 
				goodsmap.put("buyGift_amount", obj.getBuyGift_amount());
				goodsmap.put("order_enough_if_give", obj.getOrder_enough_if_give());
				goodsmap.put("goods_details", obj.getGoods_details());
				goodsmap.put("advance_sale_type", obj.getAdvance_sale_type());
				goodsmap.put("advance_date", obj.getAdvance_date());
				//预计到达时间
				goodsmap.put("arrive_begin", "");
				goodsmap.put("arrive_end", "");
			}
				objlist.add(goodsmap);
			}
			storemap.put("store_goods_info", objlist);
	
			if(user != null){
				params.put("user_id", user.getId());
				params.put("store_id", CommUtil.null2Long(store.getId()));
				List<Favorite> list = this.favoriteService
						.query("select obj from Favorite obj where obj.user_id=:user_id and obj.store_id=:store_id",
								params, -1, -1);
				if(!list.isEmpty()){
					storemap.put("favorite_store", list.size());
				}
			}else{
				storemap.put("favorite_store", 0);
			}
			storemap.put("store_name", store.getStore_name());
			result = new Result(0,"success",storemap);
	}
		String store_temp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(store_temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}*/

	/**
	 * @methodsName self_goods_sale
	 * @description 移动端自营商品
	 * @param request
	 * @param response
	 * @param goods_type 商品类型
	 */
	@RequestMapping("/self_goods_info.json")
	public void self_goods_sale(HttpServletRequest request, HttpServletResponse response,int goods_type) {
		Result result = null;
		Map storemap = new HashMap();
		List<Goods> self_obj = new ArrayList<Goods>();
			Map params = new HashMap();
			params.put("goods_type", 0);
			params.put("goods_status", 0);
			//["select new Goods(id, goods_name, goods_current_price,goods_collect, goods_salenum,goods_main_photo,goods_price) from Goods obj where obj.goods_type=:goods_type and obj.goods_status=:goods_status order by obj.goods_salenum desc",]
			self_obj = this.goodsService
					.query("select obj from Goods obj where obj.goods_type=:goods_type and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, -1, -1);
			List<Map> objlist = new ArrayList<Map>();
			for(Goods obj : self_obj){
				Map objmap = new HashMap();
				List<Accessory> acc = obj.getGoods_photos();
				List<Map> acclist = new ArrayList<Map>();
				for(Accessory accessory:acc){
					Map accmap = new HashMap();
					accmap.put("photos", configService.getSysConfig().getImageWebServer()+"/"+accessory.getPath()+"/"+accessory.getName());
					acclist.add(accmap);
			}
				objmap.put("photos", acclist);
				objmap.put("goodsimg", configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName());
				//objmap.put(key,  configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName());
				objmap.put("goodsid", obj.getId());
				objmap.put("goodsname", obj.getGoods_name());
				objmap.put("goods_current_price", obj.getGoods_current_price());
				objmap.put("goods_price", obj.getGoods_price());
				objmap.put("evaluate_count", obj.getEvaluate_count());
				objmap.put("well_evaluate", obj.getWell_evaluate()); 
				objmap.put("middle_evaluate", obj.getMiddle_evaluate());
				objmap.put("middle_evaluate", obj.getMiddle_evaluate());
				objmap.put("goods_salenum", obj.getGoods_salenum());
				objmap.put("goods_cod", obj.getGoods_cod());
				objmap.put("goods_gc_id", obj.getGc().getId());
				objmap.put("act_status", obj.getActivity_status());
				objmap.put("group_buy", obj.getGroup_buy());
				objmap.put("give_status", obj.getOrder_enough_give_status());
				objmap.put("BuyGift_id", obj.getBuyGift_id());
				objmap.put("order_enough_give_status", obj.getOrder_enough_give_status()); 
				objmap.put("buyGift_amount", obj.getBuyGift_amount());
				objmap.put("order_enough_if_give", obj.getOrder_enough_if_give());
				objmap.put("goods_details", obj.getGoods_details());
				objmap.put("advance_sale_type", obj.getAdvance_sale_type());
				objmap.put("advance_date", obj.getAdvance_date());
				objlist.add(objmap);
		}
			storemap.put("store_goods_info", objlist);
			storemap.put("store_name", "self");
		String self_temp = Json.toJson(new Result(0,"success",storemap), JsonFormat.compact());
		try {
			response.getWriter().print(self_temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 国际直营
	 * @param mv
	 * @param store
	 */
	@RequestMapping("/global_goods_info.json")
	private void global_goods_info(HttpServletRequest request, HttpServletResponse response) {
		Result result = null;
		Map storemap = new HashMap();
		Map params = new HashMap();
		params.put("goods_status", 0);
		params.put("goods_type", -1);
		List<Goods> store_obj = this.goodsService
				.query("select obj from Goods obj where obj.goods_type=:goods_type and obj.goods_status=:goods_status order by obj.goods_salenum desc",
						params, -1, -1);
		List<Map> objlist = new ArrayList<Map>();
		for(Goods obj : store_obj){
			Map goodsmap = new HashMap();
			if(obj.getCgoods().size() > 0){
				for(CGoods cgs : obj.getCgoods()){
					if(cgs.getGoods_disabled().equals("0")){
						goodsmap.put("cgoodsId", cgs.getId());
						goodsmap.put("goodsid", obj.getId());
						goodsmap.put("goodsType", obj.getGoods_type());
						goodsmap.put("goodsname", obj.getGoods_name());
						goodsmap.put("well_evaluate", obj.getWell_evaluate());
						goodsmap.put("goods_price", cgs.getGoods_price());
						goodsmap.put("goods_current_price", cgs.getDiscount_price());
						goodsmap.put("evaluate_count", obj.getEvaluate_count());
						goodsmap.put("goods_inventory", cgs.getGoods_inventory());
						goodsmap.put("goods_details", obj.getGoods_details());
						goodsmap.put("goods_salenum", obj.getGoods_salenum());
						if(CommUtil.isNotNull(cgs.getC_goods_photos()) && CommUtil.isNotNull(obj.getGoods_main_photo())){
							goodsmap.put("goodsimg", this.configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName());
						}else{
							goodsmap.put("goodsimg", this.configService.getSysConfig().getImageWebServer()+"/"+cgs.getC_goods_photos().get(0).getPath()+"/"+cgs.getC_goods_photos().get(0).getName());
						}
						if(obj.getGoods_store() != null && !obj.getGoods_store().equals("")){
							try {
								goodsmap.put("store_logo", this.configService.getSysConfig().getImageWebServer() +"/"+ obj.getGoods_store().getStore_logo().getPath() +"/"+ obj.getGoods_store().getStore_logo().getName());
						} catch (NullPointerException e) {
							// TODO Auto-generated catch block
							goodsmap.put("store_logo", "");
						}
					}
					}
					break;
				}
			}else{
				List<Accessory> acc = obj.getGoods_photos();
				List<Map> acclist = new ArrayList<Map>();
				for(Accessory accessory:acc){
					Map accmap = new HashMap();
					accmap.put("photos", configService.getSysConfig().getImageWebServer()+"/"+accessory.getPath()+"/"+accessory.getName());
					acclist.add(accmap);
			}
				goodsmap.put("photos", acclist);
				goodsmap.put("goodsimg", configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName());
				//goodsmap.put(key,  configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName());
				goodsmap.put("goodsid", obj.getId());
				goodsmap.put("goodsname", obj.getGoods_name());
				goodsmap.put("goods_current_price", obj.getGoods_current_price());
				goodsmap.put("goods_price", obj.getGoods_price());
				goodsmap.put("evaluate_count", obj.getEvaluate_count());
				goodsmap.put("well_evaluate", obj.getWell_evaluate()); 
				goodsmap.put("middle_evaluate", obj.getMiddle_evaluate());
				goodsmap.put("middle_evaluate", obj.getMiddle_evaluate());
				goodsmap.put("goods_salenum", obj.getGoods_salenum());
				goodsmap.put("goods_cod", obj.getGoods_cod());
				goodsmap.put("goods_gc_id", obj.getGc().getId());
				goodsmap.put("act_status", obj.getActivity_status());
				goodsmap.put("group_buy", obj.getGroup_buy());
				goodsmap.put("give_status", obj.getOrder_enough_give_status());
				goodsmap.put("BuyGift_id", obj.getBuyGift_id());
				goodsmap.put("order_enough_give_status", obj.getOrder_enough_give_status()); 
				goodsmap.put("buyGift_amount", obj.getBuyGift_amount());
				goodsmap.put("order_enough_if_give", obj.getOrder_enough_if_give());
				goodsmap.put("goods_details", obj.getGoods_details());
				goodsmap.put("advance_sale_type", obj.getAdvance_sale_type());
				goodsmap.put("advance_date", obj.getAdvance_date());
				goodsmap.put("store_name", obj.getGoods_store().getStore_name());
				if(obj.getGoods_store() != null && !obj.getGoods_store().equals("")){
					try {
						goodsmap.put("store_logo", this.configService.getSysConfig().getImageWebServer() +"/"+ obj.getGoods_store().getStore_logo().getPath() +"/"+ obj.getGoods_store().getStore_logo().getName());
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					goodsmap.put("store_logo", "");
				}
			}
				
			}
			objlist.add(goodsmap);
			}
		storemap.put("store_goods_info", objlist);
		result = new Result(0,"success",storemap);
		String store_temp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(store_temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
	
	/**
	 * 商城精选商品
	 */
	@RequestMapping("/store_recommend.json")
	public void store_recommend(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String store_recommend, String store_creativity){
		ModelAndView mv = new JModelAndView("admin/blue/goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map store_recommend_map = new HashMap(); 
		Result result = null;
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, Goods.class, mv);
		if (store_recommend != null && !store_recommend.equals("")) {
			qo.addQuery(
					"obj.store_recommend",
					new SysMap("goods_store_recommend", CommUtil
							.null2Boolean(store_recommend)), "=");
			mv.addObject("store_recommend", store_recommend);
		}
		if (store_creativity != null && !store_creativity.equals("")) {
			qo.addQuery(
					"obj.store_creativity",
					new SysMap("store_creativity", CommUtil
							.null2Boolean(store_creativity)), "=");
			mv.addObject("store_creativity", store_creativity);
		}
		IPageList pList = this.goodsService.list(qo);
		List<Goods> goodses= pList.getResult();
		if(goodses.size() > 0){
			List<Map> objlist = new ArrayList<Map>();
			for(Goods obj : goodses){
				Map goodsmap = new HashMap(); 
				if(obj.getCgoods().size() > 0){
					for(CGoods cgs : obj.getCgoods()){
						if(cgs.getGoods_disabled().equals("0")){
							goodsmap.put("cgoodsId", cgs.getId());
							goodsmap.put("goodsid", obj.getId());
							goodsmap.put("goodsType", obj.getGoods_type());
							goodsmap.put("goodsname", obj.getGoods_name());
							goodsmap.put("well_evaluate", obj.getWell_evaluate());
							goodsmap.put("goods_price", cgs.getGoods_price());
							goodsmap.put("goods_current_price", cgs.getDiscount_price());
							goodsmap.put("evaluate_count", obj.getEvaluate_count());
							goodsmap.put("goods_inventory", cgs.getGoods_inventory());
							goodsmap.put("goods_details", obj.getGoods_details());
							goodsmap.put("goods_salenum", obj.getGoods_salenum());
							if(CommUtil.isNotNull(cgs.getC_goods_photos()) && CommUtil.isNotNull(obj.getGoods_main_photo())){
								goodsmap.put("goodsimg", this.configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName());
							}else{
								goodsmap.put("goodsimg", this.configService.getSysConfig().getImageWebServer()+"/"+cgs.getC_goods_photos().get(0).getPath()+"/"+cgs.getC_goods_photos().get(0).getName());
							}
							if(obj.getGoods_store() != null && !obj.getGoods_store().equals("")){
								try {
									goodsmap.put("store_name", obj.getGoods_store().getStore_name());
									goodsmap.put("store_logo", this.configService.getSysConfig().getImageWebServer() +"/"+ obj.getGoods_store().getStore_logo().getPath() +"/"+ obj.getGoods_store().getStore_logo().getName());
							} catch (NullPointerException e) {
								// TODO Auto-generated catch block
								goodsmap.put("store_name", "");
								goodsmap.put("store_logo", "");
							}
						}
						}
						break;
					}
				}else{
					List<Accessory> acc = obj.getGoods_photos();
					List<Map> acclist = new ArrayList<Map>();
					for(Accessory accessory:acc){
						Map accmap = new HashMap();
						accmap.put("photos", configService.getSysConfig().getImageWebServer()+"/"+accessory.getPath()+"/"+accessory.getName());
						acclist.add(accmap);
				}
					goodsmap.put("photos", acclist);
					goodsmap.put("goodsimg", configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName());
					//goodsmap.put(key,  configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName());
					goodsmap.put("goodsid", obj.getId());
					goodsmap.put("goodsname", obj.getGoods_name());
					goodsmap.put("goods_current_price", obj.getGoods_current_price());
					goodsmap.put("goods_price", obj.getGoods_price());
					goodsmap.put("evaluate_count", obj.getEvaluate_count());
					goodsmap.put("well_evaluate", obj.getWell_evaluate()); 
					goodsmap.put("middle_evaluate", obj.getMiddle_evaluate());
					goodsmap.put("middle_evaluate", obj.getMiddle_evaluate());
					goodsmap.put("goods_salenum", obj.getGoods_salenum());
					goodsmap.put("goods_cod", obj.getGoods_cod());
					goodsmap.put("goods_gc_id", obj.getGc().getId());
					goodsmap.put("act_status", obj.getActivity_status());
					goodsmap.put("group_buy", obj.getGroup_buy());
					goodsmap.put("give_status", obj.getOrder_enough_give_status());
					goodsmap.put("BuyGift_id", obj.getBuyGift_id());
					goodsmap.put("order_enough_give_status", obj.getOrder_enough_give_status()); 
					goodsmap.put("buyGift_amount", obj.getBuyGift_amount());
					goodsmap.put("order_enough_if_give", obj.getOrder_enough_if_give());
					goodsmap.put("goods_details", obj.getGoods_details());
					goodsmap.put("advance_sale_type", obj.getAdvance_sale_type());
					goodsmap.put("advance_date", obj.getAdvance_date());
					if(obj.getGoods_store() != null && !obj.getGoods_store().equals("")){
						try {
							goodsmap.put("store_name", obj.getGoods_store().getStore_name());
							goodsmap.put("store_logo", this.configService.getSysConfig().getImageWebServer() +"/"+ obj.getGoods_store().getStore_logo().getPath() +"/"+ obj.getGoods_store().getStore_logo().getName());
					} catch (NullPointerException e) {
						// TODO Auto-generated catch block
						goodsmap.put("store_name", "");
						goodsmap.put("store_logo", "");
					}
				}
					
				}
				objlist.add(goodsmap);
			}
			store_recommend_map.put("store_goods_info", objlist);
		}
		
		result = new Result(0, "success",store_recommend_map);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(result, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据运费模板信息、商品重量及配送城市计算商品运费，配送城市根据IP自动获取
	 * 
	 * @param json
	 * @param weight
	 * @param city_name
	 * @return 商品的配送费用
	 */
	@RequestMapping("/fee_info.json")
	public void cal_order_trans(HttpServletRequest request, HttpServletResponse response, String goods_id, String type, String volume, String city_name, String number) {
		int count = 1;
		if(number != null && !number.equals("")){
			count = CommUtil.null2Int(number);
		}
		Result result = null;
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		Object goods_weight = goods.getGoods_weight();
		if(goods != null){
			if(goods.getGoods_transfee() == 0){
				Transport trans = this.transportService.getObjById(goods.getTransport().getId());
				String json = "";
				if (type.equals("mail")) {
					json = trans.getTrans_mail_info();
				}
				if (type.equals("express")) {
					json = trans.getTrans_express_info();
				}
				if (type.equals("ems")) {
					json = trans.getTrans_ems_info();
				}
				float fee = 0;
				boolean cal_flag = false;// 是否已经计算过运费，用在没有特殊配置的区域，没有特殊配置的区域使用默认价格计算
				List<Map> list = Json.fromJson(ArrayList.class,
						CommUtil.null2String(json));
				if (list != null && list.size() > 0) {
					for (Map map : list) {
						String[] city_list = CommUtil.null2String(map.get("city_name"))
								.split("、");
						for (String city : city_list) {
							if (city_name.indexOf(city) >= 0 || city.equals(city_name)) {
								cal_flag = true;
								float trans_weight = CommUtil.null2Float(map
										.get("trans_weight"));
								float trans_fee = CommUtil.null2Float(map
										.get("trans_fee"));
								float trans_add_weight = CommUtil.null2Float(map
										.get("trans_add_weight"));
								float trans_add_fee = CommUtil.null2Float(map
										.get("trans_add_fee"));
								if (trans.getTrans_type() == 0) {// 按照件数计算运费
									fee = trans_fee;
								}
								if (trans.getTrans_type() == 1) {// 按照重量计算运费用
									if(count > 1){
										goods_weight = CommUtil.mul(count, goods_weight);
									}
									double lw_price = CommUtil.mul(goods.getGoods_length(), goods.getGoods_width());
									double lwh_price = CommUtil.mul(lw_price, goods.getGoods_high())/5000;
									double kg_price = lwh_price*1000;
									if (CommUtil.null2Float(goods_weight) > CommUtil.null2Double(trans_weight) || kg_price > CommUtil.null2Double(trans_weight)) {
										fee = trans_fee;
										float other_price = 0;
										if (trans_add_weight > 0) { 
											other_price = trans_add_fee * Math.round(Math.ceil(CommUtil .subtract(goods_weight, trans_weight) / trans_add_weight));
										}
										float volume_price = (float)(trans_add_fee*(Math.ceil(CommUtil.subtract(kg_price,trans_weight)/trans_add_weight)));
										if(volume_price > other_price){
											fee = fee + volume_price;
										}else{
											fee = fee + other_price;
										}
									} else {
										fee = trans_fee;
									}
								}
								if (trans.getTrans_type() == 2) {// 按照体积计算运费用
									float goods_volume = CommUtil.null2Float(volume);
									if (goods_volume > 0) {
										fee = trans_fee;
										float other_price = 0;
										if (trans_add_weight > 0) {
											other_price = (float)(trans_add_fee*(Math.ceil(CommUtil.subtract(goods_volume,trans_weight)/trans_add_weight)));
										}
										fee = fee + other_price;
									}
								}
								break;
							}
						}
					}
					if (!cal_flag) {// 如果没有找到配置所在的区域运费信息，则使用全国价格进行计算
						for (Map map : list) {
							String[] city_list = CommUtil.null2String(
									map.get("city_name")).split("、");
							for (String city : city_list) {
								if (city.equals("全国")) {
									float trans_weight = CommUtil.null2Float(map
											.get("trans_weight"));
									float trans_fee = CommUtil.null2Float(map
											.get("trans_fee"));
									float trans_add_weight = CommUtil.null2Float(map
											.get("trans_add_weight"));
									float trans_add_fee = CommUtil.null2Float(map
											.get("trans_add_fee"));
									if (trans.getTrans_type() == 0) {// 按照件数计算运费
										fee = trans_fee;
									}
									if (trans.getTrans_type() == 1) {// 按照重量计算运费用
										if(count > 1){
											goods_weight = CommUtil.mul(count, goods_weight);
										}
										double lw_price = CommUtil.mul(goods.getGoods_length(), goods.getGoods_width());
										double lwh_price = CommUtil.mul(lw_price, goods.getGoods_high())/5000;
										double kg_price = lwh_price*1000;
										if (CommUtil.null2Float(goods_weight) > CommUtil.null2Double(trans_weight) || kg_price > CommUtil.null2Double(trans_weight)) {
											fee = trans_fee;
											float other_price = 0;
											if (trans_add_weight > 0) { 
												other_price = trans_add_fee * Math.round(Math.ceil(CommUtil .subtract(goods_weight, trans_weight) / trans_add_weight));
											}
											float volume_price = (float)(trans_add_fee*(Math.ceil(CommUtil.subtract(kg_price,trans_weight)/trans_add_weight)));
											if(volume_price > other_price){
												fee = fee + volume_price;
											}else{
												fee = fee + other_price;
											}
										} else {
											fee = trans_fee;
										}
									}
									if (trans.getTrans_type() == 2) {// 按照体积计算运费用
										float goods_volume = CommUtil
												.null2Float(volume);
										if (goods_volume > 0) {
											fee = trans_fee;
											float other_price = 0;
											if (trans_add_weight > 0) {
												other_price = (float)(trans_add_fee*(Math.ceil(CommUtil.subtract(goods_volume,trans_weight)/trans_add_weight)));
											}
											fee = fee + other_price;
										}
									}
									break;
								}
							}
						}
					}
				}
				result = new Result(0,"success",fee);
			}else{
				double fee = 0;
				result = new Result(2,"商家承担运费",fee);
			}
		}else{
			result = new Result(1,"没有该商品");
		}
		try {
			String fee_temp = Json.toJson(result, JsonFormat.compact());	
			response.getWriter().print(fee_temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping("/remove_cgoods.json")
	public void delete_cgoods(HttpServletRequest request,
			HttpServletResponse response, String id){
		Result result = null;
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		if(obj != null){
			List<CGoods> cgoods = obj.getCgoods();
			List<HashMap> list = Json.fromJson(ArrayList.class,
					obj.getGoods_inventory_detail());
			List<String> spec_id = new ArrayList<String>();;
			for(Map map : list){
				String ids = CommUtil.null2String(map.get("id"));
				 spec_id.add(ids);
			}
			for(CGoods cobj : cgoods){
				if(!spec_id.contains(cobj.getCombination_id())){
					this.cGoodsService.delete(cobj.getId());
				}
			}
		}
	}
	
}