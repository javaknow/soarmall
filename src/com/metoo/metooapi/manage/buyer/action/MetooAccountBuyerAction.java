package com.metoo.metooapi.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
import com.metoo.core.annotation.SecurityMapping;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.WebForm;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.VerifyCode;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.foundation.service.IVerifyCodeService;
import com.metoo.metooapi.foundation.service.IUserMetooService;
import com.metoo.msg.MsgTools;
@Controller
public class MetooAccountBuyerAction {
	
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IVerifyCodeService mobileverifycodeService;
	@Autowired 
	private IUserMetooService userMetooService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private MsgTools msgTools;
	
	@SecurityMapping(title = "个人信息", value = "/buyer_account.json*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer_account.json")
	public void account(HttpServletRequest request, HttpServletResponse response, String token) {
		String accountMap = this.userMetooService.metoo_account(request, response,token);
		try {
			response.getWriter().println(accountMap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return addressTemp;
	}
	
	@SecurityMapping(title = "个人信息保存", value = "/buyer_account_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer_account_save.json")
	public void account_save(HttpServletRequest request,
			HttpServletResponse response, String area_id, String birthday, String token) {
		String account_save = this.userMetooService.account_metoo_saveimpl(request, response, area_id, birthday,token);
		try {
			response.getWriter().println(account_save);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "密码修改保存", value = "/buyer_account_password_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer_accoun_password_save.json")
	public void account_password_save(HttpServletRequest request, HttpServletResponse response,
			String old_password, String new_password, String token) throws Exception{
	        String accountSavePwdTemp = this.userMetooService.account_metoo_password_saveimpl(request, response, old_password, new_password,token);
		
		    response.getWriter().println(accountSavePwdTemp);
	}
	
	@SecurityMapping(title = "邮箱修改保存", value = "/buyer_account_email_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer_account_email_save.json")
	public void account_email_save(HttpServletRequest request,
			HttpServletResponse response, String password, String email, String token) {
			      String account_saveEmail = this.userMetooService.account_email_saveimpl(request, response, password, email,token);
	      try {
			response.getWriter().println(account_saveEmail);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 手机短信发送
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @throws UnsupportedEncodingException
	 */
	@SecurityMapping(title = "手机短信发送", value = "/buyer/account_mobile_sms.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer_account_mobile_sms.json")
	public void account_mobile_sms(HttpServletRequest request,
			HttpServletResponse response, String type, String mobile, String token)
			throws UnsupportedEncodingException {
		Map params = new HashMap();
		Result result = null;
		if(token.equals("")){
			result = new Result(-100, "token Invalidation");
		}else{
			params.put("app_login_token", token);
			List<User> users =  this.userService.query("select obj from User obj where obj.app_login_token=:app_login_token order by obj.addTime desc",
					params, -1, -1);
			if(users.isEmpty()){
				result = new Result(-100, "token Invalidation");
			}else{
				User user = users.get(0);
				int ret = 0;
				if (type.equals("mobile_vetify_code")) {
					String code = CommUtil.randomString(4).toUpperCase();
					String content = "尊敬的"
							+ user.getUserName()
							+ "您好，您在试图修改"
							+ this.configService.getSysConfig().getWebsiteName()
							+ "用户绑定手机，手机验证码为：" + code + "。["
							+ this.configService.getSysConfig().getTitle() + "]";
					if (this.configService.getSysConfig().isSmsEnbale()) {
						boolean ret1 = this.msgTools.sendSMS(mobile, content);
						if (ret1) {
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
							ret = 1;
						}
					} else {
						ret = 2;
					}
					result = new Result(ret);
				}
			}
			String temp = Json.toJson(result, JsonFormat.compact());
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.print(temp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param mobile_verify_code
	 * @param mobile
	 * @param token
	 * @throws Exception
	 */
	@SecurityMapping(title = "手机号码保存", value = "/buyer/account_mobile_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer_account_mobile_save.json")
	public void account_mobile_save(HttpServletRequest request, HttpServletResponse response,
			String mobile_verify_code, String mobile, String token) throws Exception {
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
				Map mobilemap = new HashMap();
				ModelAndView mv = new JModelAndView("success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				WebForm wf = new WebForm();
				VerifyCode mvc = this.mobileverifycodeService.getObjByProperty(null,
						"mobile", mobile);
				if (mvc != null && mvc.getCode().equalsIgnoreCase(mobile_verify_code)) {
					user.setMobile(mobile);
					this.userService.update(user);
					this.mobileverifycodeService.delete(mvc.getId());
					// 绑定成功后发送手机短信提醒
					String content = "尊敬的"
							+ user.getUserName()
							+ "您好，您于" + CommUtil.formatLongDate(new Date())
							+ "绑定手机号成功。["
							+ this.configService.getSysConfig().getTitle() + "]";
					this.msgTools.sendSMS(user.getMobile(), content);
					result = new Result(0,"success");
				} else {
					String content = "Verification code error, phone binding failed";
					result = new Result(1,"error",content);
				}
			}
		}
		try {
			response.getWriter().print(Json.toJson(result, JsonFormat.compact()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
