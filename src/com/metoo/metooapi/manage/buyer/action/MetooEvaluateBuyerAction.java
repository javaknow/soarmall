package com.metoo.metooapi.manage.buyer.action;

import java.io.IOException;
import java.util.ArrayList;
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
import com.metoo.core.annotation.SecurityMapping;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.Accessory;
import com.metoo.foundation.domain.Evaluate;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.query.EvaluateQueryObject;
import com.metoo.foundation.service.IEvaluateService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.manage.admin.tools.ImageTools;
import com.metoo.manage.admin.tools.OrderFormTools;
import com.metoo.manage.buyer.tools.EvaluateTools;

@Controller
public class MetooEvaluateBuyerAction {
	
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private ImageTools imageTools;
	@Autowired
	private EvaluateTools evaluateTools;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IUserService userService;
	
	@SecurityMapping(title = "买家评价列表", value = "/buyer/evaluate_list.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer_evaluate_list.json")
	public void evaluate_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String token) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/evaluate_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
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
				EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv,
						"addTime", "desc");
				qo.addQuery("obj.evaluate_user.id", new SysMap("user_id",
						user.getId()), "=");
				IPageList pList = this.evaluateService.list(qo);
				
				List<Evaluate> evaluates = pList.getResult();
				List<Map> evaluate_Map_List = new ArrayList<Map>();
				for(Evaluate evaluate : evaluates){
					Map evaluateMap = new HashMap();
					evaluateMap.put("Evaluate_order_id", evaluate.getOf().getId());//买家评价，评价类型，1为好评，0为中评，-1为差评
					evaluateMap.put("Evaluate_buyer_val", evaluate.getEvaluate_buyer_val());//买家评价，评价类型，1为好评，0为中评，-1为差评
					evaluateMap.put("Evaluate_status", evaluate.getEvaluate_status());//// 0为正常，1为禁止显示，2为取消评价
					evaluateMap.put("Evaluate_info", evaluate.getEvaluate_info());// 买家评价信息
					evaluateMap.put("Add_Time", evaluate.getAddTime());
					List<Accessory> acc = imageTools.queryImgs(evaluate.getEvaluate_photos());
					List<Map> evaphoto_list = new ArrayList<Map>();
					if(CommUtil.isNotNull(evaphoto_list)){
						for(Accessory accessory:acc){
							Map photoMap = new HashMap();
							photoMap.put("photo", this.configService.getSysConfig().getImageWebServer()+"/"+accessory.getPath()+"/"+accessory.getName());
							evaphoto_list.add(photoMap);
							evaluateMap.put("photos", evaphoto_list);
						}
					}
					evaluateMap.put("Addeva_info", evaluate.getAddeva_info());//追评
					evaluateMap.put("Addeva_status", evaluate.getAddeva_status());
					evaluateMap.put("Addeva_time", evaluate.getAddeva_time());
				
					List<Accessory> add_acc = imageTools.queryImgs(evaluate.getAddeva_photos());
					List<Map> eva_photo_list = new ArrayList<Map>();
					if(CommUtil.isNotNull(eva_photo_list)){
						for(Accessory accessory:add_acc){
							Map photo_map = new HashMap();
							photo_map.put("eva_photo", this.configService.getSysConfig().getImageWebServer()+"/"+accessory.getPath()+"/"+accessory.getName());
							eva_photo_list.add(photo_map);
							evaluateMap.put("eva_photo", eva_photo_list);
						}
						evaluateMap.put("Eva_photos", evaluate.getEvaluate_photos());	
					}
					evaluateMap.put("Reply_status", evaluate.getReply_status());// 评价回复的状态默认为0未回复 已回复为1；
					evaluateMap.put("Reply", evaluate.getReply());// 评价的回复
					evaluateMap.put("Evaluate_buyer_val", evaluate.getEvaluate_buyer_val());//评价类型// 买家评价，评价类型，1为好评，0为中评，-1为差评
					int num = evaluateTools.evaluate_add_able(evaluate.getAddTime());
					
					evaluateMap.put("overTime", num);
					evaluate_Map_List.add(evaluateMap);
				}
				if(!evaluate_Map_List.isEmpty()){
					result = new Result(0,"查询成功",evaluate_Map_List);
				}else{
					result = new Result(1,"null");
				}
			}
		}
		String evaluatetemp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(evaluatetemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
