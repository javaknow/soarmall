package com.metoo.metooapi.view.web.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.tools.CommUtil;
import com.metoo.buyer.domain.Result;
import com.metoo.core.mv.JModelAndView;
import com.metoo.foundation.domain.GoodsBrand;
import com.metoo.foundation.service.IGoodsBrandCategoryService;
import com.metoo.foundation.service.IGoodsBrandService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.lucene.LuceneResult;
import com.metoo.lucene.LuceneUtil;
import com.metoo.lucene.LuceneVo;
import com.metoo.view.web.tools.GoodsViewTools;
import com.metoo.view.web.tools.StoreViewTools;

@Controller
public class MetooBrandViewAction {
	
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired 
	private ISysConfigService configService;
	@Autowired
	private IGoodsBrandCategoryService goodsBrandCategorySerivce;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	
	@RequestMapping("/wap_brand.json")
	public void wap_brand(HttpServletRequest request, HttpServletResponse response){
		Map goodsBrandMap = new HashMap();
		Result result = null;
		Map params = new HashMap();
		params.put("recommend", true);
		params.put("audit", 1);// 是否通过审核,1为审核通过，0为未审核，-1为审核未通过
		List<GoodsBrand> goodsbrands = this.goodsBrandService.query("select obj from GoodsBrand obj where obj.recommend=:recommend and obj.audit=:audit order by obj.sequence asc",
				params, -1, -1);
		List<Map> brandsmap = new ArrayList<Map>(); 
		for(GoodsBrand goodsBrand:goodsbrands){
			Map goodsBrandInfo = new HashMap();
			goodsBrandInfo.put("brand_id", goodsBrand.getId());
			goodsBrandInfo.put("brand_name", goodsBrand.getName());
			goodsBrandInfo.put("brand_photo", this.configService.getSysConfig().getImageWebServer()+"/"+goodsBrand.getBrandLogo().getPath()+"/"+goodsBrand.getBrandLogo().getName());
			brandsmap.add(goodsBrandInfo);
		}
		goodsBrandMap.put("brands", brandsmap);
		
		List<GoodsBrand> brands = new ArrayList<GoodsBrand>();
		params.clear();
		params.put("audit", 1);
		brands = this.goodsBrandService
				.query("select obj from GoodsBrand obj where obj.audit=:audit order by obj.sequence asc ",
						params, -1, -1);
		List all_list = new ArrayList();
		String list_word = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
		String words[] = list_word.split(",");
		for(String word : words){
			Map brand_map = new HashMap();
			List<Map> word_brand_map = new ArrayList<Map>();
			List<GoodsBrand> brand_list = new ArrayList<GoodsBrand>();
			for(GoodsBrand brand:brands){
				//品牌首字母，后台管理添加
				if(!CommUtil.null2String(brand.getFirst_word()).equals("") 
						&& word.equals(brand.getFirst_word().toUpperCase())){
					brand_list.add(brand);
				}
			}
			for(GoodsBrand goodsBrand:brand_list){
				Map gbsmap = new HashMap();
				gbsmap.put("goods_brand_id", goodsBrand.getId());
				gbsmap.put("recommend", goodsBrand.isRecommend());
				gbsmap.put("goods_brand_name", goodsBrand.getName());
				gbsmap.put("goods_brand_remark", goodsBrand.getRemark());
				gbsmap.put("brand_photo", this.configService.getSysConfig().getImageWebServer()+"/"+goodsBrand.getBrandLogo().getPath()+"/"+goodsBrand.getBrandLogo().getName());
				word_brand_map.add(gbsmap);
			}
			brand_map.put("word_brand_map", word_brand_map);
			brand_map.put("word", word);
			all_list.add(brand_map);
			goodsBrandMap.put("all_list", all_list);
		}
			if(CommUtil.isNotNull(goodsBrandMap)){
				result = new Result(0,"查询成功",goodsBrandMap);
			}else{
				result = new Result(1,"查询失败");
			}
			String brand_temp = Json.toJson(result, JsonFormat.compact());
			try {
				response.setHeader("Access-Control-Allow-Credentials", "true");
				response.getWriter().print(brand_temp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	/**
	 * 根据品牌id查看商品
	 */
	@RequestMapping("/wap_brand_goods.json")
	public void brand_goods(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String orderBy, String orderType, String goods_inventory,
			String goods_type, String goods_transfee, String goods_cod) {
		Result result = null;
		Map brandGoodsMap = new HashMap();
		ModelAndView mv = new JModelAndView("brand_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsBrand goodsBrand = this.goodsBrandService.getObjById(CommUtil
				.null2Long(id));
		if (goodsBrand != null) {
			Map gbmap = new HashMap();
			gbmap.put("goods_brand_id", goodsBrand.getId());
			gbmap.put("recommend", goodsBrand.isRecommend());
			gbmap.put("goods_brand_name", goodsBrand.getName());
			gbmap.put("goods_brand_remark", goodsBrand.getRemark());
			gbmap.put("brand_photo", this.configService.getSysConfig().getImageWebServer() 
					+ "/"
					+ goodsBrand.getBrandLogo().getPath()
					+ "/"
					+ goodsBrand.getBrandLogo().getName()
					+ "_small."
					+ goodsBrand.getBrandLogo().getExt());
			brandGoodsMap.put("gb_info", gbmap);
			String path = System.getProperty("metoob2b2c.root")
					+ File.separator + "luence" + File.separator + "goods";
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(path);
			Sort sort = null;
			boolean order_type = true;
			String order_by = "";
			// 处理排序方式
			if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
				order_by = "goods_salenum";
				sort = new Sort(new SortField(order_by, SortField.Type.INT,
						order_type));
			}
			if (CommUtil.null2String(orderBy).equals("goods_collect")) {
				order_by = "goods_collect";
				sort = new Sort(new SortField(order_by, SortField.Type.INT,
						order_type));
			}
			if (CommUtil.null2String(orderBy).equals("well_evaluate")) {
				order_by = "well_evaluate";
				sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,
						order_type));
			}
			if (CommUtil.null2String(orderType).equals("asc")) {
				order_type = false;
			}
			if (CommUtil.null2String(orderType).equals("")) {
				orderType = "desc";
			}
			if (CommUtil.null2String(orderBy).equals("goods_current_price")) {
				order_by = "store_price";
				sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,
						order_type));
			}
			LuceneResult pList = null;
			if (sort != null) {
				pList = lucene.search(null, CommUtil.null2Int(currentPage),
						goods_inventory, goods_type, null, goods_transfee,
						goods_cod, sort, null, null, goodsBrand.getName());
			} else {
				pList = lucene.search(null, CommUtil.null2Int(currentPage),
						goods_inventory, goods_type, null, goods_transfee,
						goods_cod, null, null, goodsBrand.getName());
			}
			List<LuceneVo> goodslist = pList.getVo_list();
			List<Map> goodsmaplist = new ArrayList<Map>();
			for(LuceneVo goods:goodslist){
				Map goodsmap = new HashMap();   
				goodsmap.put("goods_id", goods.getVo_id());
				goodsmap.put("goods_name", goods.getVo_title());
				goodsmap.put("goods_type", goods.getVo_goods_type());
				goodsmap.put("goods_main_photo", this.configService.getSysConfig().getImageWebServer()+"/"+goods.getVo_main_photo_url());
				
				/*List<String> goods_img = goodsViewTools.query_LuceneVo_photos_url(goods.getVo_photos_url());
				for(String img : goods_img){
					if(img != null && !img.equals("")){
						goodsmap.put("goodsphoto",  this.configService.getSysConfig().getImageWebServer()
								+ "/"
								+ img);
					}
				}*/
				
				goodsmap.put("goodsphoto",  "");
				goodsmap.put("goods_store_price", goods.getVo_store_price());
				goodsmap.put("goods_cost_price", goods.getVo_cost_price());
				goodsmap.put("goods_salenum", goods.getVo_goods_salenum());
				goodsmap.put("goods_well_evaluate", goods.getVo_well_evaluate());
				goodsmap.put("goods_vo_goods_evas", goods.getVo_goods_evas());
				goodsmap.put("goods_evas", goods.getVo_goods_evas());
				goodsmaplist.add(goodsmap);
			}
			brandGoodsMap.put("goodsinfo", goodsmaplist);
			brandGoodsMap.put("allCount", pList.getRows());
		}
		if(CommUtil.isNotNull(brandGoodsMap)){
			result = new Result(0,"查询成功",brandGoodsMap);
		}else{
			result = new Result(1,"查询失败");
		}
		String brandgoodstemp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(brandgoodstemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
