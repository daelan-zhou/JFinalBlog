package com.jfinal.ext.plugin.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.jfinal.core.JFinal;
import com.jfinal.ext.plugin.redis.JedisKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

/**
 * Model扩展
 * @author L.cm
 * email: 596392912@qq.com
 * site:  http://www.dreamlu.net
 * @date 2014-4-11 8:31:33
 */
@SuppressWarnings({ "rawtypes" })
public abstract class DbModel<M extends Model> extends Model<M> {

	private static final long serialVersionUID = -6215428115177000482L;
	static final Object[] NULL_PARA_ARRAY = new Object[0];
	static boolean devMode = JFinal.me().getConstants().getDevMode();
	
	/**
	 * 用来针对DataTables封装的分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param select
	 * @param sqlExceptSelect
	 * @param paras
	 * @return
	 */
	public Map<String, Object> paginateDataTables(int pageNumber, int pageSize,
			String select, String sqlExceptSelect, String sEcho, Object... paras) {
		Page<M> pages = super.paginate(pageNumber, pageSize, select, sqlExceptSelect, paras);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("iTotalRecords", pages.getTotalRow());
		map.put("iTotalDisplayRecords", pages.getTotalRow());
		map.put("sEcho", sEcho);
		map.put("aaData", pages.getList());
		return map;
	}

	/**
	 * Find model by cache.
	 * @param sql
	 * @param paras
	 * @return M	返回类型 * add limit 1 in sql
	 * @throws
	 */
	public M findFirstByCache(String sql, Object... paras) {
		List<M> result = findByCache(sql, paras);
		return result.size() > 0 ? result.get(0) : null;
	}
	
	/**
	 * Find model by cache.
	 * @param key
	 * @param sql
	 * @param paras
	 * @return M	返回类型 * add limit 1 in sql
	 * @throws
	 */
	public M findFirstByCache(String key, String sql, Object... paras) {
		List<M> result = findByCache(key, sql, paras);
		return result.size() > 0 ? result.get(0) : null;
	}
	
	/**
	 * Find model by cache.
	 * @see #find(String, Object...)
	 * @param cacheName the cache name
	 * @param key the key used to get date from cache
	 * @return the list of Model
	 */
	public List<M> findByCache(String sql, Object... paras) {
		String key = initCache(null, null, sql, null, paras);
		return findByCache(key, sql, paras);
	}
	
	/**
	 * Find model by cache.
	 * @see #find(key, String, Object...)
	 * @param cacheName the cache name
	 * @param key the key used to get date from cache
	 * @return the list of Model
	 */
	public List<M> findByCache(String key, String sql, Object... paras) {
		ArrayList<M> result = null;
		if (devMode) {
			result =  (ArrayList<M>) find(sql, paras);
		} else {
			if (JedisKit.exists(key)) {
				result = JedisKit.get(key);
			}else{
				result = (ArrayList<M>) find(sql, paras);
				JedisKit.set(key, result);
			}
		}
		return result;
	}
	
	/**
	 * Find model by cache.
	 * @param sql
	 * @param paras
	 * @return M	返回类型
	 * @throws
	 */
	public M findFirstByCache(int timeout,String sql, Object... paras) {
		List<M> result = findByCache(timeout, sql, paras);
		return result.size() > 0 ? result.get(0) : null;
	}
	
	/**
	 * Find model by cache.
	 * @param key
	 * @param sql
	 * @param paras
	 * @return M	返回类型
	 * @throws
	 */
	public M findFirstByCache(String key, int timeout,String sql, Object... paras) {
		List<M> result = findByCache(key, timeout, sql, paras);
		return result.size() > 0 ? result.get(0) : null;
	}
	
	/**
	 * Find model by cache.
	 * @see #find(String, Object...)
	 * @param cacheName the cache name
	 * @param key the key used to get date from cache
	 * @return the list of Model
	 */
	public List<M> findByCache(int timeout, String sql, Object... paras) {
		String key = initCache(null, null, sql, null, paras);
		return findByCache(key, timeout, sql, paras);
	}
	
	/**
	 * Find model by cache.
	 * @see #find(String, String, Object...)
	 * @param cacheName the cache name
	 * @param key the key used to get date from cache
	 * @return the list of Model
	 */
	public List<M> findByCache(String key, int timeout, String sql, Object... paras) {
		ArrayList<M> result = null;
		if (devMode) {
			result = (ArrayList<M>) find(sql, paras);
		} else {
			if (JedisKit .exists(key)) {
				result = JedisKit.get(key);
			}else{
				result = (ArrayList<M>) find(sql, paras);
				JedisKit.set(key, result, timeout);
			}
		}
		return result;
	}
	
	/**
	 * @see #findByCache(String)
	 */
	public List<M> findByCache(String sql) {
		return findByCache(sql, NULL_PARA_ARRAY);
	}
	
	/**
	 * @see #findByCache(String, String)
	 */
	public List<M> findByCache(String key, String sql) {
		return findByCache(key, sql, NULL_PARA_ARRAY);
	}
	
	/**
	 * @see #findByCache(String, String)
	 */
	public List<M> findByCache(String key, int timeout, String sql) {
		return findByCache(key, timeout, sql, NULL_PARA_ARRAY);
	}
	
	/**
	 * Paginate by cache.
	 * @see #paginate(int, int, String, String, Object...)
	 * @param cacheName the cache name
	 * @param key the key used to get date from cache
	 * @return Page
	 */
	public Page<M> paginateByCache(int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) {
		
		String key = initCache(pageNumber, pageSize, select, sqlExceptSelect, paras);
		Page<M> result = null;
		if (devMode) {
			result = paginate(pageNumber, pageSize, select, sqlExceptSelect, paras);
		} else {
			if (JedisKit.exists(key)) {
				result = JedisKit.get(key);
			}else{
				result = paginate(pageNumber, pageSize, select, sqlExceptSelect, paras);
				JedisKit.set(key, result);
			}
		}
		return result;
	}
	
	/**
	 * Paginate by cache.
	 * @see #paginate(int, int, String, String, Object...)
	 * @param cacheName the cache name
	 * @param key the key used to get date from cache
	 * @return Page
	 */
	public Page<M> paginateByCache(int timeout, int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) {
		String key = initCache(pageNumber, pageSize, select, sqlExceptSelect, paras);
		Page<M> result = null;
		if (devMode) {
			result = paginate(pageNumber, pageSize, select, sqlExceptSelect, paras);
		} else {
			if (JedisKit.exists(key)) {
				result = JedisKit.get(key);
			}else{
				result = paginate(pageNumber, pageSize, select, sqlExceptSelect, paras);
				JedisKit.set(key, result, timeout);
			}
		}
		return result;
	}
	
	/**
	 * @see #paginateByCache(String, Object, int, int, String, String, Object...)
	 */
	public Page<M> paginateByCache(int pageNumber, int pageSize, String select, String sqlExceptSelect) {
		return paginateByCache(pageNumber, pageSize, select, sqlExceptSelect, NULL_PARA_ARRAY);
	}

	/**
	 * memcache key 使用sql和paras
	 * @param pageNumber
	 * @param pageSize
	 * @param select
	 * @param sqlExceptSelect
	 * @param paras
	 * @return	设定文件
	 * @return String	返回类型
	 * @throws
	 */
	private String initCache(Integer pageNumber, Integer pageSize, String select, String sqlExceptSelect, Object... paras) {
		StringBuilder key = new StringBuilder(select);
		if (null != pageNumber) {
			key.append(pageNumber);
		}
		if (null != pageSize) {
			key.append(pageSize);
		}
		if (null != sqlExceptSelect) {
			key.append(sqlExceptSelect);
		}
		if (null != paras) {
			for (Object object : paras) {
				key.append(object);
			}
		}
		return DigestUtils.md5Hex(key.toString());
	}
}
