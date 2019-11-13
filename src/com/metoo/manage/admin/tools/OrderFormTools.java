package com.metoo.manage.admin.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.Evaluate;
import com.metoo.foundation.domain.ExpressCompany;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsSpecProperty;
import com.metoo.foundation.domain.IntegralGoods;
import com.metoo.foundation.domain.IntegralGoodsOrder;
import com.metoo.foundation.domain.OrderForm;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.SysConfig;
import com.metoo.foundation.domain.virtual.TransContent;
import com.metoo.foundation.domain.virtual.TransInfo;
import com.metoo.foundation.service.IEvaluateService;
import com.metoo.foundation.service.IExpressCompanyService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IGoodsSpecPropertyService;
import com.metoo.foundation.service.IIntegralGoodsOrderService;
import com.metoo.foundation.service.IIntegralGoodsService;
import com.metoo.foundation.service.IOrderFormService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.kuaidi100.domain.ExpressInfo;
import com.metoo.kuaidi100.service.IExpressInfoService;


/**
 * 
 * <p>
 * Title: MsgTools.java
 * </p>
 * 
 * <p>
 * Description: 订单解析工具，解析订单中json数据
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
 * @author hezeng
 * 
 * @date 2014-5-4
 * 
 * @version koala_b2b2c v2.0 2015版
 */
@Component
public class OrderFormTools {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsSpecPropertyService gspService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IExpressCompanyService expressCompanyService;
	@Autowired
	private IExpressInfoService expressInfoService;
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IIntegralGoodsService integralGoodsService;

	/**
	 * 解析订单商品信息json数据
	 * 
	 * @param order_id
	 * @return
	 */
	public List<Map> queryGoodsInfo(String json) {

		List<Map> map_list = new ArrayList<Map>();
		if (json != null && !json.equals("")) {
			map_list = Json.fromJson(ArrayList.class, json);
		}
		return map_list;
	}
	
	/**
	 * 根据订单id查询该订单中所有商品,包括子订单中的商品
	 * 
	 * @param order_id
	 * @return
	 */
	public List<Goods> queryOfGoods(String of_id) {
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(of_id));
		List<Map> map_list = this.queryGoodsInfo(of.getGoods_info());
		List<Goods> goods_list = new ArrayList<Goods>();
		for (Map map : map_list) {
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(map
					.get("goods_id")));
			goods_list.add(goods);
		}
		if (!CommUtil.null2String(of.getChild_order_detail()).equals("")) {// 查询子订单中的商品信息
			List<Map> maps = this.queryGoodsInfo(of.getChild_order_detail());
			for (Map map : maps) {
				OrderForm child_order = this.orderFormService
						.getObjById(CommUtil.null2Long(map.get("order_id")));
				map_list.clear();
				map_list = this.queryGoodsInfo(child_order.getGoods_info());
				for (Map map1 : map_list) {
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(map1.get("goods_id")));
					goods_list.add(goods);
				}
			}
		}
		return goods_list;
	}

	/**
	 * 根据订单id查询该订单中所有商品的价格总和
	 * 
	 * @param order_id
	 * @return
	 */
	public double queryOfGoodsPrice(String order_id) {
		double price = 0;
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		List<Map> map_list = this.queryGoodsInfo(of.getGoods_info());
		for (Map map : map_list) {
			price = price + CommUtil.null2Double(map.get("goods_all_price"));
		}
		return price;
	}

	/**
	 * 根据订单id和商品id查询该商品在该订单中的数量
	 * 
	 * @param order_id
	 * @return
	 */
	public int queryOfGoodsCount(String order_id, String goods_id) {
		int count = 0;
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		List<Map> map_list = this.queryGoodsInfo(of.getGoods_info());
		for (Map map : map_list) {
			if (CommUtil.null2String(map.get("goods_id")).equals(goods_id)) {
				count = CommUtil.null2Int(map.get("goods_count"));
				break;
			}
		}
		if (count == 0) {// 主订单无数量信息，继续从子订单中查询
			if (!CommUtil.null2String(of.getChild_order_detail()).equals("")) {
				List<Map> maps = this
						.queryGoodsInfo(of.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(map.get("order_id")));
					map_list.clear();
					map_list = this.queryGoodsInfo(child_order.getGoods_info());
					for (Map map1 : map_list) {
						if (CommUtil.null2String(map1.get("goods_id")).equals(
								goods_id)) {
							count = CommUtil.null2Int(map1.get("goods_count"));
							break;
						}
					}
				}
			}
		}
		return count;
	}

	/**
	 * 根据订单id和商品id查询该商品在该订单中的规格
	 * 
	 * @param order_id
	 * @return
	 */
	public List<GoodsSpecProperty> queryOfGoodsGsps(String order_id,
			String goods_id) {
		List<GoodsSpecProperty> list = new ArrayList<GoodsSpecProperty>();
		String goods_gsp_ids = "";
		OrderForm of = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		List<Map> map_list = this.queryGoodsInfo(of.getGoods_info());
		boolean add = false;
		for (Map map : map_list) {
			if (CommUtil.null2String(map.get("goods_id")).equals(goods_id)) {
				goods_gsp_ids = CommUtil.null2String(map.get("goods_gsp_ids"));
				break;
			}
		}
		String gsp_ids[] = goods_gsp_ids.split(",");
		Arrays.sort(gsp_ids);
		for (String id : gsp_ids) {
			if (!id.equals("")) {
				GoodsSpecProperty gsp = this.gspService.getObjById(CommUtil
						.null2Long(id));
				list.add(gsp);
				add = true;
			}
		}
		if (!add) {// 如果主订单中添加失败，则从子订单中添加
			if (!CommUtil.null2String(of.getChild_order_detail()).equals("")) {
				List<Map> maps = this
						.queryGoodsInfo(of.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(child_map
									.get("order_id")));
					map_list.clear();
					map_list = this.queryGoodsInfo(child_order.getGoods_info());
					for (Map map : map_list) {
						if (CommUtil.null2String(map.get("goods_id")).equals(
								goods_id)) {
							goods_gsp_ids = CommUtil.null2String(map
									.get("goods_gsp_ids"));
							break;
						}
					}
					String child_gsp_ids[] = goods_gsp_ids.split("/");
					for (String id : child_gsp_ids) {
						if (!id.equals("")) {
							GoodsSpecProperty gsp = this.gspService
									.getObjById(CommUtil.null2Long(id));
							list.add(gsp);
							add = true;
						}
					}
				}
			}

		}
		return list;
	}

	/**
	 * 解析订单物流信息json数据
	 * 
	 * @param json
	 * @return
	 */
	public String queryExInfo(String json, String key) {
		Map map = new HashMap();
		if (json != null && !json.equals("")) {
			map = Json.fromJson(HashMap.class, json);
		}
		return CommUtil.null2String(map.get(key));
	}

	/**
	 * 解析订单优惠券信息json数据
	 * 
	 * @param json
	 * @return
	 */
	public Map queryCouponInfo(String json) {
		Map map = new HashMap();
		if (json != null && !json.equals("")) {
			map = Json.fromJson(HashMap.class, json);
		}
		return map;
	}

	/**
	 * 解析生活类团购订单json数据
	 * 
	 * @param json
	 * @return
	 */
	public Map queryGroupInfo(String json) {
		Map map = new HashMap();
		if (json != null && !json.equals("")) {
			map = Json.fromJson(HashMap.class, json);
		}
		return map;
	}

	/**
	 * 根据订单id查询订单信息
	 * 
	 * @param id
	 * @return
	 */
	public OrderForm query_order(String id) {
		return this.orderFormService.getObjById(CommUtil.null2Long(id));
	}

	/**
	 * 查询订单的状态，用在买家中心的订单列表中，多商家复合订单中只有全部商家都已经发货，卖家中心才会出现确认收货按钮
	 * 
	 * @param order_id
	 * @return
	 */
	public int query_order_status(String order_id) {
		int order_status = 0;
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (order != null) {
			order_status = order.getOrder_status();
/*			if (order.getOrder_main() == 1
					&& !CommUtil.null2String(order.getChild_order_detail())
							.equals("")) {
				List<Map> maps = this.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(child_map
									.get("order_id")));
					if (child_order.getOrder_status() < 30) {
						order_status = child_order.getOrder_status();
					}
				}
			}*/
		}
		return order_status;
	}

	/**
	 * 查询订单总价格（如果包含子订单，将子订单价格与主订单价格相加）
	 * 
	 * @param order_id
	 * @return
	 */
	public double query_order_price(String order_id) {
		double all_price = 0;
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (order != null) {
			all_price = CommUtil.null2Double(order.getTotalPrice());
			if (order.getChild_order_detail() != null
					&& !order.getChild_order_detail().equals("")) {
				List<Map> maps = this.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(map.get("order_id")));
					all_price = all_price
							+ CommUtil.null2Double(child_order.getTotalPrice());
				}

			}
		}
		return all_price;
	}

	public double query_order_goods(String order_id) {
		double all_goods = 0;
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (order != null) {
			all_goods = CommUtil.null2Double(order.getGoods_amount());
			if (order.getChild_order_detail() != null
					&& !order.getChild_order_detail().equals("")) {
				List<Map> maps = this.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(map.get("order_id")));
					all_goods = all_goods
							+ CommUtil.null2Double(child_order
									.getGoods_amount());
				}
			}
		}
		return all_goods;
	}

	/**
	 * 解析订单中组合套装详情
	 * 
	 * @param order_id
	 * @return
	 */
	public Map query_order_suitinfo(String goods_info) {
		Map map = (Map) Json.fromJson(goods_info);
		return map;
	}

	/**
	 * 解析订单中组合套装详情
	 * 
	 * @param order_id
	 * @return
	 */
	public List<Map> query_order_suitgoods(Map suit_map) {
		List<Map> map_list = new ArrayList();
		if (suit_map != null && !suit_map.equals("")) {
			map_list = (List<Map>) suit_map.get("goods_list");
		}
		return map_list;
	}

	/**
	 * 根据店铺id查询是否开启了二级域名。
	 * 
	 * @param id为参数
	 *            type为store时查询store type为goods时查询商品
	 * @return
	 */
	public Store goods_second_domain(String id, String type) {
		Store store = null;
		if (type.equals("store")) {
			store = this.storeService.getObjById(CommUtil.null2Long(id));
		}
		if (type.equals("goods")) {
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			if (goods != null && goods.getGoods_type() == 1) {
				store = goods.getGoods_store();
			}
		}
		return store;
	}

	public TransInfo query_ship_getData(String id) {
		TransInfo info = new TransInfo();
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null && !CommUtil.null2String(obj.getShipCode()).equals("")) {
			if (this.configService.getSysConfig().getKuaidi_type() == 0) {// 免费物流接口
				try {
					ExpressCompany ec = this.queryExpressCompany(obj
							.getExpress_info());
					String query_url = "http://api.kuaidi100.com/api?id="
							+ this.configService.getSysConfig().getKuaidi_id()
							+ "&com="
							+ (ec != null ? ec.getCompany_mark() : "") + "&nu="
							+ obj.getShipCode() + "&show=0&muti=1&order=asc";
					URL url = new URL(query_url);
					URLConnection con = url.openConnection();
					con.setAllowUserInteraction(false);
					InputStream urlStream = url.openStream();
					String type = con.guessContentTypeFromStream(urlStream);
					String charSet = null;
					if (type == null)
						type = con.getContentType();
					if (type == null || type.trim().length() == 0
							|| type.trim().indexOf("text/html") < 0)
						return info;
					if (type.indexOf("charset=") > 0)
						charSet = type.substring(type.indexOf("charset=") + 8);
					byte b[] = new byte[10000];
					int numRead = urlStream.read(b);
					String content = new String(b, 0, numRead, charSet);
					while (numRead != -1) {
						numRead = urlStream.read(b);
						if (numRead != -1) {
							// String newContent = new String(b, 0, numRead);
							String newContent = new String(b, 0, numRead,
									charSet);
							content += newContent;
						}
					}
					info = Json.fromJson(TransInfo.class, content);
					urlStream.close();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (this.configService.getSysConfig().getKuaidi_type() == 1) {// 收费物流接口
				ExpressInfo ei = this.expressInfoService
						.getObjByPropertyWithType("order_id", obj.getId(), 0);
				if (ei != null) {
					List<TransContent> data = (List<TransContent>) Json
							.fromJson(CommUtil.null2String(ei
									.getOrder_express_info()));
					info.setData(data);
					info.setStatus("1");
				}
			}
		}
		return info;
	}

	/**
	 * 解析订单中自提点信息
	 * 
	 * @param order_id
	 * @return
	 */
	public Map query_order_delivery(String delivery_info) {
		Map map = (Map) Json.fromJson(delivery_info);
		return map;
	}

	private ExpressCompany queryExpressCompany(String json) {
		ExpressCompany ec = null;
		if (json != null && !json.equals("")) {
			HashMap map = Json.fromJson(HashMap.class, json);
			ec = this.expressCompanyService.getObjById(CommUtil.null2Long(map
					.get("express_company_id")));
		}
		return ec;
	}

	/**
	 * 查询订单中所以商品数量
	 * 
	 * @param order_id
	 * @return
	 */
	public int query_goods_count(String order_id) {
		OrderForm orderForm = this.query_order(order_id);
		List<Map> list_map = new ArrayList<Map>();
		int count = 0;
		if (orderForm != null) {
			list_map = this.queryGoodsInfo(orderForm.getGoods_info());
			for (Map map : list_map) {
				count = count + CommUtil.null2Int(map.get("goods_count"));
			}
			if (orderForm.getOrder_main() == 1
					&& !CommUtil.null2String(orderForm.getChild_order_detail())
							.equals("")) {
				list_map = this.queryGoodsInfo(orderForm
						.getChild_order_detail());
				for (Map map : list_map) {
					List<Map> list_map1 = new ArrayList<Map>();
					list_map1 = this.queryGoodsInfo(map.get("order_goods_info")
							.toString());
					for (Map map2 : list_map1) {
						count = count
								+ CommUtil.null2Int(map2.get("goods_count"));
					}
				}
			}
		}
		return count;
	}

	/**
	 * 查询订单中所有团购数量
	 * 
	 * @param order_id
	 * @return
	 */
	public int query_group_count(String order_id) {
		OrderForm orderForm = this.query_order(order_id);
		Map map = new HashMap();
		int count = 0;
		if (orderForm != null) {
			map = this.queryGroupInfo(orderForm.getGroup_info());
			count = CommUtil.null2Int(map.get("goods_count"));
		}
		return count;
	}

	/**
	 * 查询订单中所有积分商品数量
	 * 
	 * @param order_id
	 * @return
	 */
	public List<Map> query_integral_goodsinfo(String json) {
		List<Map> maps = new ArrayList<Map>();
		if (json != null && !json.equals("")) {
			maps = Json.fromJson(List.class, json);
		}
		return maps;
	}

	/**
	 * 查询订单中所有积分商品数量
	 * 
	 * @param order_id
	 * @return
	 */
	public int query_integral_count(String order_id) {
		IntegralGoodsOrder igo = this.integralGoodsOrderService
				.getObjById(CommUtil.null2Long(order_id));
		if (igo != null) {
			List<Map> objs = Json.fromJson(List.class, igo.getGoods_info());
			int count = objs.size();
			return count;
		} else {
			return 0;
		}
	}

	/**
	 * 查询积分订单中所有商品，返回IntegralGoods集合
	 * 
	 * @param order_id
	 * @return
	 */
	public List<IntegralGoods> query_integral_all_goods(String order_id) {
		IntegralGoodsOrder igo = this.integralGoodsOrderService
				.getObjById(CommUtil.null2Long(order_id));
		List<IntegralGoods> objs = new ArrayList<IntegralGoods>();
		List<Map> maps = Json.fromJson(List.class, igo.getGoods_info());
		for (Map obj : maps) {
			IntegralGoods ig = this.integralGoodsService.getObjById(CommUtil
					.null2Long(obj.get("id")));
			if (ig != null) {
				objs.add(ig);
			}
		}
		return objs;
	}

	/**
	 * 查询积分订单中某商品的下单数量
	 * 
	 * @param order_id
	 * @return
	 */
	public int query_integral_one_goods_count(IntegralGoodsOrder igo,
			String ig_id) {
		int count = 0;
		List<IntegralGoods> objs = new ArrayList<IntegralGoods>();
		List<Map> maps = Json.fromJson(List.class, igo.getGoods_info());
		for (Map obj : maps) {
			if (obj.get("id").equals(ig_id)) {
				count = CommUtil.null2Int(obj.get("ig_goods_count"));
				break;
			}
		}
		return count;
	}

	/**
	 * 查询订单中某件是否评价
	 * 
	 * @param order_id
	 * @param goods_id
	 * @return
	 */
	public Evaluate query_order_evaluate(Object order_id, Object goods_id) {
		Map para = new HashMap();
		para.put("order_id", CommUtil.null2Long(order_id));
		para.put("goods_id", CommUtil.null2Long(goods_id));
		List<Evaluate> list = this.evaluateService
				.query("select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.of.id=:order_id",
						para, -1, -1);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 判断是否可修改评价
	 * 
	 * @param date
	 * @return
	 */
	public int evaluate_able(Date date) {
		if (date != null) {
			long begin = date.getTime();
			long end = new Date().getTime();
			SysConfig config = this.configService.getSysConfig();
			long day = (end - begin) / 86400000;
			if (day <= config.getEvaluate_edit_deadline()) {
				return 1;
			}
		}
		return 0;
	}

	/**
	 * 判断是否可追加评价
	 * 
	 * @param date
	 * @return
	 */
	public int evaluate_add_able(Date date) {
		if (date != null) {
			long begin = date.getTime();
			long end = new Date().getTime();
			SysConfig config = this.configService.getSysConfig();
			long day = (end - begin) / 86400000;
			if (day <= config.getEvaluate_add_deadline()) {
				return 1;
			}
		}
		return 0;
	}

	/**
	 * 计算今天到指定时间天数
	 * 
	 * @param date
	 * @return
	 */
	public int how_soon(Date date) {
		if (date != null) {
			long begin = date.getTime();
			long end = new Date().getTime();
			long day = (end - begin) / 86400000;
			return CommUtil.null2Int(day);
		}
		return 999;
	}

}
