package com.metoo.metooapi.manage.buyer.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.metoo.core.annotation.SecurityMapping;
import com.metoo.metooapi.foundation.service.ICouponInfoMetooService;

@Controller
public class MetooCouponBuyerAction {
	
	@Autowired
	private ICouponInfoMetooService couponInfoMetooService;

	@SecurityMapping(title = "买家优惠券列表", value = "/buyer/coupon.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer_coupon.json")
	public void coupon(HttpServletRequest request,
			HttpServletResponse response, String reply, String currentPage) {
		String couponResult =  this.couponInfoMetooService.coupon_listimpl(request, response, reply, currentPage);
		try {
			response.getWriter().println(couponResult);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
