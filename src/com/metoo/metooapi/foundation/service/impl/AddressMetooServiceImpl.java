package com.metoo.metooapi.foundation.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.buyer.domain.Result;
import com.metoo.core.annotation.SecurityMapping;
import com.metoo.core.dao.IGenericDAO;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.GenericPageList;
import com.metoo.core.query.PageObject;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.query.support.IQueryObject;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.WebForm;
import com.metoo.foundation.domain.Address;
import com.metoo.foundation.domain.Area;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.query.AddressQueryObject;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.metooapi.foundation.service.IAddressMetooService;
import com.metoo.metooapi.foundation.service.IAreaMetooService;
/**
 * @description 因为spring事务是基于aop的代理机制，当方法中调用this本身的方法时候即使在this的方法标明事务注解，但是事务注解会失效
 * @author Administrator
 *
 */
@Service
@Transactional
public class AddressMetooServiceImpl implements IAddressMetooService{

	@Resource(name = "AddressMetooDao")
	private IGenericDAO<Address>  addressMetooDao;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IAreaMetooService areaMetooService;
	@Autowired
	private IUserService userService;
	
	public boolean save(Address address) {
		try {
			this.addressMetooDao.save(address);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
	   }	
	}
	
	public boolean update(Address address) {
		// TODO Auto-generated method stub
		try {
			this.addressMetooDao.update(address);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean delete(Long id) {
		// TODO Auto-generated method stub
	     try {
			this.addressMetooDao.remove(id);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	
	public Address getObjById(Long id) {
		// TODO Auto-generated method stub
		Address address = this.addressMetooDao.get(id);
		if(address != null){
			return address;
		}
		return null;
	}

	public List<Address> query(String query, Map params, int begin, int max) {
		// TODO Auto-generated method stub
		return this.addressMetooDao.query(query, params, begin, max);
	}

	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		String construct = properties.getConstruct();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(Address.class, construct,
				query, params, this.addressMetooDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null)
				pList.doList(
						pageObj.getCurrentPage() == null ? 0 : pageObj
								.getCurrentPage(),
						pageObj.getPageSize() == null ? 0 : pageObj
								.getPageSize());
		} else
			pList.doList(0, -1);
		return pList;
	}
	
	public String address_metoo_list(HttpServletRequest request, HttpServletResponse response,
			String currentPage, String token) {
		Result result = null;
		Map addressMap = new HashMap();
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/address.html",
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
				User user = users.get(0);
				String url = this.configService.getSysConfig().getAddress();
				if (url == null || url.equals("")) {
					url = CommUtil.getURL(request);
				}
				AddressQueryObject qo = new AddressQueryObject(currentPage, mv,
						"default_val desc,obj.addTime", "desc");
				qo.addQuery("obj.user.id", new SysMap("user_id", user.getId()), "=");//SecurityUserHolder.getCurrentUser().getId()
				IPageList pList = this.list(qo);
				List<Map> addressList = CommUtil.saveIPageList2ModelAndView2(pList);
				if(addressList.isEmpty()){
					result = new Result(1,"用户地址为空");
				}else{
					addressMap.put("addresList", addressList);
					List<Area> areas = this.areaMetooService.query(
							"select obj from Area obj where obj.parent.id is null", null,
							
							-1, -1);
					List<Map> arealist = new ArrayList<Map>();
					for (Area area : areas) {
						Map map = new HashMap();
						map.put("id", area.getId());
						map.put("areaName", area.getAreaName());
						arealist.add(map);
						addressMap.put("areaMap", arealist);
					}
					if(!addressMap.isEmpty()){
						result = new Result(0,"查询成功",addressMap);
					}else{
						result = new Result(1,"您目前没有收货地址！");
					}
				}
			}
		}
		return Json.toJson(result, JsonFormat.compact());
	}

	public String address_metoo_edit(HttpServletRequest request, HttpServletResponse response,
			String id, String currentPage, String token) {
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
				User user= users.get(0);
				List<Area> areas = this.areaMetooService.query(
						"select obj from Area obj where obj.parent.id is null", null,
						-1, -1);
				Map map = new HashMap();
				List<Map> aeraList = new ArrayList<Map>();
				for(Area obj:areas){
					Map areaMap = new HashMap();
					areaMap.put("areaId", obj.getId());
					areaMap.put("area_name", obj.getAreaName());
					aeraList.add(areaMap);
					map.put("areaMap", aeraList);
				}
				map.put("currentPage", currentPage);
				Address obj = this.getObjById(CommUtil.null2Long(id));
				map.put("userName",obj.getTrueName());
				map.put("areaName",obj.getArea().getAreaName());
				map.put("mobile",obj.getMobile());
				map.put("telephone",obj.getTelephone());
				map.put("parentAreaName",obj.getArea().getParent().getAreaName());
				map.put("parentParentName",obj.getArea().getParent().getParent().getAreaName());
				map.put("Area_info",obj.getArea_info());
				map.put("Area_zip",obj.getZip());
				String addressTemp=null;
				//比较当前用户id与地址对应得id是否相同
				if (obj.getUser().getId()
						.equals(user.getId())) {// 只允许修改自己的地址信息
				}
				 result = new Result(0,"修改成功",map);
				}
		}
		return Json.toJson(result, JsonFormat.compact());
	}

	/**
	 * address保存管理
	 * 
	 * @param id
	 * @return
	 */
	
	public String address_metoo_save(HttpServletRequest request, HttpServletResponse response,
			String id, String area_id,String flag, String currentPage, String token) {
		boolean saveboolean = false;
		Result result = new Result();
		WebForm wf = new WebForm();//封装添加表单对象
		Address address = null;
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
				if (id.equals("")) {
					address = wf.toPo(request, Address.class);
					address.setAddTime(new Date());
				} else {
					Address obj = this.getObjById(Long.parseLong(id));
					if (obj.getUser().getId()
							.equals(user.getId())) {
						address = (Address) wf.toPo(request, obj);
					}
				}
				address.setUser(user);
				Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
				address.setArea(area);
				if (id.equals("")) {
					saveboolean= this.save(address);
					if(saveboolean) {
						if(flag.equals("0")){
							String address_id = CommUtil.null2String(address.getId());
							this.address_metoo_default(request, response, address_id,currentPage,token);
						}else{
							List<Address> addresses =user.getAddrs();
							if(addresses.size() == 1){
								this.address_metoo_default(request, response, CommUtil.null2String(addresses.get(0).getId()),currentPage,token);
							}
						}
						result = new Result(0,"添加成功");
					}else{
						result = new Result(1,"添加失败");
					}
				} else{
					saveboolean = this.update(address);
					if(saveboolean) {
						result = new Result(0,"更新成功");
					}else{
						result = new Result(1,"更新失败");
					}
				}
			}
		}
			String addressTemp = Json.toJson(result, JsonFormat.compact());
			return addressTemp;
	}
	
	/**
	 * 删除用户地址(仅用户自己)
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	public String address_metoo_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage, String token) {
		Result result = null;
		boolean del = false;
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
				String[] ids = mulitId.split(",");
				for (String id : ids) {
					if (!id.equals("")) {
						Address address = this.getObjById(Long
								.parseLong(id));
						if (address.getUser().getId()
								.equals(user.getId())) {// 只允许删除自己的地址信息
							 del= this.delete(Long.parseLong(id));
						}
					}
				}
				Map currentPageMap = new HashMap();
				currentPageMap.put("currentPage", currentPage);
				if(del == true){
					 result = new Result(0,"删除成功",currentPageMap);
				}else{
					result = new Result(1,"删除失败");
				}
			}
		}
		String deleteTemp = Json.toJson(result, JsonFormat.compact());
		return deleteTemp;
	}
	
	/**
	 * 设置地址默认设置
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	public String address_metoo_default(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage, String token) {
		Result result = new Result();
		boolean defaultbol = false;
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
				String[] ids = mulitId.split(",");
				for (String id : ids) {
					if (!id.equals("")) {
						Address address = this.getObjById(Long
								.parseLong(id));
						if (address.getUser().getId()
								.equals(user.getId())) {// 只允许修改自己的地址信息
							//[设置查询条件]
							params.clear();
							params.put("user_id", user.getId());
							params.put("id", CommUtil.null2Long(id));
							params.put("default_val", 1);//[是否为默认收货地址，1为默认地址]
							//[获取当前用户的默认地址 ]
							List<Address> addrs = this
									.query("select obj from Address obj where obj.user.id=:user_id and obj.id!=:id and obj.default_val=:default_val",
											params, -1, -1);
							Map currentPageMap = new HashMap();
							currentPageMap.put("currentPage", currentPage);
							//[循环设值为0并更新 ]
							for (Address addr1 : addrs) {
								addr1.setDefault_val(0);
								this.update(addr1);
							}
							 address.setDefault_val(1);
							 defaultbol = this.update(address);
								if(defaultbol){
									result = new Result(0,"设置默认地址成功",currentPageMap);
								}else{
									result = new Result(1,"设置默认地址失败");
								}
						}
					}
				}
			}
		}
		String defaultTemp = Json.toJson(result, JsonFormat.compact());
		return defaultTemp;
	}
	
	public String address__default_cancle(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,String token) {
		Result result = new Result();
		boolean defaultbol = false;
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
				String[] ids = mulitId.split(",");
				for (String id : ids) {
					if (!id.equals("")) {
						Address address = this.getObjById(Long.parseLong(id));
						if (address.getUser().getId()
								.equals(user.getId())) {// 只允许修改自己的地址信息
							address.setDefault_val(0);
							defaultbol = this.update(address);
						}
					}
				}
				Map currentPageMap = new HashMap();
				currentPageMap.put("currentPage", currentPage);
				if(defaultbol){
					result = new Result(0,"取消默认地址成功",currentPageMap);
				}else{
					result = new Result(1,"取消默认地址失败");
				}
			}
		}
		return Json.toJson(result, JsonFormat.compact());
	}
}
