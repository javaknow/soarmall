package com.metoo.metooapi.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.UniqueConstraint;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.core.ApplicationContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.metoo.buyer.domain.Result;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.Accessory;
import com.metoo.foundation.domain.Address;
import com.metoo.foundation.domain.CGoods;
import com.metoo.foundation.domain.Evaluate;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.StoreStat;
import com.metoo.foundation.domain.SystemTip;
import com.metoo.foundation.domain.Transport;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.query.GoodsQueryObject;
import com.metoo.foundation.service.IAccessoryService;
import com.metoo.foundation.service.IEvaluateService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IStoreStatService;
import com.metoo.foundation.service.ISystemTipService;
import com.metoo.foundation.service.ITransportService;
import com.metoo.foundation.service.IUserService;
import com.metoo.metooapi.manage.buyer.action.MetooAddressBuyerAction;

@Controller
public class MetooTest {
	@Autowired 
	private MetooAddressBuyerAction add;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ITransportService transportService;
	@Autowired 
	private IUserService userService;
	@Autowired 
	private IEvaluateService evaluateService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IStoreStatService storeStatService;
	@Autowired
	private ISystemTipService systemTipService;
	
	
	/**
	 * 9-26
	 * @param request
	 * @param response
	 */
	@RequestMapping("/goods_tranFee.json")
	public void goods_tranFee(HttpServletRequest request, HttpServletResponse response){
		Map params = new HashMap();
		params.put("goods_type", 1);
		List<Goods> goodses = this.goodsService.query("select obj from Goods obj where obj.goods_type=:goods_type and transport_id=null", params, -1, -1);
		List<Transport> transPort = this.transportService.query("select obj from Transport obj", null, -1, -1);	
		for(Goods obj : goodses){
			System.err.println(obj.getId());
				if(obj.getGoods_transfee() == 0){ //商品运费承担方式，0为买家承担，1为卖家承担
					if(obj.getGoods_store().getGrade().getGradeName().equals("China")){
						for(Transport tobj : transPort){
							if(tobj.getTrans_name().equals("China")){
								System.out.println(tobj.getTrans_name());
								obj.setTransport(tobj);
								this.goodsService.update(obj);
							}
						}
					}else{
						if(obj.getGoods_store().getGrade().getGradeName().equals("Dubai")){	
							for(Transport tobj : transPort){
								if(tobj.getTrans_name().equals("Dubai")){
									System.out.println(tobj.getTrans_name());
									obj.setTransport(tobj);
									this.goodsService.update(obj);
								}
							}
						}
					}
				}
		}
	}
	
	@RequestMapping("/goods_map.json")
	public void goods_map(HttpServletRequest request, HttpServletResponse response){
		Map params = new HashMap();
		params.put("goods_type", 1);
		
		List<Goods> goodses = this.goodsService.query("select obj from Goods obj where obj.goods_type=:goods_type and transport_id=null", params, -1, -1);
		List<Map> list = new ArrayList<Map>();
		for(Goods obj : goodses){
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("name", obj.getGoods_name());
			list.add(map);
		}
		try {
			response.getWriter().print(list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param id 商品id
	 * @param userName 用户名
	 */
	@RequestMapping("/evaluate.json")
	public void evalue(HttpServletRequest request, HttpServletResponse response, String id,String userName){
		Result result = null;
		Map params = new HashMap();
		params.put("email", userName);
		params.put("mobile", userName);
		params.put("userName", userName);
		List<User> users = this.userService
				.query("select obj from User obj where obj.userName =:userName or obj.email=:email or obj.mobile=:mobile",
						params, -1, -1);
		if(users.isEmpty()){
			result = new Result(1,"没有该用户");
		}else{
			User user = users.get(0);
			Evaluate eva = new Evaluate();
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			List<Evaluate> evaluates = user.getUser_evaluate();
			boolean flag = false;
			for(Evaluate evaluate : evaluates){
				if(evaluate.getEvaluate_goods().getId() == goods.getId()){
					flag = true;
				}
			}	
			if(!flag){
				eva.setAddTime(new Date());
				eva.setEvaluate_goods(goods);
				goods.setEvaluate_count(goods.getEvaluate_count() + 1);
				eva.setGoods_price(CommUtil.null2String(goods.getGoods_current_price()));
				eva.setEvaluate_info(request.getParameter("evaluate_info"));
				eva.setEvaluate_photos(request.getParameter("evaluate_photos"));
				eva.setEvaluate_buyer_val(CommUtil
						.null2Int(eva_rate(request.getParameter("evaluate_buyer_val"))));
				eva.setDescription_evaluate(BigDecimal.valueOf(CommUtil
						.null2Double(eva_rate(request.getParameter("description_evaluate")))));
				eva.setService_evaluate(BigDecimal.valueOf(CommUtil
						.null2Double(eva_rate(request.getParameter("service_evaluate")))));
				eva.setShip_evaluate(BigDecimal.valueOf(
						CommUtil.null2Double(eva_rate(request.getParameter("ship_evaluate")))));
				eva.setEvaluate_type("goods");
				eva.setEvaluate_user(user);
				
				eva.setReply_status(0);
				this.evaluateService.save(eva);
				String im_str = request.getParameter("evaluate_photos_" + goods.getId());
				if (im_str != null && !im_str.equals("") && im_str.length() > 0) {
					for (String str : im_str.split(",")) {
						if (str != null && !str.equals("")) {
							Accessory image = this.accessoryService.getObjById(CommUtil.null2Long(str));
							if (image.getInfo().equals("eva_temp")) {
								image.setInfo("eva_img");
								this.accessoryService.save(image);
							}
						}
					}
				}
				result = new Result(0,"success");
			}else{
				result = new Result(2,"商品不可重复评价");
			}
		}
		String evatemp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(evatemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private int eva_rate(String rate) {
		int score = 0;
		if (rate.equals("a")) {
			score = 1;
		} else if (rate.equals("b")) {
			score = 2;
		} else if (rate.equals("c")) {
			score = 3;
		} else if (rate.equals("d")) {
			score = 4;
		} else if (rate.equals("e")) {
			score = 5;
		}
		return score;
	}
	
	
	  @Test
	  public void traverse(HttpServletRequest request, HttpServletResponse resposne){
		  Map<String, String> map=new HashMap<String, String>();
		  map.put("张三1", "男");
		  map.put("张三2", "男");
		  map.put("张三3", "男");
		  map.put("张三4", "男");
		  map.put("张三5", "男");
		  //第一种遍历map的方法，通过加强for循环map.keySet()，然后通过键key获取到value值
		  for(String s:map.keySet()){
		   System.out.println("key : "+s+" value : "+map.get(s));
		  }
		  System.out.println("====================================");
		  //第二种只遍历键或者值，通过加强for循环
		  for(String s1:map.keySet()){//遍历map的键
		   System.out.println("键key ："+s1);
		  }
		  for(String s2:map.values()){//遍历map的值
		   System.out.println("值value ："+s2);
		  }
		  System.out.println("===================================="); 
		  //第三种方式Map.Entry<String, String>的加强for循环遍历输出键key和值value
		  for(Map.Entry<String, String> entry : map.entrySet()){
		   System.out.println("键 key ："+entry.getKey()+" 值value ："+entry.getValue());
		  }
		  System.out.println("====================================");
		  //第四种Iterator遍历获取，然后获取到Map.Entry<String, String>，再得到getKey()和getValue()
		  Iterator<Map.Entry<String, String>> it=map.entrySet().iterator();
		  while(it.hasNext()){
		   Map.Entry<String, String> entry=it.next();
		   System.out.println("键key ："+entry.getKey()+" value ："+entry.getValue());
		  }
		  System.out.println("====================================");
		 }
	  
	  @RequestMapping("/return.json")
	  public void requestparam4(HttpServletRequest request, HttpServletResponse response,String token) {
		  	Map params = new HashMap();
			params.put("app_login_token", token);
			List<User> users =  this.userService.query("select obj from User obj where obj.app_login_token=:app_login_token order by obj.addTime desc",
					params, -1, -1);
		  Map<String, String> map=new HashMap<String, String>();
		  map.put("张三1", "男");
		  map.put("张三2", "男");
		  map.put("张三3", "男");
		  map.put("张三4", "男");
		  map.put("张三5", "男");
		  
		  try { 
			response.getWriter().print(JSON.toJSONString(map));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
		
	  @Test
	  public void sel(){
		  System.out.println("select");
	  }
	  
	  @RequestMapping("/return2.json")
	  @ResponseBody
	  public void returnJson2(HttpServletRequest request, HttpServletResponse response){
		 /* Map<String, String> map=new HashMap<String, String>();
		  map.put("张三1", "男");
		  map.put("张三2", "男");
		  map.put("张三3", "男");
		  map.put("张三4", "女");
		  map.put("张三4", "男");
		  map.put("张三5", "男");*/
		  List<Integer> list = new ArrayList<Integer>();
		  list.add(1);
		  list.add(2);
		  list.add(2);
		  list.add(3);
		  list.add(4);
		  list.add(5);
		  
		  
		  
		  try {
			//response.getWriter().print(Json.toJson(list, JsonFormat.compact()));
			response.getWriter().print(list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	  
	  @RequestMapping("/global_changes.json")
	  public void goods_cgoods(HttpServletRequest request, HttpServletResponse response){
		  Map params = new HashMap();
		  params.put("goods_type", 1);
		  List<Goods> goods_list = this.goodsService.query("select obj from Goods obj where obj.goods_type=:goods_type", params, -1, -1);
		  int num = 0;
		  for(Goods goods : goods_list){
			  if(goods.getCgoods().size() != 0){
				  List<CGoods> cgoods_list = goods.getCgoods();
				  List<Integer> inventorys = new ArrayList<Integer>(); 
				  for(CGoods cgoods : cgoods_list){
					  inventorys.add(cgoods.getGoods_inventory());
				  }
				  goods.setGoods_inventory(inventorys.size() == 0 ? 0 : CommUtil.null2Int(Collections.min(inventorys)));
				  this.goodsService.save(goods);
			  }
		  }
		  
		  try {
			response.getWriter().print(num);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	  }
/*	  
	  @RequestMapping("/cookie.json")
	  public void cookie(HttpServletRequest request, HttpServletResponse response){
		  String requestUrl = request.getRequestURL().toString();//得到请求的URL地址
	       String requestUri = request.getRequestURI();//得到请求的资源
	       String queryString = request.getQueryString();//得到请求的URL地址中附带的参数
	       String remoteAddr = request.getRemoteAddr();//得到来访者的IP地址
	       String remoteHost = request.getRemoteHost();
	       int remotePort = request.getRemotePort();
	       String remoteUser = request.getRemoteUser();
	       String method = request.getMethod();//得到请求URL地址时使用的方法
	       String pathInfo = request.getPathInfo();
	       String localAddr = request.getLocalAddr();//获取WEB服务器的IP地址
	       String localName = request.getLocalName();//获取WEB服务器的主机名
	      
	       Cookie[] cookies = request.getCookies();
	       List<Map> cookie_list = new ArrayList<Map>();
	       for(Cookie cookie : cookies){
	    	   Map cookie_map = new HashMap();
	    	   cookie_map.put("comment", cookie.getComment());
	    	   cookie_map.put("domain", cookie.getDomain());
	    	   cookie_map.put("maxAge", cookie.getMaxAge());
	    	   cookie_map.put("name", cookie.getName());
	    	   cookie_map.put("path", cookie.getPath());
	    	   cookie_map.put("secure", cookie.getSecure());
	    	   cookie_map.put("value", cookie.getValue());
	    	   cookie_map.put("version", cookie.getVersion());
	    	   cookie_list.add(cookie_map);
	       }
	       
	       //通过设置响应头控制浏览器以UTF-8的编码显示数据，如果不加这句话，那么浏览器显示的将是乱码
	       response.setHeader("content-type", "text/html;charset=UTF-8");
	       PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       out.write("获取到的客户机信息如下：");
	       out.write("<hr/>");
	       out.write("请求的URL地址："+requestUrl);
	       out.write("<br/>");
	       out.write("请求的资源："+requestUri);
	       out.write("<br/>");
	       out.write("请求的URL地址中附带的参数："+queryString);
	       out.write("<br/>");
	       out.write("来访者的IP地址："+remoteAddr);
	       out.write("<br/>");
	       out.write("来访者的主机名："+remoteHost);
	       out.write("<br/>");
	       out.write("使用的端口号："+remotePort);
	       out.write("<br/>");
	       out.write("remoteUser："+remoteUser);
	       out.write("<br/>");
	       out.write("请求使用的方法："+method);
	       out.write("<br/>");
	       out.write("pathInfo："+pathInfo);
	       out.write("<br/>");
	       out.write("localAddr："+localAddr);
	       out.write("<br/>");
	       out.write("localName："+localName);
	       out.write("<br/>");
	       out.write("Cookie："+cookie_list);
	  }*/
	  
	 /*@RequestMapping("add_cookie.json")
	  public void add_cookie(HttpServletRequest request, HttpServletResponse response) throws IOException{
		    response.setCharacterEncoding("UTF-8");//设置输出内容的编码格式
	        response.setContentType("text/plain");//设置输出的文件类型
	        PrintWriter out=response.getWriter();
			 Cookie cok = new Cookie("goodscookie", "123");
		     cok.setDomain(CommUtil.generic_domain(request));
		     cok.setMaxAge(60 * 60 * 24 * 7);
		     cok.setDomain(CommUtil.generic_domain(request));
		     response.addCookie(cok);
	  }*/
	  
	  
}
