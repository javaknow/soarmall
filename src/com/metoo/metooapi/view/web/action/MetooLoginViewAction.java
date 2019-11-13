package com.metoo.metooapi.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.metoo.buyer.domain.Result;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.Md5Encrypt;
import com.metoo.foundation.domain.Album;
import com.metoo.foundation.domain.Document;
import com.metoo.foundation.domain.IntegralLog;
import com.metoo.foundation.domain.Role;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.IAlbumService;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.IDocumentService;
import com.metoo.foundation.service.IIntegralLogService;
import com.metoo.foundation.service.IRoleService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.foundation.service.IVerifyCodeService;
import com.metoo.module.app.service.IQRLoginService;
import com.metoo.view.web.tools.ImageViewTools;

@Controller
public class MetooLoginViewAction {
	
	@Autowired
	private IUserService userService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IDocumentService documentService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IVerifyCodeService verifyCodeService;
	@Autowired
	private IQRLoginService qRLoginService;
	@Autowired
	private ImageViewTools imageViewTools;
	private static final String REGEX1 = "(.*管理员.*)";
	private static final String REGEX2 = "(.*admin.*)";

	
	@RequestMapping("/register.json")
	public void register_json(HttpServletRequest request,
			HttpServletResponse response) {
		Map registermap = new HashMap();
		request.getSession(false).removeAttribute("verify_code");
		Document doc = this.documentService.getObjByProperty(null, "mark",
				"reg_agree");
		registermap.put("dos_content", doc.getContent());
		registermap.put("dos_id", doc.getId());
		Result result = new Result(0,"success",registermap);
		String regist_temp = Json.toJson(result, JsonFormat.compact());
		try {
			response.getWriter().print(regist_temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/verify_username.json")
	@ResponseBody
	public void verify_username(HttpServletRequest request,
			HttpServletResponse response, String userName, String id) {
		int ret = 1;
		Map params = new HashMap();
		params.put("userName", userName);
		params.put("id", CommUtil.null2Long(id));
		List<User> users = this.userService
				.query("select obj.id from User obj where (obj.userName=:userName or obj.mobile=:userName or obj.email=:userName) and obj.id!=:id",
						params, -1, -1);
		if (!users.isEmpty()) {
			ret = 0;
		}
		Result result = new Result(ret);
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
	 * 验证Email
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 */
	@RequestMapping("/verify_email.json")
	@ResponseBody
	public void verify_email(HttpServletRequest request,
			HttpServletResponse response, String email, String id) {
		int ret = 1;
		Map params = new HashMap();
		params.put("email", email);
		params.put("id", CommUtil.null2Long(id));
		List<User> users = this.userService
				.query("select obj.id from User obj where obj.email=:email and obj.id!=:id",
						params, -1, -1);
		if (!users.isEmpty()) {
			ret = 0;
		}
		Result result = new Result(ret);
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
	 * 验证Email
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 */
	@RequestMapping("/verify_mobile.json")
	@ResponseBody
	public void verify_mobile(HttpServletRequest request,
			HttpServletResponse response, String mobile, String id) {
		int ret = 1;
		Map params = new HashMap();
		params.put("email", mobile);
		params.put("id", CommUtil.null2Long(id));
		List<User> users = this.userService
				.query("select obj.id from User obj where obj.mobile=:mobile and obj.id!=:id",
						params, -1, -1);
		if (users != null && users.size() > 0) {
			ret = 0;
		}
		Result result = new Result(ret);
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
	
	@RequestMapping("/register_finish.json")
	public void register_finish_json(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String email, String mobile, String code, String user_type) throws HttpException,
			IOException, InterruptedException {
		Result result = null;
		try {
			boolean reg = true;// 防止机器注册，如后台开启验证码则强行验证验证码
			/*if (code != null && !code.equals("")) {
				code = CommUtil.filterHTML(code);// 过滤验证码
			}
			// System.out.println(this.configService.getSysConfig().isSecurityCodeRegister());
			if (this.configService.getSysConfig().isSecurityCodeRegister()) {
				if (!request.getSession(false).getAttribute("verify_code")
						.equals(code)) {
					reg = false;
				}
			}*/
			// 禁止用户注册带有 管理员 admin 等字样用户名
			if (userName.matches(REGEX1)
					|| userName.toLowerCase().matches(REGEX2)) {
				reg = false;
			}
			if (reg) {
				User user = new User();
				user.setUserName(userName);
				user.setUserRole("BUYER");
				user.setAddTime(new Date());
				user.setEmail(email);
				user.setMobile(mobile);
				user.setTelephone(mobile);
				user.setAvailableBalance(BigDecimal.valueOf(0));
				user.setFreezeBlance(BigDecimal.valueOf(0));
				user.setPassword(Md5Encrypt.md5(password).toLowerCase());
				Map params = new HashMap();
				params.put("type", "BUYER");
				List<Role> roles = this.roleService
						.query("select new Role(id) from Role obj where obj.type=:type",
								params, -1, -1);
				user.getRoles().addAll(roles);
				if (this.configService.getSysConfig().isIntegral()) { //[积分]
					user.setIntegral(this.configService.getSysConfig()
							.getMemberRegister());
					this.userService.save(user);
					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("用户注册增加"
							+ this.configService.getSysConfig()
									.getMemberRegister() + "分");
					log.setIntegral(this.configService.getSysConfig()
							.getMemberRegister());
					log.setIntegral_user(user);
					log.setType("reg");
					this.integralLogService.save(log);
				} else {
					this.userService.save(user);
				}
				// 创建用户默认相册
				Album album = new Album();
				album.setAddTime(new Date());
				album.setAlbum_default(true);
				album.setAlbum_name("默认相册");
				album.setAlbum_sequence(-10000);
				album.setUser(user);
				this.albumService.save(album);
				request.getSession(false).removeAttribute("verify_code");
				 result = new Result(0,"success"); 
			} else {
				result = new Result(1,"error");
			}
		} catch (Exception e) {
			result = new Result(2,"error");
		}
		String register_temp = Json.toJson(result, JsonFormat.compact());
		response.getWriter().print(register_temp);

	}

}
