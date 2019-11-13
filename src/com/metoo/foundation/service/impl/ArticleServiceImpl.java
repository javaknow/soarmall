package com.metoo.foundation.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import com.metoo.core.dao.IGenericDAO;
import com.metoo.core.mv.JModelAndView;
import com.metoo.core.query.GenericPageList;
import com.metoo.core.query.PageObject;
import com.metoo.core.query.support.IPageList;
import com.metoo.core.query.support.IQueryObject;
import com.metoo.core.tools.CommUtil;
import com.metoo.core.tools.WebForm;
import com.metoo.foundation.domain.Article;
import com.metoo.foundation.domain.query.ArticleQueryObject;
import com.metoo.foundation.service.IArticleService;
import com.metoo.foundation.service.ISysConfigService;
import com.metoo.foundation.service.IUserConfigService;

@Service
@Transactional
public class ArticleServiceImpl implements IArticleService {
	
	
	
	@Resource(name = "articleDAO")
	private IGenericDAO<Article> articleDao;
	
	

	public boolean save(Article article) {
		/**
		 * init other field here
		 */
		try {
			this.articleDao.save(article);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Article getObjById(Long id) {
		Article article = this.articleDao.get(id);
		if (article != null) {
			return article;
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			this.articleDao.remove(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean batchDelete(List<Serializable> articleIds) {
		// TODO Auto-generated method stub
		for (Serializable id : articleIds) {
			delete((Long) id);
		}
		return true;
	}

	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		    String query = properties.getQuery();
		String construct = properties.getConstruct();
		      Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(Article.class, construct,
				query, params, this.articleDao);
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

	public boolean update(Article article) {
		try {
			this.articleDao.update(article);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Article> query(String query, Map params, int begin, int max) {
		return this.articleDao.query(query, params, begin, max);

	}

	@Override
	public Article getObjByProperty(String construct, String propertyName,
			Object value) {
		// TODO Auto-generated method stub
		try {
			return this.articleDao.getBy(construct, propertyName, value);
		} catch (Exception e) {
			Article obj = new Article();
			obj.setTitle("文章错误");
			return obj;
		}
	}

	
}
