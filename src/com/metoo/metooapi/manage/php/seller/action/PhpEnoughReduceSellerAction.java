package com.metoo.metooapi.manage.php.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.metoo.buyer.domain.Result;
import com.metoo.core.annotation.SecurityMapping;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.WebForm;
import com.metoo.foundation.domain.EnoughReduce;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.IEnoughReduceService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserService;

@Controller
public class PhpEnoughReduceSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired 
	private IUserService userService;
	@Autowired
	private IEnoughReduceService enoughreduceService;
	@Autowired
	private IGoodsService goodsService;

	/**
	 * @description php满就送保存
	 * @param request
	 * @param response
	 * @param id满就送id
	 * @param currentPage
	 * @param uid用户id
	 * @param jsonmap满就送金额json
	 */
	@RequestMapping("/php/seller/enoughSave.json")
	public void save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String uid, String jsonmap){
		Result result = null;
		Map map = new HashMap();
		String msg = "";
		Store store = null;
		if(uid == null && uid.equals("")){
			result = new Result(-100, "User information error");
		}else{
			User user = this.userService.getObjById(CommUtil.null2Long(uid));
			user = user.getParent() == null ? user : user.getParent();
			 store = user.getStore();
			Map params = new HashMap();
			params.put("sid", store.getId().toString());
			List<EnoughReduce> ers = this.enoughreduceService.query("select obj from EnoughReduce obj where obj.store_id=:sid and (obj.erstatus=10 or obj.erstatus=5)", params, -1, -1);
			for(EnoughReduce enoughReduce : ers){
				if(enoughReduce.getErend_time().before(new Date())){
					enoughReduce.setErstatus(20);
				}
				this.enoughreduceService.update(enoughReduce);
			}
			ers = this.enoughreduceService
					.query("select obj from EnoughReduce obj where obj.store_id=:sid and (obj.erstatus=10 or obj.erstatus=5)",
							params, -1, -1);
			if (ers.size() > this.configService.getSysConfig()
					.getEnoughreduce_max_count()) {
				map.put("op_title", "您当前正在审核或进行的满就减超过了规定的最大值");
				msg = "您当前正在审核或进行的满就减超过了规定的最大值";
				result = new Result(1, msg);
			}else{
				WebForm wf = new WebForm();
				EnoughReduce enoughreduce = null;
				if (CommUtil.null2String(id).equals("")) {
					enoughreduce = wf.toPo(request, EnoughReduce.class);
					enoughreduce.setAddTime(new Date());
					enoughreduce.setEr_type(1);
				} else {
					EnoughReduce obj = this.enoughreduceService.getObjById(Long
							.parseLong(id));
					if (obj.getErstatus() > 0) {
						map.put("op_title", "该活动不可编辑");// 审核状态 默认为0待审核 10为 审核通过 -10为审核未通过 20为已结束。5为提交审核，此时商家不能再修改
					}
					enoughreduce = (EnoughReduce) wf.toPo(request, obj);
				}
				
				enoughreduce.setEr_json(jsonmap);
				String ertag = "";
				TreeMap mapType = JSON.parseObject(jsonmap,TreeMap.class);  
				Iterator it = mapType.keySet().iterator();
				while(it.hasNext()){
 					 double key = CommUtil.null2Double(it.next());
 					 double value =  CommUtil.null2Double(mapType.get("key"));
					ertag = "Enoughreduce" + key + "subtract" + value + ",";
				}
				ertag = ertag.substring(0, ertag.length() - 1);
				enoughreduce.setErtag(ertag);
				enoughreduce.setErstatus(0);
				enoughreduce.setEr_type(1);
				enoughreduce.setStore_id("" + store.getId());
				enoughreduce.setStore_name(store.getStore_name());
				enoughreduce.setErgoods_ids_json("[]");
				if (id.equals("")) {
					this.enoughreduceService.save(enoughreduce);
					msg = "保存满就减活动成功";
					result = new Result(0, msg);
				} else {
					this.enoughreduceService.update(enoughreduce);
					msg = "保存满就减活动成功";
					result = new Result(0, msg);
				}
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
	 * @Description 满就送删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @param uid 用户id
	 */
	@RequestMapping("/php/seller/delete.json")
	public void enoughreduce_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage, String uid) {
		Result result = null;
		for (String id : mulitId.split(",")) {
			if (!id.equals("")) {
				EnoughReduce enoughreduce = this.enoughreduceService
						.getObjById(Long.parseLong(id));
				User user = this.userService.getObjById(CommUtil.null2Long(uid));
				user = user.getParent() == null ? user : user.getParent();
				Store store = user.getStore();
				if (enoughreduce.getStore_id().equals(store.getId().toString())) {
					String goods_json = enoughreduce.getErgoods_ids_json();
					if (goods_json != null && !goods_json.equals("")) {
						List<String> goods_id_list = (List) Json
								.fromJson(goods_json);
						for (String goods_id : goods_id_list) {
							Goods ergood = this.goodsService
									.getObjById(CommUtil.null2Long(goods_id));
							if (ergood.getOrder_enough_reduce_id().equals(id)) {
								ergood.setEnough_reduce(0);
								ergood.setOrder_enough_reduce_id("");
								this.goodsService.update(ergood);
							}
						}
					}
					this.enoughreduceService.delete(Long.parseLong(id));
				}
			}
		}
		result = new Result(0, "success");
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
