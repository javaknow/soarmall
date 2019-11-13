package com.metoo.metooapi.app.view.action;

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
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.Md5Encrypt;
import com.metoo.core.tools.WebForm;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.VerifyCode;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.foundation.service.IVerifyCodeService;
import com.metoo.msg.MsgTools;

@Controller
public class AppLoginViewMetooAction {
	@Autowired
	private IUserService userService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IVerifyCodeService mobileverifycodeService;
	@Autowired
	private MsgTools msgTools;
	/**
	 * 手机客户端登陆
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param token
	 */
	//@CrossOrigin(allowCredentials="true")
	@RequestMapping("/app_koala_token_login.json")
	public void app_login_token(HttpServletRequest request,
			HttpServletResponse response, String userName, String password, String token) {
		String code = "-3";//  0,登陆成功,-1账号不存在，-2,密码不正确，-3登录失败
		Result result = null;
		Map json_map = new HashMap();
		String user_id = "";
		String user_name = "";
		String user_sex = "";
		String login_token = "";
		User login_user = null;
			
			if (userName != null && !userName.equals("") && password != null
					&& !password.equals("")) {
				password = Md5Encrypt.md5(password).toLowerCase();
				Map params = new HashMap();
				params.put("email", userName);
				params.put("mobile", userName);
				params.put("userName", userName);
				List<User> users = this.userService
						.query("select obj from User obj where obj.userName =:userName or obj.email=:email or obj.mobile=:mobile",
								params, -1, -1);
				if (users.size() > 0) {
					for (User u : users) {
						if (!u.getPassword().equals(password)) {
							code = "-2";
						} else {
							if (u.getUserRole().equalsIgnoreCase("admin")) {
								code = "-1";
							} else {
								user_id = CommUtil.null2String(u.getId());
								user_sex = CommUtil.null2String(u.getSex());
								user_name = u.getUserName();
								code = "0";
								login_token = CommUtil.randomString(12) + user_id;
								u.setApp_login_token(login_token.toLowerCase());
								this.userService.update(u);
								login_user = u;
								break;
							}
						}
					}
				} else {
					code = "-4";
				}
			}else{
				if(token != null && !token.equals("")){
					Map params = new HashMap();
					params.put("app_login_token", token);
					List<User> users_t =  this.userService.query("select obj from User obj where obj.app_login_token=:app_login_token order by obj.addTime desc",
							params, -1, -1);
					String usr_name = null;
					String usr_password = null;
					if(users_t.isEmpty()){
						code = "-5";
					}else{
					for(User obj : users_t){
						usr_name = obj.getUsername();
						usr_password = obj.getPassword();
					}
					if (usr_name != null && !usr_name.equals("") && usr_password != null
							&& !usr_password.equals("")) {
						if (users_t.size() > 0) {
							for (User u : users_t) {
								if (!u.getPassword().equals(usr_password)) {
									code = "-2";
								} else {
									if (u.getUserRole().equalsIgnoreCase("admin")) {
										code = "-1";
									} else {
										user_id = CommUtil.null2String(u.getId());
										user_sex = CommUtil.null2String(u.getSex());
										user_name = u.getUserName();
										code = "0";
										login_token = token; 
										u.setApp_login_token(token);
										this.userService.update(u);
										login_user = u;
										break;
									}
								}
							}
						} else {
							code = "-4";
					}
				}
			}
		}
	}
			if (code.equals("0")) {
				json_map.put("verify", this.create_appverify(login_user));
				json_map.put("user_id", user_id.toString());
				json_map.put("userName", user_name);
				json_map.put("token", login_token);
				if(CommUtil.null2Int(user_sex) == -1){
					json_map.put("user_photo", this.configService.getSysConfig().getImageWebServer()+"/"+"resources"+"/"+"style"+"/"+"common"+"/"+"images"+"/"+"member.png");
				}
				if(CommUtil.null2Int(user_sex) == 0){
					json_map.put("user_photo", this.configService.getSysConfig().getImageWebServer()+"/"+"resources"+"/"+"style"+"/"+"common"+"/"+"images"+"/"+"member0.png");
				}
				if(CommUtil.null2Int(user_sex) == 1){
					json_map.put("user_photo", this.configService.getSysConfig().getImageWebServer()+"/"+"resources"+"/"+"style"+"/"+"common"+"/"+"images"+"/"+"member1.png");
				}
			}
			if(!json_map.isEmpty()){
				result = new Result(CommUtil.null2Int(code),json_map);
			}else{
				result = new Result(CommUtil.null2Int(code), "error");
			}
		String json = Json.toJson(result, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SecurityMapping(title = "手机号码验证", value = "/buyer/account_mobile_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/app_mobile.json")
	public void account_mobile_save(HttpServletRequest request,
			HttpServletResponse response, String mobile_verify_code,
			String mobile) throws Exception {
		Result result = null;
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		WebForm wf = new WebForm();
		VerifyCode mvc = this.mobileverifycodeService.getObjByProperty(null,
				"mobile", mobile);
		if (mvc != null && mvc.getCode().equalsIgnoreCase(mobile_verify_code)) {
			this.mobileverifycodeService.delete(mvc.getId());
			// 绑定成功后发送手机短信提醒
			/*String content = "尊敬的"
					+ "Ebuyair网站用户"SecurityUserHolder.getCurrentUser().getUserName()
					+ "您好，您于" + CommUtil.formatLongDate(new Date())
					+ "验证身份成功。["
					+ this.configService.getSysConfig().getTitle() + "]";*/
			String content = "";
			//this.msgTools.sendSMS(user.getMobile(), content);
			this.msgTools.sendSMS(mobile, content);
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/account.htm");
			result = new Result(0,"验证身份通过，请注册");
		} else {
			result = new Result(1,"验证码错误，请使用本人手机号");
		}
		response.getWriter().print(Json.toJson(result, JsonFormat.compact()));
	}

	
	@RequestMapping("/app_verify.json")
	public void order_mobile(HttpServletRequest request,
			HttpServletResponse response, String type, String mobile)
			throws UnsupportedEncodingException {
		Result result = null;
		String ret = "0";
		if (type.equals("mobile_verify_code")) {
			String code = CommUtil.randomString(4).toUpperCase();
			/*String content = "尊敬的"
					+ "Ebuyair用户"
					+ "您好，您在注册平台账号"
					+ this.configService.getSysConfig().getWebsiteName()
					+ "，您的验证码为：" + code +"短信内容5分钟内有效。如非本人操作请忽略短信"+"。["
					+ this.configService.getSysConfig().getTitle() + "]";*/
			String content = "Dear Ebuyair user, you are registering the platform account Ebuyair, your verification code is:"
					+ code
					+ "SMS content is valid within 5 minutes.  "
					+ "Please ignore the SMS if you are not using it.  [Ebuyair.com]";
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
					ret = "1";
				}
			} else {
				ret = "2";
			}
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			result = new Result(CommUtil.null2Int(ret),"success");
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
	 * 当用户登录后生成verify返回给客户端保存，每次发送用户中心中请求时将verify放入到请求头中，
	 * 用来验证用户密码是否已经被更改，如已经更改，手机客户端提示用户重新登录
	 * 
	 * @param user
	 * @return
	 */
	private String create_appverify(User user) {
		String app_verify = user.getPassword() + user.getApp_login_token();
		app_verify = Md5Encrypt.md5(app_verify).toLowerCase();
		return app_verify;
	}
}
