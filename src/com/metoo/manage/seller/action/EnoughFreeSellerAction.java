package com.metoo.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
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
import org.springframework.web.servlet.ModelAndView;

import com.metoo.buyer.domain.Result;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.WebForm;
import com.metoo.foundation.domain.EnoughFree;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.query.EnoughFreeQueryObject;
import com.metoo.foundation.service.IEnoughFreeService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 *
 * @Title EnoughFreeSellerAction.java
 * 
 * Description: 卖家满包邮控制器
 * 
 * @author Administrator
 *
 */
@Controller
public class EnoughFreeSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IEnoughFreeService enoughFreeService;
	@Autowired
	private IGoodsService goodsService;
	
	/**
	 * 
	 * 满包邮列表
	 * @param request
	 * @param response
	 * @param currentPage 
	 * @param title
	 * @param begin_time
	 * @param end_time
	 * @param status
	 * @param uid
	 */
	@RequestMapping("/php/seller/enoughFree.json")
	public void get(HttpServletRequest request,
			HttpServletResponse response, String currentPage, 
			String title, String begin_time, String end_time, String status, String uid){
		Result result = null;
		Map map = new HashMap();
		if(uid != null && !"".equals(uid)){
			User user = this.userService.getObjById(CommUtil.null2Long(uid));
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			System.out.println(store.getStore_name());
			ModelAndView mv = new ModelAndView();
			EnoughFreeQueryObject qo = new EnoughFreeQueryObject(currentPage,
					mv,"addTime", "desc");
			qo.addQuery("obj.store_id", new SysMap("store_id", store.getId().toString()), "=");
			if(title != null && !"".equals(title)){
				qo.addQuery("obj.eftitle", new SysMap("eftitle", "%" + title + "%"), "like");
			}
			if (begin_time != null && !begin_time.equals("")
					&& end_time != null && !end_time.equals("")) {
				qo.addQuery("DATE_FORMAT(obj.efbegin_time,'%Y-%m-%d')", new SysMap(
						"efbegin_time", begin_time), ">=");
				qo.addQuery("DATE_FORMAT(obj.efend_time,'%Y-%m-%d')", new SysMap(
						"efend_time", end_time), "<=");
			}
			if (status != null && !"".equals(status)) {
				qo.addQuery("obj.efstatus",
						new SysMap("efstatus", CommUtil.null2Int(status)), "=");
			}
			IPageList pList = this.enoughFreeService.list(qo);
			List<EnoughFree> enoughFrees = pList.getResult();
			JSONArray json = JSONArray.fromObject(pList.getResult());
			/*List<Map> efList = new ArrayList<Map>();
			for(EnoughFree ef : enoughFrees){
				Map efMap = new HashMap();
				efMap.put("title", ef.getEftitle());
				efMap.put("begin_time", ef.getEfbegin_time());
				efMap.put("end_time", ef.getEfend_time());
				efMap.put("content", ef.getEfcontent());
				efMap.put("tag", ef.getEftag());
				efList.add(efMap);
			}*/
			map.put("ecoughRess", json);
			//map.put("enoughFress", efList);
			map.put("currentpage", pList.getCurrentPage());
			map.put("pageSize", pList.getPageSize());
			map.put("begin_time", begin_time);
			map.put("end_time", end_time);
			result = new Result(0,"success",map);
		}else{
			result = new Result(-100, "User information error");
		}
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		try {
			
			JSONObject obj = new JSONObject().fromObject(result);
			response.getWriter().print(obj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * EnoughFree新增满包邮
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param uid
	 * @param price
	 */
	@RequestMapping("/php/seller/enoughFree_save.json")
	public void POST(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String uid, String price){
		Result result = null;
		Map map = new HashMap();
		String msg = "";
		User user = this.userService.getObjById(CommUtil.null2Long(uid));
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map params = new HashMap();
		params.put("sid", store.getId().toString());
		List<EnoughFree> efoughFrees = this.enoughFreeService.query("select obj from EnoughFree obj where obj.store_id=:sid and (obj.efstatus=10 or obj.efstatus=5)", params, -1, -1);
		for(EnoughFree ef : efoughFrees){
			if(ef.getEfend_time().before(new Date())){
				ef.setDeleteStatus(20);
			}
			this.enoughFreeService.update(ef);
		}
		efoughFrees = this.enoughFreeService.query("select obj from EnoughFree obj where obj.store_id=:sid and (obj.efstatus=10 or obj.efstatus=5)", params, -1, -1);
		if(efoughFrees.size() > 100){
			msg = "您当前正在审核或正在进行的满包邮活动超过最大限制";
		}else{
			WebForm wf = new WebForm();
			EnoughFree enoughFree = null;
			if(CommUtil.null2String(id).equals("")){
				enoughFree = wf.toPo(request, EnoughFree.class);
				enoughFree.setAddTime(new Date());
				enoughFree.setEf_type(1);
			}else{
				EnoughFree obj = this.enoughFreeService.getObjById(CommUtil.null2Long(id));
				enoughFree = (EnoughFree) wf.toPo(request, obj);
			}
			enoughFree.setEfstatus(0);
			enoughFree.setEf_type(1);
			enoughFree.setStore_id("" +store.getId());
			enoughFree.setStore_name(store.getStore_name());
			enoughFree.setCondition_amount(CommUtil.null2BigDecimal(price));
			if(id.equals("")){
				this.enoughFreeService.save(enoughFree);
				result = new Result(0, "save successfully");
			}else{
				this.enoughFreeService.update(enoughFree);
				result = new Result(0, "Edit success");
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
	/**
	 * 满包邮删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param uid
	 * @param currentPage
	 */
	@RequestMapping("/php/seller/enoughFree_delete.json")
	public void delete(HttpServletRequest request, HttpServletResponse response, String mulitId, String uid, String currentPage){
		Result result = null;
		for(String id : mulitId.split(",")){
			if(!id.equals("")){
				EnoughFree enoughFree = this.enoughFreeService.getObjById(CommUtil.null2Long(id));
				User user = this.userService.getObjById(CommUtil.null2Long(uid));
				user = user.getParent() == null ? user : user.getParent();
				Store store = user.getStore();
				if(enoughFree.getStore_id().equals(store.getId().toString())){
					String ids = enoughFree.getErgoods_ids_json();
					if(ids != null && !ids.equals("")){
						List<String> goods_ids = (List) Json
								.fromJson(ids);
						for(String goods_id : goods_ids){
							Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
							if(goods.getOrder_enough_free_id().equals(id)){
								goods.setEnough_free(0);
								goods.setOrder_enough_free_id("");
								this.goodsService.update(goods);
							}
						}
					}
					this.enoughFreeService.delete(CommUtil.null2Long(id));
				}
			}
		}
		result = new Result(0,"success");
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
	 * 商品ajax更新
	 * @param request
	 * @param response
	 */
	@RequestMapping("/php/seller/enoughFree_goods_ajax.json")
	public void enoughFreeGoods(HttpServletRequest request,
			HttpServletResponse response, String id, String eid, String uid){
		Result result = null;
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		EnoughFree enoughFree = this.enoughFreeService.getObjById(CommUtil.null2Long(eid));
		User user = this.userService.getObjById(CommUtil.null2Long(uid));
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if(goods.getGoods_store().getId() == store.getId()){
			int flag = goods.getEnough_free();
			String ids = enoughFree.getErgoods_ids_json();
			List<String> goods_id_list = new ArrayList<String>();
			if(ids != null && !id.equals("")){
				goods_id_list = (List)Json.fromJson(ids);
			}
			if(flag == 0){
				if (goods.getCombin_status() == 0 && goods.getGroup_buy() == 0
						&& goods.getGoods_type() == 1
						&& goods.getActivity_status() == 0
						&& goods.getF_sale_type() == 0
						&& goods.getAdvance_sale_type() == 0
						&& goods.getOrder_enough_give_status() == 0
						&& goods.getEnough_reduce() == 0) {
					goods.setEnough_free(1);
					goods.setOrder_enough_free_id(eid);
					goods_id_list.add(id);
					enoughFree.setErgoods_ids_json(Json.toJson(goods_id_list,
							JsonFormat.compact()));
				}
			}else{
				goods.setEnough_free(1);
				goods.setOrder_enough_free_id("");
				if(goods_id_list.contains(id)){
					goods_id_list.remove(id);
				}
				enoughFree.setErgoods_ids_json(Json.toJson(goods_id_list,
						JsonFormat.compact()));
			}
			this.goodsService.update(goods);
			this.enoughFreeService.update(enoughFree);
			result = new Result(0, "设置成功");
		}else{
			result = new Result(-1, "商品信息错误");
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
	 * 满包邮批量更新
	 * @param request
	 * @param response
	 * @param uid
	 * @param mulitid
	 * @param eid
	 * @param type
	 */
	@RequestMapping("/php/seller/enoughFree_goods_batch_ajax.json")
	public void mulitGoods(HttpServletRequest request,
			HttpServletResponse response, String uid, String mulitid, String eid, String type){
		Result result = null;
		EnoughFree enoughFree = this.enoughFreeService.getObjById(CommUtil.null2Long(eid));
		User user = this.userService.getObjById(CommUtil.null2Long(uid));
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		String json = enoughFree.getErgoods_ids_json();
		List<String> goods_id_list = new ArrayList<String>();
		if(json != null && !json.equals("")){
			goods_id_list = (List)Json.fromJson(json);
		}
		String[] ids = mulitid.split(",");
		for(String id : ids){
			if(!id.equals("")){
				Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
				if(goods.getGoods_store().getId() == store.getId()){
					if(goods.getEnough_free() == 0 || goods.getOrder_enough_free_id().equals(eid)){
						if(type.equals("add")){
							if(goods.getCombin_status() == 0
									&& goods.getGroup_buy() == 0
									&& goods.getGoods_type() == 1
									&& goods.getActivity_status() == 0
									&& goods.getF_sale_type() == 0
									&& goods.getAdvance_sale_type() == 0
									&& goods.getOrder_enough_give_status() == 0
									&& goods.getEnough_reduce() == 0) {
								goods_id_list.add(id);
								goods.setEnough_free(1);
								goods.setOrder_enough_free_id(eid);
							}	
						}else{
							if(goods_id_list.contains(id)){
								goods_id_list.remove(id);
							}
							goods.setEnough_free(0);
							goods.setOrder_enough_free_id("");
						}
					}
					this.goodsService.update(goods);
				}
			}
		}
		enoughFree.setErgoods_ids_json(Json.toJson(goods_id_list, JsonFormat.compact()));
		this.enoughFreeService.update(enoughFree);
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
	
	@RequestMapping("/php/seller/emoughFree_apply.json")
	public void apply(HttpServletRequest rquest,
			HttpServletResponse response, String id, String uid){
		Result result = null;
		if(id != null && !id.equals("")){
			EnoughFree enoughFree = this.enoughFreeService.getObjById(CommUtil.null2Long(id));
			User user = this.userService.getObjById(CommUtil.null2Long(uid));
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			if(enoughFree != null && enoughFree.getStore_id().equals("" +store.getId())){
				Map params = new HashMap();
				params.put("sid", store.getId().toString());
				List<EnoughFree> enoughFrees = this.enoughFreeService.query("select obj from obj where EnoughFree obj where obj.store_id=:sid and(obj.efstatus=10 or obj.efstatus=5)", params, -1, -1);
				if(enoughFrees.size() < 100){
					if(enoughFree.getEfstatus() == 0 || enoughFree.getEfstatus() == -10){
						enoughFree.setEfstatus(5);
						enoughFree.setFailed_reason("");
						this.enoughFreeService.update(enoughFree);
						result = new Result(0, "submit successfully");
					}
				}else{
					result = new Result(-1, "submit failure,You do not have this activity");
				}
			}else{
				result = new Result(-1, "submit failure,You do not have this activity");
			}
		}else{
			result = new Result(-1, "submit failure,You do not have this activity");
		}
	}
}
