package com.metoo.metooapi.foundation.service.impl;

import java.io.File;
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
import org.nutz.mvc.annotation.PUT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.buyer.domain.Result;
import com.metoo.core.dao.IGenericDAO;
import com.metoo.core.domain.virtual.SysMap;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.GenericPageList;
import com.metoo.core.query.PageObject;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.query.support.IQueryObject;
import com.metoo.core.security.support.SecurityUserHolder;
import com.metoo.core.tools.CommUtil;
import com.metoo.foundation.domain.Favorite;
import com.metoo.foundation.domain.Goods;
import com.metoo.foundation.domain.Store;
import com.metoo.foundation.domain.User;
import com.metoo.foundation.domain.query.FavoriteQueryObject;
import com.metoo.foundation.service.IFavoriteService;
import com.metoo.foundation.service.IGoodsService;
import com.metoo.foundation.service.IMessageService;
import com.metoo.foundation.service.IStoreService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;
import com.metoo.foundation.service.IUserService;
import com.metoo.lucene.LuceneUtil;
import com.metoo.lucene.tools.LuceneVoTools;
import com.metoo.manage.admin.tools.UserTools;
import com.metoo.metooapi.foundation.service.IFavoriteMetooService;
import com.metoo.view.web.tools.GoodsViewTools;
@Service
@Transactional
public class FavoriteMetooServiceImpl implements IFavoriteMetooService{
	
	@Resource(name = "favoriteMetooDAO")
	private IGenericDAO<Favorite> favoriteMetooDao;

	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private UserTools userTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IFavoriteService favoriteService;
	
	public Favorite getObjById(Long id) {
		Favorite favorite = this.favoriteMetooDao.get(id);
		if(favorite != null){
			return favorite;
		}
		return null;
	}
	
	@Override
	public boolean delete(Long id) {
		try {
			this.favoriteMetooDao.remove(id);
			return true;
		} catch (Exception e) {	
			e.printStackTrace();
			return false;
		}
	}	
	
	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();//默认查询语句1=1
		String construct = properties.getConstruct();// 查询构造器，为空时查询obj所有字段
		Map params = properties.getParameters();//取出封装的参数列表
		//[该类用来进行数据查询并分页返回数据信息  构造分页信息]
		GenericPageList pList = new GenericPageList(Favorite.class, construct,
				query, params, this.favoriteMetooDao);
		if (properties != null) {
			//[获取分页信息 默认 currentPage=-1 pageSize=-1]
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
	
	//收藏商品信息
	public String favorite_goods(HttpServletRequest request, HttpServletResponse response,
			String currentPage, String orderBy, String orderType, String token) {
		Map favoriteMaps = new HashMap();
		Result result = null;
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/favorite_goods.html",
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
				params.clear();
				FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv,
						orderBy, orderType);
				qo.addQuery("obj.type", new SysMap("type", 0), "=");//收藏类型，0为商品收藏、1为店铺收藏
				qo.addQuery("obj.user_id", new SysMap("user_id", user.getId()), "=");
				IPageList pList = this.list(qo);
				List<Favorite> favorite =  pList.getResult();
				int num = favorite.size();
			
				favoriteMaps.put("facoriteNum", num);
				List<Map> favoriteList = new ArrayList<Map>();
				for(Favorite favorites:favorite){
					Map favoriteMap = new HashMap();
					favoriteMap.put("favorites_id", favorites.getId());
					favoriteMap.put("Goods_id", favorites.getGoods_id());
					favoriteMap.put("Goods_name", favorites.getGoods_name());
					favoriteMap.put("Goods_current_price", favorites.getGoods_current_price());//[收藏时候的商品价格,若发送降价通知则更新为降价后的价格]
					favoriteMap.put("Goods_photo", this.configService.getSysConfig().getImageWebServer()+"/"+favorites.getGoods_photo());
					favoriteMap.put("Goods_type", favorites.getGoods_type());
					favoriteMap.put("Goods_store_id", favorites.getGoods_store_id());
					favoriteMap.put("AddTime", favorites.getAddTime());
					favoriteList.add(favoriteMap);
				}
				favoriteMaps.put("favoriteList", favoriteList);
				if(!favoriteMaps.isEmpty()){
					result = new Result(0,"success",favoriteMaps);
				}else{
					result = new Result(1,"null");
				}
			}
		}
		return Json.toJson(result, JsonFormat.compact());
	}
	
	//收藏店铺品信息
	public String favorite_storeimpl(HttpServletRequest request,
			HttpServletResponse response,String currentPage,String orderBy,String orderType, String token){
		Map favoriteMaps = new HashMap();
		Result result = null;
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/favorite_store.html",
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
				//构造查询对象
				FavoriteQueryObject qo = new FavoriteQueryObject(currentPage,mv,
						orderBy,orderType);
				qo.addQuery("obj.type",new SysMap("type",1), "=");//收藏类型 1 店铺
				qo.addQuery("obj.user_id", new SysMap("user_id",user.getId()),"=");
				IPageList pList = this.list(qo);
				List<Favorite> favorites = pList.getResult();
				List<Map> favoriteList = new ArrayList<Map>();
				for(Favorite favorite:favorites){
					Map favoriteMap = new HashMap();
					favoriteMap.put("favorite",favorite.getId());
					favoriteMap.put("Store_photo", favorite.getStore_photo() == null ? "" : this.configService.getSysConfig().getImageWebServer()+"/"+favorite.getStore_photo());
					favoriteMap.put("Store_name",favorite.getStore_name());
					favoriteMap.put("Store_id",favorite.getStore_id());
					favoriteMap.put("AddTime", favorite.getAddTime());
					favoriteMap.put("store_addr", favorite.getStore_addr().equals("") ? "" : favorite.getStore_addr());
					favoriteMap.put("store_second_domain", favorite.getStore_second_domain() == null ? "" : favorite.getStore_second_domain());
					favoriteList.add(favoriteMap);
				}
				if(!favoriteList.isEmpty()){
					favoriteMaps.put("favoriteMap", favoriteList); 
				}
				if(favoriteMaps.size() > 0){
					result = new Result(0,"查询成功",favoriteMaps);
				}else{
					result = new Result(1,"您还未收藏任何店铺！");
				}
			}
		}
		String favoriteTemp = Json.toJson(result, JsonFormat.compact());
		return favoriteTemp;
		
	}
//收藏删除
	public String favorite_delimpl(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		Result result = null;
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Favorite favorite = this.favoriteService.getObjById(Long
						.parseLong(id));
				if (favorite != null && favorite.getGoods_id() != null) {
					// 更新lucene索引
					String goods_lucene_path = System.getProperty("metoob2b2c.root")
							+ File.separator + "luence" + File.separator
							+ "goods";
					File file = new File(goods_lucene_path);
					if (!file.exists()) {
						CommUtil.createFolder(goods_lucene_path);
					}
					LuceneUtil lucene = LuceneUtil.instance();
					lucene.setIndex_path(goods_lucene_path);
					Goods goods = this.goodsService.getObjById(favorite
							.getGoods_id());
					lucene.update(CommUtil.null2String(favorite.getGoods_id()),
							luceneVoTools.updateGoodsIndex(goods));
				}
				
				//[ 收藏类型，0为商品收藏、1为店铺收藏]
				if (favorite.getType() == 0) {
					Goods goods = this.goodsService.getObjById(favorite
							.getGoods_id());
					//商品收藏次数减一
					goods.setGoods_collect(goods.getGoods_collect() - 1);
					//更新
					this.goodsService.update(goods);
					
				}
				if (favorite.getType() == 1) {
					Store store = this.storeService.getObjById(favorite
							.getStore_id());
					//店铺收藏人气减一
					store.setFavorite_count(store.getFavorite_count() - 1);
					
					this.storeService.update(store);
				}
				boolean delStore = this.delete(Long.parseLong(id));
				if(delStore){
					result = new Result(0,"删除成功",delStore);
				}else{
					result = new Result(1,"删除失败",delStore);
				}
			}
		}
			String favoriteTemp = Json.toJson(result, JsonFormat.compact());
			return favoriteTemp;
	}
}
