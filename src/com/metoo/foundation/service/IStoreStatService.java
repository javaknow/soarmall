﻿package com.metoo.foundation.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.metoo.core.query.support.IPageList;
import com.metoo.core.query.support.IQueryObject;
import com.metoo.foundation.domain.StoreStat;

public interface IStoreStatService {
	/**
	 * 保存一个StoreStat，如果保存成功返回true，否则返回false
	 * 
	 * @param instance
	 * @return 是否保存成功
	 */
	boolean save(StoreStat instance);
	
	/**
	 * 根据一个ID得到StoreStat
	 * 
	 * @param id
	 * @return
	 */
	StoreStat getObjById(Long id);
	
	/**
	 * 删除一个StoreStat
	 * 
	 * @param id
	 * @return
	 */
	boolean delete(Long id);
	
	/**
	 * 批量删除StoreStat
	 * 
	 * @param ids
	 * @return
	 */
	boolean batchDelete(List<Serializable> ids);
	
	/**
	 * 通过一个查询对象得到StoreStat
	 * 
	 * @param properties
	 * @return
	 */
	IPageList list(IQueryObject properties);
	
	/**
	 * 更新一个StoreStat
	 * 
	 * @param id
	 *            需要更新的StoreStat的id
	 * @param dir
	 *            需要更新的StoreStat
	 */
	boolean update(StoreStat instance);
	/**
	 * 
	 * @param query
	 * @param params
	 * @param begin
	 * @param max
	 * @return
	 */
	List<StoreStat> query(String query, Map params, int begin, int max);
}
