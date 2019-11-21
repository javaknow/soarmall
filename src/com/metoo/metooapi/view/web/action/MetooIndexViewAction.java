package com.metoo.metooapi.view.web.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsBrand;
import com.metoo.foundation.domain.GoodsClass;
import com.metoo.foundation.domain.SysConfig;
import com.metoo.foundation.domain.query.GoodsQueryObject;
import com.metoo.foundation.service.IArticleClassService;
import com.metoo.foundation.service.IArticleService;
import com.metoo.foundation.service.IGoodsBrandService;
import com.metoo.foundation.service.IGoodsClassService;
import com.metoo.foundation.service.IGoodsFloorService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.metooapi.view.web.tool.GoodsMetooFloorViewTools;

@Controller
public class MetooIndexViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IArticleClassService articleClassService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGoodsFloorService goodsFloorService;
	@Autowired 
	private GoodsMetooFloorViewTools GoodsMetooFloorViewToole;
	@Autowired
	private IGoodsService goodsService;
	@Autowired 
	private IGoodsClassService gcService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IUserConfigService userConfigService;
	
	/*@RequestMapping("/goodsinfo.json")	
	public void goodsinfo(HttpServletRequest request, HttpServletResponse response, String json) {
		Result result = null;
		Map goodsinfomap = new HashMap();
		List<Map> map_list = new ArrayList<Map>();
		if (json != null && !json.equals("")) {
			map_list = Json.fromJson(ArrayList.class, json);
			List<Map> goodsmaplist = new ArrayList<Map>();
			for(Map goodsinfo:map_list){
				Map goodsinfojson = new HashMap();
				goodsinfojson.put("goods_id", goodsinfo.get("goods_id"));
				goodsinfojson.put("goods_name", goodsinfo.get("goods_name"));
				goodsinfojson.put("goods_count", goodsinfo.get("goods_count"));
				goodsinfojson.put("goods_price", goodsinfo.get("goods_price"));
				goodsinfojson.put("goods_mainphoto_path", this.configService.getSysConfig().getImageWebServer()+"/"+goodsinfo.get("goods_mainphoto_path"));
				goodsmaplist.add(goodsinfojson);
			}
			goodsinfomap.put("godosinfolist", goodsmaplist);
		}
		
		if(CommUtil.isNotNull(goodsinfomap)){
			result = new Result(0,"查询成功",goodsinfomap);
		}else{
			result = new Result(1,"查询失败");
		}
		String jsontemp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print("jsontemp");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	/**
	 * 递归查询该类目下的所有子类目
	 * @param id
	 * @return
	 */
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
	 *app商品类目列表
	 */
	@RequestMapping("/nav_querygc_pnull.json")	
	public void nav(HttpServletRequest request,HttpServletResponse response){
		List<GoodsClass> goodslist = new ArrayList<GoodsClass>();
		Result result = null;
		Map navmap = new HashMap();
		Map params = new HashMap();
		params.put("display", true);
		goodslist = this.gcService
				.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
						params, -1, -1);
		List gcslist = new ArrayList();
		if(goodslist.size() > 0){
			for(GoodsClass obj:goodslist){
				Map gcsmap = new HashMap();
				gcsmap.put("gcid", obj.getId());
				gcsmap.put("gcname", obj.getClassName());
				try {
					gcsmap.put("icon_acc", this.configService.getSysConfig().getImageWebServer()+"/"+obj.getIcon_acc().getPath()+"/"+obj.getIcon_acc().getName());
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					gcsmap.put("icon_acc", "");
				}
				gcslist.add(gcsmap);
			}
		}
		
		navmap.put("gcslist", gcslist);
		if(CommUtil.isNotNull(navmap)){
			result = new Result(0,"查询成功",navmap);
		}else{
			result = new Result(1,"查询失败");
		}
		String gcs_temp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(gcs_temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * app查询子类目以及类目下最热销商品
	 * 
	 * 可优化【使用递归】
	 * @param request
	 * @param response
	 * @param pid
	 */
	@RequestMapping("/nav_gcchilds.json")
	public void navchilds(HttpServletRequest request,HttpServletResponse response,String pid){
		Map navmap = new HashMap();
		Result result = null;
		List<GoodsClass> gcs = new ArrayList<GoodsClass>();
		if(!CommUtil.null2String(pid).equals("")){
			GoodsClass gc = this.goodsClassService.getObjById(CommUtil
					.null2Long(pid));
			List<Map> topgclist = new ArrayList<Map>();
			Map topgcmap = new HashMap();
			topgcmap.put("gcname", gc.getClassName());
			topgcmap.put("gcid", gc.getId());
			topgcmap.put("level", gc.getLevel());
			topgclist.add(topgcmap);
			navmap.put("topgc", topgclist);
			Map params = new HashMap();
			params.put("display", true);
			params.put("pid",CommUtil.null2Long(pid));
			gcs = this.gcService.query("select obj from GoodsClass obj where obj.parent.id=:pid and obj.display=:display order by obj.sequence asc", params, -1, -1);
			List<Map> gcslist = new ArrayList<Map>();
			for(GoodsClass goodsclass:gcs){
				Map gcsmap = new HashMap() ;
				Set<GoodsClass> gcchilds = goodsclass.getChilds();
				List<Map> childList = new ArrayList<Map>();
				if(gcchilds != null && !gcchilds.equals("")){
					for(GoodsClass gcd:gcchilds){
						Map childsmap = new HashMap();
						Map goodsmap = gc_hot_goods(request, response, CommUtil.null2String(gcd.getId()));
						if(goodsmap.isEmpty()){
						}else{
							childsmap.put("goodsimg", goodsmap.get("goods_img_path"));
							childsmap.put("goodsid", goodsmap.get("goodsid"));
							childsmap.put("goods_name", goodsmap.get("goods_name"));
							childsmap.put("gcname", gcd.getClassName());
							childsmap.put("gcdid", gcd.getId());
						}
						if(!childsmap.isEmpty()){
							childList.add(childsmap);
						}
					}
					if(!childList.isEmpty()){
						gcsmap.put("childList", childList); 
						gcsmap.put("gcid", goodsclass.getId());
						gcsmap.put("gcname", goodsclass.getClassName());
						gcslist.add(gcsmap);
					}
				}
			}
			navmap.put("gcslist", gcslist);
			if(CommUtil.isNotNull(navmap)){
				result = new Result(0,"查询成功",navmap);
			}else{
				result = new Result(1,"查询失败",navmap);
			}
		}else{
			result = new Result(1,"参数错误");
		}
		String navtemp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(navtemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * app查询二级类目下最热销商品
	 * @param request
	 * @param response
	 * @param gc_id
	 * @return
	 */
	public Map gc_hot_goods(HttpServletRequest request,
			HttpServletResponse response, String gc_id){
		Result result = null;
		String imageWebServer = this.configService.getSysConfig().getImageWebServer();
		if(gc_id == null && gc_id.equals("")){
			return null;
		}else{
		GoodsClass gc = this.goodsClassService.getObjById(CommUtil
				.null2Long(gc_id));
		ModelAndView mv = new JModelAndView("",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
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
		GoodsQueryObject gqo = new GoodsQueryObject(null, null, mv,
				"goods_salenum", "desc");
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
		IPageList pList = this.goodsService.list(gqo);
		List<Goods> obj = pList.getResult();
		Map goodsmap = new HashMap();
 		for(Goods goods:obj){
			goodsmap.put("goodsid", goods.getId());
			goodsmap.put("goods_name", goods.getGoods_name());
			goodsmap.put("goods_img_path", goods.getGoods_main_photo() != null
					? imageWebServer + "/" + goods.getGoods_main_photo().getPath() + "/"
							+ goods.getGoods_main_photo().getName() +"_small."+ goods.getGoods_main_photo().getExt() 
					: imageWebServer
							+ "/"
							+ this.configService.getSysConfig()
									.getGoodsImage().getPath()
							+ "/"
							+ this.configService.getSysConfig()
									.getGoodsImage().getName()
							+"_small."
							+this.configService.getSysConfig().getGoodsImage().getExt());
			goodsmap.put("goodsext", goods.getAccessory() != null
					 ? goods.getAccessory().getExt() : null);
 		}
 		return goodsmap;
		}
	}
	
	
	/**
	 * 商品分类移动端
	 * @param request
	 * @param response
	 * @param pid
	 * @param count
	 */
/*	@RequestMapping("/nav_queryGc.json")				   
	public void nav(HttpServletRequest request,HttpServletResponse response,String pid, int count){
		List<GoodsClass> gcs = new ArrayList<GoodsClass>();
		Result result = null;
		String filePathIndexSubject = this.configService.getSysConfig()
				.getUploadFilePath()+"/"+"index_subject";
		Map gcmap = new HashMap();
		Map params = new HashMap();
		params.put("display", true);
		if(!CommUtil.null2String(pid).equals("")){
			params.put("pid",CommUtil.null2Long(pid));
			gcs = this.gcService.query("select obj from GoodsClass obj where obj.parent.id=:pid and obj.display=:display order by obj.sequence asc", params, 0, count);
			List<Map> gcslist = new ArrayList<Map>();
			for(GoodsClass goodsclass:gcs){
				Map gcsmap = new HashMap() ;
				gcsmap.put("gcid", goodsclass.getId());
				gcsmap.put("gcname", goodsclass.getClassName());
				//[一级分类图标显示类型，0为系统图标，1为上传图标 ]
//				gcsmap.put("gcIcon_type", goodsclass.getIcon_type());
//				gcsmap.put("gcIcon_sys", goodsclass.getIcon_sys());//系统图标 
//				try {
//					gcsmap.put("gcimgname", goodsclass.getIcon_acc().getName());
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					gcsmap.put("gcimgname", "");
//				} 
				Set<GoodsClass> gcchilds = goodsclass.getChilds();
				List<Map> childList = new ArrayList<Map>();
				for(GoodsClass gcd:gcchilds){
					Map childsmap = new HashMap();
					childsmap.put("gcname", gcd.getClassName());
					childsmap.put("gcdid", gcd.getId());
					childList.add(childsmap);
				}
				gcsmap.put("childList", childList); 
				gcslist.add(gcsmap);
				gcmap.put("gcslist", gcslist);
			}
		}else{
			gcs = this.gcService
					.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
							params, 0, count);
			List<Map> gcslist = new ArrayList<Map>();
			for(GoodsClass goodsclass:gcs){
				Map gcsmap = new HashMap() ;
				gcsmap.put("gcid", goodsclass.getId());
				gcsmap.put("gcname", goodsclass.getClassName());
				//[一级分类图标显示类型，0为系统图标，1为上传图标 ]
//				gcsmap.put("gcIcon_type", goodsclass.getIcon_type());
//				gcsmap.put("gcIcon_sys", goodsclass.getIcon_sys());//系统图标 
//				try {
//					gcsmap.put("gcimgname", goodsclass.getIcon_acc().getName());
//				} catch (Exception NullPointerException) {
//					// TODO Auto-generated catch block
//					
//					gcsmap.put("gcimgname", "");
//				} 
				Set<GoodsClass> childs = goodsclass.getChilds();
				List<Map> childsList = new ArrayList<Map>();
				
				for(GoodsClass gcChilds: childs){
					Map childsmap = new HashMap();
					childsmap.put("gcname", gcChilds.getClassName());
					childsmap.put("gcdid", gcChilds.getId());
					Set<GoodsClass> childs2 = gcChilds.getChilds();
					List<Map> childsList2 = new ArrayList<Map>();
					for(GoodsClass gcChilds2:childs2){
						Map childsmap2 = new HashMap();
						childsmap2.put("gcname", gcChilds2.getClassName());
						childsmap2.put("gcdid", gcChilds2.getId());
						childsList2.add(childsmap2);
						childsmap.put("childsList2", childsList2);
					}
					childsList.add(childsmap);
				}
				gcsmap.put("childList", childsList); 
				gcslist.add(gcsmap);
				gcmap.put("gcslist", gcslist);
			}
		}
		if(CommUtil.isNotNull(gcmap)){
			result = new Result(0,"查询成功",gcmap);
		}else{
			result = new Result(1,"查询失败");
		}
		String gcs_temp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(gcs_temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/	
	
	/**
	 * 二三级 移动端
	 * @param request
	 * @param response
	 */
	@RequestMapping("/nav_queryGcs.json")				   
	public void nav1(HttpServletRequest request,HttpServletResponse response ){
		List<GoodsClass> gcs = new ArrayList<GoodsClass>();
		Result result = null;
		String filePathIndexSubject = this.configService.getSysConfig()
				.getUploadFilePath()+"/"+"index_subject";
		Map gcmap = new HashMap();
		Map params = new HashMap();
		
		params.put("display", true);
	
			gcs = this.gcService
					.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
							params, -1, -1);
			List<Map> gcslist = new ArrayList<Map>();
			for(GoodsClass goodsclass:gcs){
				Map gcsmap = new HashMap() ;
				gcsmap.put("gcid", goodsclass.getId());
				gcsmap.put("gcname", goodsclass.getClassName());
				//[一级分类图标显示类型，0为系统图标，1为上传图标 ]
				gcsmap.put("gcIcon_type", goodsclass.getIcon_type());
				gcsmap.put("gcIcon_sys", goodsclass.getIcon_sys());//系统图标 
				try {
					gcsmap.put("gcimgname", goodsclass.getIcon_acc().getName());
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					gcsmap.put("gcimgname", "");
				} 
				Set<GoodsClass> childs = goodsclass.getChilds();
				List<Map> childsList = new ArrayList<Map>();
				
				for(GoodsClass gcChilds: childs){
					Map childsmap = new HashMap();
					childsmap.put("gcname", gcChilds.getClassName());
					childsmap.put("gcdid", gcChilds.getId());
					
					Set<GoodsClass> childs2 = gcChilds.getChilds();
					List<Map> childsList2 = new ArrayList<Map>();
					for(GoodsClass gcChilds2:childs2){
						Map childsmap2 = new HashMap();
						childsmap2.put("gcname", gcChilds2.getClassName());
						childsmap2.put("gcdid", gcChilds2.getId());
						childsList2.add(childsmap2);
						childsmap.put("childsList2", childsList2);
					}
					childsList.add(childsmap);
				}
				gcsmap.put("childList", childsList); 
				gcslist.add(gcsmap);
				gcmap.put("gcslist", gcslist);
			}
		
		if(CommUtil.isNotNull(gcmap)){
			result = new Result(0,"查询成功",gcmap);
		}else{
			result = new Result(1,"查询失败");
		}
		String gcs_temp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(gcs_temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	/**
	 * pc 首页品牌数据 
	 * 默认十个
	 * @param request
	 * @param response
	 */
	@RequestMapping("/index_brand.json")
	public void index_brand(HttpServletRequest request,HttpServletResponse response){
		Result result = null;
		Map brand_map = new HashMap();
		List<Map> configlist = new ArrayList<Map>();
		Map sysmap = new HashMap();
		SysConfig syscon = this.configService.getSysConfig();
		sysmap.put("imgwebServer",syscon.getImageWebServer());
		configlist.add(sysmap);
		
		brand_map.put("config",configlist);
		Map params = new HashMap();
		params.put("show_index",true);
		params.put("audit", 1);
		List<GoodsBrand> goodsbrands = this.goodsBrandService.query("select new GoodsBrand(id,name,brandLogo) from GoodsBrand obj where obj.show_index=:show_index and obj.audit=:audit order by obj.sequence asc ",
				params, 0, 10);
		
		List<Map> goodsbrandslist = new ArrayList<Map>(); 
		for(GoodsBrand goodsbrand:goodsbrands){
			Map goodsbrandmap = new HashMap();
			goodsbrandmap.put("brandimgpath", configlist.get(0).get("imgwebServer")+"/"+goodsbrand.getBrandLogo().getPath()+"/"+goodsbrand.getBrandLogo().getName());
			goodsbrandmap.put("brandid", goodsbrand.getId());
			goodsbrandmap.put("brandname",goodsbrand.getName());
			goodsbrandslist.add(goodsbrandmap);
			brand_map.put("goodsbrandlist", goodsbrandslist);
		}
		if(CommUtil.isNotNull(brand_map)){
			result = new Result(0,"查询成功",brand_map);
		}else{	
			result = new Result(1,"查询失败");
		}
		String brand_temp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(brand_temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 	}
	
	/**
	 * pc首页floor
	 * @param request
	 * @param response
	 */
	/*@RequestMapping("/index_floor.json")
	public void index_floor(HttpServletRequest request, HttpServletResponse response){
		Result result = null;
		Map floor_map = new HashMap();
		Map params = new HashMap();
		params.put("gf_display", true);
		List<GoodsFloor> goodsfloors = this.goodsFloorService.query("select obj from GoodsFloor obj where obj.gf_display=:gf_display and parent.id is null order by obj.gf_sequence asc",
				params, -1, -1);
		List<GoodsFloor> goodsfloorlist = new ArrayList<GoodsFloor>();
		List<Map> goodsfloormaps = new ArrayList<Map>();
		for(GoodsFloor goodsfloor:goodsfloors){
			Map goodsfloormap = new HashMap();
			goodsfloormap.put("Gf_name", goodsfloor.getGf_name());
			List<GoodsFloor> floorchilds = goodsfloor.getChilds();
			List<Map> floorchildslist = new ArrayList<Map>();
			for(GoodsFloor goodsfloorchilds : floorchilds){
				Map goodsfloorchildsName = new HashMap();
				goodsfloorchildsName.put("childsname", goodsfloorchilds.getGf_name());
				goodsfloorchildsName.put("Gf_gc_goods", goodsfloorchilds.getGf_gc_goods());
				floorchildslist.add(goodsfloorchildsName);
				
			}
			goodsfloormap.put("floorchildslist",floorchildslist);
			goodsfloormaps.add(goodsfloormap);
		}
		floor_map.put("goodsfloormaps", goodsfloormaps);
		
		if(CommUtil.isNotNull(floor_map)){
			result = new Result(0,"查询成功",floor_map);
		}else{
			result = new Result(1,"查询失败");
		}
		try {
			String floortemp = Json.toJson(result, JsonFormat.compact());
			response.getWriter().print(floortemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error");
		}
	}*/
	
	/**
	 * @param request
	 * @param response
	 * @param json
	 */
	/*@RequestMapping("/index_floor_goods.json")
	public void generic_goods(HttpServletRequest request, HttpServletResponse response,String json) {
		List<Goods> goods_list = new ArrayList<Goods>();
		Result result = null;
		Set<Long> ids = new HashSet<Long>();
		Map goodsfloor_map = new HashMap();
		
		List<Map> configlist = new ArrayList<Map>();
		Map sysmap = new HashMap();
		SysConfig syscon = this.configService.getSysConfig();
		sysmap.put("imgwebServer",syscon.getImageWebServer());
		configlist.add(sysmap);
		
		Map params = new HashMap();
		if (json != null && !json.equals("")) {
			try {
				Map map = Json.fromJson(Map.class, json);
				for (int i = 1; i <= 10; i++) {
					String key = "goods_id" + i;
					ids.add(CommUtil.null2Long(map.get(key)));
				}
				if (!ids.isEmpty()) {
					params.put("ids", ids);
					goods_list = this.goodsService
							.query("select new Goods(id,goods_name,goods_current_price,goods_price,goods_main_photo) from Goods obj where obj.id in(:ids)",
									params, -1, -1);
					List<Map> goodslist = new ArrayList<Map>(); 
					for(Goods goods:goods_list){
						Map goodsfloorchilds = new HashMap();
						goodsfloorchilds.put("path", configlist.get(0).get("imgwebServer")+"/"+goods.getGoods_main_photo().getPath()+"/"+goods.getGoods_main_photo().getName()+"_small."+goods.getGoods_main_photo().getExt());
						goodsfloorchilds.put("ext", goods.getGoods_main_photo().getExt());
						goodsfloorchilds.put("id", goods.getGoods_main_photo().getId());
						try {
							goodsfloorchilds.put("store_second_domain", goods.getGoods_store().getStore_second_domain());
						} catch (NullPointerException e) {
							// TODO Auto-generated catch block
							goodsfloorchilds.put("store_second_domain", "");						}
						goodsfloorchilds.put("goods_type", goods.getGoods_name());
						goodsfloorchilds.put("well_evaluate", goods.getWell_evaluate());
						goodsfloorchilds.put("eva_count", goods.getEvaluate_count());
						goodsfloorchilds.put("goods_price", goods.getGoods_price());
						goodsfloorchilds.put("goods_current_price", goods.getGoods_current_price());
						goodslist.add(goodsfloorchilds);
					}
					goodsfloor_map.put("goodsfloorchilds", goodslist);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(CommUtil.isNotNull(goodsfloor_map)){
			result = new Result(0,"查询商品成功",goodsfloor_map);
		}else{
			result = new Result(1,"查询失败");
		}
		String floorgoodstemp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().println(floorgoodstemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
