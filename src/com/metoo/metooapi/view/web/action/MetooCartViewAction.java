package com.metoo.metooapi.view.web.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.metoo.buyer.domain.Result;
import com.metoo.core.annotation.SecurityMapping;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.WebForm;
import com.metoo.foundation.domain.ActivityGoods;
import com.metoo.foundation.domain.Address;
import com.metoo.foundation.domain.Area;
import com.metoo.foundation.domain.BuyGift;
import com.metoo.foundation.domain.CombinPlan;
import com.metoo.foundation.domain.CouponInfo;
import com.metoo.foundation.domain.DeliveryAddress;
import com.metoo.foundation.domain.EnoughReduce;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.GoodsCart;
import com.metoo.foundation.domain.GoodsSpecProperty;
import com.metoo.foundation.domain.GoodsSpecification;
import com.metoo.foundation.domain.GroupGoods;
import com.metoo.foundation.domain.OrderForm;
import com.metoo.foundation.domain.OrderFormLog;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.Transport;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.VerifyCode;
import com.metoo.foundation.service.IActivityGoodsService;
import com.metoo.foundation.service.IAddressService;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.IBuyGiftService;
import com.metoo.foundation.service.ICombinPlanService;
import com.metoo.foundation.service.ICouponInfoService;
import com.metoo.foundation.service.IDeliveryAddressService;
import com.metoo.foundation.service.IEnoughReduceService;
import com.metoo.foundation.service.IGoodsCartService;
import com.metoo.foundation.service.IGoodsLogService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IGoodsSpecPropertyService;
import com.metoo.foundation.service.IGroupGoodsService;
import com.metoo.foundation.service.IGroupInfoService;
import com.metoo.foundation.service.IGroupLifeGoodsService;
import com.metoo.foundation.service.IIntegralGoodsOrderService;
import com.metoo.foundation.service.IMessageService;
import com.metoo.foundation.service.IOrderFormLogService;
import com.metoo.foundation.service.IOrderFormService;
import com.metoo.foundation.service.IPaymentService;
import com.metoo.foundation.service.IPayoffLogService;
import com.metoo.foundation.service.IPredepositLogService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.ITemplateService;
import com.metoo.foundation.service.ITransportService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.foundation.service.IVerifyCodeService;
import com.metoo.lucene.tools.LuceneVoTools;
import com.metoo.manage.admin.tools.OrderFormTools;
import com.metoo.manage.admin.tools.PaymentTools;
import com.metoo.manage.admin.tools.UserTools;
import com.metoo.manage.buyer.tools.CartTools;
import com.metoo.manage.delivery.tools.DeliveryAddressTools;
import com.metoo.manage.seller.tools.CombinTools;
import com.metoo.manage.seller.tools.TransportTools;
import com.metoo.metooapi.view.web.tool.MetooCartViewTools;
import com.metoo.metooapi.view.web.tool.MetooGoodsViewTools;
import com.metoo.msg.MsgTools;
import com.metoo.pay.tools.PayTools;
import com.metoo.view.web.tools.ActivityViewTools;
import com.metoo.view.web.tools.BuyGiftViewTools;
import com.metoo.view.web.tools.GoodsViewTools;
import com.metoo.view.web.tools.GroupViewTools;
import com.metoo.view.web.tools.IntegralViewTools;
import com.metoo.view.web.tools.StoreViewTools;

@Controller
public class MetooCartViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private PayTools payTools;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private CartTools cartTools;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private IGroupInfoService groupInfoService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private GroupViewTools groupViewTools;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private UserTools userTools;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private IIntegralGoodsOrderService igorderService;
	@Autowired
	private IEnoughReduceService enoughReduceService;
	@Autowired
	private IBuyGiftService buyGiftService;
	@Autowired
	private ICombinPlanService combinplanService;
	@Autowired
	private CombinTools combinTools;
	@Autowired
	private BuyGiftViewTools buyGiftViewTools;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private IActivityGoodsService actgoodsService;
	@Autowired
	private ActivityViewTools activityTools;
	@Autowired
	private IDeliveryAddressService deliveryaddrService;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private DeliveryAddressTools DeliveryAddressTools;
	@Autowired
	private ITransportService transportService;
	@Autowired
	private IVerifyCodeService mobileverifycodeService;
	@Autowired
	private MetooGoodsViewTools metooGoodsViewTools;
	@Autowired
	private MetooCartViewTools metooCartViewTools;
	/**
	 * 添加商品到购物车
	 * 
	 * @param request
	 * @param response
	 * @param id
	 *            添加到购物车的商品id
	 * @param count
	 *            添加到购物车的商品数量
	 * @param price
	 *            添加到购物车的商品的价格,该逻辑会更加gsp再次计算实际价格，避免用户在前端篡改
	 * @param gsp
	 *            商品的属性值，这里传递id值，如12,1,21
	 * @param buy_type
	 *            购买的商品类型，组合销售时用于判断是套装购买还是配件购买,普通商品：不传值，配件组合:parts,组合套装：suit
	 * @param combin_ids
	 *            组合搭配中配件id
	 * @param combin_version
	 *            组合套装中套装版本
	 */
	//[该接口原用在wap端，后使用app时改用app app/add_goods_cart.htm]
	/*@RequestMapping("/add_goods_cart.json")
	public void add_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id, String count,
			String price, String gsp, String buy_type, String combin_ids,
			String combin_version) {
		Result result = null;
		int next = 0;// 0为添加成功，-3库存不足,
						// -1添加失败，-2商品下架，添加失败，0普通商品添加,1组合配件添加，2组合套装添加
		Map json_map = new HashMap();
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		//[ 0为上架，1为在仓库中，2为定时自动上架，3为店铺过期自动下架，-1为手动下架状态，-2为违规下架状态]
		if(goods != null){
			if (goods.getGoods_status() == 0) {
				if (CommUtil.null2String(gsp).equals("")) {// 从商品列表页添加到购车，生成默认的gsp信息
					gsp = this.generic_default_gsp(goods);
				}
				int goods_inventory = CommUtil.null2Int(this.generic_default_info(
						goods, gsp).get("count"));// 计算商品库存信息
				//[ 是否为F码销售商品，0为不是F码销售商品，1为F码销售商品，F码商品不可以参加任何商城活动]
				if (goods.getF_sale_type() == 0 && goods_inventory > 0) {// 非F码商品且库存大于0，正常加入购物车
					String cart_session_id = "";
					Cookie[] cookies = request.getCookies();
					if (cookies != null) {
						for (Cookie cookie : cookies) {
							if (cookie.getName().equals("cart_session_id")) {
								cart_session_id = CommUtil.null2String(cookie
										.getValue());
							}
						}
					}
					if (cart_session_id.equals("")) {
						cart_session_id = UUID.randomUUID().toString();
						Cookie cookie = new Cookie("cart_session_id",
								cart_session_id);
						cookie.setDomain(CommUtil.generic_domain(request));
						response.addCookie(cookie);
					}
					List<GoodsCart> carts_list = new ArrayList<GoodsCart>();// 用户整体购物车
					List<GoodsCart> carts_cookie = new ArrayList<GoodsCart>();// 未提交的用户cookie购物车
					List<GoodsCart> carts_user = new ArrayList<GoodsCart>();// 未提交的用户user购物车
					User user = SecurityUserHolder.getCurrentUser();
					Map cart_map = new HashMap();
					if (user != null) {
						user = userService.getObjById(user.getId());
						if (!cart_session_id.equals("")) {
							cart_map.clear();
							cart_map.put("cart_session_id", cart_session_id);
							cart_map.put("cart_status", 0);
							carts_cookie = this.goodsCartService
									.query("select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
											cart_map, -1, -1);
							// 如果用户拥有自己的店铺，删除carts_cookie购物车中自己店铺中的商品信息
							if (user.getStore() != null) {
								for (GoodsCart gc : carts_cookie) {
									if (gc.getGoods().getGoods_type() == 1) {// 该商品为商家商品
										if (gc.getGoods().getGoods_store().getId()
												.equals(user.getStore().getId())) {
											this.goodsCartService
													.delete(gc.getId());
										}
									}
								}
							}
							cart_map.clear();
							cart_map.put("user_id", user.getId());
							cart_map.put("cart_status", 0);
							carts_user = this.goodsCartService
									.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
											cart_map, -1, -1);

						} else {
							cart_map.clear();
							cart_map.put("user_id", user.getId());
							cart_map.put("cart_status", 0);
							carts_user = this.goodsCartService
									.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
											cart_map, -1, -1);

						}
					} else {
						if (!cart_session_id.equals("")) {
							cart_map.clear();
							cart_map.put("cart_session_id", cart_session_id);
							cart_map.put("cart_status", 0);
							carts_cookie = this.goodsCartService
									.query("select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
											cart_map, -1, -1);

						}
					}
					// 将cookie购物车与用户user购物车合并，去重
					if (user != null) {
						for (GoodsCart ugc : carts_user) {
							carts_list.add(ugc);
						}
						for (GoodsCart cookie : carts_cookie) {
							boolean add = true;
							for (GoodsCart gc2 : carts_user) {
								if (cookie.getGoods().getId()
										.equals(gc2.getGoods().getId())) {
									if (cookie.getSpec_info().equals(
											gc2.getSpec_info())) {
										add = false;
										this.goodsCartService
												.delete(cookie.getId());
									}
								}
							}
							if (add) {// 将cookie_cart转变为user_cart
								cookie.setCart_session_id(null);
								cookie.setUser(user);
								this.goodsCartService.update(cookie);
								carts_list.add(cookie);
							}
						}
					} else {
						for (GoodsCart cookie : carts_cookie) {
							carts_list.add(cookie);
						}
					}
					String temp_gsp = gsp;
					if ("parts".equals(buy_type)) {
						if (combin_ids != null && !combin_ids.equals("")) {
							next = 1;
						}

					}
					if ("suit".equals(buy_type)) {
						if (combin_ids != null && !combin_ids.equals("")) {
							next = 2;
						}
					}

					boolean add = true;
					boolean combin_add = true;
					if ("suit".equals(buy_type)) {
						combin_add = false;
					}
					String[] gsp_ids = CommUtil.null2String(temp_gsp).split(",");
					Arrays.sort(gsp_ids);
					for (GoodsCart gc : carts_list) {
						if (gsp_ids != null && gsp_ids.length > 0
								&& gc.getGsps().size() > 0) {
							String[] gsp_ids1 = new String[gc.getGsps().size()];
							for (int i = 0; i < gc.getGsps().size(); i++) {
								gsp_ids1[i] = gc.getGsps().get(i) != null ? gc
										.getGsps().get(i).getId().toString() : "";
							}
							Arrays.sort(gsp_ids1);
							if (gc.getGoods().getId().toString().equals(id)
									&& Arrays.equals(gsp_ids, gsp_ids1)) {
								if ("combin".equals(gc.getCart_type())) {
									if (!combin_add) {
										add = false;
										break;
									} else {
										add = true;
									}
								} else {
									add = false;
									break;
								}
							}
						} else {
							if (gc.getGoods().getId().toString().equals(id)) {
								if ("combin".equals(gc.getCart_type())) {
									if (!combin_add) {
										add = false;
										break;
									} else {
										add = true;
									}
								} else {
									add = false;
									break;
								}
							}
						}
					}
					if (add && combin_add) {// 排除购物车中没有重复商品后添加该商品到购物车,并且非组合添加
						GoodsCart obj = new GoodsCart();
						obj.setCart_gsp(gsp);
						obj.setAddTime(new Date());
						obj.setCount(CommUtil.null2Int(count));
						if (price == null || price.equals("")) {
							price = this.generGspgoodsPrice(temp_gsp, id);
						}
						obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
						obj.setGoods(goods);
						String spec_info = "";
						for (String gsp_id : gsp_ids) {
							GoodsSpecProperty spec_property = this.goodsSpecPropertyService
									.getObjById(CommUtil.null2Long(gsp_id));
							if (spec_property != null) {
								obj.getGsps().add(spec_property);
								spec_info = spec_property.getSpec().getName() + "："
										+ spec_property.getValue() + "<br>"
										+ spec_info;
							}
						}
						if (user == null) {
							obj.setCart_session_id(cart_session_id);
						} else {
							obj.setUser(user);
						}
						obj.setSpec_info(spec_info);
						this.goodsCartService.save(obj);
					}
					if (next == 1) {// 组合配件商品添加
						String part_ids[] = combin_ids.split(",");
						for (String part_id : part_ids) {
							if (!part_id.equals("")) {
								Goods part_goods = this.goodsService
										.getObjById(CommUtil.null2Long(part_id));
								GoodsCart part_cart = new GoodsCart();
								boolean part_add = true;
								part_cart.setAddTime(new Date());
								String temp_gsp_parts = null;
								temp_gsp_parts = this
										.generic_default_gsp(part_goods);
								String[] part_gsp_ids = CommUtil.null2String(
										temp_gsp_parts).split(",");
								Arrays.sort(part_gsp_ids);
								for (GoodsCart gc : carts_list) {
									if (part_gsp_ids != null
											&& part_gsp_ids.length > 0
											&& gc.getGsps() != null
											&& gc.getGsps().size() > 0) {
										String[] gsp_ids1 = new String[gc.getGsps()
												.size()];
										for (int i = 0; i < gc.getGsps().size(); i++) {
											gsp_ids1[i] = gc.getGsps().get(i) != null ? gc
													.getGsps().get(i).getId()
													.toString()
													: "";
										}
										Arrays.sort(gsp_ids1);
										if (gc.getGoods().getId().toString()
												.equals(part_id)
												&& Arrays.equals(part_gsp_ids,
														gsp_ids1)) {
											part_add = false;
										}
									} else {
										if (gc.getGoods().getId().toString()
												.equals(part_id)) {
											part_add = false;
										}
									}
								}
								if (part_add) {// 排除购物车中没有重复商品后添加该商品到购物车
									part_cart.setAddTime(new Date());
									part_cart.setCount(CommUtil.null2Int(1));
									String part_price = this.generGspgoodsPrice(
											temp_gsp_parts, part_id);
									part_cart.setPrice(BigDecimal.valueOf(CommUtil
											.null2Double(part_price)));
									part_cart.setGoods(part_goods);
									String spec_info = "";
									for (String gsp_id : part_gsp_ids) {
										GoodsSpecProperty spec_property = this.goodsSpecPropertyService
												.getObjById(CommUtil
														.null2Long(gsp_id));
										part_cart.getGsps().add(spec_property);
										if (spec_property != null) {
											spec_info = spec_property.getSpec()
													.getName()
													+ ":"
													+ spec_property.getValue()
													+ " " + spec_info;
										}
									}
									if (user == null) {
										part_cart
												.setCart_session_id(cart_session_id);
									} else {
										part_cart.setUser(user);
									}
									part_cart.setSpec_info(spec_info);
									this.goodsCartService.save(part_cart);
								}
							}
						}
					}
					if (next == 2) {// 组合套装商品添加
						boolean suit_add = true;
						Map params = new HashMap();
						params.put("combin_main", 1);
						params.put("cart_type", "combin");
						params.put("gid", goods.getId());
						String hql = "select obj from GoodsCart obj where obj.cart_type=:cart_type and obj.combin_main=:combin_main and obj.goods.id=:gid";
						if (user != null) {
							params.put("user_id", user.getId());
							hql += " and obj.user.id=:user_id";
						} else {
							params.put("cart_session_id", cart_session_id);
							hql += " and obj.cart_session_id=:cart_session_id";
						}
						params.put("gid", goods.getId());
						List<GoodsCart> suit_carts = this.goodsCartService.query(
								hql, params, -1, -1);
						if (suit_carts.size() > 0) {
							if (suit_carts.get(0).getCombin_version()
									.contains(CommUtil.null2String(combin_version))) {
								suit_add = false;
							}
						}
						if (suit_add) {
							Map suit_map = null;
							params.clear();
							params.put("main_goods_id", CommUtil.null2Long(id));
							params.put("combin_type", 0);// 组合套装
							params.put("combin_status", 1);
							List<CombinPlan> suits = this.combinplanService
									.query("select obj from CombinPlan obj where obj.main_goods_id=:main_goods_id and obj.combin_type=:combin_type and obj.combin_status=:combin_status",
											params, -1, -1);
							for (CombinPlan plan : suits) {
								List<Map> map_list = (List<Map>) Json.fromJson(plan
										.getCombin_plan_info());
								for (Map temp_map : map_list) {
									String ids = this.goodsViewTools
											.getCombinPlanGoodsIds(temp_map);
									if (ids.equals(combin_ids)) {
										suit_map = temp_map;
										break;
									}
								}
							}
							String combin_mark = "combin" + UUID.randomUUID();
							if (suit_map != null) {
								String suit_ids = "";//
								List<Map> goods_list = (List<Map>) suit_map
										.get("goods_list");
								for (Map good_map : goods_list) {
									Goods suit_goods = this.goodsService
											.getObjById(CommUtil.null2Long(good_map
													.get("id")));
									GoodsCart cart = new GoodsCart();
									cart.setAddTime(new Date());
									cart.setGoods(suit_goods);
									String spec_info = "";
									String temp_gsp_ids[] = CommUtil.null2String(
											this.generic_default_gsp(goods)).split(
											",");
									for (String gsp_id : temp_gsp_ids) {
										GoodsSpecProperty spec_property = this.goodsSpecPropertyService
												.getObjById(CommUtil
														.null2Long(gsp_id));
										if (spec_property != null) {
											cart.getGsps().add(spec_property);
											spec_info = spec_property.getSpec()
													.getName()
													+ "："
													+ spec_property.getValue()
													+ "<br>" + spec_info;
										}
									}
									cart.setSpec_info(spec_info);
									cart.setCombin_mark(combin_mark);
									cart.setCart_type("combin");
									cart.setPrice(BigDecimal.valueOf(CommUtil
											.null2Double(suit_goods
													.getGoods_current_price())));
									cart.setCount(1);
									if (user == null) {
										cart.setCart_session_id(cart_session_id);
									} else {
										cart.setUser(user);
									}
									this.goodsCartService.save(cart);
									suit_ids = suit_ids + ","
											+ CommUtil.null2String(cart.getId());
								}
								GoodsCart obj = new GoodsCart();// 套装主购物车
								String combin_main_default_gsp = this
										.generic_default_gsp(goods);
								obj.setCart_gsp(combin_main_default_gsp);
								obj.setAddTime(new Date());
								obj.setCount(CommUtil.null2Int(count));
								if (price == null || price.equals("")) {
									price = this.generGspgoodsPrice(temp_gsp, id);
								}
								obj.setPrice(BigDecimal.valueOf(CommUtil
										.null2Double(price)));
								obj.setGoods(goods);
								if (user == null) {
									obj.setCart_session_id(cart_session_id);
								} else {
									obj.setUser(user);
								}
								obj.setCombin_suit_ids(suit_ids);
								obj.setCombin_version("【套装" + combin_version + "】");
								obj.setCombin_main(1);
								obj.setCount(CommUtil.null2Int(count));
								obj.setPrice(BigDecimal.valueOf(CommUtil
										.null2Double(suit_map
												.get("plan_goods_price"))));
								obj.setCombin_mark(combin_mark);
								obj.setCart_type("combin");
								String spec_info = "";
								String temp_gsp_ids[] = CommUtil.null2String(
										this.generic_default_gsp(goods)).split(",");
								for (String gsp_id : temp_gsp_ids) {
									GoodsSpecProperty spec_property = this.goodsSpecPropertyService
											.getObjById(CommUtil.null2Long(gsp_id));
									if (spec_property != null) {
										obj.getGsps().add(spec_property);
										spec_info = spec_property.getSpec()
												.getName()
												+ "："
												+ spec_property.getValue()
												+ "<br>"
												+ spec_info;
									}
								}
								obj.setCart_gsp(this.generic_default_gsp(goods));
								obj.setSpec_info(spec_info);
								suit_map.put("suit_count", CommUtil.null2Int(count));
								String suit_all_price = CommUtil
										.formatMoney(CommUtil.mul(CommUtil
												.null2Int(count), CommUtil
												.null2Double(suit_map
														.get("plan_goods_price"))));
								suit_map.put("suit_all_price", suit_all_price);// 套装整体价格=套装单价*数量
								suit_map.put("suit_name", "[套装" + combin_version
										+ "]");
								obj.setCombin_suit_info(Json.toJson(suit_map,
										JsonFormat.compact()));
								this.goodsCartService.save(obj);
							} else {
								next = -1;
							}
						} else {
							GoodsCart update_cart = suit_carts.get(0);
							Map temp_map = Json.fromJson(Map.class,
									update_cart.getCombin_suit_info());
							temp_map.put("suit_count", update_cart.getCount() + 1);
							update_cart.setCombin_suit_info(Json.toJson(temp_map,
									JsonFormat.compact()));
							update_cart.setCount(update_cart.getCount() + 1);
							this.goodsCartService.update(update_cart);
						}
					}
				} else {// F码商品不允许直接添加到购物车
					next = -1;
					if (goods_inventory == 0) {
						next = -3;
					}
				}
			} else {
				next = -2;
			}
		
		
		List<GoodsCart> carts = this.cart_calc(request);
		json_map.put("count", carts.size());
		//json_map.put("code", next);
		result = new Result(next,"success",json_map);
	}else{
		result = new Result(1,"没有该商品");		
	}
		
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
	*/
	/**
	 * 根据商品信息，计算该商品默认的规格信息，以各个规格值的第一个为默认值
	 * 
	 * @param goods
	 *            商品
	 * @return 默认规格id组合，如1,2
	 */
	private String generic_default_gsp(Goods goods) {
		String gsp = "";
		if (goods != null) {
			List<GoodsSpecification> specs = this.goodsViewTools
					.generic_spec(CommUtil.null2String(goods.getId()));
			for (GoodsSpecification spec : specs) {
				// System.out.println(spec.getName());
				for (GoodsSpecProperty prop : goods.getGoods_specs()) {
					if (prop.getSpec().getId().equals(spec.getId())) {
						gsp = prop.getId() + "," + gsp;
						break;
					}
				}
			}
		}
		// System.out.println(gsp);
		return gsp;
	}
	
	/**
	 * 用户登陆后清除用户购物车中自己店铺的商品，将cookie购物车与用户user购物车合并，去重复商品（相同商品不同规格不去掉）
	 * 未使用
	 * @param request
	 * @param response
	 * @return
	 */
	private List<GoodsCart> cart_calc(HttpServletRequest request) {
		List<GoodsCart> carts_list = new ArrayList<GoodsCart>();// 用户整体购物车
	//	List<GoodsCart> carts_cookie = new ArrayList<GoodsCart>();// 未提交的用户cookie购物车
		List<GoodsCart> carts_user = new ArrayList<GoodsCart>();// 未提交的用户user购物车
		User user = SecurityUserHolder.getCurrentUser();
		Map cart_map = new HashMap();
		if (user != null) {
			user = userService.getObjById(user.getId());
			
				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", 0);
				carts_user = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
			
		} 
		// 将cookie购物车与用户user购物车合并，去重
		if (user != null) {
			for (GoodsCart ugc : carts_user) {
				carts_list.add(ugc);
			}
			
		} 
		// 组合套装处理，只显示套装主购物车,套装内其他购物车不显示
		List<GoodsCart> combin_carts_list = new ArrayList<GoodsCart>();
		for (GoodsCart gc : carts_list) {
			if (gc.getCart_type() != null && gc.getCart_type().equals("combin")) {
				if (gc.getCombin_main() != 1) {// 组合购物车中非主购物车
					combin_carts_list.add(gc);
				}
			}
		}
		if (combin_carts_list.size() > 0) {
			carts_list.removeAll(combin_carts_list);
		}
		return carts_list;
	}
	
	/**
	 * 根据商品规格获取价格
	 * 未使用
	 * @param request
	 * @param response
	 */
	private String generGspgoodsPrice(String gsp, String id) {
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		double price = CommUtil.null2Double(goods.getGoods_current_price());
		User user = SecurityUserHolder.getCurrentUser();
		if (user != null && goods.getActivity_status() == 2) {
			Map map = this.activityTools.getActivityGoodsInfo(
					CommUtil.null2String(goods.getId()),
					CommUtil.null2String(user.getId()));
			price = CommUtil.null2Double(map.get("rate_price"));
		}
		if ("spec".equals(goods.getInventory_type())) {
			List<HashMap> list = Json.fromJson(ArrayList.class,
					goods.getGoods_inventory_detail());
			String[] gsp_ids = gsp.split(",");
			for (Map temp : list) {
				String[] temp_ids = CommUtil.null2String(temp.get("id")).split(
						"_");
				Arrays.sort(gsp_ids);
				Arrays.sort(temp_ids);
				if (Arrays.equals(gsp_ids, temp_ids)) {
					price = CommUtil.null2Double(temp.get("price"));
				}
			}
			if (user != null && goods.getActivity_status() == 2) {
				Map map = this.activityTools.getActivityGoodsInfo(
						CommUtil.null2String(goods.getId()),
						CommUtil.null2String(user.getId()));
				Double rate = CommUtil.div(
						CommUtil.null2Double(map.get("rate")), 10);
				price = CommUtil.null2Double(CommUtil.formatMoney(CommUtil.mul(
						rate, price)));
			}
		}
		return CommUtil.null2String(price);
	}
	
	/**
	 * 根据商品及传递的规格信息，计算该规格商品的价格、库存量
	 * 
	 * @param goods
	 * @param gsp
	 * @return 价格、库存组成的Map
	 */
	private Map generic_default_info(Goods goods, String gsp, User user) {
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
			Long id = goods.getId();
			count = goods.getGoods_inventory();
			price = CommUtil.null2Double(goods.getStore_price());
			if ("spec".equals(goods.getInventory_type())) {
				if (gsp != null && !gsp.equals("")) {
					List<HashMap> list = Json.fromJson(ArrayList.class,
							goods.getGoods_inventory_detail());
					String[] gsp_ids = gsp.split(",");
					for (Map temp : list) {
						String[] temp_ids = CommUtil
								.null2String(temp.get("id")).split("_");
						Arrays.sort(gsp_ids);
						Arrays.sort(temp_ids);
						if (Arrays.equals(gsp_ids, temp_ids)) {
							count = CommUtil.null2Int(temp.get("count"));
							price = CommUtil.null2Double(temp.get("price"));
						}
					}
				}
			}
		}
		BigDecimal ac_rebate = null;
		if (goods.getActivity_status() == 2
				&& user != null) {// 如果是促销商品，并且用户已登录，根据规格配置价格计算相应配置的促销价格
			ActivityGoods actGoods = this.actgoodsService.getObjById(goods
					.getActivity_goods_id());
			// 0—铜牌会员1—银牌会员2—金牌会员3—超级会员
			BigDecimal rebate = BigDecimal.valueOf(0.00);
			int level = this.integralViewTools.query_user_level(CommUtil
					.null2String(user.getId()));
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
	
	
	private List<GoodsCart> metoo_cart_calc(User user) {
		List<GoodsCart> carts_list = new ArrayList<GoodsCart>();// 用户整体购物车
	//	List<GoodsCart> carts_cookie = new ArrayList<GoodsCart>();// 未提交的用户cookie购物车
		List<GoodsCart> carts_user = new ArrayList<GoodsCart>();// 未提交的用户user购物车
		Map cart_map = new HashMap();
		if (user != null) {
				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", 0);
				carts_user = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status order by obj.addTime desc",
								cart_map, -1, -1);
		} 
		// 将cookie购物车与用户user购物车合并，去重
		if (user != null) {
			for (GoodsCart ugc : carts_user) {
				carts_list.add(ugc);
			}
		} 
		// 组合套装处理，只显示套装主购物车,套装内其他购物车不显示
		List<GoodsCart> combin_carts_list = new ArrayList<GoodsCart>();
		for (GoodsCart gc : carts_list) {
			if (gc.getCart_type() != null && gc.getCart_type().equals("combin")) {
				if (gc.getCombin_main() != 1) {// 组合购物车中非主购物车
					combin_carts_list.add(gc);
				}
			}
		}
		if (combin_carts_list.size() > 0) {
			carts_list.removeAll(combin_carts_list);
		}
		return carts_list;
	}
	
	/**
	 * 确认购物车商品
	 * [购物车列表]
	 * <>
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/goods_cart1_load.json")
	public void goods_cart1_load(HttpServletRequest request,
			HttpServletResponse response, String load_class,String token) {
		Result result = null;
		Map goods_cart_map = new HashMap();
		ModelAndView mv = new JModelAndView("goods_cart1_load.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Date date = new Date();
		if(token.equals("")){
			result = new Result(-100,"token Invalidation");
		}else{
			Map params = new HashMap();
			params.put("app_login_token", token);
			List<User> users =  this.userService.query("select obj from User obj where obj.app_login_token=:app_login_token order by obj.addTime asc",
					params, -1, -1);
			if(users.isEmpty()){
				result = new Result(-100,"token Invalidation");	
			}else{
				User user = users.get(0);
				List<GoodsCart> carts = this.metoo_cart_calc(user);
				if (carts.size() > 0) {
					Set<Long> set = new HashSet<Long>();
					List<Long> storeIds = new ArrayList<Long>();
					List<GoodsCart> native_goods = new ArrayList<GoodsCart>();
					Map<Long, List<GoodsCart>> ermap = new HashMap<Long, List<GoodsCart>>();
					Map<Long, String> erString = new HashMap<Long, String>();
					for (GoodsCart cart : carts) {
						if (cart.getGoods().getOrder_enough_give_status() == 1 && cart.getGoods().getBuyGift_id() != null) {
							BuyGift bg = this.buyGiftService.getObjById(cart.getGoods().getBuyGift_id());
							if (bg.getBeginTime().before(date)) {
								set.add(cart.getGoods().getBuyGift_id());
							} else {
								native_goods.add(cart);
							}
						} else if (cart.getGoods().getEnough_reduce() == 1) {// 满就减
							String er_id = cart.getGoods().getOrder_enough_reduce_id();
							EnoughReduce er = this.enoughReduceService
									.getObjById(CommUtil.null2Long(er_id));
							if (er.getErbegin_time().before(date)) {
								if (ermap.containsKey(er.getId())) {
									ermap.get(er.getId()).add(cart);
								} else {
									List<GoodsCart> list = new ArrayList<GoodsCart>();
									list.add(cart);
									ermap.put(er.getId(), list);
									Map map = (Map) Json.fromJson(er.getEr_json());
									double k = 0;
									String str = "";
									for (Object key : map.keySet()) {
										if (k == 0) {
											k = Double.parseDouble(key.toString());
											str = "The activity products buy "+k+" AED, you can enjoy the discount";
										}
										if (Double.parseDouble(key.toString()) < k) {
											k = Double.parseDouble(key.toString());
											str = "The activity product buy "+k+" AED, you can enjoy the discount";
										}
									}
									erString.put(er.getId(), str);
								}
							} else {
								native_goods.add(cart);
							}
						} else {
							native_goods.add(cart);
						}
						if(!storeIds.contains(cart.getGoods().getGoods_store().getId())){
							storeIds.add(cart.getGoods().getGoods_store().getId());
						}
					}
					
					List<GoodsCart> erCarts = new ArrayList<GoodsCart>();// 将有活动的商品分组(满就减)
					List goodsList = new ArrayList();
					List<List<Map>> ermaps = new ArrayList<List<Map>>();
						for(Long key : ermap.keySet()){  
							erCarts = ermap.get(key);
						//	goodsList = this.metooCartViewTools.queryGoods(erCarts, storeIds, erString, key);
							ermaps.add(this.metooCartViewTools.queryGoods(erCarts, storeIds, erString, key));
						}
						goods_cart_map.put("ermaps", ermaps);
					
					if (set.size() > 0) {// 将有活动的商品分组(满就送)
						Map<Long, List<GoodsCart>> map = new HashMap<Long, List<GoodsCart>>();
						for (Long id : set) {
							map.put(id, new ArrayList<GoodsCart>());
						}
						for (GoodsCart cart : carts) {
							
							if (cart.getGoods().getOrder_enough_give_status() == 1
									&& cart.getGoods().getBuyGift_id() != null) {
								if (map.containsKey(cart.getGoods().getBuyGift_id())) {
									map.get(cart.getGoods().getBuyGift_id()).add(cart);
								}
							}
						}
						List<GoodsCart> ac_list = new ArrayList<GoodsCart>();
						for(Long ac_key : map.keySet()){
							ac_list = map.get(ac_key);
							List<Map> ac_goods_list = new ArrayList<Map>();
							for(GoodsCart obj : ac_list){
								Map acmap = new HashMap();
								acmap.put("goods_cart_id", obj.getId());
								acmap.put("goods_main_photo", obj.getGoods().getGoods_main_photo() == null ? "" : 
									this.configService.getSysConfig().getImageWebServer()
									+ "/"
									+ obj.getGoods().getGoods_main_photo()
										.getPath()
									+ "/"
									+ obj.getGoods().getGoods_main_photo()
										.getName());
								acmap.put("goods_id", obj.getGoods().getId());
								acmap.put("goods_name", obj.getGoods().getGoods_name());
								acmap.put("goods_type", obj.getGoods().getGoods_type());
								acmap.put("goods_inventory", obj.getGoods().getGoods_inventory());
								acmap.put("goods_curren_price", obj.getPrice());
								acmap.put("goods_store_price", obj.getGoods().getStore_price());
								if(obj.getSpec_info() != null){
									acmap.put("goods_spec", obj.getSpec_info());
								}else{
									acmap.put("goods_spec", "");
								}
								if(obj.getGoods().getGoods_store() != null){
									acmap.put("store_name", obj.getGoods().getGoods_store().getStore_name());
									acmap.put("store_id", obj.getGoods().getGoods_store().getId());
									acmap.put("store_logo", obj.getGoods().getGoods_store().getStore_logo() == null ? "" 
											: this.configService.getSysConfig().getImageWebServer() 
												+ "/"
												+ obj.getGoods().getGoods_store().getStore_logo().getPath() 
												+ "/"
												+ obj.getGoods().getGoods_store().getStore_logo().getName());
								}
								ac_goods_list.add(acmap);
							}
							goods_cart_map.put("ac_goods_list", ac_goods_list);
							
							BuyGift	bg = goodsViewTools.query_buyGift(CommUtil.null2String(ac_key));
							List<Map> bg_goodslist = new ArrayList<Map>();
							for(Map  bgt: CommUtil.Json2List(bg.getGift_info())){
								Map bgtmap = new HashMap();
								bgtmap.put("storegoods_count", bgt.get("storegoods_count"));//[赠送数量100 当前商品库存变为100 storegoods_count为1时 使用商品当前库存为赠送数量 如库存为200 则赠送个数为200 当正常出售1个赠送商品 后库存为199 赠送数量也为199  赠送数与库存数同步 此时没有goods_count]
								bgtmap.put("goods_id", bgt.get("goods_id"));
								bgtmap.put("goods_name", bgt.get("goods_name"));
								bgtmap.put("goods_price", bgt.get("goods_price"));
								bgtmap.put("goods_main_photo", this.configService.getSysConfig().getImageWebServer() + "/" + bgt.get("goods_main_photo"));
								bg_goodslist.add(bgtmap);
							}
							goods_cart_map.put("bg_goods_info", bg_goodslist);
						}
					}
					
					Map<String, List<GoodsCart>> separate_carts = this
							.separateCombin(native_goods);// 传入没有分离组合活动商品的购物车
					List<GoodsCart> normalGoodsCart = (List<GoodsCart>) separate_carts.get("normal");// 无活动的商品购物车
					List<Map> normallist = this.metooCartViewTools.queryGoods(normalGoodsCart, storeIds, null, null);
					goods_cart_map.put("normalmap", normallist);
					
					
					/*List<GoodsCart> combin = (List<GoodsCart>) separate_carts.get("combin");// 组合套装商品购物车 //[默认为空，组合销售时候为"combin"]
					List<Map> combinlist = this.metooCartViewTools.queryGoods(combin, storeIds);
					goods_cart_map.put("combinlist", combinlist);
					goods_cart_map.put("cart_num", combin.size());*/
				}
				goods_cart_map.put("cart_num", carts.size());
				if(goods_cart_map.isEmpty()){
					result = new Result(1,"购物车为空");
				}else{
					result = new Result(0,"success",goods_cart_map);
				}
			}
		}
		String goodscarttemp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(goodscarttemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 计算所有购物车商品的店铺id
	 * @param carts
	 * @return
	 */
	public List<Object> store(List<GoodsCart> carts){
		List<Object> store_list = new ArrayList<Object>();
		if(!carts.isEmpty()){
			for(GoodsCart gc : carts){
				if(gc.getGoods().getGoods_type() == 1){
					store_list.add(gc.getGoods().getGoods_store().getId());
				}else{
					store_list.add("self");
				}
			}
			HashSet hs = new HashSet(store_list);
			store_list.removeAll(store_list);
			store_list.addAll(hs);
		}
		return store_list;
		
	}
	
	// 分离组合销售活动购物车,只显示主体套装商品购物车
	private Map<String, List<GoodsCart>> separateCombin(List<GoodsCart> carts) {
		Map<String, List<GoodsCart>> map = new HashMap<String, List<GoodsCart>>();
		List<GoodsCart> normal_carts = new ArrayList<GoodsCart>();
		List<GoodsCart> combin_carts = new ArrayList<GoodsCart>();
		for (GoodsCart cart : carts) {
			if (cart.getCart_type() != null
					&& cart.getCart_type().equals("combin")) {
				if (cart.getCombin_main() == 1) {
					combin_carts.add(cart);
				}
			} else {
				normal_carts.add(cart);
			}
		}
		map.put("combin", combin_carts);
		map.put("normal", normal_carts);
		return map;
	}
	
	/**
	 * 移除购物车
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param count
	 * @param price
	 * @param spec_info
	 */
	@RequestMapping("/remove_goods_cart.json")
	public void remove_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String ids,String token) {
		Map params = new HashMap();
		Result result = null;
		if(token.equals("")){
			result = new Result(-100,"token Invalidation");
		}else{
			params.put("app_login_token", token);
			List<User> users =  this.userService.query("select obj from User obj where obj.app_login_token=:app_login_token order by obj.addTime desc",
					params, -1, -1);
			if(users.isEmpty()){
				result = new Result(-100,"token Invalidation");	
			}else{
				User user = users.get(0);
				List<String> list_ids = new ArrayList<String>();
				Double total_price = 0.00;
				int code = 0;// 100表示删除成功，1表示删除失败
				List<GoodsCart> carts = new ArrayList<GoodsCart>();
				if (ids != null && !ids.equals("")) {
					String cart_ids[] = ids.split(",");
					for (String id : cart_ids) {
						if (id != null && !id.equals("")) {
							list_ids.add(id);
							if (id.indexOf("combin") < 0) {
								GoodsCart gc = this.goodsCartService
										.getObjById(CommUtil.null2Long(id));
								if (gc != null) {
									//[默认为空，组合销售时候为"combin"]
									if (gc.getCart_type() != null
											&& gc.getCart_type().equals("combin")) {// 如果商城的购物车为组合套装，则删除最后一个组合套装购物车时，组合套装主购物车更随删除
										params.clear();
										params.put("combin_mark", gc.getCombin_mark());
										params.put("combin_main", 1);
										List<GoodsCart> suit_main_carts = this.goodsCartService
												.query("select obj from GoodsCart obj where obj.combin_mark=:combin_mark and obj.combin_main=:combin_main",
														params, -1, -1);
										if (suit_main_carts.size() > 0) {
											String suit_cart_ids[] = suit_main_carts
													.get(0).getCombin_suit_ids()
													.split(",");
											// 取消其他购物车组合状态
											for (String suit_cart_id : suit_cart_ids) {
												if (!suit_cart_id.equals("")) {
													GoodsCart suit_cart = this.goodsCartService
															.getObjById(CommUtil
																	.null2Long(suit_cart_id));
													if (suit_cart != null) {
														suit_cart.setCart_type(null);
														suit_cart.setCombin_mark(null);
														suit_cart.setCombin_main(0);
														suit_cart
																.setCombin_suit_ids(null);
														// 设置默认规格及价钱2Long(id));
														String default_gsp = this
																.generic_default_gsp(suit_cart
																		.getGoods());
														double default_price = CommUtil
																.null2Double(this.generic_default_info(
																				suit_cart
																						.getGoods(),
																				default_gsp, user)
																		.get("price"));
														suit_cart
																.setPrice(BigDecimal
																		.valueOf(default_price));
														suit_cart
																.setCart_gsp(default_gsp);
														String[] gsp_ids = CommUtil
																.null2String(
																		default_gsp)
																.split(",");
														String spec_info = "";
														for (String gsp_id : gsp_ids) {
															GoodsSpecProperty spec_property = this.goodsSpecPropertyService
																	.getObjById(CommUtil
																			.null2Long(gsp_id));
															if (spec_property != null) {
																suit_cart
																		.getGsps()
																		.add(spec_property);
																spec_info = spec_property
																		.getSpec()
																		.getName()
																		+ "："
																		+ spec_property
																				.getValue()
																		+ "<br>"
																		+ spec_info;
															}
														}
														suit_cart
																.setSpec_info(spec_info);
														this.goodsCartService
																.update(suit_cart);
													}
												}
											}
											// 取消组合套装主购物车状态
											for (GoodsCart main_suit_gc : suit_main_carts) {
												main_suit_gc.setCart_type(null);
												main_suit_gc.setCombin_mark(null);
												main_suit_gc.setCombin_main(0);
												main_suit_gc.setCombin_suit_ids(null);
												// 设置默认规格及价钱;
												String default_gsp = this
														.generic_default_gsp(main_suit_gc
																.getGoods());
												double default_price = CommUtil
														.null2Double(this
																.generic_default_info(
																		main_suit_gc
																				.getGoods(),
																		default_gsp,user)
																.get("price"));
												main_suit_gc.setPrice(BigDecimal
														.valueOf(default_price));
												main_suit_gc.setCart_gsp(default_gsp);
												String[] gsp_ids = CommUtil
														.null2String(default_gsp)
														.split(",");
												String spec_info = "";
												for (String gsp_id : gsp_ids) {
													GoodsSpecProperty spec_property = this.goodsSpecPropertyService
															.getObjById(CommUtil
																	.null2Long(gsp_id));
													if (spec_property != null) {
														main_suit_gc.getGsps().add(
																spec_property);
														spec_info = spec_property
																.getSpec().getName()
																+ "："
																+ spec_property
																		.getValue()
																+ "<br>" + spec_info;
													}
												}
												main_suit_gc.setSpec_info(spec_info);
												this.goodsCartService
														.update(main_suit_gc);
											}
										}
									}
									gc.getGsps().clear();
									this.goodsCartService
											.delete(CommUtil.null2Long(id));
								}
							} else {
								params.clear();
								params.put("combin_mark", id);
								List<GoodsCart> suit_carts = this.goodsCartService
										.query("select obj from GoodsCart obj where obj.combin_mark=:combin_mark",
												params, -1, -1);
								for (GoodsCart suit_gc : suit_carts) {
									this.goodsCartService.delete(suit_gc.getId());
								}
							}
						}
					}
				} else {
					code = 1;
				}

				
				carts = this.metoo_cart_calc(user);
				total_price = this.calCartPrice(carts, "");
				Map map = new HashMap();
				map.put("total_price", BigDecimal.valueOf(total_price));
				map.put("count", carts.size());
				map.put("ids", list_ids);
				result = new Result(code,"success",map);
			}
		}
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
	 * 获得购物车中用户勾选需要购买的商品总价格
	 * 
	 * @param request
	 * @param response
	 */
	private double calCartPrice(List<GoodsCart> carts, String gcs) {
		double all_price = 0.0;
		Map<String, Double> ermap = new HashMap<String, Double>();
		if (CommUtil.null2String(gcs).equals("")) {
			for (GoodsCart gc : carts) {
				if (gc.getCart_type() == null || gc.getCart_type().equals("")) {// 普通商品处理
					all_price = CommUtil.add(all_price,
							CommUtil.mul(gc.getCount(), gc.getPrice()));
				} else if (gc.getCart_type().equals("combin")) {// 组合套装商品处理
					if (gc.getCombin_main() == 1) {
						Map map = (Map) Json.fromJson(gc.getCombin_suit_info());
						all_price = CommUtil.add(all_price,
								map.get("suit_all_price"));
					}
				}
				if (gc.getGoods().getEnough_reduce() == 1) {// 是满就减商品，记录金额
					String er_id = gc.getGoods().getOrder_enough_reduce_id();
					if (ermap.containsKey(er_id)) {
						double last_price = (double) ermap.get(er_id);
						ermap.put(
								er_id,
								CommUtil.add(
										last_price,
										CommUtil.mul(gc.getCount(),
												gc.getPrice())));
					} else {
						ermap.put(er_id,
								CommUtil.mul(gc.getCount(), gc.getPrice()));
					}
				}
			}
		} else {
			String[] gc_ids = gcs.split(",");
			for (GoodsCart gc : carts) {
				for (String gc_id : gc_ids) {
					if (gc.getId().equals(CommUtil.null2Long(gc_id))) {
						if (gc.getCart_type() != null
								&& gc.getCart_type().equals("combin")
								&& gc.getCombin_main() == 1) {
							Map map = (Map) Json.fromJson(gc
									.getCombin_suit_info());
							all_price = CommUtil.add(all_price,
									map.get("suit_all_price"));
						} else {
							all_price = CommUtil.add(all_price,
									CommUtil.mul(gc.getCount(), gc.getPrice()));
						}
						if (gc.getGoods().getEnough_reduce() == 1) {// 是满就减商品，记录金额
							String er_id = gc.getGoods()
									.getOrder_enough_reduce_id();
							if (ermap.containsKey(er_id)) {
								double last_price = (double) ermap.get(er_id);
								ermap.put(
										er_id,
										CommUtil.add(
												last_price,
												CommUtil.mul(gc.getCount(),
														gc.getPrice())));
							} else {
								ermap.put(
										er_id,
										CommUtil.mul(gc.getCount(),
												gc.getPrice()));
							}
						}
					}
				}
			}
		}

		double all_enough_reduce = 0;
		for (String er_id : ermap.keySet()) {
			EnoughReduce er = this.enoughReduceService.getObjById(CommUtil
					.null2Long(er_id));
			if (er.getErstatus() == 10
					&& er.getErbegin_time().before(new Date())) {// 活动通过审核且正在进行
				String erjson = er.getEr_json();
				double er_money = ermap.get(er_id);// 购物车中的此类满减的金额
				Map fromJson = (Map) Json.fromJson(erjson);
				double reduce = 0;
				for (Object enough : fromJson.keySet()) {
					if (er_money >= CommUtil.null2Double(enough)) {
						reduce = CommUtil.null2Double(fromJson.get(enough));
					}
				}
				all_enough_reduce = CommUtil.add(all_enough_reduce, reduce);
			}
		}
		double d2 = Math.round((all_price - all_enough_reduce) * 100) / 100.0;
		return CommUtil.null2Double(CommUtil.formatMoney(d2));
	}

	/**
	 * 商品库存数量的调整
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param count
	 * @param gcs
	 * @param gift_id
	 */
	@RequestMapping("/goods_count_adjust.json")
	public void goods_count_adjust(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String count,
			String gcs, String gift_id, String token) {
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
				List<GoodsCart> carts = this.metoo_cart_calc(user);
			Map map = new HashMap();
			int code = 0;// 0表示修改成功，2表示库存不足,3表示团购库存不足
			double gc_price = 0.00;// 单位GoodsCart总价钱
			double total_price = 0.00;// 购物车总价钱
			String cart_type = "";// 判断是否为组合销售
			Goods goods = null;
			int temp_count = CommUtil.null2Int(count);
			GoodsCart gc = this.goodsCartService.getObjById(CommUtil
					.null2Long(gc_id));
			if (gc != null) {
				if (CommUtil.null2String(count).length() <= 9) {
					if (gc.getId().toString().equals(gc_id)) {
						cart_type = CommUtil.null2String(gc.getCart_type());
						goods = gc.getGoods();
						if (cart_type.equals("") || cart_type.equals("gg")) {// 普通商品的处理
							if (goods.getGroup_buy() == 2) {// 团购商品处理
								GroupGoods gg = new GroupGoods();
								for (GroupGoods gg1 : goods.getGroup_goods_list()) {
									if (gg1.getGg_goods().getId()
											.equals(goods.getId())) {
										gg = gg1;
										break;
									}
								}
								if (gg.getGg_count() >= CommUtil.null2Int(count)) {
									gc.setPrice(BigDecimal.valueOf(CommUtil
											.null2Double(gg.getGg_price())));
									gc_price = CommUtil
											.mul(gg.getGg_price(), count);
									gc.setCount(CommUtil.null2Int(count));
									this.goodsCartService.update(gc);
								} else {
									if (gg.getGg_count() == 0) {
										gc.setCount(0);
										this.goodsCartService.update(gc);
									}
									code = 3;
								}
					} else
							if (goods.getActivity_status() == 2) {// 活动商品处理
								if (user != null) {
									gc_price = CommUtil.mul(gc.getPrice(), count);
								}
							} else {
								String gsp = "";
								for (GoodsSpecProperty gs : gc.getGsps()) {
									gsp = gs.getId() + "," + gsp;
								}
								int inventory = goods.getGoods_inventory();
								if (("spec").equals(goods.getInventory_type())) {
									Map spec = this.metooCartViewTools.generic_default_info_color(goods, gsp, gc.getColor());
									inventory = CommUtil.null2Int(spec.get("count"));
								}
								if (inventory >= CommUtil.null2Int(count)
										&& CommUtil.null2String(count).length() <= 9
								//		&& gc.getGoods().getGroup_buy() != 2
										) {
									if (gc.getId().toString().equals(gc_id)) {
										gc.setCount(CommUtil.null2Int(count));
										this.goodsCartService.update(gc);
										gc_price = CommUtil.mul(gc.getPrice(),
												count);
									}
								} else {
									if (inventory == 0) {
										gc.setCount(0);
										this.goodsCartService.update(gc);
									}
									code = 2;
								}
							}
							if (cart_type.equals("combin")
									&& gc.getCombin_main() == 1) {// 组合销售的处理
								if (goods.getGoods_inventory() >= CommUtil
										.null2Int(count)) {
									gc.setCount(CommUtil.null2Int(count));
									this.goodsCartService.update(gc);
									String suit_all_price = "0.00";
									GoodsCart suit = gc;
									Map suit_map = (Map) Json.fromJson(suit
											.getCombin_suit_info());
									suit_map.put("suit_count",
											CommUtil.null2Int(count));
									suit_all_price = CommUtil
											.formatMoney(CommUtil.mul(
													CommUtil.null2Int(count),
													CommUtil.null2Double(suit_map
															.get("plan_goods_price"))));
									suit_map.put("suit_all_price", suit_all_price);// 套装整体价格=套装单价*数量
									String new_json = Json.toJson(suit_map,
											JsonFormat.compact());
									suit.setCombin_suit_info(new_json);
									suit.setCount(CommUtil.null2Int(count));
									this.goodsCartService.update(suit);
									gc_price = CommUtil.null2Double(suit_all_price);
								} else {
									if (goods.getGoods_inventory() == 0) {
										gc.setCount(0);
										this.goodsCartService.update(gc);
									}
									code = 2;
								}
							}
						}
						// 判断出是否满足满就送条件
						if (gift_id != null) {
							BuyGift bg = this.buyGiftService.getObjById(CommUtil
									.null2Long(gift_id));
							Set<Long> bg_ids = new HashSet<Long>();
							if (bg != null) {
								bg_ids.add(bg.getId());
							}
							List<GoodsCart> g_carts = new ArrayList<GoodsCart>();
							if (CommUtil.null2String(gcs).equals("")) {
								for (GoodsCart gCart : carts) {
									if (gCart.getGoods()
											.getOrder_enough_give_status() == 1
											&& gCart.getGoods().getBuyGift_id() != null) {
										bg_ids.add(gCart.getGoods().getBuyGift_id());
									}
								}
								g_carts = carts;
							} else {
								String[] gc_ids = gcs.split(",");
								for (String g_id : gc_ids) {
									GoodsCart goodsCart = this.goodsCartService
											.getObjById(CommUtil.null2Long(g_id));
									if (goodsCart != null
											&& goodsCart.getGoods()
													.getOrder_enough_give_status() == 1
											&& goodsCart.getGoods().getBuyGift_id() != null) {
										bg_ids.add(goodsCart.getGoods()
												.getBuyGift_id());
										g_carts.add(goodsCart);
									}
								}
							}
							Map<Long, List<GoodsCart>> gc_map = new HashMap<Long, List<GoodsCart>>();
							for (Long id : bg_ids) {
								gc_map.put(id, new ArrayList<GoodsCart>());
							}
							for (GoodsCart cart : g_carts) {
								if (cart.getGoods().getOrder_enough_give_status() == 1
										&& cart.getGoods().getBuyGift_id() != null) {
									for (Map.Entry<Long, List<GoodsCart>> entry : gc_map
											.entrySet()) {
										if (cart.getGoods().getBuyGift_id()
												.equals(entry.getKey())) {
											entry.getValue().add(cart);
										}
									}
								}
							}
							List<String> enough_bg_ids = new ArrayList<String>();
							for (Map.Entry<Long, List<GoodsCart>> entry : gc_map
									.entrySet()) {
								BuyGift buyGift = this.buyGiftService
										.getObjById(entry.getKey());
								// 计算出购物车价钱是否满足对应满就送
								List<GoodsCart> arrs = entry.getValue();
								BigDecimal bd = new BigDecimal("0.00");
								for (GoodsCart arr : arrs) {
									bd = bd.add(BigDecimal.valueOf(CommUtil.mul(
											arr.getPrice(), arr.getCount())));
								}
								if (bd.compareTo(buyGift.getCondition_amount()) >= 0) {
									enough_bg_ids.add(buyGift.getId().toString());
								}
							}
							map.put("bg_ids", enough_bg_ids);
						}
					}
	
				} else {
					code = 2;
				}
				map.put("count", gc.getCount());
			}
			total_price = this.calCartPrice(carts, gcs);
			Map price_map = this.calEnoughReducePrice(carts, gcs);
			Map<Long, String> erMap = (Map<Long, String>) price_map.get("erString");
			map.put("gc_price", CommUtil.formatMoney(gc_price));
			map.put("total_price", CommUtil.formatMoney(total_price));
			map.put("enough_reduce_price",
					CommUtil.formatMoney(price_map.get("reduce")));
			map.put("before", CommUtil.formatMoney(price_map.get("all")));
			for (long k : erMap.keySet()) {
				map.put("erString" + k, erMap.get(k));
			}
			result = new Result(code,"success",map);

			}
		}
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
	 * 获取满减商品总价格与满减优惠价格
	 * @param carts
	 * @param gcs
	 * @return
	 */
	private Map calEnoughReducePrice(List<GoodsCart> carts, String gcs) {
		Map<Long, String> erString = new HashMap<Long, String>();
		double all_price = 0.0;
		Map<String, Double> ermap = new HashMap<String, Double>();
		Map erid_goodsids = new HashMap();
		Date date = new Date();
		if (CommUtil.null2String(gcs).equals("")) {
			for (GoodsCart gc : carts) {
				all_price = CommUtil.add(all_price,
						CommUtil.mul(gc.getCount(), gc.getPrice()));
				
				if (gc.getGoods().getEnough_reduce() == 1) {// 是满就减商品，记录金额 [0为未参加满就减，1为已参加]
					String er_id = gc.getGoods().getOrder_enough_reduce_id();
					EnoughReduce er = this.enoughReduceService
							.getObjById(CommUtil.null2Long(er_id));
					if (er.getErstatus() == 10 //[审核状态 默认为0待审核 10为 审核通过 -10为审核未通过  20为已结束。5为提交审核，此时商家不能再修改]
							&& er.getErbegin_time().before(date)) {
						if (ermap.containsKey(er_id)) {
							double last_price = (double) ermap.get(er_id);
							ermap.put(
									er_id,
									CommUtil.add(
											last_price,
											CommUtil.mul(gc.getCount(),
													gc.getPrice())));
							((List) erid_goodsids.get(er_id)).add(gc.getGoods()
									.getId());
						} else {
							ermap.put(er_id,
									CommUtil.mul(gc.getCount(), gc.getPrice()));
							List list = new ArrayList();
							list.add(gc.getGoods().getId());
							erid_goodsids.put(er_id, list);
						}
					}
				}
			}
		} else {
			String[] gc_ids = gcs.split(",");
			for (GoodsCart gc : carts) {
				for (String gc_id : gc_ids) {
					if (gc.getId().equals(CommUtil.null2Long(gc_id))) {
						all_price = CommUtil.add(all_price,
								CommUtil.mul(gc.getCount(), gc.getPrice()));
						if (gc.getGoods().getEnough_reduce() == 1) {// 是满就减商品，记录金额
							String er_id = gc.getGoods()
									.getOrder_enough_reduce_id();
							EnoughReduce er = this.enoughReduceService
									.getObjById(CommUtil.null2Long(er_id));
							if (er.getErstatus() == 10
									&& er.getErbegin_time().before(date)) {
								if (ermap.containsKey(er_id)) {
									double last_price = (double) ermap
											.get(er_id);
									ermap.put(er_id, CommUtil.add(
											last_price,
											CommUtil.mul(gc.getCount(),
													gc.getPrice())));
									((List) erid_goodsids.get(er_id)).add(gc
											.getGoods().getId());
								} else {
									ermap.put(
											er_id,
											CommUtil.mul(gc.getCount(),
													gc.getPrice()));
									List list = new ArrayList();
									list.add(gc.getGoods().getId());
									erid_goodsids.put(er_id, list);
								}
							}
						}
					}
				}
			}
		}
		double all_enough_reduce = 0;
		for (String er_id : ermap.keySet()) {
			EnoughReduce er = this.enoughReduceService.getObjById(CommUtil
					.null2Long(er_id));
			String erjson = er.getEr_json();
			double er_money = ermap.get(er_id);// 购物车中的此类满减的金额
			Map fromJson = (Map) Json.fromJson(erjson);
			double reduce = 0;
			String erstr = "";
			for (Object enough : fromJson.keySet()) {
				if (er_money >= CommUtil.null2Double(enough)) {
					reduce = CommUtil.null2Double(fromJson.get(enough));
					erstr = "The activity products already bought full"+ enough +" AED.  Has been reduced "+ reduce +" AED";
					erid_goodsids.put("enouhg_" + er_id, enough);
				}
			}
			erString.put(er.getId(), erstr);
			erid_goodsids.put("all_" + er_id, er_money);
			erid_goodsids.put("reduce_" + er_id, reduce);

			all_enough_reduce = CommUtil.add(all_enough_reduce, reduce);
		}
		Map prices = new HashMap();
		prices.put("er_json", Json.toJson(erid_goodsids, JsonFormat.compact()));
		prices.put("erString", erString);

		double d2 = Math.round(all_price * 100) / 100.0;
		BigDecimal bd = new BigDecimal(d2);
		BigDecimal bd2 = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		prices.put("all", CommUtil.null2Double(bd2));// 商品总价

		double er = Math.round(all_enough_reduce * 100) / 100.0;
		BigDecimal erbd = new BigDecimal(er);
		BigDecimal erbd2 = erbd.setScale(2, BigDecimal.ROUND_HALF_UP);
		prices.put("reduce", CommUtil.null2Double(erbd2));// 满减价格

		double af = Math.round((all_price - all_enough_reduce) * 100) / 100.0;
		BigDecimal afbd = new BigDecimal(af);
		BigDecimal afbd2 = afbd.setScale(2, BigDecimal.ROUND_HALF_UP);
		prices.put("after", CommUtil.null2Double(afbd2));// 减后价格

		return prices;
	}	
		
		/**
		 * 购物确认,填写用户地址，配送方式，支付方式等
		 * 
		 * @param request
		 * @param response
		 * @param gcs
		 * @param giftids
		 * @return
		 */
		@SecurityMapping(title = "确认购物车第二步", value = "/goods_cart2.htm*", rtype = "buyer", rname = "购物流程2", rcode = "goods_cart", rgroup = "在线购物")
		@RequestMapping("/goods_cart2.json")
		public void goods_cart2(HttpServletRequest request,
				HttpServletResponse response, String gcs, String giftids, String token) {
			Result result = null;
			Map cartMap = new HashMap();
			ModelAndView mv = new JModelAndView("goods_cart2.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			if(token.equals("")){
				result = new Result(-100,"token Invalidation");
			}else{
				Map params = new HashMap();
				params.put("app_login_token", token);
				List<User> users =  this.userService.query("select obj from User obj where obj.app_login_token=:app_login_token order by obj.addTime desc",
						params, -1, -1);
				if(users != null && users.isEmpty()){
					result = new Result(-100,"token Invalidation");	
				}else{
					User user = users.get(0);
					if (gcs == null || gcs.equals("")) {
						result = new Result(1,"error","购物车为空");
					}else{
					//[清除自己店铺的商品，合并cookie的商品 ， 去除重复的（包含规格）]
					List<GoodsCart> carts = this.metoo_cart_calc(user);
					//[判断当前用户]
					boolean flag = true;
					if (carts.size() > 0) {
						for (GoodsCart gc : carts) {
							if (!gc.getUser().getId()
									.equals(user.getId())) {
								flag = false;
								break;
							}
						}
					}
					boolean goods_cod = true;// 默认支持货到付款，这样有一款产品不支持货到付款，这个订单就不支持货到付款
					int tax_invoice = 1;// 默认可以开具增值税发票，只要存在一款产品不支持增值税发票，整体订单不可以开具增值税发票
					if (flag && carts.size() > 0) {
						params.clear();
						params.put("user_id", user.getId());
						params.put("defaulr_val", 1);
						List<Address> addressList = this.addressService
								.query("select obj from Address obj where obj.user.id=:user_id and obj.default_val=:defaulr_val",
										params, -1, -1);
						if(addressList !=null && !addressList.isEmpty()){
							List<Map> addresses = this.metooCartViewTools.queryAddress(addressList);
							cartMap.put("address", addresses);
							String cart_session = CommUtil.randomString(32);
							request.getSession(false)
									.setAttribute("cart_session", cart_session);
							Date date = new Date();
							Map erpMap = this.calEnoughReducePrice(carts, gcs);
							cartMap.put("cart_session", cart_session);
							cartMap.put("order_goods_price", erpMap.get("all"));
							cartMap.put("order_er_price", erpMap.get("reduce"));
							
							List map_list = new ArrayList();
							List<Object> store_list = new ArrayList<Object>();
							for (GoodsCart gc : carts) {
								if (gc.getGoods().getGoods_type() == 1) {
									store_list.add(gc.getGoods().getGoods_store().getId());
								} else {
									store_list.add("self");
								}
							}
							HashSet hs = new HashSet(store_list);
							store_list.removeAll(store_list);
							store_list.addAll(hs);
							String[] gc_ids = CommUtil.null2String(gcs).split(",");
							List<Goods> ac_goodses = new ArrayList<Goods>();
							if (giftids != null && !giftids.equals("")) {
								String[] gift_ids = giftids.split(",");
								for (String gift_id : gift_ids) {
									Goods goods = this.goodsService.getObjById(CommUtil
											.null2Long(gift_id));
									if (goods != null) {
										ac_goodses.add(goods);
									}
								}
							}
							boolean ret = false;
							if (ac_goodses.size() > 0) {
								ret = true;
							}
							
							for (Object sl : store_list) {
								if (sl != "self" && !sl.equals("self")) {// 商家商品
									List<GoodsCart> gc_list = new ArrayList<GoodsCart>();
									List<GoodsCart> amount_gc_list = new ArrayList<GoodsCart>();
									Map<Goods, List<GoodsCart>> gift_map = new HashMap<Goods, List<GoodsCart>>();
									Map<Long, List<GoodsCart>> ermap = new HashMap<Long, List<GoodsCart>>();
									Map<Long, String> erString = new HashMap<Long, String>();
									for (Goods g : ac_goodses) {
										if (g.getGoods_type() == 1 && g.getGoods_store().getId().toString().equals(sl.toString())) {
											gift_map.put(g, new ArrayList<GoodsCart>());
										}
									}
									for (GoodsCart gc : carts) {
										for (String gc_id : gc_ids) {
											if (!CommUtil.null2String(gc_id).equals("")
													&& CommUtil.null2Long(gc_id).equals(gc.getId())) {
												if (gc.getGoods().getGoods_store() != null) {
													if (gc.getGoods().getGoods_store().getId().equals(sl)) {
														if (ret && gift_map.size() > 0
																&& gc.getGoods().getOrder_enough_give_status() == 1
																&& gc.getGoods().getBuyGift_id() != null) {
															BuyGift bg = this.buyGiftService.getObjById(gc.getGoods().getBuyGift_id());
															if (bg.getBeginTime().before(date)) {
																for (Map.Entry<Goods, List<GoodsCart>> entry : gift_map.entrySet()) {
																	if (entry.getKey().getBuyGift_id()
																			.equals(gc.getGoods().getBuyGift_id())) {
																		entry.getValue().add(gc);
																	} else {
																		gc_list.add(gc);
																	}
																}
															} else {
																gc_list.add(gc);
															}
														} else if (gc.getGoods().getEnough_reduce() == 1) {

															String er_id = gc.getGoods().getOrder_enough_reduce_id();
															EnoughReduce er = this.enoughReduceService
																	.getObjById(CommUtil.null2Long(er_id));
															if (er.getErbegin_time().before(date)) {
																if (ermap.containsKey(er.getId())) {
																	ermap.get(er.getId()).add(gc);
																} else {
																	List<GoodsCart> list = new ArrayList<GoodsCart>();
																	list.add(gc);
																	ermap.put(er.getId(), list);
																	Map map = (Map) Json.fromJson(er.getEr_json());
																	double k = 0;
																	String str = "";
																	for (Object key : map.keySet()) {
																		if (k == 0) {
																			k = Double.parseDouble(key.toString());
																			str = "The activity product buy " + k
																					+ " AED, you can enjoy the discount";
																		}
																		if (Double.parseDouble(key.toString()) < k) {
																			k = Double.parseDouble(key.toString());
																			str = "The activity product buy " + k
																					+ " AED, you can enjoy the discount";
																		}
																	}

																	erString.put(er.getId(), str);
																}
															} else {
																gc_list.add(gc);
															}

														} else {
															gc_list.add(gc);
														}
														amount_gc_list.add(gc);
													}
												}
											}
										}
									}
									if ((gc_list != null && gc_list.size() > 0) || (gift_map != null && gift_map.size() > 0)
											|| (ermap != null && ermap.size() > 0)) {
										Map map = new HashMap();
										Map ergcMap = this.calEnoughReducePrice(amount_gc_list, gcs);// 满减相关信息
										if (gift_map.size() > 0) {
											map.put("ac_goods", gift_map);
										}
										if (ermap.size() > 0) {
											map.put("er_goods", ermap);
											map.put("erString", ergcMap.get("erString"));
										}
										map.put("store_id", sl);
										map.put("store", this.storeService.getObjById(CommUtil.null2Long(sl)));
										map.put("store_goods_price", this.calCartPrice(amount_gc_list, gcs));
										map.put("store_enough_reduce", ergcMap.get("reduce"));
										map.put("gc_list", gc_list);
										map_list.add(map);
									}
									for (GoodsCart gc : gc_list) {
										if (gc.getGoods().getGoods_cod() == -1 || gc.getGoods().getGoods_choice_type() == 1) {// 只要存在一件不允许使用货到付款购买的商品整个订单就不允许使用货到付款
											goods_cod = false;
										}
										if (gc.getGoods().getTax_invoice() == 0) {// 只要存在一件不支持开具增值税发票的商品，整个订单就不允许开具增值税发票
											tax_invoice = 0;
										}
									}
								} else {// 自营商品
									List<GoodsCart> gc_list = new ArrayList<GoodsCart>();
									List<GoodsCart> amount_gc_list = new ArrayList<GoodsCart>();
									Map<Goods, List<GoodsCart>> gift_map = new HashMap<Goods, List<GoodsCart>>();
									Map<Long, List<GoodsCart>> ermap = new HashMap<Long, List<GoodsCart>>();
									Map<Long, String> erString = new HashMap<Long, String>();
									for (Goods g : ac_goodses) {
										if (g.getGoods_type() == 0) {
											gift_map.put(g, new ArrayList<GoodsCart>());
										}
									}
									for (GoodsCart gc : carts) {
										for (String gc_id : gc_ids) {
											if (!CommUtil.null2String(gc_id).equals("")
													&& CommUtil.null2Long(gc_id).equals(gc.getId())) {
												if (gc.getGoods().getGoods_store() == null) {
													if (ret && gift_map.size() > 0 && gc.getGoods().getOrder_enough_give_status() == 1
															&& gc.getGoods().getBuyGift_id() != null) {
														BuyGift bg = this.buyGiftService.getObjById(gc.getGoods().getBuyGift_id());
														if (bg.getBeginTime().before(date)) {
															for (Map.Entry<Goods, List<GoodsCart>> entry : gift_map.entrySet()) {
																if (entry.getKey().getBuyGift_id()
																		.equals(gc.getGoods().getBuyGift_id())) {
																	entry.getValue().add(gc);
																} else {
																	gc_list.add(gc);
																}
															}
														} else {
															gc_list.add(gc);
														}
													} else if (gc.getGoods().getEnough_reduce() == 1) {

														String er_id = gc.getGoods().getOrder_enough_reduce_id();
														EnoughReduce er = this.enoughReduceService
																.getObjById(CommUtil.null2Long(er_id));
														if (er.getErbegin_time().before(date)) {
															if (ermap.containsKey(er.getId())) {
																ermap.get(er.getId()).add(gc);
															} else {
																List<GoodsCart> list = new ArrayList<GoodsCart>();
																list.add(gc);
																ermap.put(er.getId(), list);
																Map map = (Map) Json.fromJson(er.getEr_json());
																double k = 0;
																String str = "";
																for (Object key : map.keySet()) {
																	if (k == 0) {
																		k = Double.parseDouble(key.toString());
																		str = "The activity product buy " + k
																				+ " AED, you can enjoy the discount";
																	}
																	if (Double.parseDouble(key.toString()) < k) {
																		k = Double.parseDouble(key.toString());
																		str = "The activity product buy " + k
																				+ " AED, you can enjoy the discount";
																	}
																}

																erString.put(er.getId(), str);
															}
														} else {
															gc_list.add(gc);
														}

													} else {
														gc_list.add(gc);
													}
													amount_gc_list.add(gc);
												}
											}
										}
									}
									if ((gc_list != null && gc_list.size() > 0) || (gift_map != null && gift_map.size() > 0)
											|| (ermap != null && ermap.size() > 0)) {
										Map map = new HashMap();
										Map ergcMap = this.calEnoughReducePrice(amount_gc_list, gcs);// 满减相关信息
										if (gift_map.size() > 0) {
											map.put("ac_goods", gift_map);
										}
										if (ermap.size() > 0) {
											map.put("er_goods", ermap);
											map.put("erString", ergcMap.get("erString"));
										}
										map.put("store_id", sl);
										map.put("store_goods_price", this.calCartPrice(amount_gc_list, gcs));
										map.put("store_enough_reduce", ergcMap.get("reduce"));
										map.put("gc_list", gc_list);
										map_list.add(map);
									}
									for (GoodsCart gc : gc_list) {
										if (gc.getGoods().getGoods_cod() == -1 || gc.getGoods().getGoods_choice_type() == 1) {// 只要存在一件不允许使用货到付款购买的商品整个订单就不允许使用货到付款
											goods_cod = false;
										}
										if (gc.getGoods().getTax_invoice() == 0) {// 只要存在一件不支持开具增值税发票的商品，整个订单就不允许开具增值税发票
											tax_invoice = 0;
										}
									}
								}
							}
							// 比较当日时间段
							Calendar cal = Calendar.getInstance();
							//[用户信息 -- 订单结算页展示商品对应的店铺信息]
							Map usermap = new HashMap();
							usermap.put("userinvoiceType", user.getInvoiceType());
							usermap.put("username", user.getUsername());
							usermap.put("user_id", user.getId());
							cartMap.put("user", usermap);
							List<Map> orderlist = new ArrayList<Map>();
							Object order_tota_price = 0;
							Object store_total_price = 0;
							Object store_order_tota_price = 0; 
							for(int i=0; i<map_list.size(); i++){
								Map order_map = new HashMap();
								Map map_info = (Map) map_list.get(i);
								try {
									if(map_info.get("store_id") == "self"){
										order_map.put("store_id", "self");
										order_map.put("OnLine", userTools.adminOnLine());
									}else{
										order_map.put("store_id", map_info.get("store_id"));
										Long sotre_id = CommUtil.null2Long(map_info.get("store_id"));
										order_map.put("store_name", storeService.getObjById(sotre_id).getStore_name());
										order_map.put("OnLine",userTools.userOnLine(storeService.getObjById(sotre_id).getUser().getTrueName()));
									}
								} catch (NullPointerException e) {
									// TODO Auto-generated catch block
									order_map.put("store_name", "");
								}
								List<GoodsCart> goodscart_list = (List<GoodsCart>) map_info.get("gc_list");
								List<Map> gc_list = new ArrayList<Map>();
								boolean delivery = false;
								for(GoodsCart gc : goodscart_list){
									if(gc.getGoods().getGoods_choice_type() == 0){
										delivery = true;
									}
									Map gcmap = new HashMap();
									gcmap.put("gc_id", gc.getId());
									gcmap.put("gcg_choice_type", gc.getGoods().getGoods_choice_type());
									gcmap.put("gcg_id", gc.getGoods().getId());
									gcmap.put("all_price", CommUtil.mul(gc.getCount(), gc.getPrice()));
									gcmap.put("gcg_name", gc.getGoods().getGoods_name());
									gcmap.put("gcg_price", gc.getPrice());
									gcmap.put("gcg_type", gc.getGoods().getGoods_type());
									gcmap.put("gcg_activity_status", gc.getGoods().getActivity_status());
									gcmap.put("gcg_group_buy", gc.getGoods().getGroup_buy());
									gcmap.put("gcg_main_photo", this.configService.getSysConfig().getImageWebServer()+"/"+gc.getGoods().getGoods_main_photo().getPath()+"/"+gc.getGoods().getGoods_main_photo().getName());
									gcmap.put("gc_count", gc.getCount());
									gcmap.put("gc_sepc_info", gc.getSpec_info());
									gcmap.put("goods_spec_color", gc.getColor()  == null ? "" : gc.getColor());
									List<Map> combinList = new ArrayList<Map>();
									if(gc.getCart_type() == "combin"){
										gcmap.put("suit_name", goodsViewTools.getsuitName(gc.getCombin_suit_info()));
										String weburl= request.getSession().getServletContext().getContextPath();
										List<Map> suit_goods_list = goodsViewTools.getsuitGoods(weburl,CommUtil.null2String(gc.getId()));
										List<Map> suit_goods_listmap = new ArrayList<Map>();
										for(Map suit_goods:suit_goods_list){
											Map combinmap = new HashMap();
											combinmap.put("url", suit_goods.get("url"));
											combinmap.put("name", suit_goods.get("name"));
											combinmap.put("img", suit_goods.get("img"));
											suit_goods_listmap.add(combinmap);
										}
										gcmap.put("suit_goods", suit_goods_listmap);
									}
									Map suit_map = goodsViewTools.getSuitInfo(CommUtil.null2String(gc.getId()));
									if(gc.getCart_type() != null ){
										if(gc.getCart_type() == "combin"){
											gcmap.put("gc_price", suit_map.get("plan_goods_price"));
											gcmap.put("all_price", suit_map.get("suit_all_price"));
										}else{
											gcmap.put("gc_price", gc.getPrice());
											gcmap.put("all_price", CommUtil.mul(gc.getCount(), gc.getPrice()));
										}
										gcmap.put("gc_count", gc.getCount());
										gcmap.put("spec_info", gc.getSpec_info());
									}
									gc_list.add(gcmap);
									order_map.put("gc_list", gc_list);
								}
								
									Map<Long, List<GoodsCart>> goods_er_map = null;
									if(map_info.get("er_goods") != null){
										List<GoodsCart> goodscart_er = new ArrayList<GoodsCart>();
									    goods_er_map = (Map<Long, List<GoodsCart>>) map_info.get("er_goods");
										List er_map_list = new ArrayList();
										for(Long key : goods_er_map.keySet()){
											Map er_reduce_map = new HashMap();
											goodscart_er = goods_er_map.get(key);
											Map restringmap = (Map) map_info.get("erString");
											er_reduce_map.put("erstr", restringmap.get(key));
											er_reduce_map.put("erprice", map_info.get("store_enough_reduce"));
											order_map.put("erTag", er_reduce_map);
											for(GoodsCart er_gc : goodscart_er){
												Map gcmap = new HashMap();
												if(er_gc.getGoods().getGoods_choice_type() == 0){
													delivery = true;
												}
												gcmap.put("goods_photo", this.configService.getSysConfig().getImageWebServer()
																	+ "/" 
																	+ er_gc.getGoods().getGoods_main_photo().getPath()
																	+ "/"
																	+ er_gc.getGoods().getGoods_main_photo().getName());
												gcmap.put("id", er_gc.getGoods().getId());
												gcmap.put("gc_id", er_gc.getId());
												gcmap.put("name", er_gc.getGoods().getGoods_name());
												gcmap.put("type", er_gc.getGoods().getGoods_type());
												gcmap.put("activity_status", er_gc.getGoods().getActivity_status());
												gcmap.put("group_buy", er_gc.getGoods().getGroup_buy());
												gcmap.put("gc_price", er_gc.getPrice());
												gcmap.put("gc_count", er_gc.getCount());
												gcmap.put("gc_sepc_info", er_gc.getSpec_info());//[规格内容]
												gcmap.put("all_price", CommUtil.mul(er_gc.getCount(), er_gc.getPrice()));
												er_map_list.add(gcmap);
											}
										}	
										order_map.put("er_info", er_map_list);
									}
									
									Map<Goods, List<GoodsCart>> goods_ac_map = null;
									if(map_info.get("ac_goods") != null){
										List ac_info_list = new ArrayList();
										List<GoodsCart> goodscart_ac = new ArrayList<GoodsCart>();
										goods_ac_map = 	(Map<Goods, List<GoodsCart>>) map_info.get("ac_goods");
										for(Goods key : goods_ac_map.keySet()){
											goodscart_ac = goods_ac_map.get(key);
											String goods_name = goodscart_ac.get(i).getGoods().getGoods_name();
											ac_info_list.add(goods_name);
											for(GoodsCart ac_gc : goodscart_ac){
												Map gcmap = new HashMap();
												if(ac_gc.getGoods().getGoods_choice_type() == 0){
													 delivery = true;
												}
												gcmap.put("goods_photo", this.configService.getSysConfig().getImageWebServer()
																	+ "/"
																	+ ac_gc.getGoods().getGoods_main_photo().getPath()
																	+ "/"
																	+ ac_gc.getGoods().getGoods_main_photo().getName());
												gcmap.put("goods_id", ac_gc.getGoods().getId());
												gcmap.put("gc_id", ac_gc.getId());
												gcmap.put("goods_name", ac_gc.getGoods().getGoods_name());
												gcmap.put("goods_type", ac_gc.getGoods().getGoods_type());
												gcmap.put("goods_activity_status", ac_gc.getGoods().getActivity_status());
												gcmap.put("goods_group_buy", ac_gc.getGoods().getGroup_buy());
												gcmap.put("gc_peice", ac_gc.getPrice());
												gcmap.put("goods_id", ac_gc.getGoods().getId());
												gcmap.put("gc_count", ac_gc.getCount());
												gcmap.put("gc_sepc_info", ac_gc.getSpec_info());
												gcmap.put("gc_all_price", CommUtil.mul(ac_gc.getCount(), ac_gc.getPrice()));
												ac_info_list.add(gcmap);
											}
										}
										order_map.put("ac_info_list", ac_info_list);
									}
									
									Object ship_price = 0.00;
 									if(delivery == true){
										List<SysMap> syslist = new ArrayList<SysMap>();
										for(Map map : addresses){
											syslist = transportTools.query_cart_trans_goods_cart2(goodscart_list,goods_er_map,goods_ac_map,CommUtil.null2String(map.get("area_id")));
										}
										Object price = 0;
										List sys_info =  new ArrayList();
										Map sysmap = new HashMap();
										for(SysMap obj : syslist){
											if(obj.getKey().equals("快递")){
												ship_price = obj.getValue();
											}
										}
										sysmap.put("ship_price", ship_price);
										sys_info.add(sysmap);
										order_map.put("sys_info", sys_info);
									}
								    store_total_price = CommUtil.add(ship_price,map_info.get("store_goods_price"));
								    order_tota_price = CommUtil.add(order_tota_price, ship_price) + CommUtil.null2Double(map_info.get("store_goods_price"));
									order_map.put("store_total_price", store_total_price);
									orderlist.add(order_map);
									cartMap.put("orderlist", orderlist);
						}
							cartMap.put("gcs", gcs);
							cartMap.put("order_tota_price", CommUtil.null2BigDecimal(order_tota_price));
							if(cartMap.isEmpty()){
								result = new Result(1,"error","参数错误");
							}else{
								result = new Result(0,"success",cartMap);
							}
						}else{
							result = new Result(2,"添加地址");
						}
					} else {
						result = new Result(3,"购物车信息为空");
					}
					}
				}
			}
			String cart2_temp = Json.toJson(result, JsonFormat.compact());
			try {
				response.getWriter().print(cart2_temp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private String generic_day(int day) {
			String[] list = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
					"Friday", "Saturday" };
			return list[day - 1];
		}

	@SecurityMapping(title = "完成订单提交进入支付", value = "/goods_cart3.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/goods_cart3.json")
	public void goods_cart3(HttpServletRequest request, HttpServletResponse response, String cart_session,
			String store_id, String addr_id, String gcs, String delivery_time, String delivery_type, String delivery_id,
			String payType, String gifts, String mobile, String mobile_verify_code, String token) throws Exception {
		Result result = null;
		Map goods_cart3_map = new HashMap();
		ModelAndView mv = new JModelAndView("goods_cart3.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
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
				//根据手机号获取验证码
				VerifyCode mvc = this.mobileverifycodeService.getObjByProperty(null,
						"mobile", mobile);
				if (mvc != null && mvc.getCode().equalsIgnoreCase(mobile_verify_code)) {
					this.mobileverifycodeService.delete(mvc.getId());
					// 绑定成功后发送手机短信提醒
					String content = "尊敬的"
							+ user.getUserName()
							+ "您好，您于" + CommUtil.formatLongDate(new Date())
							+ "验证身份成功。["
							+ this.configService.getSysConfig().getTitle() + "]";
					this.msgTools.sendSMS(user.getMobile(), content);
					
				String cart_session1 = (String) request.getSession(false).getAttribute("cart_session");
				if (CommUtil.null2String(cart_session1).equals(cart_session) && !CommUtil.null2String(store_id).equals("")) {
					List<GoodsCart> order_carts = new ArrayList<GoodsCart>();
					Address addr = this.addressService.getObjById(CommUtil.null2Long(addr_id));
					Date date = new Date();
					String[] gc_ids = gcs.split(",");
					String[] gift_ids = gifts.split(",");
					List<Goods> gift_goods = new ArrayList<Goods>();
					for (String gid : gift_ids) {
						Goods goods = this.goodsService.getObjById(CommUtil.null2Long(gid));
						if (goods != null) {
							BuyGift bg = this.buyGiftService.getObjById(CommUtil.null2Long(goods.getBuyGift_id()));
							if (bg != null && bg.getBeginTime().before(date)) {
								gift_goods.add(goods);
							}
						}
					}
					for (String gc_id : gc_ids) {
						if (!gc_id.equals("")) {
							GoodsCart car = this.goodsCartService.getObjById(CommUtil.null2Long(gc_id));
							if(car != null){
								order_carts.add(car);
							}
						}
					}
					for (String gc_id : gc_ids) {
						GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(gc_id));
						if (gc != null && gc.getGoods().getGoods_cod() == -1) {// 只要存在一件不允许使用货到付款购买的商品整个订单就不允许使用货到付款
							if (!payType.equals("online")) {// 订单不支持货到付款，用户从页面前端恶意篡改支付方式为货到付款
								result = new Result(1,"您恶意修改付款方式，订单已过期");
							}
						}
					}
					if (order_carts.size() > 0 && addr != null) {//[根据购物车id查询订单购物车]
						// 验证购物车中是否存在库存为0的商品
						boolean inventory_very = true;
						for (GoodsCart gc : order_carts) {
							if (gc.getCount() == 0) {
								inventory_very = false;
							}
							//int goods_inventory = CommUtil.null2Int(this.generic_default_info(gc.getGoods(), gc.getCart_gsp(), user).get("count"));// 计算商品库存信息
							int goods_inventory = CommUtil.null2Int(this.metooCartViewTools.generic_default_info_color(gc.getGoods(), gc.getCart_gsp(), gc.getColor()).get("count"));// 计算商品库存信息
							if (goods_inventory == 0 || goods_inventory < gc.getCount()) {
								inventory_very = false;
							}
						}
						if (inventory_very) {
							User buyer = this.userService
									.getObjById(CommUtil.null2Long(user.getId()));
							OrderForm main_order = null;
							if (payType.equals("payafter")) {// 使用货到付款
								Map payafter_payTypemap = new HashMap();
								String pay_session = CommUtil.randomString(32);
								request.getSession(false).setAttribute("pay_session", pay_session);
								payafter_payTypemap.put("pay_session", pay_session);
								goods_cart3_map.put("payafter_payTypemap", payafter_payTypemap);
								result = new Result(0,"货到付款提交成功",goods_cart3_map);
							}
							double all_of_price = 0;
							request.getSession(false).removeAttribute("cart_session");// 删除订单提交唯一标示，用户不能进行第二次订单提交
							String store_ids[] = store_id.split(",");
							List<Map> child_order_maps = new ArrayList<Map>();
							int whether_gift_in = 0;// 判断是否有满就送 子订单中包含赠品
													// 则主订单whether_gift变为1
							String order_suffix = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
							for (int i = 0; i < store_ids.length; i++) {// 根据店铺id，保存多个子订单
								String sid = store_ids[i];
								Store store = null;
								List<GoodsCart> gc_list = new ArrayList<GoodsCart>();
								List<Map> map_list = new ArrayList<Map>();
								if (sid != "self" && !sid.equals("self")) {
									store = this.storeService.getObjById(CommUtil.null2Long(sid));
								}
								String tempString = "";
								for (GoodsCart gc : order_carts) {
									if (gc.getGoods().getGoods_type() == 1) {// 商家商品
										boolean add = false;
										for (String gc_id : gc_ids) {
											if (!CommUtil.null2String(gc_id).equals("")
													&& gc.getId().equals(CommUtil.null2Long(gc_id))) {// 判断是否是用户勾选要购买的商品
												add = true;
												break;
											}
										}
										if (add) {
											if (gc.getGoods().getGoods_store().getId().equals(CommUtil.null2Long(sid))) {
												String goods_type = "";
												if ("combin" == gc.getCart_type() || "combin".equals(gc.getCart_type())) {
													if (gc.getCombin_main() == 1) { //[1为套装主购物车，0为其他套装购物车]
														goods_type = "combin";
													}
												}
												if ("group" == gc.getCart_type() || "group".equals(gc.getCart_type())) {
													goods_type = "group";
												}
												final String genId = user.getId()
														+ UUID.randomUUID().toString() + ".html";
												final String goodsId = gc.getGoods().getId().toString();
												final String url = CommUtil.getHttpURL(request);
												HttpClient client = new HttpClient();
												HttpMethod method = new GetMethod(url + "/goods.json?id=" + goodsId);
												try {
													client.executeMethod(method);
												} catch (HttpException e2) {
													// TODO Auto-generated catch
													// block
													e2.printStackTrace();
												} catch (IOException e2) {
													// TODO Auto-generated catch
													// block
													e2.printStackTrace();
												}
												
												try {
													String resposneBody = method.getResponseBodyAsString();
													JSONObject jsonObject =  JSON.parseObject(resposneBody);System.out.println(resposneBody);
													String code = jsonObject.get("code").toString();System.out.println(code);
													if(code != null && !"".equals(code)){
														tempString = jsonObject.get("data").toString();
													}
													
												} catch (IOException e2) {
													// TODO Auto-generated catch
													// block
													e2.printStackTrace();
												}
												Map json_map = new HashMap();
												json_map.put("goods_id", gc.getGoods().getId());
												json_map.put("goods_name", gc.getGoods().getGoods_name());
												json_map.put("goods_choice_type", gc.getGoods().getGoods_choice_type());
												json_map.put("goods_type", goods_type);
												json_map.put("goods_count", gc.getCount());
												json_map.put("goods_price", gc.getPrice());// 商品单价
												json_map.put("goods_all_price", CommUtil.mul(gc.getPrice(), gc.getCount()));// 商品总价
												json_map.put("goods_commission_price", this.getGoodscartCommission(gc));// 设置该商品总佣金
												json_map.put("goods_commission_rate",
														gc.getGoods().getGc().getCommission_rate());// 设置该商品的佣金比例
												json_map.put("goods_payoff_price",
														CommUtil.subtract(CommUtil.mul(gc.getPrice(), gc.getCount()),
																this.getGoodscartCommission(gc)));// 该商品结账价格=该商品总价格-商品总佣金
												json_map.put("goods_gsp_val", gc.getSpec_info());
												json_map.put("goods_gsp_ids", gc.getCart_gsp());
												json_map.put("goods_snapshoot", tempString);
												if (gc.getGoods().getGoods_main_photo() != null) {
													json_map.put("goods_mainphoto_path",
															gc.getGoods().getGoods_main_photo().getPath() + "/"
																	+ gc.getGoods().getGoods_main_photo().getName() + "_small."
																	+ gc.getGoods().getGoods_main_photo().getExt());
												} else {
													json_map.put("goods_mainphoto_path",
															this.configService.getSysConfig().getGoodsImage().getPath() + "/"
																	+ this.configService.getSysConfig().getGoodsImage()
																			.getName());
												}
												String goods_domainPath = CommUtil.getURL(request) + "/goods_"
														+ gc.getGoods().getId() + ".htm";
												String store_domainPath = CommUtil.getURL(request) + "/store_"
														+ gc.getGoods().getGoods_store().getId() + ".htm";
												if (this.configService.getSysConfig().isSecond_domain_open()
														&& gc.getGoods().getGoods_store().getStore_second_domain() != ""
														&& gc.getGoods().getGoods_type() == 1) {
													String store_second_domain = "http://"
															+ gc.getGoods().getGoods_store().getStore_second_domain() + "."
															+ CommUtil.generic_domain(request);
													goods_domainPath = store_second_domain + "/goods_" + gc.getGoods().getId()
															+ ".htm";
													store_domainPath = store_second_domain;
												}
												json_map.put("goods_domainPath", goods_domainPath);// 商品二级域名路径
												json_map.put("store_domainPath", store_domainPath);// 店铺二级域名路径
												// 设置商品组合套装信息
												if (goods_type.equals("combin")) {
													json_map.put("combin_suit_info", gc.getCombin_suit_info());
												}
												map_list.add(json_map);
												gc_list.add(gc);
											}
										}
									} else {// 自营商品
										boolean add = false;
										for (String gc_id : gc_ids) {
											if (!CommUtil.null2String(gc_id).equals("")
													&& gc.getId().equals(CommUtil.null2Long(gc_id))) {// 判断是否是用户勾选要购买的商品
												add = true;
												break;
											}
										}
										if (add) {
											if (sid == "self" || sid.equals("self")) {
												String goods_type = "";
												if ("combin" == gc.getCart_type() || "combin".equals(gc.getCart_type())) {
													if (gc.getCombin_main() == 1) {
														goods_type = "combin";
													}
			
												}
												if ("group" == gc.getCart_type() || "group".equals(gc.getCart_type())) {
													goods_type = "group";
												}
												final String genId = user.getId()
														+ UUID.randomUUID().toString() + ".html";
												final String goodsId = gc.getGoods().getId().toString();
												String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
												final String saveFilePathName = request.getSession().getServletContext()
														.getRealPath("/") + uploadFilePath + File.separator + "snapshoot"
														+ File.separator + genId;
												File file = new File(request.getSession().getServletContext().getRealPath("/")
														+ uploadFilePath + File.separator + "snapshoot");
												if (!file.exists()) {
													file.mkdir();
												}
												final String url = CommUtil.getURL(request);
												Thread t = new Thread(new Runnable() {
													public void run() {
														HttpClient client = new HttpClient();
														HttpMethod method = new GetMethod(url + "/goods_" + goodsId + ".htm");
														try {
															client.executeMethod(method);
														} catch (HttpException e2) {
															// TODO Auto-generated catch
															// block
															e2.printStackTrace();
														} catch (IOException e2) {
															// TODO Auto-generated catch
															// block
															e2.printStackTrace();
														}
														String tempString = "";
														try {
															tempString = method.getResponseBodyAsString();
														} catch (IOException e2) {
															// TODO Auto-generated catch
															// block
															e2.printStackTrace();
														}
														method.releaseConnection();
														BufferedWriter writer = null;
														try {
															writer = new BufferedWriter(new FileWriter(saveFilePathName));
														} catch (IOException e1) {
															e1.printStackTrace();
														}
														try {
															writer.append(tempString);
															writer.flush();// 需要及时清掉流的缓冲区，万一文件过大就有可能无法写入了
															writer.close();
														} catch (IOException e) {
															e.printStackTrace();
														}
													}
												});
												t.start();
												Map json_map = new HashMap();
												json_map.put("goods_id", gc.getGoods().getId());
												json_map.put("goods_name", gc.getGoods().getGoods_name());
												json_map.put("goods_choice_type", gc.getGoods().getGoods_choice_type());
												json_map.put("goods_type", goods_type);
												json_map.put("goods_count", gc.getCount());
												json_map.put("goods_price", gc.getPrice());// 商品单价
												json_map.put("goods_all_price", CommUtil.mul(gc.getPrice(), gc.getCount()));// 商品总价
												json_map.put("goods_gsp_val", gc.getSpec_info());
												json_map.put("goods_gsp_ids", gc.getCart_gsp() == null ? "" : gc.getCart_gsp());
												json_map.put("goods_snapshoot", CommUtil.getURL(request) + "/" + uploadFilePath
														+ "/snapshoot/" + genId);
												if (gc.getGoods().getGoods_main_photo() != null) {
													json_map.put("goods_mainphoto_path",
															gc.getGoods().getGoods_main_photo().getPath() + "/"
																	+ gc.getGoods().getGoods_main_photo().getName() + "_small."
																	+ gc.getGoods().getGoods_main_photo().getExt());
												} else {
													json_map.put("goods_mainphoto_path",
															this.configService.getSysConfig().getGoodsImage().getPath() + "/"
																	+ this.configService.getSysConfig().getGoodsImage()
																			.getName());
												}
												json_map.put("goods_domainPath",
														CommUtil.getURL(request) + "/goods_" + gc.getGoods().getId() + ".htm");// 商品二级域名路径
												// 设置商品组合套装信息
												if (goods_type.equals("combin")) {
													json_map.put("combin_suit_info", gc.getCombin_suit_info());
												}
												map_list.add(json_map);
												gc_list.add(gc);
											}
										}
									}
								}
								// 赠品信息
								List<Map> gift_map = new ArrayList<Map>();
								for (int j = 0; gift_goods.size() > 0 && j < gift_goods.size(); j++) {
									if (gift_goods.get(j).getGoods_type() == 1) {
										if (gift_goods.get(j).getGoods_store() != null
												&& gift_goods.get(j).getGoods_store().getId().toString().equals(sid)) {
											Map map = new HashMap();
											map.put("goods_id", gift_goods.get(j).getId());
											map.put("goods_name", gift_goods.get(j).getGoods_name());
											map.put("goods_main_photo",
													gift_goods.get(j).getGoods_main_photo().getPath() + "/"
															+ gift_goods.get(j).getGoods_main_photo().getName() + "_small."
															+ gift_goods.get(j).getGoods_main_photo().getExt());
											map.put("goods_price", gift_goods.get(j).getGoods_current_price());
											String goods_domainPath = CommUtil.getURL(request) + "/goods_"
													+ gift_goods.get(j).getId() + ".htm";
											if (this.configService.getSysConfig().isSecond_domain_open()
													&& gift_goods.get(j).getGoods_store().getStore_second_domain() != ""
													&& gift_goods.get(j).getGoods_type() == 1) {
												String store_second_domain = "http://"
														+ gift_goods.get(j).getGoods_store().getStore_second_domain() + "."
														+ CommUtil.generic_domain(request);
												goods_domainPath = store_second_domain + "/goods_" + gift_goods.get(j).getId()
														+ ".htm";
											}
											map.put("goods_domainPath", goods_domainPath);// 商品二级域名路径
											map.put("buyGify_id", gift_goods.get(j).getBuyGift_id());
											gift_map.add(map);
										}
									} else {
										if (sid.equals("self") || sid == "self") {
											Map map = new HashMap();
											map.put("goods_id", gift_goods.get(j).getId());
											map.put("goods_name", gift_goods.get(j).getGoods_name());
											map.put("goods_main_photo",
													gift_goods.get(j).getGoods_main_photo().getPath() + "/"
															+ gift_goods.get(j).getGoods_main_photo().getName() + "_small."
															+ gift_goods.get(j).getGoods_main_photo().getExt());
											map.put("goods_price", gift_goods.get(j).getGoods_current_price());
											String goods_domainPath = CommUtil.getURL(request) + "/goods_"
													+ gift_goods.get(j).getId() + ".htm";
											if (this.configService.getSysConfig().isSecond_domain_open()
													&& gift_goods.get(j).getGoods_store().getStore_second_domain() != ""
													&& gift_goods.get(j).getGoods_type() == 1) {
												String store_second_domain = "http://"
														+ gift_goods.get(j).getGoods_store().getStore_second_domain() + "."
														+ CommUtil.generic_domain(request);
												goods_domainPath = store_second_domain + "/goods_" + gift_goods.get(j).getId()
														+ ".htm";
											}
											map.put("goods_domainPath", goods_domainPath);// 商品二级域名路径
											map.put("buyGify_id", gift_goods.get(j).getBuyGift_id());
											gift_map.add(map);
										}
									}
								}
								double goods_amount = this.calCartPrice(gc_list, gcs);// 订单中商品价格
								/**
								 * 订单页面根据用户选择地址计算运费
								 */
								List<SysMap> sms = this.transportTools.query_cart_trans_goods_cart3(gc_list,
										CommUtil.null2String(addr.getArea().getId()));
								String transport = request.getParameter("transport_" + sid);
								if (CommUtil.null2String(transport).indexOf("平邮") < 0
										&& CommUtil.null2String(transport).indexOf("快递") < 0
										&& CommUtil.null2String(transport).indexOf("EMS") < 0) {
									transport = "快递";
								}
								double ship_price = 0.00;
/*								for (SysMap sm : sms) {
									if (CommUtil.null2String(sm.getKey()).indexOf(transport) >= 0) {
										ship_price = CommUtil.null2Double(sm.getValue());// 订单物流运费
									}
								}*/
								String transfee = "-1"; //0为商家 1为买家
								double store_ship_price = 0.00;
								for (SysMap sm : sms) {
										if (CommUtil.null2String(sm.getKey()).indexOf(transport) >= 0) {
											ship_price = ship_price + CommUtil.null2Double(sm.getValue());// 订单物流运费
										}else{
											store_ship_price = CommUtil.null2Double(sm.getValue());
										}
								}
								//[多个商品有一件商家承担运费，结算页面就展示商家承担的运费]
								if(store_ship_price == 0.0){
									transfee = "1";
								}else{
									 transfee = "0";
								}
								double totalPrice = CommUtil.add(goods_amount, ship_price);// 订单总价
								double commission_amount = this.getOrderCommission(gc_list);// 订单总体佣金
								//[服务VAT]
								double goods_vat = CommUtil.mul(goods_amount, 0.05);
								//[佣金VAT]
								double commission_vat = CommUtil.mul(commission_amount, 0.05);
								Map ermap = this.calEnoughReducePrice(gc_list, gcs);
								String er_json = (String) ermap.get("er_json");
								double all_goods = Double.parseDouble(ermap.get("all").toString());
								double reduce = Double.parseDouble(ermap.get("reduce").toString());
								OrderForm of = new OrderForm();
								of.setAddTime(new Date());
			
								String order_store_id = "0";
								if (sid != "self" && !sid.equals("self")) {
									order_store_id = CommUtil.null2String(store.getId());
								}
								of.setOrder_id(user.getId() + order_suffix + order_store_id);
								System.out.println(user.getId() + order_suffix + order_store_id);
								// 设置收货地址信息
								of.setReceiver_Name(addr.getTrueName());
								of.setReceiver_area(addr.getArea().getParent().getParent().getAreaName()
										+ addr.getArea().getParent().getAreaName() + addr.getArea().getAreaName());
								of.setReceiver_area_info(addr.getArea_info());
								of.setReceiver_mobile(addr.getMobile());
								of.setReceiver_telephone(addr.getTelephone());
								of.setReceiver_zip(addr.getZip());
								of.setEnough_reduce_amount(BigDecimal.valueOf(reduce));
								of.setEnough_reduce_info(er_json);
								of.setTransport_type(transfee);
								of.setTransport(transport);
								of.setOrder_status(10);
								of.setUser_id(buyer.getId().toString());
								of.setUser_name(buyer.getUserName());
								of.setGoods_info(Json.toJson(map_list, JsonFormat.compact()));// 设置商品信息json数据
								of.setMsg(request.getParameter("msg_" + sid));
								of.setInvoiceType(CommUtil.null2Int(request.getParameter("invoiceType")));
								of.setInvoice(request.getParameter("invoice"));
								of.setShip_price(BigDecimal.valueOf(ship_price));
								of.setStore_ship_price(BigDecimal.valueOf(store_ship_price));
								of.setGoods_amount(BigDecimal.valueOf(all_goods));
								of.setTotalPrice(BigDecimal.valueOf(totalPrice));
								//of.setSnapshooot(tempString);
								//[查询优惠券信息]
						/*		String coupon_id = request.getParameter("coupon_id_" + sid);
								if (!coupon_id.equals("")) {
									CouponInfo ci = this.couponInfoService.getObjById(CommUtil.null2Long(coupon_id));
									if (ci != null) {
										if (SecurityUserHolder.getCurrentUser().getId().equals(ci.getUser().getId())) {
											ci.setStatus(1);
											this.couponInfoService.update(ci);
											Map coupon_map = new HashMap();
											coupon_map.put("couponinfo_id", ci.getId());
											coupon_map.put("couponinfo_sn", ci.getCoupon_sn());
											coupon_map.put("coupon_amount", ci.getCoupon().getCoupon_amount());
											double rate = CommUtil.div(ci.getCoupon().getCoupon_amount(), goods_amount);
											coupon_map.put("coupon_goods_rate", rate);
											of.setCoupon_info(Json.toJson(coupon_map, JsonFormat.compact()));
											of.setTotalPrice(BigDecimal.valueOf(
													CommUtil.subtract(of.getTotalPrice(), ci.getCoupon().getCoupon_amount())));
										}
									}
								}*/
								all_of_price = all_of_price + of.getTotalPrice().doubleValue();// 总订单价格
								if (sid.equals("self") || sid == "self") {
									of.setOrder_form(1);// 平台自营商品订单
								} else {
									//of.setCommission_amount(BigDecimal.valueOf(commission_amount));// 该订单总体佣金费用
									of.setCommission_amount(BigDecimal.valueOf(commission_amount));// 该订单总体佣金费用
									of.setGoods_vat(BigDecimal.valueOf(goods_vat));
									of.setCommission_vat(BigDecimal.valueOf(commission_vat));
									of.setOrder_form(0);// 商家商品订单
									of.setStore_id(store.getId().toString());
									of.setStore_name(store.getStore_name());
								}
								of.setOrder_type("web");// 设置为PC网页订单
								of.setDelivery_time(delivery_time);
								if (gift_map.size() > 0) {
									of.setGift_infos(Json.toJson(gift_map, JsonFormat.compact()));
									of.setWhether_gift(1);
									whether_gift_in++;
								}
								of.setDelivery_type(0);
								if (CommUtil.null2Int(delivery_type) == 1 && delivery_id != null && !delivery_id.equals("")) {// 自提点信息，使用json管理
									of.setDelivery_type(1);
									DeliveryAddress deliveryAddr = this.deliveryaddrService
											.getObjById(CommUtil.null2Long(delivery_id));
									String service_time = "全天";
									if (deliveryAddr.getDa_service_type() == 1) {
										service_time = deliveryAddr.getDa_begin_time() + "点至" + deliveryAddr.getDa_end_time()
												+ "点";
									}
									params.clear();
									params.put("id", deliveryAddr.getId());
									params.put("da_name", deliveryAddr.getDa_name());
									params.put("da_content", deliveryAddr.getDa_content());
									params.put("da_contact_user", deliveryAddr.getDa_contact_user());
									params.put("da_tel", deliveryAddr.getDa_tel());
									params.put("da_address",
											deliveryAddr.getDa_area().getParent().getParent().getAreaName()
													+ deliveryAddr.getDa_area().getParent().getAreaName()
													+ deliveryAddr.getDa_area().getAreaName() + deliveryAddr.getDa_address());
									params.put("da_service_day",
											this.DeliveryAddressTools.query_service_day(deliveryAddr.getDa_service_day()));
									params.put("da_service_time", service_time);
									of.setDelivery_address_id(deliveryAddr.getId());
									of.setDelivery_info(Json.toJson(params, JsonFormat.compact()));
								}
								if (i == store_ids.length - 1) {
									of.setOrder_main(1);// 同时购买多个商家商品，最后一个订单为主订单，其他的作为子订单，以json信息保存，用在买家中心统一显示大订单，统一付款
									if (whether_gift_in > 0) {
										of.setWhether_gift(1);
									}
									if (child_order_maps.size() > 0) {
										of.setChild_order_detail(Json.toJson(child_order_maps, JsonFormat.compact()));
									}
								}
								boolean flag = this.orderFormService.save(of);
								main_order = of;
								if (i == store_ids.length - 1) {
									mv.addObject("order", of);// 将主订单信息封装到前台视图中
									goods_cart3_map.put("order_id", of.getId());
									goods_cart3_map.put("order_num", of.getOrder_id());
								}
								if (flag) {
									// 如果是多个店铺的订单同时提交，则记录子订单信息到主订单中，用在买家中心统一显示及统一付款
									if (store_ids.length > 1) {
										Map order_map = new HashMap();
										order_map.put("order_id", of.getId());
										order_map.put("order_goods_info", of.getGoods_info());
										child_order_maps.add(order_map);
									}
									for (GoodsCart gc : gc_list) {// 删除已经提交订单的购物车信息
										if (gc.getCart_type() != null && gc.getCart_type().equals("combin")
												&& gc.getCombin_main() == 1) {// 购物车提交订单时如果为组合套装购物车，只提交组合套装主购物车，删除主购物车同时删除该套装中其他购物车
											Map combin_map = new HashMap();
											combin_map.put("combin_mark", gc.getCombin_mark());
											combin_map.put("combin_main", 1);
											List<GoodsCart> suits = this.goodsCartService.query(
													"select obj from GoodsCart obj where obj.combin_mark=:combin_mark and obj.combin_main!=:combin_main",
													combin_map, -1, -1);
											for (GoodsCart suit : suits) {
												gc.getGsps().clear();
												this.goodsCartService.delete(suit.getId());
											}
										}
										for (String gc_id : gc_ids) {
											if (!CommUtil.null2String(gc_id).equals("")
													&& CommUtil.null2Long(gc_id).equals(gc.getId())) {
												gc.getGsps().clear();
												this.goodsCartService.delete(gc.getId());
											}
										}
									}
									OrderFormLog ofl = new OrderFormLog();
									ofl.setAddTime(new Date());
									ofl.setOf(of);
									ofl.setLog_info("提交订单");
									ofl.setLog_user(user);
									this.orderFormLogService.save(ofl);
								}
							}
							// 在循环外，给买家只发送一次短信邮件
							if (main_order.getOrder_form() == 0) {
								this.msgTools.sendEmailCharge(CommUtil.getURL(request), "email_tobuyer_order_submit_ok_notify",
										buyer.getEmail(), null, CommUtil.null2String(main_order.getId()),
										main_order.getStore_id());
								this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_tobuyer_order_submit_ok_notify",
										buyer.getMobile(), null, CommUtil.null2String(main_order.getId()),
										main_order.getStore_id());
							} else {
								this.msgTools.sendEmailFree(CommUtil.getURL(request), "email_tobuyer_order_submit_ok_notify",
										buyer.getEmail(), null, CommUtil.null2String(main_order.getId()));
								this.msgTools.sendSmsFree(CommUtil.getURL(request), "sms_tobuyer_order_submit_ok_notify",
										buyer.getMobile(), null, CommUtil.null2String(main_order.getId()));
							}
							goods_cart3_map.put("all_of_price", CommUtil.formatMoney(all_of_price));
			
						} else {// 验证库存不成功，返回购物车，并给出提示！
							result = new Result(2,"订单中库存为零");
						}
					} else {
						result = new Result(3,"订单信息错误");
					}
				} else {
					result = new Result(4,"订单已经失效");
				}
				}else{
					result = new Result(5,"验证码错误");
				}
			}
		}
		String goods_cart3_temp = Json.toJson(result, JsonFormat.compact());
		response.getWriter().print(goods_cart3_temp);
	}

	/**
	 * 获得商品佣金
	 * 
	 * @param request
	 * @param response
	 */
	private double getOrderCommission(List<GoodsCart> gcs) {
		double commission_price = 0.00;
		for (GoodsCart gc : gcs) {
			commission_price = commission_price + this.getGoodscartCommission(gc);
		}
		return commission_price;
	}

	/** 获得商品佣金
	 * 
	 * @param request
	 * @param response
	 */
	private double getGoodscartCommission(GoodsCart gc) {
		double commission_price = CommUtil.mul(gc.getGoods().getGc().getCommission_rate(),
				CommUtil.mul(gc.getPrice(), gc.getCount()));
		return commission_price;
	}
	
	@SecurityMapping(title = "地址切换", value = "/order_address.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_address.json")
	public void order_address(HttpServletRequest request, HttpServletResponse response,String gcs, String addr_id,
			String store_ids) {
		Map add_map = new HashMap();
		Result result = null;
		String[] gcs_id = gcs.split(",");
		List<GoodsCart> carts = new ArrayList<GoodsCart>();
		List<Map> sms_list = new ArrayList<Map>();
		Object order_goods_price = 0;
		for(String id : gcs_id){
			GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(id));
			if(gc != null){
				carts.add(gc);
			}
		}
		String[] ids = store_ids.split(",");
		List<GoodsCart> gc_list = new ArrayList<GoodsCart>();
		List<GoodsCart> amount_gc_list = new ArrayList<GoodsCart>();
		for (GoodsCart gc : carts) {
			gc_list = new ArrayList<GoodsCart>();
			amount_gc_list = new ArrayList<GoodsCart>();
			Map sms_map = new HashMap();
			for(String id : ids){
				if (id != "self" && !id.equals("self")) {
					if (gc.getGoods().getGoods_type() == 1
							&& gc.getGoods().getGoods_store().getId().equals(CommUtil.null2Long(id))) {

						gc_list.add(gc);
						sms_map.put("store_id", id);
						amount_gc_list.add(gc);
					}
				} else {
					if (gc.getGoods().getGoods_type() == 0) {
						gc_list.add(gc);
					}
				}
				
			}
		double store_goods_price = this.calCartPrice(amount_gc_list, gcs);
		Address addr = this.addressService.getObjById(CommUtil.null2Long(addr_id));
		List<SysMap> sms = this.transportTools.query_cart_trans_goods_cart3(gc_list, CommUtil.null2String(addr.getArea().getId()));
		Object price = 0;
		Object ship_price = 0; 
		for(SysMap obj : sms){
			if(obj.getKey().equals("快递")){
				price = obj.getValue(); 
			}else{
				price = 0;
			}
			ship_price = CommUtil.add(ship_price, price);
		}

		double store_total_price = CommUtil.add(store_goods_price, ship_price);
		order_goods_price = CommUtil.add(order_goods_price, store_total_price);
		sms_map.put("ship_price", ship_price);
		sms_map.put("store_total_price", store_total_price);
		sms_list.add(sms_map);
		add_map.put("store", sms_list);
		}
		add_map.put("order_goods_price", order_goods_price);
		result = new Result(0,"success",add_map);
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
	 * 货到付款
	 * @param request
	 * @param response
	 * @param payType
	 * @param order_id
	 * @param pay_msg
	 * @param pay_session
	 * @return
	 * @throws Exception
	 */
	
	@SecurityMapping(title = "订单货到付款", value = "/order_pay_payafter.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping("/order_pay_payafter.json")
	public void order_pay_payafter(HttpServletRequest request, HttpServletResponse response, String payType,
			String order_id, String pay_msg, String pay_session, String token) throws Exception {
		Result result = null;
		Map order_pay_map = new HashMap();
		Map order_pay_payafter = new HashMap();
		ModelAndView mv = new JModelAndView("success.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
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
				String pay_session1 = CommUtil.null2String(request.getSession(false).getAttribute("pay_session"));
				if (pay_session1.equals(pay_session)) {
					OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
					order.setPay_msg(pay_msg);
					order.setPayTime(new Date());
					order.setPayType("payafter");
					order.setOrder_status(16);
					this.orderFormService.update(order);
					if (order.getOrder_main() == 1 && !CommUtil.null2String(order.getChild_order_detail()).equals("")) {
						List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
						for (Map child_map : maps) {
							OrderForm child_order = this.orderFormService
									.getObjById(CommUtil.null2Long(child_map.get("order_id")));
							child_order.setOrder_status(16);
							child_order.setPayType("payafter");
							child_order.setPayTime(new Date());
							child_order.setPay_msg(pay_msg);
							this.orderFormService.update(child_order);
							// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
							Store store = this.storeService.getObjById(CommUtil.null2Long(child_order.getStore_id()));
							if (child_order.getOrder_form() == 0) {
								this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_toseller_payafter_pay_ok_notify",
										store.getUser().getMobile(), null, CommUtil.null2String(child_order.getId()),
										child_order.getStore_id());
								this.msgTools.sendEmailCharge(CommUtil.getURL(request), "email_toseller_payafter_pay_ok_notify",
										store.getUser().getEmail(), null, CommUtil.null2String(child_order.getId()),
										child_order.getStore_id());
							}
						}
					}
					// 记录支付日志
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setLog_info("提交货到付款申请");
					ofl.setLog_user(user);
					ofl.setOf(order);
					this.orderFormLogService.save(ofl);
					request.getSession(false).removeAttribute("pay_session");
					order_pay_map.put("msg", "货到付款提交成功，等待发货");
				} else {
					order_pay_map.put("msg", "订单已经支付，禁止重复支付");
					result = new Result(1,order_pay_map);
				}
				result = new Result(0,order_pay_map);
			}
		}
		String order_pay_temp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(order_pay_temp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "订单短信发送", value = "/order_account_mobile.json*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/Order_verify.json")
	public void order_mobile(HttpServletRequest request,
			HttpServletResponse response, String type, String mobile, String token)
			throws UnsupportedEncodingException {
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
				if (type.equals("mobile_verify_code")) {
					String ret = "0";
					String code = CommUtil.randomString(4).toUpperCase();
					String content = "尊敬的"
							+ user.getUserName()
							+ "您好，您在提交订单"
							+ this.configService.getSysConfig().getWebsiteName()
							+ "，手机验证码为：" + code + "。["
							+ this.configService.getSysConfig().getTitle() + "]";
					if (this.configService.getSysConfig().isSmsEnbale()) {
						boolean ret1 = this.msgTools.sendSMS(mobile, content);
						if (true) {
							VerifyCode mvc = this.mobileverifycodeService
									.getObjByProperty(null, "mobile", mobile);
							if (mvc == null) {
								mvc = new VerifyCode();
							}
							mvc.setAddTime(new Date());
							mvc.setCode(code);
							mvc.setMobile(mobile);
							this.mobileverifycodeService.update(mvc);
						} else {
							ret = "1";
						}
					} else {
						ret = "2";
					}
					result = new Result(CommUtil.null2Int(ret),"success");
				}

			}
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
	
	@SecurityMapping(title = "手机号码验证", value = "/buyer/account_mobile_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/order_mobile.json")
	public void account_mobile_save(HttpServletRequest request,
			HttpServletResponse response, String mobile_verify_code,
			String mobile, String token) throws Exception {
		Result result = null;
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		WebForm wf = new WebForm();
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
				VerifyCode mvc = this.mobileverifycodeService.getObjByProperty(null,
						"mobile", mobile);
				if (mvc != null && mvc.getCode().equalsIgnoreCase(mobile_verify_code)) {
					this.mobileverifycodeService.delete(mvc.getId());
					// 绑定成功后发送手机短信提醒
					String content = "尊敬的"
							+ user.getUserName()
							+ "您好，您于" + CommUtil.formatLongDate(new Date())
							+ "验证身份成功。["
							+ this.configService.getSysConfig().getTitle() + "]";
					this.msgTools.sendSMS(user.getMobile(), content);
					mv.addObject("url", CommUtil.getURL(request) + "/buyer/account.htm");
					result = new Result(0,"验证身份通过，可下单");
				} else {
					result = new Result(1,"验证码错误，请使用本人手机号");
				}
			}
		}
		response.getWriter().print(Json.toJson(result, JsonFormat.compact()));
	}
	
	/**
	 * 购物车修改规格保存
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/goods_cart1_spec_save.json")
	public void goods_cart1_spec_save(HttpServletRequest request, HttpServletResponse response, String gsp, String id, String color, String token) {
		Result result = null;
		Map json_map = new HashMap();
		int code = 100;// 100修改成功，90库存不足
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
				GoodsCart obj = this.goodsCartService.getObjById(CommUtil.null2Long(id));
				if (obj != null) {
					//int goods_inventory = CommUtil.null2Int(this.generic_default_info(obj.getGoods(), gsp, user).get("count"));// 计算商品库存信息
					//double price = CommUtil.null2Double(this.generic_default_info(obj.getGoods(), gsp, user).get("price"));// 计算商品库存信息
					Map spec = this.metooCartViewTools.generic_default_info_color(obj.getGoods(), gsp, color);
					int goods_inventory = CommUtil.null2Int(spec.get("count"));
					double price = CommUtil.null2Int(spec.get("price"));
					if (goods_inventory == 0) {
						code = 90;
					} else {
						String[] gsp_ids = CommUtil.null2String(gsp).split(",");
						String spec_info = "";
						obj.getGsps().removeAll(obj.getGsps());
						for (String gsp_id : gsp_ids) {
							GoodsSpecProperty spec_property = this.goodsSpecPropertyService
									.getObjById(CommUtil.null2Long(gsp_id));
							if (spec_property != null) {
								obj.getGsps().add(spec_property);
								spec_info = spec_property.getSpec().getName() + "：" + spec_property.getValue() + "<br>"
										+ spec_info;
							}
						}
						obj.setSpec_info(spec_info);
						obj.setPrice(BigDecimal.valueOf(price));
						this.goodsCartService.update(obj);
					}
				} else {
					code = -100;
				}
				result = new Result(0,"success");
			}
		}
		try {
			json_map.put("code", code);
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			writer = response.getWriter();
			writer.print(Json.toJson(result, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * 根据购物车查询购物车规格
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/goods_cart1_spec.json")
	public void goods_cart1_spec(HttpServletRequest request, HttpServletResponse response, String cart_id) {
		ModelAndView mv = new JModelAndView("goods_cart1_spec.html", configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Result result = null;
		GoodsCart cart = this.goodsCartService.getObjById(CommUtil.null2Long(cart_id));
		if(cart == null){
			result = new Result(1,"The goods has been deleted");
		}else{
			List<Map> spec_list = new ArrayList<Map>();
			Map spec_map = this.metooGoodsViewTools.generic_spec(CommUtil.null2String(cart.getGoods().getId()));
			spec_map.put("color", this.metooGoodsViewTools.color(CommUtil.null2String(cart.getGoods().getId())));
			spec_list.add(spec_map);
			if(spec_list.isEmpty()){
				result = new Result(1,"Specification is empty");
			}else{
				result = new Result(0,"success",spec_list);
			}
		}
		try {
			response.getWriter().print(Json.toJson(result, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
