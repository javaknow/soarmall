package com.metoo.metooapi.foundation.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.buyer.domain.Result;
import com.metoo.core.dao.IGenericDAO;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.Md5Encrypt;
import com.metoo.core.tools.WebForm;
import com.metoo.foundation.domain.Area;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.metooapi.foundation.service.IAreaMetooService;
import com.metoo.metooapi.foundation.service.IUserMetooService;
import com.metoo.msg.MsgTools;
@Service
@Transactional
public class UserMetooServiceImpl implements IUserMetooService{

	@Resource(name = "userMetooDAO")
	private IGenericDAO<User> userMetooDao;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAreaMetooService areaMetooService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IUserService userService;
	
	@Override
	public User getObjById(Long id) {
		// TODO Auto-generated method stub
		
		User user = this.userMetooDao.get(id);
		if(user != null){
			return user;
		}
		return null;
	}

	@Override
	public boolean update(User user) {
		// TODO Auto-generated method stub
		try {
			this.userMetooDao.update(user);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	   //个人信息
		public String metoo_account(HttpServletRequest request,
				HttpServletResponse response, String token) {
			Result result = new Result();
			Map map = new HashMap();
			Map userMap = new HashMap();
			Map addressMap = new HashMap();
			ModelAndView mv = new JModelAndView(
					"user/default/usercenter/account.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
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
				try {
					User user = users.get(0);
					userMap.put("HeadImgUrl", user.getHeadImgUrl() == null ? "" : user.getHeadImgUrl());
					userMap.put("TrueName", user.getTrueName() == null ? "" : user.getTrueName());
					userMap.put("Sex", user.getSex());
					userMap.put("Email", user.getEmail() == null ? "" : user.getEmail());
					userMap.put("Telephone", user.getTelephone() == null ? "" : user.getTelephone());
					userMap.put("Mobile", user.getMobile() == null ? "" : user.getMobile());
					userMap.put("Birthday", user.getBirthday() == null ? "" : user.getBirthday());
					
					List<Area> areas = this.areaMetooService.query(
							"select obj from Area obj where obj.parent.id is null", null,
							-1, -1);
					List<Map> areaList = new ArrayList<Map>();
					for (Area area : areas) {
						Map areaMap = new HashMap();
						areaMap.put("id", area.getId());
						areaMap.put("areaName", area.getAreaName());
						areaList.add(areaMap);
						map.put("areaMap", areaList);
					}
					map.put("userMap", userMap);
					if(map != null && !map.isEmpty()){
						result = new Result(0,"输出成功",map);
					}else{
						result = new Result(1,"参数错误");
					}
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					result = new Result(-1,"请登陆");
				}
				}
		}
			return Json.toJson(result, JsonFormat.compact());
		}
		
		//信息保存
		public String account_metoo_saveimpl(HttpServletRequest request,
				HttpServletResponse response, String area_id, String birthday, String token) {
			//[封装表单信息]
			Result result = new Result();
			boolean account_save=false;
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
					User user = (User) wf.toPo(request, this.userService
							.getObjById(users.get(0).getId()));
					if (area_id != null && !area_id.equals("")) {
						Area area = this.areaMetooService
								.getObjById(CommUtil.null2Long(area_id));
					}
					if (birthday != null && !birthday.equals("")) {
						String y[] = birthday.split("-");
						Calendar calendar = new GregorianCalendar();
						int years = calendar.get(Calendar.YEAR) - CommUtil.null2Int(y[0]);
						user.setYears(years);
					}
					account_save = this.update(user);
					if(account_save){
						result = new Result(0,"保存成功",account_save);
					}else{
						result = new Result(1,"保存成功");
					}
				}
			}
			return Json.toJson(result, JsonFormat.compact());
		}
		
		//密码保存
		public String account_metoo_password_saveimpl(HttpServletRequest request, HttpServletResponse response,
				String old_password, String new_password, String token){
			Result result = new Result();
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
					//获取当前登录用户数据库密码，比较用户输入密码加密 (md5)if-true 对新的密码加密
					if(user.getPassword().equals(
							Md5Encrypt.md5(old_password).toLowerCase())){
						user.setPassword(Md5Encrypt.md5(new_password).toLowerCase());
						boolean pwd = this.update(user);
						if(pwd){
							String content = "尊敬的"
									+ user.getUserName()
									+ "您好，您于" + CommUtil.formatLongDate(new Date())
									+ "修改密码成功，新密码为：" + new_password + ",请妥善保管。["
									+ this.configService.getSysConfig().getTitle() + "]";
							try {
								this.msgTools.sendSMS(user.getMobile(), content);
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							result = new Result(0,content,pwd);
						}
					}else{
						result = new Result(1,"原始密码错误");
					}
				}
			}
			return Json.toJson(result, JsonFormat.compact());
		}
		
		//修改邮箱
		public String  account_email_saveimpl(HttpServletRequest request, HttpServletResponse response,
				String password, String email, String token) {
			Result result = null;
			boolean emailUP =false;
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
			      if(user.getPassword().equals(Md5Encrypt.md5(password).toLowerCase())){
			    	  user.setEmail(email);
			    	  emailUP =  this.update(user);
			    	 if(emailUP){
			    		 result = new Result(0,"邮箱修改成功",emailUP);
			    	 }else{
			    		 result = new Result(1,"未知错误！请确认身份");
			    	 }
			      }else{
			    	  result = new Result(1,"密码输入错误,邮箱修改失败");
			      }
				}
			}
			return Json.toJson(result, JsonFormat.compact());
		}
	}
