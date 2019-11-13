package com.metoo.metooapi.manage.php.seller.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import com.metoo.buyer.domain.SOAPUtils;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.ddu.pojo.DduTaskRequest;
import com.metoo.foundation.domain.Area;
import com.metoo.foundation.domain.ExpressCompanyCommon;
import com.metoo.foundation.domain.OrderForm;
import com.metoo.foundation.domain.OrderFormLog;
import com.metoo.foundation.domain.ShipAddress;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.IExpressCompanyCommonService;
import com.metoo.foundation.service.IOrderFormLogService;
import com.metoo.foundation.service.IOrderFormService;
import com.metoo.foundation.service.IShipAddressService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.IUserService;
import com.metoo.manage.admin.tools.OrderFormTools;
import com.metoo.msg.MsgTools;
import com.metoo.pay.alipay.config.AlipayConfig;
import com.metoo.pay.alipay.util.AlipaySubmit;

@Controller
public class PhpOrderSellerAction {

	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IExpressCompanyCommonService expressCompanyCommonService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IShipAddressService shipAddressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private OrderFormTools orderFormTools;
	
	/**
	 * 物流单号由手动输入改为从ddu获取
	 * @param request
	 * @param response
	 * @param id
	 * @param state_info
	 * @param ecc_id
	 * @param sa_id
	 * @param uid
	 * @throws Exception
	 */
	@RequestMapping("/php/order_shipping.json")
	public void orderShipping(HttpServletRequest request,
			HttpServletResponse response,String id, String state_info,
			String ecc_id, String sa_id, String uid) throws Exception{
		Result result = null;
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		ExpressCompanyCommon ecc = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(ecc_id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(CommUtil.null2Long(uid));
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			obj.setOrder_status(30);
			//obj.setShipCode(shipCode);物流单
			obj.setShipTime(new Date());
			if (ecc != null) {
				Map json_map = new HashMap();
				json_map.put("express_company_id", ecc.getId());
				json_map.put("express_company_name", ecc.getEcc_name());
				json_map.put("express_company_mark", ecc.getEcc_code());
				json_map.put("express_company_type", ecc.getEcc_ec_type());
				obj.setExpress_info(Json.toJson(json_map));
			}
			String[] order_seller_intros = request
					.getParameterValues("order_seller_intro");
			String[] goods_ids = request.getParameterValues("goods_id");
			String[] goods_names = request.getParameterValues("goods_name");
			String[] goods_counts = request.getParameterValues("goods_count");
			ShipAddress shipAddress = this.shipAddressService.getObjById(CommUtil
					.null2Long(sa_id));
			if (shipAddress != null) {
				obj.setShip_addr_id(shipAddress.getId());
				Area area = this.areaService.getObjById(shipAddress.getSa_area_id());
				obj.setShip_addr(area.getParent().getParent().getAreaName()
						+ area.getParent().getAreaName() + area.getAreaName()
						+ shipAddress.getSa_addr());
			}
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("确认发货");
			ofl.setState_info(state_info);
			ofl.setLog_user(user);
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
			User buyer = this.userService.getObjById(CommUtil.null2Long(obj
					.getUser_id()));
			//DDU
			Map express_map = Json.fromJson(Map.class,
					obj.getExpress_info());
			Long area_id = shipAddress.getSa_area_id();
			List<Map> goods_map = this.orderFormTools.queryGoodsInfo(
					obj.getGoods_info());
			Area area = areaService.getObjById(CommUtil.null2Long(area_id));
			DduTaskRequest ddutaskRequest = new DduTaskRequest();
		    ddutaskRequest.setBatchNumber(obj.getOrder_id());
	        ddutaskRequest.setFromCompany(shipAddress.getSa_company());
	        ddutaskRequest.setFromAddress(area.getAreaName());
	        ddutaskRequest.setFromLocation(shipAddress.getSa_addr());
	        ddutaskRequest.setFromCountry(area.getParent().getParent().getAreaName());
	        ddutaskRequest.setFromCperson(shipAddress.getSa_user_name());
	        ddutaskRequest.setFromContactno(shipAddress.getSa_telephone());
	        //[面单不显示]
	        //ddutaskRequest.setFromMobileno(sa.getSa_telephone());
	        //ddutaskRequest.setToCompany();
	        ddutaskRequest.setToAddress(obj.getReceiver_area());
	        ddutaskRequest.setToLocation(obj.getReceiver_area_info());
	        //ddutaskRequest.setToCountry(sa.getSa_addr());
	        ddutaskRequest.setToCountry(obj.getReceiver_area());
	        ddutaskRequest.setToCperson(obj.getReceiver_Name());
	        ddutaskRequest.setToContactno(obj.getReceiver_telephone());
	        ddutaskRequest.setToMobileno(obj.getReceiver_telephone());
	        ddutaskRequest.setReferenceNumber(obj.getReceiver_mobile());
	        ddutaskRequest.setCompanyCode(CommUtil.null2String(express_map.get("express_company_mark")));
	        int pieces;
	        int weight;
	        int weightnum=0;
	        int piecesnum=0;
	        for(Map goods_maps:goods_map){
	        	pieces = CommUtil.null2Int(goods_maps.get("goods_count"));
	        	piecesnum += pieces;
	        	weight = CommUtil.null2Int(goods_maps.get("weight"));
	        }
	        ddutaskRequest.setWeight(weightnum);
	        ddutaskRequest.setPieces(piecesnum);
	        //ddutaskRequest.setPackageType("Document");
	        ddutaskRequest.setCurrencyCode("AED");
	        ddutaskRequest.setNcndAmount(obj.getTotalPrice());
	        ddutaskRequest.setItemDescription("DESCRIPTION");
	        ddutaskRequest.setSpecialInstruction("SPECIAL");
	        StringBuffer stringBuffer = SOAPUtils.webServiceTow(ddutaskRequest);
	        String xmlResult = stringBuffer.toString().replace("<", "<");
	        String AWBNumber = SOAPUtils.getXmlMessageByName(xmlResult, "AWBNumber");
	        //报文返回状态码，0表示正常，3表示错误
	        String responseCode = SOAPUtils.getXmlMessageByName(xmlResult, "responseCode");
	        if("1".equals(responseCode)){
	        	obj.setShipCode(AWBNumber);
	        	this.orderFormService.update(obj);
	        }
		    System.out.println(AWBNumber);
			Map map = new HashMap();
			map.put("buyer_id", buyer.getId().toString());
			map.put("seller_id", store.getUser().getId().toString());
			map.put("order_id", obj.getId());
			String json = Json.toJson(map);
			if (obj.getOrder_form() == 0) {
				this.msgTools.sendEmailCharge(CommUtil.getURL(request),
						"email_tobuyer_order_ship_notify", buyer.getEmail(),
						json, null, obj.getStore_id());
				this.msgTools.sendSmsCharge(CommUtil.getURL(request),
						"sms_tobuyer_order_ship_notify", buyer.getMobile(),
						json, null, obj.getStore_id());
			} else {
				this.msgTools.sendEmailFree(CommUtil.getURL(request),
						"email_tobuyer_order_ship_notify", buyer.getEmail(),
						json, null);
				this.msgTools.sendSmsFree(CommUtil.getURL(request),
						"sms_tobuyer_order_ship_notify", buyer.getMobile(),
						json, null);
			}
			result = new Result(0,"success");
		}else{
			result = new Result(-100,"User information error");
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(Json.toJson(result, JsonFormat.compact()));
	}
	
	@RequestMapping("/php/cancel.json")
	public void cancel(HttpServletRequest request,
			HttpServletResponse response,String id, String uid,String state_info){
		Result result = null;
		User user = this.userService.getObjById(CommUtil.null2Long(uid));
		user = user.getParent() == null ? user : user.getParent();
		if(user != null){
			OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
			if(obj != null){
				Store store = this.storeService.getObjById(CommUtil.null2Long(obj.getStore_id()));
				if(user.getStore().getId().equals(store.getId())){
					obj.setOrder_status(0);
					this.orderFormService.update(obj);
					OrderFormLog objLog = new OrderFormLog();
					objLog.setAddTime(new Date());
					objLog.setLog_info("取消订单");
					objLog.setLog_user(user);
					objLog.setOf(obj);
					objLog.setState_info(state_info);
					this.orderFormLogService.save(objLog);
					result = new Result(0, "Order cancelled successfully");
				}else{
					result = new Result(1, "You do not have this order");
				}
			}else{
				result = new Result(1, "You do not have this order");
			}
		}else{
			result = new Result(-100, "User information error");
		}
		try {

			response.setContentType("text/html;charset=UTF-8");
			request.setCharacterEncoding("UTF-8");
			response.getWriter().print(Json.toJson(result, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
