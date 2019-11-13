package com.metoo.metooapi.foundation.service.impl;

import java.io.IOException;
import java.util.ArrayList;
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
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.Address;
import com.metoo.foundation.domain.Area;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.service.IAreaService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.metooapi.foundation.service.IAddressMetooService;
import com.metoo.metooapi.foundation.service.IAreaMetooService;
@Service
@Transactional
public class AreaMetooServiceImpl implements IAreaMetooService{
	
	@Resource(name = "AreaMetooDao")
	private IGenericDAO<Area> areaMetooDao;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	
	@Override
	public Area getObjById(Long id) {
		// TODO Auto-generated method stub
		Area area = this.areaMetooDao.get(id);
		if(area != null){
			return area;
		}
		return null;
	}
	
	@Override
	public List<Area> query(String query, Map params, int begin, int max) {
		// TODO Auto-generated method stub
		return this.areaMetooDao.query(query, params, begin, max);
	}
	
	public String areaParent(HttpServletRequest request,
			HttpServletResponse response, String currentPage){
		Result result= null;
			List<Area> areas = this.query(
						"select obj from Area obj where obj.parent.id is null", null,
						-1, -1);
				Map areamap = new HashMap();
				List<Map> areaList = new ArrayList<Map>();
				for(Area area:areas){
					Map areaMap = new HashMap();
					areaMap.put("areaId", area.getId());
					areaMap.put("areaName", area.getAreaName());
					areaList.add(areaMap);
				}
				areamap.put("areaMap", areaList);
				areamap.put("currentPage", currentPage);
				if(!areamap.isEmpty()){
					result = new Result(0,"成功",areamap);
				}else{
					result = new Result(1,"区域信息为空！");
				}
		String addressTemp = Json.toJson(result, JsonFormat.compact());
		return addressTemp;
	}

}
