package com.metoo.metooapi.wap.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserService;

@Controller		
public class WapBuyerIndexAction {
	
	@Autowired
	private ISysConfigService sysConfigService;
	@Autowired 
	private IUserService userService;
	
	@RequestMapping("/userinfo_json")
	public void wapBuyerIndex(HttpServletRequest request, HttpServletResponse response){
		User user =  this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
		
		Map usermap = new HashMap();
		usermap.put("username", user.getTrueName());
		usermap.put("username", user.getSex());
		
	}
}
