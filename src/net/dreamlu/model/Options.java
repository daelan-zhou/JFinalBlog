package net.dreamlu.model;

import com.jfinal.ext.plugin.db.DbModel;
import com.jfinal.ext.plugin.redis.JedisKit;

/**
 * 基本的网站信息
 * @author L.cm
 * @date 2013-5-30 下午3:21:56
 */
public class Options extends DbModel<Options> {

    private static final long serialVersionUID = 9028341703520802896L;
    public static final Options dao = new Options();

    public static final String TABLE_NAME = "options";
    public static final String ID = "id";                                   // id
    public static final String SITE_NAME = "site_name";                     // 站点名称
    public static final String SITE_URL = "site_url";                       // 站点url，无觅配置
    public static final String GIT_URL = "git_url";                         // git链接
    public static final String CDN_PATH = "cdn_path";                       // cdn path

    public static final String GOOGLE = "google";                           // google+，google搜索显示作者信息
    public static final String WB_QQ = "wb_qq";                             // qq 微博
    public static final String WB_SINA = "wb_sina";                         // sina 微博
    
    public static final String DUOSHUO_DOMAIN = "duoshuo_domain";           // 多说配置
    public static final String RECORD_NUMBER = "record_number";             // 备案号
    public static final String SITE_VERSION = "site_version";               // 网站版本

    public static final String CACHE_OPTIONS_KEY = "cache_options_key"; 

    /**
     * 从缓存中读取
     */
    public Options findByCache() {
    	String sql = "select * from options limit 1";
    	return dao.findFirstByCache(CACHE_OPTIONS_KEY, sql);
    }

    /**
     * 清除缓存
     */
    public void removeCache() {
    	JedisKit.del(CACHE_OPTIONS_KEY);
    }

    /**
     * 添加配置
     */
	public boolean saveOptions() {
		removeCache();
		return this.save();
	}

	/**
     * 跟新配置
     */
	public boolean updateOptions() {
		removeCache(); 
		return this.update();
	}
}
