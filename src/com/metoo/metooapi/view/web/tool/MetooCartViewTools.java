package com.metoo.metooapi.view.web.tool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.ActivityGoods;
import com.metoo.foundation.domain.Address;
import com.metoo.foundation.domain.Area;
import com.metoo.foundation.domain.CGoods;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsCart;
import com.metoo.foundation.domain.GroupGoods;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.service.IActivityGoodsService;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.manage.admin.tools.UserTools;
import com.metoo.manage.seller.tools.TransportTools;
import com.metoo.view.web.tools.GoodsViewTools;
import com.metoo.view.web.tools.IntegralViewTools;
@Component
public class MetooCartViewTools {

	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IActivityGoodsService actgoodsService;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private UserTools userTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	private TransportTools transportTools;
	
	public List<Map> queryGoods(List<GoodsCart> carts, List<Long> ids, Map erString, Long key){
		if(carts == null){
			return null;
		}else{
			List<Map> normals = new ArrayList<Map>();
			if(CommUtil.isNotNull(carts)){
				for(Object id : ids){
					Store store = this.storeService.getObjById(CommUtil.null2Long(id));
					Map map = new HashMap();
					map.put("store_status", store.getStore_status());
					map.put("store_name", store.getStore_name());
					map.put("store_id", store.getId());
					map.put("store_logo", store.getStore_logo() == null ? "" 
													: this.configService.getSysConfig().getImageWebServer() 
														+ "/"
														+ store.getStore_logo().getPath() 
														+ "/"
														+ store.getStore_logo().getName());
					map.put("store_user_id", store.getUser().getId());
					if(erString != null && !erString.isEmpty()){
						map.put("store_enoughreduce", erString.get(key));
					}
					List<Map> goodslist = new ArrayList<Map>();
					for(GoodsCart obj : carts){
						if(obj.getGoods().getGoods_store().getId().equals(id)){
							Map goods = new HashMap();
							goods.put("goods_cart_id", obj.getId());
							goods.put("goods_main_photo", obj.getGoods().getGoods_main_photo() == null ? "" : 
													this.configService.getSysConfig().getImageWebServer()
													+ "/"
													+ obj.getGoods().getGoods_main_photo()
														.getPath()
													+ "/"
													+ obj.getGoods().getGoods_main_photo()
														.getName());
							goods.put("goods_id", obj.getGoods().getId());
							goods.put("goods_type", obj.getGoods().getGoods_type());
							goods.put("goods_name", obj.getGoods().getGoods_name());
							goods.put("goods_goods_inventory", obj.getGoods().getGoods_inventory());
							goods.put("goods_current_price",  obj.getPrice());
							goods.put("goods_price",  obj.getGoods().getGoods_price());
							goods.put("cart_count", obj.getCount());
							goods.put("goods_spec", obj.getSpec_info() == null ? "" : obj.getSpec_info());
							goods.put("goods_spec_color", obj.getColor()  == null ? "" : obj.getColor());
							goodslist.add(goods);
						}
					}
					if(goodslist != null && !goodslist.isEmpty()){
						map.put("goods", goodslist);
						normals.add(map);
					}
				}
			}
			return normals;
		}
	}
	
	/**
	 * 根据商品及传递的规格信息，计算该规格商品的价格、库存量
	 * 
	 * @param goods
	 * @param gsp
	 * @return 价格、库存组成的Map
	 */
	public Map generic_default_info_color(Goods goods, String gsp, String color) {
		double price = 0;
		Map map = new HashMap();
		int count = 0;
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
			if ("spec".equals(goods.getInventory_type())) {
				if(color != null && !color.equals("")){
					List<CGoods> cgoodsList = goods.getCgoods();
					for(CGoods obj : cgoodsList){
						String spec_id = obj.getCombination_id();
						String[] s_id = spec_id.split("_");
						String[] gsp_ids = gsp.split(",");
						Arrays.sort(gsp_ids);
						Arrays.sort(s_id);
						if (Arrays.equals(gsp_ids, s_id) && obj.getGoods_disabled().equals("0")) {
							price = CommUtil.null2Double(obj.getDiscount_price());
							count = obj.getGoods_inventory();
						}
					}
				}else{
					if (gsp != null && !gsp.equals("")) {
						List<CGoods> cgoodsList = goods.getCgoods();
						for(CGoods obj : cgoodsList){
							String spec_id = obj.getCombination_id();
							String[] s_id = spec_id.split("_");
							String[] gsp_ids = gsp.split(",");
							Arrays.sort(gsp_ids);
							Arrays.sort(s_id);
							if (Arrays.equals(gsp_ids, s_id) && obj.getGoods_disabled().equals("0")) {
								price = CommUtil.null2Double(obj.getDiscount_price());
								count = obj.getGoods_inventory();
							}
						}
					}
				}
			}
		}
		BigDecimal ac_rebate = null;
		if (goods.getActivity_status() == 2 && SecurityUserHolder.getCurrentUser() != null) {// 如果是促销商品，并且用户已登录，根据规格配置价格计算相应配置的促销价格
			ActivityGoods actGoods = this.actgoodsService.getObjById(goods.getActivity_goods_id());
			// 0—铜牌会员1—银牌会员2—金牌会员3—超级会员
			BigDecimal rebate = BigDecimal.valueOf(0.00);
			int level = this.integralViewTools
					.query_user_level(CommUtil.null2String(SecurityUserHolder.getCurrentUser().getId()));
			if (level == 0) {
				rebate = actGoods.getAct().getAc_rebate();
			} else if (level == 1) {
				rebate = actGoods.getAct().getAc_rebate1();
			} else if (level == 2) {
				rebate = actGoods.getAct().getAc_rebate2();
			} else if (level == 3) {
				rebate = actGoods.getAct().getAc_rebate3();
			}
			price = CommUtil.mul(rebate, price);
		}
		map.put("price", price);
		map.put("count", count);
		return map;
	}
	
	/**
	 * 提交购物车获取用户地址
	 * @param address
	 * @return
	 */
	public List<Map> queryAddress(List<Address> address){
		List<Map> addrlist = new ArrayList<Map>();
		Map addrmap = null;
		for(Address obj : address){
			addrmap = new HashMap();
			addrmap.put("addr_id", obj.getId());
			addrmap.put("trueName", obj.getTrueName());
			addrmap.put("telephone", obj.getTelephone());
			addrmap.put("zip", obj.getZip());
			addrmap.put("mobile", obj.getMobile());
			addrmap.put("default_val", obj.getDefault_val());
			if(obj.getArea() != null){
				Area area = this.areaService.getObjById(obj.getArea().getId());
				addrmap.put("area_id", area.getId());
				addrmap.put("area_parent_name", area.getAreaName());
				addrmap.put("area_name", area.getParent().getAreaName());
				addrmap.put("addr_child_name", area.getAreaName());
				addrmap.put("area_info", obj.getArea_info());
			}
			addrlist.add(addrmap);
		}
		return addrlist;
	}
	
}
