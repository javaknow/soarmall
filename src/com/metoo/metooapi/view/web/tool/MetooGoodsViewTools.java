package com.metoo.metooapi.view.web.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.buyer.domain.Result;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.Accessory;
import com.metoo.foundation.domain.ActivityGoods;
import com.metoo.foundation.domain.CGoods;
import com.metoo.foundation.domain.Evaluate;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsSpecProperty;
import com.metoo.foundation.domain.GoodsSpecification;
import com.metoo.foundation.domain.GroupGoods;
import com.metoo.foundation.domain.query.GoodsQueryObject;
import com.metoo.foundation.service.IActivityGoodsService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.view.web.tools.IntegralViewTools;

@Controller
public class MetooGoodsViewTools {

	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IActivityGoodsService actgoodsService;
	@Autowired
	private IntegralViewTools integralViewTools;
	
	@RequestMapping("index_discount.json")
	public void index_discount(HttpServletRequest request,
			HttpServletResponse response,String orderBy, String orderType,String currentPage,
				String store_recommend, String store_creativity,
					String goods_global, String goods_type){
		ModelAndView mv = new JModelAndView("",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Result result = null;
		Map discountmap = new HashMap();
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
		gqo.setPageSize(30);
		gqo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		IPageList pList = this.goodsService.list(gqo);
		List<Goods> goods = pList.getResult();
		List goodslist = new ArrayList();
		for(Goods obj : goods){
			Map goodsmap = new HashMap();
			/*原用于查询主商品下的第一个主商品if(!obj.getCgoods().isEmpty()){
				List<CGoods> cgoods = obj.getCgoods();
					for(CGoods cgs : cgoods){
						if(cgs.getGoods_disabled().equals("0")){
							goodsmap.put("cgoodsId", cgs.getId());
							goodsmap.put("goods_id", obj.getId());
							goodsmap.put("goods_type", obj.getGoods_type());
							
							if(CommUtil.isNotNull(cgs.getC_goods_photos()) && CommUtil.isNotNull(obj.getGoods_main_photo())){
								goodsmap.put("goodsimaPath", this.configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName());
							}else{
								goodsmap.put("goodsimaPath", this.configService.getSysConfig().getImageWebServer()+"/"+cgs.getC_goods_photos().get(0).getPath()+"/"+cgs.getC_goods_photos().get(0).getName());
							}
							goodsmap.put("goods_name", obj.getGoods_name());
							goodsmap.put("goods_discount_rate", cgs.getCgoods_discount_rate());
							goodsmap.put("well_evaluate", obj.getWell_evaluate());
							goodsmap.put("goodsprice", cgs.getGoods_price());
							goodsmap.put("goods_current_price", cgs.getDiscount_price());
							goodsmap.put("goods_inventory", cgs.getGoods_inventory());
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
					
			}else{*/
			if(CommUtil.isNotNull(obj.getGoods_main_photo())){
				goodsmap.put("goodsimaPath", this.configService.getSysConfig().getImageWebServer() +"/"+ obj.getGoods_main_photo().getPath() +"/"+ obj.getGoods_main_photo().getName() +"_middle."+obj.getGoods_main_photo().getExt());
			}
				try {
					goodsmap.put("Store_second_domain", obj.getGoods_store().getStore_second_domain());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					goodsmap.put("Store_second_domain", "");
				}
				goodsmap.put("goods_id", obj.getId());
				goodsmap.put("goods_type", obj.getGoods_type());
				goodsmap.put("goods_name", obj.getGoods_name());
				goodsmap.put("goods_discount_rate", obj.getGoods_discount_rate());
				goodsmap.put("goodsprice", obj.getGoods_price());
				goodsmap.put("goods_current_price", obj.getGoods_current_price() == null ? 0 : obj.getGoods_current_price());
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
			//}
			goodslist.add(goodsmap);
		}
		discountmap.put("goods_info", goodslist);
		discountmap.put("goods_Pages", pList.getPages());
		if(discountmap.isEmpty()){
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
	 * 移动端首页最热商品
	 * @param request
	 * @param response
	 * @param count
	 */
	@RequestMapping("/wap_index_hot.json")
	public void index_hot(HttpServletRequest request,
			HttpServletResponse response,int count) {
		
		Result result = null;
		Map HotMap = new HashMap();
			Map params = new HashMap();
			params.put("goods_status", 0);
			List<Goods> goodses = this.goodsService
					.query("select obj from Goods obj where obj.goods_status=:goods_status order by obj.addTime desc",
							params, 0, count);
			List<Map> goodsList = new ArrayList<Map>();
			for(Goods obj:goodses){
				Map goodsmap = new HashMap();
				if(!obj.getCgoods().isEmpty()){
					List<CGoods> cgoods = obj.getCgoods();
						for(CGoods cgs : cgoods){
							if(cgs.getGoods_disabled().equals("0")){
								goodsmap.put("cgoodsId", cgs.getId());
								goodsmap.put("goodsId", obj.getId());
								goodsmap.put("goodsType", obj.getGoods_type());
								
								if(CommUtil.isNotNull(cgs.getC_goods_photos()) && CommUtil.isNotNull(obj.getGoods_main_photo())){
									goodsmap.put("goodsimaPath", this.configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName());
								}else{
									goodsmap.put("goodsimaPath", this.configService.getSysConfig().getImageWebServer()+"/"+cgs.getC_goods_photos().get(0).getPath()+"/"+cgs.getC_goods_photos().get(0).getName());
								}
								goodsmap.put("goodsName", obj.getGoods_name());
								goodsmap.put("well_evaluate", obj.getWell_evaluate());
								goodsmap.put("goodsprice", cgs.getGoods_price());
								goodsmap.put("goods_current_price", cgs.getDiscount_price());
								goodsmap.put("goods_inventory", cgs.getGoods_inventory());
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
					if(CommUtil.isNotNull(obj.getGoods_main_photo())){
						goodsmap.put("goodsimaPath", this.configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName());
					}
					try {
						goodsmap.put("Store_second_domain", obj.getGoods_store().getStore_second_domain());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						goodsmap.put("Store_second_domain", "");
					}
					goodsmap.put("goods_id", obj.getId());
					goodsmap.put("goods_type", obj.getGoods_type());
					goodsmap.put("goods_name", obj.getGoods_name());
					goodsmap.put("goodsprice", obj.getGoods_price());
					goodsmap.put("goods_current_price", obj.getGoods_current_price());
					goodsmap.put("well_evaluate", obj.getWell_evaluate());
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
						
				}
				
				goodsList.add(goodsmap);
				HotMap.put("goodsList", goodsList);
			}
			if(!HotMap.isEmpty()){
				result = new Result(0,"查询成功",HotMap);
			}else{
				result = new Result(1,"查询失败");
			}
		String goodsTemp = Json.toJson(result, JsonFormat.compact());
		
		try {
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.getWriter().print(goodsTemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 移动端最新商品
	 * @param request
	 * @param response
	 * @param count
	 */
	/*@RequestMapping("/index_new.json")
	public void index_new(HttpServletRequest request,
			HttpServletResponse response,int count) {
		List<Goods> list = new ArrayList<Goods>();
		Result result = null;
		Map HotMap = new HashMap();
		List<Map> configlist = new ArrayList<Map>();
		Map sysmap = new HashMap();
		SysConfig syscon = this.configService.getSysConfig();
		sysmap.put("imgwebServer", syscon.getImageWebServer());
		sysmap.put("imgpath", syscon.getGoodsImage().getPath());
		sysmap.put("imgname", syscon.getGoodsImage().getName());
		sysmap.put("Second_domain_open", syscon.getSecond_domain_open());
		sysmap.put("logopath", syscon.getWebsiteLogo().getPath());
		sysmap.put("logoname", syscon.getWebsiteLogo().getName());
		
		configlist.add(sysmap);
		HotMap.put("configlist", configlist);
		if(count!=30){
			result = new Result(1,"parameter error");
		}else{
			Map params = new HashMap();
		params.put("goods_status", 0);
			list = this.goodsService
					.query("select obj from Goods obj where obj.goods_status=:goods_status order by obj.addTime desc",
							params, 0, count);
			List<Map> goodsList = new ArrayList<Map>();
			for(Goods goodslist:list){
				
				Map<String, Comparable> goodsmap = new HashMap();
				if(CommUtil.isNotNull(goodslist.getGoods_main_photo())){
					goodsmap.put("goodsimaPath", configlist.get(0).get("imgwebServer")+"/"+goodslist.getGoods_main_photo().getPath()+"/"+goodslist.getGoods_main_photo().getName());
				}
				try {
					goodsmap.put("Store_second_domain", goodslist.getGoods_store().getStore_second_domain());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					goodsmap.put("Store_second_domain", "");
				}
				goodsmap.put("goodsId", goodslist.getId());
				goodsmap.put("goodsType", goodslist.getGoods_type());
				goodsmap.put("goodsName", goodslist.getGoods_name());
				goodsmap.put("goodsPrice", goodslist.getGoods_price());
				goodsmap.put("goods_current_price", goodslist.getGoods_current_price());
				goodsmap.put("well_evaluate", goodslist.getWell_evaluate());
				
				for(Evaluate eva:goodslist.getEvaluates()){
					goodsmap.put("evaluate_status", eva.getEvaluate_status());
				}
				goodsList.add(goodsmap);
				HotMap.put("goodsList", goodsList);
			}
			if(!HotMap.isEmpty()){
				result = new Result(0,"查询成功",HotMap);
			}else{
				result = new Result(1,"查询失败");
			}
		}
	
		String goodsTemp = Json.toJson(result, JsonFormat.compact());
		
		try {
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.getWriter().print(goodsTemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param count
	 */
	@RequestMapping("/new_goods.json")
	public void index_new(HttpServletRequest request,
			HttpServletResponse response,int count) {
		List<Goods> list = new ArrayList<Goods>();
		Result result = null;
		Map HotMap = new HashMap();
		if(count!=30){
			result = new Result(1,"parameter error");
		}else{
			Map params = new HashMap();
			params.put("goods_status", 0);
			list = this.goodsService
					.query("select obj from Goods obj where obj.goods_status=:goods_status order by obj.addTime desc",
							params, 0, count);
			List<Map> goodsList = new ArrayList<Map>();
			for(Goods obj:list){
				Map<String, Comparable> goodsmap = new HashMap();
				if(obj.getCgoods().size() > 0){
					for(CGoods cgs : obj.getCgoods()){
						if(cgs.getGoods_disabled().equals("0")){
							goodsmap.put("cgoodsId", cgs.getId());
							goodsmap.put("goodsId", obj.getId());
							goodsmap.put("goodsType", obj.getGoods_type());
							goodsmap.put("goodsName", obj.getGoods_name());
							goodsmap.put("well_evaluate", obj.getWell_evaluate());
							goodsmap.put("goodsPrice", cgs.getGoods_price());
							goodsmap.put("goods_current_price", cgs.getDiscount_price());
							goodsmap.put("goods_inventory", cgs.getGoods_inventory());
							if(CommUtil.isNotNull(cgs.getC_goods_photos()) && CommUtil.isNotNull(obj.getGoods_main_photo())){
								goodsmap.put("goodsimaPath", this.configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName());
							}else{
								goodsmap.put("goodsimaPath", this.configService.getSysConfig().getImageWebServer()+"/"+cgs.getC_goods_photos().get(0).getPath()+"/"+cgs.getC_goods_photos().get(0).getName());
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
					if(CommUtil.isNotNull(obj.getGoods_main_photo())){
					goodsmap.put("goodsimaPath", this.configService.getSysConfig().getImageWebServer()+"/"+obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName());
					}
					try {
						goodsmap.put("Store_second_domain", obj.getGoods_store().getStore_second_domain());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						goodsmap.put("Store_second_domain", "");
					}
					goodsmap.put("goodsId", obj.getId());
					goodsmap.put("goodsType", obj.getGoods_type());
					goodsmap.put("goodsName", obj.getGoods_name());
					goodsmap.put("goodsPrice", obj.getGoods_price());
					goodsmap.put("goods_current_price", obj.getGoods_current_price());
					goodsmap.put("well_evaluate", obj.getWell_evaluate());
					
					for(Evaluate eva:obj.getEvaluates()){
						goodsmap.put("evaluate_status", eva.getEvaluate_status());
					}
				}
				goodsList.add(goodsmap);
				HotMap.put("goodsList", goodsList);
			}
			if(!HotMap.isEmpty()){
				result = new Result(0,"查询成功",HotMap);
			}else{
				result = new Result(1,"查询失败");
			}
		}
	
		String goodsTemp = Json.toJson(result, JsonFormat.compact());
		
		try {
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.getWriter().print(goodsTemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据商品id查询万能属性
	 * @param id
	 * @return
	 */
	public List color(String id){
		Set<GoodsSpecification> specs = new HashSet<GoodsSpecification>();
		Goods goods = null;
		if (id != null && !id.equals("")) {
			 goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			 List<CGoods> cgoods = goods.getCgoods();
			 List<Map> colorList = new ArrayList<Map>();
			 for(CGoods obj : cgoods){
				 if(obj.getSpec_color() != null && !"".equals(obj.getSpec_color())){
					 Map colorMap = new HashMap();
					 colorMap.put("value", obj.getSpec_color());
					 if(!colorList.contains(colorMap)){
						 colorList.add(colorMap);
					 }
				 }
			 }
			 return colorList;
		 }
		return null;
	}
	
	/**
	 * 移动端商品规格信息
	 * 将商品属性归类便于前台展示
	 * @param id
	 * @return
	 */
	public Map generic_spec(String id) {
		Result result = null;
		Map map = new HashMap();
		Goods goods = null;
		List<GoodsSpecification> specs = new ArrayList<GoodsSpecification>();
		if (id != null && !id.equals("")) {
			 goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			if ("spec".equals(goods.getInventory_type())) {
				List<GoodsSpecProperty> ss = goods.getGoods_specs();
				for (GoodsSpecProperty gsp : goods.getGoods_specs()) {
					GoodsSpecification spec = gsp.getSpec();
					if (!specs.contains(spec)) {
						specs.add(spec);
					}
				}
				java.util.Collections.sort(specs, 
						new Comparator<GoodsSpecification>() {
							@Override
							public int compare(GoodsSpecification gs1, GoodsSpecification gs2) {
								// TODO Auto-generated method stub
								return gs1.getSequence() - gs2.getSequence();
							}
						});
			}
		}
		List<Map> spec_list = new ArrayList<Map>();
 		for(GoodsSpecification g_spec : specs){
			Map spec_map = new HashMap();
			spec_map.put("id", g_spec.getId());
			spec_map.put("spec_name", g_spec.getName());
			List<GoodsSpecProperty> val = goods.getGoods_specs();
			List<Map> gsp_list = new ArrayList<Map>();
			for(GoodsSpecProperty gsp : val){
				Map gsp_map = new HashMap();
				if(gsp.getSpec().getId() == g_spec.getId()){
					gsp_map.put("gsp_id", gsp.getId());
					if(g_spec.getType() != null && !g_spec.getType().equals("")){
						String tyoe =  g_spec.getType();
						if(g_spec.getType().equals("img")){
							gsp_map.put("gsp_value", this.configService.getSysConfig().getImageWebServer()+"/"+gsp.getSpecImage().getPath()+"/"+gsp.getSpecImage().getName());
						}
						if(g_spec.getType().equals("text")){
							gsp_map.put("gsp_value", gsp.getValue());
						}
					}
					gsp_list.add(gsp_map);
				}
				
			}
			spec_map.put("gsp_list", gsp_list);
			spec_list.add(spec_map);
		}
 		map.put("spec_info", spec_list);
 		return map;
	}
	
	/**
	 * 移动端商品规格信息
	 * 将商品属性归类便于前台展示
	 * @param id
	 * @return
	 */
	@RequestMapping("/load_generic_spec.json")
	public void load_generic_spec(HttpServletRequest request, HttpServletResponse response, String id) {
		Result result = null;
		Map map = new HashMap();
		Goods goods = null;
		List<GoodsSpecification> specs = new ArrayList<GoodsSpecification>();
		if (id != null && !id.equals("")) {
			 goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			if ("spec".equals(goods.getInventory_type())) {
				for (GoodsSpecProperty gsp : goods.getGoods_specs()) {
					GoodsSpecification spec = gsp.getSpec();
					if (!specs.contains(spec)) {
						specs.add(spec);
					}
				}
				java.util.Collections.sort(specs,
						new Comparator<GoodsSpecification>() {

							@Override
							public int compare(GoodsSpecification gs1,
									GoodsSpecification gs2) {
								// TODO Auto-generated method stub
								return gs1.getSequence() - gs2.getSequence();
							}
						});
			}
		}
		List<Map> spec_list = new ArrayList<Map>();
 		for(GoodsSpecification g_spec : specs){
			Map spec_map = new HashMap();
			spec_map.put("id", g_spec.getId());
			spec_map.put("name", g_spec.getName());
			List<GoodsSpecProperty> val = goods.getGoods_specs();
			List<Map> gsp_list = new ArrayList<Map>();
			for(GoodsSpecProperty gsp : val){
				Map gsp_map = new HashMap();
				if(gsp.getSpec().getId() == g_spec.getId()){
					gsp_map.put("gsp_id", gsp.getId());
					if(g_spec.getType() != null && !g_spec.getType().equals("")){
						String tyoe =  g_spec.getType();
						if(g_spec.getType().equals("img")){
							gsp_map.put("gsp_value", this.configService.getSysConfig().getImageWebServer()+"/"+gsp.getSpecImage().getPath()+"/"+gsp.getSpecImage().getName());
						}
						if(g_spec.getType().equals("text")){
							gsp_map.put("gsp_value", gsp.getValue());
						}
					}
					gsp_list.add(gsp_map);
				}
				
			}
			spec_map.put("gsp_list", gsp_list);
			spec_list.add(spec_map);
		}
 		map.put("spec_info", spec_list);
 		result = new Result(0,"success",map);
 		try {
			response.getWriter().print(Json.toJson(result, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/load_goods_gsps.json")
	public void load_goods_gsp_colors(HttpServletRequest request,
			HttpServletResponse response, String gsp, String id, String color) {
		Result result = null;
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		Map map = new HashMap();
		double current = 0;
		int count = 0;
		double price = 0;
		double act_price = 0;
		if (goods.getGroup() != null && goods.getGroup_buy() == 2) {// 团购商品统一按照团购价格处理
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
					count = gg.getGg_count();
					price = CommUtil.null2Double(gg.getGg_price());
				}
			}
		} else {
			count = goods.getGoods_inventory();
			price = CommUtil.null2Double(goods.getStore_price());
			if (goods.getInventory_type().equals("spec")) {
				/*List<HashMap> list = Json.fromJson(ArrayList.class,
						goods.getGoods_inventory_detail());*/
				
					List<CGoods> cgoods = goods.getCgoods();
					for(CGoods obj : cgoods){
					boolean flag = false;
					String goods_color = obj.getSpec_color() == null ? "" : obj.getSpec_color();
					if(color != null && !goods_color.equals(color)){
						flag = false;
					}else{
						flag = true;
					}
					if(flag){
						Map goods_map = new HashMap();
						String spec_id = obj.getCombination_id();
						String[] s_id = spec_id.split("_");
						String[] gsp_ids = gsp.split(",");
							Arrays.sort(gsp_ids);
							Arrays.sort(s_id);
							if (Arrays.equals(gsp_ids, s_id) && obj.getGoods_disabled().equals("0")) {
								goods_map.put("goods_price", CommUtil.formatMoney(obj.getGoods_price()));
								goods_map.put("goods_current_price", obj.getDiscount_price().equals("") ? "" :  obj.getDiscount_price());
								if(!obj.getDiscount_price().equals("")){
									double prices = CommUtil.div( CommUtil.subtract(obj.getGoods_price(),obj.getDiscount_price().equals("")), obj.getGoods_price())*100;
									goods_map.put("goods_off", Math.round(prices));
								}else{
									goods_map.put("goods_off", "");
								}
								goods_map.put("goods_inventory", obj.getGoods_inventory() == 0? "" : obj.getGoods_inventory());
								List<Accessory> accessorys = obj.getC_goods_photos();
								List<Map> photos = new ArrayList<Map>();
								if(!accessorys.isEmpty()){
									for(Accessory acc : accessorys){
										Map photo_map = new HashMap();
										photo_map.put("photo", configService.getSysConfig().getImageWebServer() 
												+ "/"
												+ acc.getPath() 
												+ "/"+ 
												acc.getName() +"_middle."+ acc.getExt());
										photos.add(photo_map);
									}
									goods_map.put("goods_photo", photos);
								}else{
									goods_map.put("goods_photo", "");
								}
								map.put("spec_map", goods_map);
							}
					}
				
				}
			}
		}
		BigDecimal ac_rebate = null;
		if (goods.getActivity_status() == 2
				&& SecurityUserHolder.getCurrentUser() != null) {// 如果是促销商品，并且用户已登录，根据规格配置价格计算相应配置的促销价格
			ActivityGoods actGoods = this.actgoodsService.getObjById(goods
					.getActivity_goods_id());
			// 0—铜牌会员1—银牌会员2—金牌会员3—超级会员
			BigDecimal rebate = BigDecimal.valueOf(0.00);
			int level = this.integralViewTools.query_user_level(CommUtil
					.null2String(SecurityUserHolder.getCurrentUser().getId()));
			if (level == 0) {
				rebate = actGoods.getAct().getAc_rebate();
			} else if (level == 1) {
				rebate = actGoods.getAct().getAc_rebate1();
			} else if (level == 2) {
				rebate = actGoods.getAct().getAc_rebate2();
			} else if (level == 3) {
				rebate = actGoods.getAct().getAc_rebate3();
			}
			act_price = CommUtil.mul(rebate, price);
		}
		if (act_price != 0) {
			map.put("act_price", CommUtil.formatMoney(act_price));
		}
		result = new Result(0,"success",map);
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
}
