package com.metoo.metooapi.foundation.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.metoo.core.query.support.IPageList;
import com.metoo.core.query.support.IQueryObject;
import com.metoo.foundation.domain.Favorite;

public interface IFavoriteMetooService {
	

	/**
	 * 根据一个ID得到Favorite
	 * 
	 * @param id
	 * @return
	 */
	Favorite getObjById(Long id);
	
	/**
	 * 删除一个Favorite
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	/**
	 * 通过一个查询对象得到Favorite
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	String favorite_goods(HttpServletRequest request, HttpServletResponse response,
			String currentPage, String orderBy, String orderType, String token);
	
	String favorite_storeimpl(HttpServletRequest request,
			HttpServletResponse response,String currentPage,String orderBy,String orderType, String token);
	
	String favorite_delimpl(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage);
}
