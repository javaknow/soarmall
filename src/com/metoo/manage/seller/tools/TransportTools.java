package com.metoo.manage.seller.tools;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.metoo.core.domain.virtual.CglibBean;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.Area;
import com.metoo.foundation.domain.ExpressCompanyCommon;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsCart;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.Transport;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.IExpressCompanyCommonService;
import com.metoo.foundation.service.IExpressCompanyService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.ITransportService;
import com.metoo.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: TransportTools.java
 * </p>
 * 
 * <p>
 * Description:运费模板工具类，用来处理运费模板相关信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.koala.com
 * </p>
 * 
 * @author erikzhang
 * 
 * @date 2014-11-14
 * 
 * @version koala_b2b2c 2015
 */
@Component
public class TransportTools {
	@Autowired
	private ITransportService transportService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IExpressCompanyService expressCompanyService;
	@Autowired
	private IExpressCompanyCommonService expressCompanyCommonService;

	/**
	 * 根据json查询对应的运费数据信息
	 * 
	 * @param json
	 * @param mark
	 * @return
	 */
	public String query_transprot(String json, String mark) {
		String ret = "";
		List<Map> list = Json.fromJson(ArrayList.class,
				CommUtil.null2String(json));
		if (list != null && list.size() > 0) {
			for (Map map : list) {
				if (CommUtil.null2String(map.get("city_id")).equals("-1")) {
					ret = CommUtil.null2String(map.get(mark));
				}
			}
		}
		return ret;
	}

	/**
	 * 解析出运费模板列表
	 * 
	 * @param json
	 *            运费json数据
	 * @param type
	 *            0为解析所有信息（包含全国配送），1为解析所有区域配送信息
	 * @return 运费模板列表信息
	 * @throws ClassNotFoundException
	 */
	public List<CglibBean> query_all_transprot(String json, int type)
			throws ClassNotFoundException {
		List<CglibBean> cbs = new ArrayList<CglibBean>();
		List<Map> list = Json.fromJson(ArrayList.class,
				CommUtil.null2String(json));
		if (list != null && list.size() > 0) {
			if (type == 0) {
				for (Map map : list) {
					HashMap propertyMap = new HashMap();
					propertyMap.put("city_id",
							Class.forName("java.lang.String"));
					propertyMap.put("city_name",
							Class.forName("java.lang.String"));
					propertyMap.put("trans_weight",
							Class.forName("java.lang.String"));
					propertyMap.put("trans_fee",
							Class.forName("java.lang.String"));
					propertyMap.put("trans_add_weight",
							Class.forName("java.lang.String"));
					propertyMap.put("trans_add_fee",
							Class.forName("java.lang.String"));
					CglibBean cb = new CglibBean(propertyMap);
					cb.setValue("city_id",
							CommUtil.null2String(map.get("city_id")));
					cb.setValue("city_name",
							CommUtil.null2String(map.get("city_name")));
					cb.setValue("trans_weight",
							CommUtil.null2String(map.get("trans_weight")));
					cb.setValue("trans_fee",
							CommUtil.null2String(map.get("trans_fee")));
					cb.setValue("trans_add_weight",
							CommUtil.null2String(map.get("trans_add_weight")));
					cb.setValue("trans_add_fee",
							CommUtil.null2String(map.get("trans_add_fee")));
					cbs.add(cb);
				}
			}
			if (type == 1) {
				for (Map map : list) {
					if (!CommUtil.null2String(map.get("city_id")).equals("-1")) {
						HashMap propertyMap = new HashMap();
						propertyMap.put("city_id",
								Class.forName("java.lang.String"));
						propertyMap.put("city_name",
								Class.forName("java.lang.String"));
						propertyMap.put("trans_weight",
								Class.forName("java.lang.String"));
						propertyMap.put("trans_fee",
								Class.forName("java.lang.String"));
						propertyMap.put("trans_add_weight",
								Class.forName("java.lang.String"));
						propertyMap.put("trans_add_fee",
								Class.forName("java.lang.String"));
						CglibBean cb = new CglibBean(propertyMap);
						cb.setValue("city_id",
								CommUtil.null2String(map.get("city_id")));
						cb.setValue("city_name",
								CommUtil.null2String(map.get("city_name")));
						cb.setValue("trans_weight",
								CommUtil.null2String(map.get("trans_weight")));
						cb.setValue("trans_fee",
								CommUtil.null2String(map.get("trans_fee")));
						cb.setValue("trans_add_weight", CommUtil
								.null2String(map.get("trans_add_weight")));
						cb.setValue("trans_add_fee",
								CommUtil.null2String(map.get("trans_add_fee")));
						cbs.add(cb);
					}
				}
			}
		}
		return cbs;
	}

	/**
	 * 根据运费模板信息、商品重量及配送城市计算商品运费，配送城市根据IP自动获取
	 * 
	 * @param json
	 * @param weight
	 * @param city_name
	 * @return 商品的配送费用
	 */
	public float cal_goods_trans_fee(String trans_id, String type,
			String weight, String volume, String city_name) {
		Transport trans = this.transportService.getObjById(CommUtil
				.null2Long(trans_id));
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
						if (trans.getTrans_type() == 1) {//  按照重量计算运费用
							float goods_weight = CommUtil.null2Float(weight);
							if (goods_weight > 0) {
								fee = trans_fee;
								float other_price = 0;
								if (trans_add_weight > 0) {
									other_price = (float)(trans_add_fee*(Math.ceil(CommUtil.subtract(goods_weight,trans_weight)/trans_add_weight)));
								}
								fee = fee + other_price;
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
								float goods_weight = CommUtil
										.null2Float(weight);
								if (goods_weight > 0) {
									fee = trans_fee;
									float other_price = 0;
									if (trans_add_weight > 0) {
										other_price = (float)(trans_add_fee*(Math.ceil(CommUtil.subtract(goods_weight,trans_weight)/trans_add_weight)));
									}
									fee = fee + other_price;
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
		return fee;
	}
	
	/**
	 * 根据运费模板信息、商品重量及配送城市计算商品运费，配送城市根据IP自动获取
	 * 
	 * @param json
	 * @param weight
	 * @param city_name
	 * @return 商品的配送费用
	 */
	public float cal_goods_trans_fee_metoo(String trans_id, String type,
		Goods obj, String city_name) {
		int count = 1;
		double goods_weight = CommUtil.null2Double(obj.getGoods_weight());
		Transport trans = this.transportService.getObjById(CommUtil
				.null2Long(trans_id));
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
							double lw_price = CommUtil.mul(obj.getGoods_length(), obj.getGoods_width());
							double lwh_price = CommUtil.mul(lw_price, obj.getGoods_high())/5000;
							double kg_price = lwh_price*1000;
							if (CommUtil.null2Float(goods_weight) > CommUtil.null2Double(trans_weight) || kg_price > CommUtil.null2Double(trans_weight)) {
								fee = trans_fee;
								float other_price = 0;
								if (trans_add_weight > 0) {
									other_price = trans_add_fee * Math.round(Math.ceil(CommUtil.subtract(goods_weight,trans_weight)/ trans_add_weight));
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
							float goods_volume = CommUtil.null2Float(obj.getGoods_volume());
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
								double lw_price = CommUtil.mul(obj.getGoods_length(), obj.getGoods_width());
								double lwh_price = CommUtil.mul(lw_price, obj.getGoods_high())/5000;
								double kg_price = lwh_price*1000;
								if (CommUtil.null2Float(goods_weight) > CommUtil.null2Double(trans_weight) || kg_price > CommUtil.null2Double(trans_weight)) {
									fee = trans_fee;
									float other_price = 0;
									if (trans_add_weight > 0) {
										other_price = trans_add_fee * Math.round(Math.ceil(CommUtil.subtract(goods_weight,trans_weight)/ trans_add_weight));
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
										.null2Float(obj.getGoods_volume());
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
		return fee;
	}

	/**
	 * 订单页面地址切换
	 * @param carts
	 * @param area_id
	 * @return
	 */
	public List<SysMap> query_cart_trans_goods_cart3(List<GoodsCart> carts, String area_id) {
		List<SysMap> sms = new ArrayList<SysMap>();
		if (area_id != null && !area_id.equals("")) {
			Area area = this.areaService
					.getObjById(CommUtil.null2Long(area_id)).getParent();
			String city_name = area.getAreaName();
			// System.out.println(city_name);
			float mail_fee = 0;
			float express_fee = 0;
			float express_fee2 = 0;
			float store_express_fee = 0;
			float store_express_fee2 = 0;
			float ems_fee = 0;
			for (GoodsCart gc : carts) {
				Goods goods = this.goodsService.getObjById(gc.getGoods()
						.getId());// 重新查询，避免卖家修改商品、运费模板导致计算运费错误
				if (goods.getGoods_transfee() == 0) {// 买家承担费用
					if (goods.getTransport() != null && goods.getTransport().getTrans_type() == 0) {
						express_fee2 =  this.cal_order_tranes(gc,goods.getTransport()
										.getTrans_express_info(), goods
										.getTransport().getTrans_type(),  goods
										.getGoods_volume(), city_name);
						if(express_fee2 > express_fee){
							express_fee = express_fee2;
						}
					} else {
							express_fee = express_fee
								+ this.cal_order_tranes(gc,goods.getTransport()
										.getTrans_express_info(), goods
										.getTransport().getTrans_type(),  goods
										.getGoods_volume(), city_name);
					}
				}
			}
			if (mail_fee == 0 && express_fee == 0 && ems_fee == 0) {
				for (GoodsCart gc : carts) {
					Goods goods = this.goodsService.getObjById(gc.getGoods()
							.getId());// 重新查询，避免卖家修改商品、运费模板导致计算运费错误
					//不判断该商品由买家或卖家承担运费，计算运费，用于商家结算
					List<Transport> transPort = this.transportService.query("select obj from Transport obj", null, -1, -1);
					for(Transport obj : transPort){
					if(goods.getGoods_global() == 1){ //[普通商品为2 国际直邮为1]
						if(obj.getTrans_name().equals("China")){
							Transport transport = obj;
							store_express_fee = express_fee
									+ this.cal_order_tranes(gc,transport.getTrans_express_info(), transport.getTrans_type(),  goods
											.getGoods_volume(), city_name);
						}
					}else{
						if(obj.getTrans_name().equals("Dubai")){
							Transport transport = obj;
							store_express_fee2 = express_fee
									+ this.cal_order_tranes(gc,transport
											.getTrans_express_info(), transport.getTrans_type(),  goods
											.getGoods_volume(), city_name);
							if(store_express_fee2 > store_express_fee){
								store_express_fee = store_express_fee2;
							}
						}
						}
					}
				}
				sms.add(new SysMap("商家承担", store_express_fee));
			} else {
				if (carts.size() == 1) {// 当只有一个购物车时,只显示开通的快递方式
					if (carts.get(0).getGoods().getTransport() != null) {
						
						if (carts.get(0).getGoods().getTransport() != null
								&& carts.get(0).getGoods().getTransport()
										.isTrans_express()) {
							sms.add(new SysMap("快递",
									express_fee));
						}
					} else {
						sms.add(new SysMap("快递",
								express_fee));
					}
				} else {
					for (GoodsCart gc : carts) {
						Goods goods = this.goodsService.getObjById(gc.getGoods()
								.getId());// 重新查询，避免卖家修改商品、运费模板导致计算运费错误
						if(goods.getGoods_transfee() == 1){
							//不判断该商品由买家或卖家承担运费，计算运费，用于商家结算
							List<Transport> transPort = this.transportService.query("select obj from Transport obj", null, -1, -1);
							for(Transport obj : transPort){
							if(goods.getGoods_global() == 1 && goods.getGoods_transfee() == 1){ //[普通商品为2 国际直邮为1]
								if(obj.getTrans_name().equals("China")){
									Transport transport = obj;
									store_express_fee = store_express_fee
											+ this.cal_order_tranes(gc,transport.getTrans_express_info(), transport.getTrans_type(),  goods
													.getGoods_volume(), city_name);
								}
							}else{
								if(obj.getTrans_name().equals("Dubai") && goods.getGoods_transfee() == 1){
									if(goods.getTransport().getTrans_type() == 1){
										Transport transport = obj;
										store_express_fee = store_express_fee
												+ this.cal_order_tranes(gc,transport
														.getTrans_express_info(), transport.getTrans_type(),  goods
														.getGoods_volume(), city_name);
									}else{
										Transport transport = obj;
										store_express_fee2 = store_express_fee
												+ this.cal_order_tranes(gc,transport
														.getTrans_express_info(), transport.getTrans_type(),  goods
														.getGoods_volume(), city_name);
											if(store_express_fee2 > store_express_fee){
												store_express_fee = store_express_fee2;
											}
										}
									}
								}
							}
						}
						
					}
					sms.add(new SysMap("商家承担", store_express_fee));//[顺序]
					sms.add(new SysMap("快递", express_fee));
				}
			}
		}
		return sms;
	}

	public List<SysMap> query_cart_trans_goods_cart2(List<GoodsCart> carts,
			Map<Long, List<GoodsCart>> er_goods,
			Map<Goods, List<GoodsCart>> ac_goods, String area_id) {
		if (er_goods != null) {
			for (Long id : er_goods.keySet()) {
				List<GoodsCart> list = er_goods.get(id);
				carts.addAll(list);
			}
		}
		if (ac_goods != null) {
			for (Goods id : ac_goods.keySet()) {
				List<GoodsCart> list = ac_goods.get(id);
				carts.addAll(list);
			}
		}
		List<SysMap> sms = new ArrayList<SysMap>();
		if (area_id != null && !area_id.equals("")) {
			Area area = this.areaService
					.getObjById(CommUtil.null2Long(area_id)).getParent();
			String city_name = area.getAreaName();
			float mail_fee = 0;
			float express_fee = 0;
			float express_fee2 = 0;
			float store_express_fee = 0;
			float store_express_fee2 = 0;
			float ems_fee = 0;
			for (GoodsCart gc : carts) {
				Goods goods = this.goodsService.getObjById(gc.getGoods()
						.getId());// 重新查询，避免卖家修改商品、运费模板导致计算运费错误
				if (goods.getGoods_transfee() == 0) {// 买家承担费用
					if (goods.getTransport() != null && goods.getTransport().getTrans_type() == 0) {
						express_fee2 = this.cal_order_tranes(gc,goods.getTransport()
										.getTrans_express_info(), goods
										.getTransport().getTrans_type(),  goods
										.getGoods_volume(), city_name);
						if(express_fee2 > express_fee){
							express_fee = express_fee2;
						}
					} else {
						express_fee = express_fee
								+ this.cal_order_tranes(gc,goods.getTransport()
										.getTrans_express_info(), goods
										.getTransport().getTrans_type(),  goods
										.getGoods_volume(), city_name);
					}
				}
			}
			if (mail_fee == 0 && express_fee == 0 && ems_fee == 0) {
				for (GoodsCart gc : carts) {
					Goods goods = this.goodsService.getObjById(gc.getGoods()
							.getId());// 重新查询，避免卖家修改商品、运费模板导致计算运费错误
					//不判断该商品由买家或卖家承担运费，计算运费，用于商家结算
					List<Transport> transPort = this.transportService.query("select obj from Transport obj", null, -1, -1);
					for(Transport obj : transPort){
					if(goods.getGoods_global() == 1){ //[普通商品为2 国际直邮为1]
						if(obj.getTrans_name().equals("China")){
							Transport transport = obj;
							store_express_fee = express_fee
									+ this.cal_order_tranes(gc,transport.getTrans_express_info(), transport.getTrans_type(),  goods
											.getGoods_volume(), city_name);
						}
					}else{
							if(obj.getTrans_name().equals("Dubai")){
								Transport transport = obj;
								store_express_fee2 = express_fee
										+ this.cal_order_tranes(gc,transport
												.getTrans_express_info(), transport.getTrans_type(),  goods
												.getGoods_volume(), city_name);
								if(store_express_fee2 > store_express_fee){
									store_express_fee = store_express_fee2;
								}
							}
						}
					}
				}
				sms.add(new SysMap("商家承担", store_express_fee));
			} else {
				if (carts.size() == 1) {// 当只有一个购物车时,只显示开通的快递方式
					if (carts.get(0).getGoods().getTransport() != null) {
						
						if (carts.get(0).getGoods().getTransport() != null
								&& carts.get(0).getGoods().getTransport()
										.isTrans_express()) {
							sms.add(new SysMap("快递",
									express_fee));
						}
					} else {
						sms.add(new SysMap("快递",
								express_fee));
					}
				} else {
					for (GoodsCart gc : carts) {
						Goods goods = this.goodsService.getObjById(gc.getGoods()
								.getId());// 重新查询，避免卖家修改商品、运费模板导致计算运费错误
						//不判断该商品由买家或卖家承担运费，计算运费，用于商家结算
						if(goods.getGoods_transfee() == 1){
							List<Transport> transPort = this.transportService.query("select obj from Transport obj", null, -1, -1);
							for(Transport obj : transPort){
							if(goods.getGoods_global() == 1 && goods.getGoods_transfee() == 1){ //[普通商品为2 国际直邮为1]
								if(obj.getTrans_name().equals("China")){
									Transport transport = obj;
									store_express_fee = store_express_fee
											+ this.cal_order_tranes(gc,transport.getTrans_express_info(), transport.getTrans_type(),  goods
													.getGoods_volume(), city_name);
								}
							}else{
								if(obj.getTrans_name().equals("Dubai") && goods.getGoods_transfee() == 1){
										Transport transport = obj;
										store_express_fee2 = store_express_fee
												+ this.cal_order_tranes(gc,transport
														.getTrans_express_info(), transport.getTrans_type(),  goods
														.getGoods_volume(), city_name);
										if(store_express_fee2 > store_express_fee){
											store_express_fee = store_express_fee2;
										}
									
									}
								}
							}
						}
						
					}
					sms.add(new SysMap("商家承担", store_express_fee));//[顺序]
					sms.add(new SysMap("快递", express_fee));
				}
			}
		}
		return sms;
	}
	
	public List<SysMap> query_cart_trans(List<GoodsCart> carts,
			Map<Long, List<GoodsCart>> er_goods,
			Map<Goods, List<GoodsCart>> ac_goods, String area_id) {
		if (er_goods != null) {
			for (Long id : er_goods.keySet()) {
				List<GoodsCart> list = er_goods.get(id);
				carts.addAll(list);
			}
		}
		if (ac_goods != null) {
			for (Goods id : ac_goods.keySet()) {
				List<GoodsCart> list = ac_goods.get(id);
				carts.addAll(list);
			}
		}
		List<SysMap> sms = new ArrayList<SysMap>();
		if (area_id != null && !area_id.equals("")) {
			Area area = this.areaService
					.getObjById(CommUtil.null2Long(area_id)).getParent();
			String city_name = area.getAreaName();
			// System.out.println(city_name);
			float mail_fee = 0;
			float express_fee = 0;
			float ems_fee = 0;
			for (GoodsCart gc : carts) {
				Goods goods = this.goodsService.getObjById(gc.getGoods()
						.getId());// 重新查询，避免卖家修改商品、运费模板导致计算运费错误
				if (goods.getGoods_transfee() == 0) {// 买家承担费用
					if (goods.getTransport() != null) {
						mail_fee = mail_fee
								+ this.cal_order_tranes(gc,goods.getTransport()
										.getTrans_mail_info(), goods
										.getTransport().getTrans_type(), goods
										.getGoods_volume(), city_name);
						express_fee = express_fee
								+ this.cal_order_tranes(gc,goods.getTransport()
										.getTrans_express_info(), goods
										.getTransport().getTrans_type(), goods
										.getGoods_volume(), city_name);
						ems_fee = ems_fee
								+ this.cal_order_tranes(gc,goods.getTransport()
										.getTrans_ems_info(), goods
										.getTransport().getTrans_type(),  goods
										.getGoods_volume(), city_name);
					} else {
						mail_fee = mail_fee
								+ CommUtil
										.null2Float(goods.getMail_trans_fee());
						express_fee = express_fee
								+ CommUtil.null2Float(goods
										.getExpress_trans_fee());
						ems_fee = ems_fee
								+ CommUtil.null2Float(goods.getEms_trans_fee());
					}
				}
			}
			if (mail_fee == 0 && express_fee == 0 && ems_fee == 0) {
				sms.add(new SysMap("商家承担", 0));
			} else {
				if (carts.size() == 1) {// 当只有一个购物车时,只显示开通的快递方式
					if (carts.get(0).getGoods().getTransport() != null) {
						if (carts.get(0).getGoods().getTransport()
								.isTrans_mail()) {
							sms.add(new SysMap("平邮[" + mail_fee + "元]",
									mail_fee));
						}
						if (carts.get(0).getGoods().getTransport() != null
								&& carts.get(0).getGoods().getTransport()
										.isTrans_express()) {
							sms.add(new SysMap("快递[" + express_fee + "元]",
									express_fee));
						}
						if (carts.get(0).getGoods().getTransport() != null
								&& carts.get(0).getGoods().getTransport()
										.isTrans_ems()) {
							sms.add(new SysMap("EMS[" + ems_fee + "元]", ems_fee));
						}
					} else {
						sms.add(new SysMap("平邮[" + mail_fee + "元]", mail_fee));
						sms.add(new SysMap("快递[" + express_fee + "元]",
								express_fee));
						sms.add(new SysMap("EMS[" + ems_fee + "元]", ems_fee));
					}
				} else {
					sms.add(new SysMap("平邮[" + mail_fee + "元]", mail_fee));
					sms.add(new SysMap("快递[" + express_fee + "元]", express_fee));
					sms.add(new SysMap("EMS[" + ems_fee + "元]", ems_fee));
				}
			}
		}
		return sms;
	}

	
	/**
	 * 购物车提交订单查询运费
	 * @param gc
	 * @param trans_json
	 * @param trans_type
	 * @param goods_volume
	 * @param city_name
	 * @return
	 */
	private float cal_order_tranes(GoodsCart gc,String trans_json, int trans_type, Object goods_volume, String city_name) {
		int count = gc.getCount();
		float fee = 0;
		Object goods_weight = gc.getGoods().getGoods_weight();
		boolean cal_flag = false;
		if (trans_json != null && !trans_json.equals("")) {
			List<Map> list = Json.fromJson(ArrayList.class, trans_json);
			if (list != null && list.size() > 0) {
				for (Map map : list) {
					String[] city_list = CommUtil.null2String(
							map.get("city_name")).split("、");
					for (String city : city_list) {
						// System.out.println("==========="+city+" "+city_name);
						if (city.equals(city_name)
								|| city_name.indexOf(city) == 0) {// 城市匹配，如“沈阳市”和“沈阳”是一致的
							cal_flag = true;
							float trans_weight = CommUtil.null2Float(map
									.get("trans_weight"));
							float trans_fee = CommUtil.null2Float(map
									.get("trans_fee"));
							float trans_add_weight = CommUtil.null2Float(map
									.get("trans_add_weight"));
							float trans_add_fee = CommUtil.null2Float(map
									.get("trans_add_fee"));
							if (trans_type == 0) {// 按照件数计算运费
								fee = trans_fee;
								/*if (CommUtil.null2Int(count) > (int) trans_weight) {
									fee = trans_fee;
									float other_price = 0;
									if (trans_add_weight > 0) {
										other_price = trans_add_fee
												* Math.round(Math.ceil(CommUtil
														.subtract(count,
																trans_weight)
														/ trans_add_weight));
									}
									fee = fee + other_price;
								} else {
									fee = trans_fee;
								}*/
							}
							if (trans_type == 1) {// 按照重量计算运费用
								if(count > 1){
									goods_weight = CommUtil.mul(count, goods_weight);
								}
								double lw_price = CommUtil.mul(gc.getGoods().getGoods_length(), gc.getGoods().getGoods_width());
								double lwh_price = CommUtil.mul(lw_price, gc.getGoods().getGoods_high())/5000;
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
							if (trans_type == 2) {// 按照体积计算运费用
								if (CommUtil.null2Float(goods_volume) > CommUtil
										.null2Double(trans_weight)) {
									fee = trans_fee;
									float other_price = 0;
									if (trans_add_weight > 0) {
										other_price = trans_add_fee
												* Math.round(Math.ceil(CommUtil
														.subtract(goods_volume,
																trans_weight)
														/ trans_add_weight));
									}
									fee = fee + other_price;
								} else {
									fee = trans_fee;
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
								float trans_add_weight = CommUtil
										.null2Float(map.get("trans_add_weight"));
								float trans_add_fee = CommUtil.null2Float(map
										.get("trans_add_fee"));
								if (trans_type == 0) {// 按照件数计算运费
							/*		if (CommUtil.null2Int(count) > (int) trans_weight) {
										fee = trans_fee;
										float other_price = 0;
										if (trans_add_weight > 0) {
											other_price = trans_add_fee
													* Math.round(Math.ceil(CommUtil
															.subtract(count,
																	trans_weight)
															/ trans_add_weight));
										}
										fee = fee + other_price;*/
										fee = trans_add_fee;
								/*	} else {
										fee = trans_fee;
									}*/
								}
								if (trans_type == 1) {// 按照重量计算运费用
									if(count > 1){
										goods_weight = CommUtil.mul(count, goods_weight);
									}
									double lw_price = CommUtil.mul(gc.getGoods().getGoods_length(), gc.getGoods().getGoods_width());
									double lwh_price = CommUtil.mul(lw_price, gc.getGoods().getGoods_high())/5000;
									double kg_price = lwh_price*1000;
									if (CommUtil.null2Float(goods_weight) > CommUtil.null2Double(trans_weight) || kg_price > CommUtil.null2Double(trans_weight)) {
										fee = trans_fee;
										float other_price = 0;
										if (trans_add_weight > 0) {
											other_price = trans_add_fee * Math.round(Math.ceil(CommUtil.subtract(goods_weight,trans_weight)/ trans_add_weight));
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
								if (trans_type == 2) {// 按照体积计算运费用
									if (CommUtil.null2Float(goods_volume) > CommUtil
											.null2Double(trans_weight)) {
										fee = trans_fee;
										float other_price = 0;
										if (trans_add_weight > 0) {
											other_price = trans_add_fee
													* Math.round(Math.ceil(CommUtil
															.subtract(
																	goods_volume,
																	trans_weight)
															/ trans_add_weight));
										}
										fee = fee + other_price;
									} else {
										fee = trans_fee;
									}
								}
								break;
							}
						}
					}
				}
			}
		}
		return fee;
	}
	
	
	private void next() {
		// TODO Auto-generated method stub

	}

	private float cal_order_trans(String trans_json, int trans_type,
			Object goods_weight, Object goods_volume, String city_name,
			int count) {
		float fee = 0;
		boolean cal_flag = false;
		if (trans_json != null && !trans_json.equals("")) {
			List<Map> list = Json.fromJson(ArrayList.class, trans_json);
			if (list != null && list.size() > 0) {
				for (Map map : list) {
					String[] city_list = CommUtil.null2String(
							map.get("city_name")).split("、");
					for (String city : city_list) {
						// System.out.println("==========="+city+" "+city_name);
						if (city.equals(city_name)
								|| city_name.indexOf(city) == 0) {// 城市匹配，如“沈阳市”和“沈阳”是一致的
							cal_flag = true;
							float trans_weight = CommUtil.null2Float(map
									.get("trans_weight"));
							float trans_fee = CommUtil.null2Float(map
									.get("trans_fee"));
							float trans_add_weight = CommUtil.null2Float(map
									.get("trans_add_weight"));
							float trans_add_fee = CommUtil.null2Float(map
									.get("trans_add_fee"));
							if (trans_type == 0) {// 按照件数计算运费
								if (CommUtil.null2Int(count) > (int) trans_weight) {
									fee = trans_fee;
									float other_price = 0;
									if (trans_add_weight > 0) {
										other_price = trans_add_fee
												* Math.round(Math.ceil(CommUtil
														.subtract(count,
																trans_weight)
														/ trans_add_weight));
									}
									fee = fee + other_price;
								} else {
									fee = trans_fee;
								}
							}
							if (trans_type == 1) {// 按照重量计算运费用
								if(count > 1){
									goods_weight = CommUtil.mul(count, goods_weight);
								}
								if (CommUtil.null2Float(goods_weight) > CommUtil
										.null2Double(trans_weight)) {
									fee = trans_fee;
									float other_price = 0;
									if (trans_add_weight > 0) {
										other_price = trans_add_fee
												* Math.round(Math.ceil(CommUtil
														.subtract(goods_weight,
																trans_weight)
														/ trans_add_weight));
									}
									fee = fee + other_price;
								} else {
									fee = trans_fee;
								}
							}
							if (trans_type == 2) {// 按照体积计算运费用
								if (CommUtil.null2Float(goods_volume) > CommUtil
										.null2Double(trans_weight)) {
									fee = trans_fee;
									float other_price = 0;
									if (trans_add_weight > 0) {
										other_price = trans_add_fee
												* Math.round(Math.ceil(CommUtil
														.subtract(goods_volume,
																trans_weight)
														/ trans_add_weight));
									}
									fee = fee + other_price;
								} else {
									fee = trans_fee;
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
								float trans_add_weight = CommUtil
										.null2Float(map.get("trans_add_weight"));
								float trans_add_fee = CommUtil.null2Float(map
										.get("trans_add_fee"));
								if (trans_type == 0) {// 按照件数计算运费
									if (CommUtil.null2Int(count) > (int) trans_weight) {
										fee = trans_fee;
										float other_price = 0;
										if (trans_add_weight > 0) {
											other_price = trans_add_fee
													* Math.round(Math.ceil(CommUtil
															.subtract(count,
																	trans_weight)
															/ trans_add_weight));
										}
										fee = fee + other_price;
									} else {
										fee = trans_fee;
									}
								}
								if (trans_type == 1) {// 按照重量计算运费用
									if(count > 1){
										goods_weight = CommUtil.mul(count, goods_weight);
									}
									if (CommUtil.null2Float(goods_weight) > CommUtil
											.null2Double(trans_weight)) {
										fee = trans_fee;
										float other_price = 0;
										if (trans_add_weight > 0) {
											other_price = trans_add_fee
													* Math.round(Math.ceil(CommUtil
															.subtract(
																	goods_weight,
																	trans_weight)
															/ trans_add_weight));
										}
										fee = fee + other_price;
									} else {
										fee = trans_fee;
									}
								}
								if (trans_type == 2) {// 按照体积计算运费用
									if (CommUtil.null2Float(goods_volume) > CommUtil
											.null2Double(trans_weight)) {
										fee = trans_fee;
										float other_price = 0;
										if (trans_add_weight > 0) {
											other_price = trans_add_fee
													* Math.round(Math.ceil(CommUtil
															.subtract(
																	goods_volume,
																	trans_weight)
															/ trans_add_weight));
										}
										fee = fee + other_price;
									} else {
										fee = trans_fee;
									}
								}
								break;
							}
						}
					}
				}
			}
		}
		return fee;
	}

	/**
	 * 查询当前快递是否为商家常用物流
	 * 
	 * @param id
	 *            快递id
	 * @return 是否为常用物流公司
	 */
	public int query_common_ec(String id) {
		int ret = 0;
		if (!CommUtil.null2String(id).equals("")) {
			Map params = new HashMap();
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			List<ExpressCompanyCommon> eccs = new ArrayList<ExpressCompanyCommon>();
			if (store != null) {
				params.put("ecc_type", 0);
				params.put("ecc_store_id", store.getId());
				eccs = this.expressCompanyCommonService
						.query("select obj from ExpressCompanyCommon obj where obj.ecc_type=:ecc_type and obj.ecc_store_id=:ecc_store_id",
								params, -1, -1);
				for (ExpressCompanyCommon ecc : eccs) {
					if (ecc.getEcc_ec_id().equals(CommUtil.null2Long(id))) {
						ret = 1;
					}
				}
			} else {
				params.put("ecc_type", 1);
				eccs = this.expressCompanyCommonService
						.query("select obj from ExpressCompanyCommon obj where obj.ecc_type=:ecc_type",
								params, -1, -1);
				for (ExpressCompanyCommon ecc : eccs) {
					if (ecc.getEcc_ec_id().equals(CommUtil.null2Long(id))) {
						ret = 1;
					}
				}
			}
		}
		return ret;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		String s = "[{\"trans_weight\":1,\"trans_fee\":13.5,\"trans_add_weight\":1,\"trans_add_fee\":2},{\"city_id\":1,\"city_name\":\"沈阳\",\"trans_weight\":1,\"trans_fee\":13.5,\"trans_add_weight\":1,\"trans_add_fee\":2}]";
		List<Map> list = Json.fromJson(ArrayList.class, s);
		for (Map map : list) {
			System.out.println(map.get("city_id"));
		}
	}
}
