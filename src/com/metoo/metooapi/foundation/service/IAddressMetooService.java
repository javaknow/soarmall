package com.metoo.metooapi.foundation.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.metoo.core.query.support.IPageList;
import com.metoo.core.query.support.IQueryObject;
import com.metoo.foundation.domain.Address;

public interface IAddressMetooService {
	/**
	 * 删除一个Address
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	/**
	 * 保存一个Address，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(Address instance);
	
	/**
	 * 更新一个Address
	 * 
	 * @param id
	 *            需要更新的Address的id
	 * @param dir
	 *            需要更新的Address
	 */
	boolean update(Address instance);
	/**
	 * 根据一个ID得到Address
	 * @param 需要更新的Address的id
	 * @return 需要更新的Address
	 */
	Address getObjById(Long id);
	/**
	 * 通过一个查询对象得到Address
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<Address> query(String query, Map params, int begin, int max);

	
	/**
	 *	获取用户地址列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	String address_metoo_list(HttpServletRequest request, HttpServletResponse response, 
			String currentPage,String token);
	
	String address_metoo_edit(HttpServletRequest request, HttpServletResponse response,
			String id, String currentPage, String token);

	String address_metoo_save(HttpServletRequest request, HttpServletResponse response,
			String id, String area_id, String currentPage,String flag, String token);
	String address_metoo_del(HttpServletRequest request, HttpServletResponse response,
			String mulitId, String currentPage, String token);
	String address_metoo_default(HttpServletRequest request, HttpServletResponse response,
			String mulitId, String currentPage, String token);
	String address__default_cancle(HttpServletRequest request, HttpServletResponse response,
			String mulitId, String currentPage, String token);
}
