package com.metoo.metooapi.view.web.action;

import java.io.IOException;
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
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsCase;
import com.metoo.foundation.domain.SysConfig;
import com.metoo.foundation.service.IGoodsCaseService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.ISysConfigService;

@Controller
public class MetooGoodsCaseViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IGoodsCaseService goodscaseService;
	@Autowired
	private IGoodsService goodsService;
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param case_id 橱窗标识
	 */
	@RequestMapping("/query_goodscase.json")
	public void queryGoodsCase(HttpServletRequest request, HttpServletResponse response,
			String case_id){
		Result result = null;
		Map casemap = new HashMap();
		Map params = new HashMap();
		params.put("case_id", case_id);
		params.put("display", 1);
		List<GoodsCase> list = this.goodscaseService
				.query("select obj from GoodsCase obj where obj.case_id=:case_id and obj.display=:display order by obj.sequence",
						params, 0, 6);
		List<Map> caselist = new ArrayList<Map>();
		for(GoodsCase goodscase:list){
			Map goodscasemap = new HashMap();
			goodscasemap.put("id", goodscase.getId());
			goodscasemap.put("goodsCase_id", goodscase.getCase_id());
			goodscasemap.put("goodsCase_name", goodscase.getCase_name());
			goodscasemap.put("case_content", goodscase.getCase_content());
			caselist.add(goodscasemap);
			casemap.put("goodscasemap", caselist);
		}
		if(CommUtil.isNotNull(casemap)){
			result = new Result(0,"查询成功",casemap);
		}else{
			result = new Result(0,"查询失败");
		}
		
	String goodscasetem = Json.toJson(result, JsonFormat.compact());
		try {
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.getWriter().println(goodscasetem);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/query_casegoods.json")
	public void querycasegoods(HttpServletRequest request, HttpServletResponse response,
			String case_content){
		
		if(case_content != null && !case_content.equals("")){
			List list = (List) Json.fromJson(case_content);
			Result result = null;
			Map casegoodsmap = new HashMap();
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
			casegoodsmap.put("config", configlist);
			
			
			List<Goods> goodslist = new ArrayList<Goods>();
			List<Map> casegoodslist = new ArrayList<Map>();
			if (list.size() > 6) {
				for (Object id : list.subList(0, 6)) {
					Map params = new HashMap();
					params.put("id", CommUtil.null2Long(id));
					List<Goods> objs = this.goodsService
							.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.id=:id",
									params, 0, 1);
					if (objs.size() > 0) {
						
						for(Goods goods:objs){
							Map goodsmap = new HashMap();
							try {
								goodsmap.put("casegoodsimgpath", configlist.get(0).get("imgwebServer")+"/"+goods.getGoods_main_photo().getPath()+"/"+goods.getGoods_main_photo().getName());
								goodsmap.put("imgpath", goods.getGoods_main_photo().getPath());
								goodsmap.put("imgname", goods.getGoods_main_photo().getName());
								goodsmap.put("imgext", goods.getGoods_main_photo().getExt());
								goodsmap.put("Store_second_domain", goods.getGoods_store().getStore_second_domain());
							} catch (Exception NullPointerException) {
								// TODO Auto-generated catch block
								goodsmap.put("imgpath", "");
								goodsmap.put("imgname", "");
								goodsmap.put("imgext", "");
								goodsmap.put("Store_second_domain", "");
							}
							
							goodsmap.put("goods_id", goods.getId());
							goodsmap.put("goods_name", goods.getGoods_name());
							goodsmap.put("well_evaluate", goods.getWell_evaluate());
							goodsmap.put("evaluate_count", goods.getEvaluate_count());
							goodsmap.put("goods_price", goods.getGoods_price());
							goodsmap.put("goods_current_price", goods.getGoods_current_price());
							goodsmap.put("goods_type", goods.getGoods_type());
							
							casegoodslist.add(goodsmap);
							
						}
						casegoodsmap.put("casegoodslist", casegoodslist);
						//goodslist.add(objs.get(0));
					}
				}
			} else {
				for (Object id : list) {
					Map params = new HashMap();
					params.put("id", CommUtil.null2Long(id));
					List<Goods> objs = this.goodsService
							.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.id=:id",
									params, 0, 1);
					if (objs.size() > 0) {
						for(Goods goods:objs){
							Map goodsmap2 = new HashMap();
							try {
								goodsmap2.put("casegoodsimgpath", configlist.get(0).get("imgwebServer")+"/"+goods.getGoods_main_photo().getPath()+"/"+goods.getGoods_main_photo().getName());
								goodsmap2.put("imgpath", goods.getGoods_main_photo().getPath());
								goodsmap2.put("imgname", goods.getGoods_main_photo().getName());
								goodsmap2.put("imgext", goods.getGoods_main_photo().getExt());
								goodsmap2.put("Store_second_domain", goods.getGoods_store().getStore_second_domain());
							} catch (Exception NullPointerException) {
								// TODO Auto-generated catch block
								goodsmap2.put("Store_second_domain", "");
								goodsmap2.put("casegoodsimgpath", "");
								goodsmap2.put("imgname", "");
								goodsmap2.put("imgext", "");
							}
							goodsmap2.put("goods_id", goods.getId());
							goodsmap2.put("goods_name", goods.getGoods_name());
							goodsmap2.put("well_evaluate", goods.getWell_evaluate());
							goodsmap2.put("evaluate_count", goods.getEvaluate_count());
							goodsmap2.put("goods_price", goods.getGoods_price());
							goodsmap2.put("goods_current_price", goods.getGoods_current_price());
							goodsmap2.put("goods_type", goods.getGoods_type());
							
							casegoodslist.add(goodsmap2);
							
						}
						casegoodsmap.put("casegoodslist", casegoodslist);
						//goodslist.add(objs.get(0));
					}
				}
			}
			if(CommUtil.isNotNull(casegoodsmap)){
				result = new Result(0,"查询成功",casegoodsmap);
			}else{
				result = new Result(1,"查询失败");
			}
			
			String casegoodstemp = Json.toJson(result, JsonFormat.compact());
			try {
				response.getWriter().println(casegoodstemp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				response.setHeader("Access-Control-Allow-Credentials", "true");
				response.getWriter().println("参数错误！");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
