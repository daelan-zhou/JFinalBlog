package net.dreamlu.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.dreamlu.config.Consts;

import com.jfinal.ext.plugin.db.DbModel;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

/**
 * blog 实体
 * @author L.cm
 * email: 596392912@qq.com
 * site:  http://www.dreamlu.net
 * @date 2013-5-7 上午9:30:18
 */
public class Blog extends DbModel<Blog> {

    private static final long serialVersionUID = -2208881735662609833L;
    public static final Blog dao = new Blog();

    public static final String TABLE_NAME = "blog";
    public static final String ID = "id";                    // id
    public static final String TITLE = "title";                // 标题
    public static final String CONTENT = "content";            // 内容
    public static final String USER_ID = "user_id";            // user id
    public static final String VIEW_COUNT = "view_count";    // 查看数
    public static final String BLOG_TYPE = "blog_type";        // 默认：0 （原创） 收藏：1   标签：2
    public static final String[] TYPE_NAME = new String[]{"原创", "收藏", "标签"};
    public static final String[] TYPE_URL  = new String[]{"/blogs", "/favorites", "/tags"};
    public static final String SHARE_URL = "share_url";        // 分享 url
    public static final String DEL_STATUS = "del_status";    // 是否删除 默认：0  删除：1
    public static final int DEL_N = 0;    // 未删
    public static final int DEL_Y = 1;    // 已删
    public static final String CREATE_TIME = "create_time"; // 添加时间
    public static final String UPDATE_TIME = "update_time"; // 更新时间
    
    public static final int BLOG_SIZE = 497;                 // 首页显示博文长度
    /**
     * 分页查找
     * @param @param pageNum
     * @param @param pageSize
     * @param @return    设定文件
     * @return Page<Blog>    返回类型
     * @throws
     */
    public Page<Blog> page(Integer pageNum, int pageSize, Map<String, Object> result) {
        final List<Object> parameters = new ArrayList<Object>();
        String select = "SELECT b.*, u.nick_name, u.url";
        StringBuilder sqlOutSelect = new StringBuilder("FROM blog AS b ,user AS u WHERE b.user_id = u.id AND b.del_status = 0 AND u.del_status = 0");
        if(StrKit.notNull(result.get(Blog.BLOG_TYPE))) {
            sqlOutSelect.append(" AND b.blog_type = ? ");
            parameters.add(result.get(Blog.BLOG_TYPE));
        }
        if(StrKit.notNull(result.get("s"))){
            sqlOutSelect.append(" AND (b.title like ? OR b.content like ?) ");
            parameters.add("%" + result.get("s") + "%");
            parameters.add("%" + result.get("s") + "%");
        }
        sqlOutSelect.append(" ORDER BY b.id DESC");
        return dao.paginateByCache(Consts.CACHE_TIME_MINI, pageNum, pageSize, select, sqlOutSelect.toString(), parameters.toArray());
    }

    /**
     * 根据标签查找博文
     * @param @param pageNum
     * @param @param blogPageSize
     * @param @param tags
     * @param @return    设定文件
     * @return Page<Blog>    返回类型
     * @throws
     */
    public Page<Blog> pageByTags(int pageNum, int pageSize, String tags) {
        String select = "SELECT b.*, u.nick_name, u.url";
        String sqlOutSelect = "FROM tags t,blog_tag bt, blog b, user u WHERE t.tag_name = ? AND t.id = bt.tag_id AND bt.blog_id = b.id AND b.user_id = u.id AND b.del_status = 0 AND u.del_status = 0 ORDER BY b.id DESC";
        return dao.paginateByCache(Consts.CACHE_TIME_MINI, pageNum, pageSize, select, sqlOutSelect.toString(), tags);
    }
    
    /**
     * 根据id查找blog相关信息
     * @param @param paraToInt
     * @param @return    设定文件
     * @return Blog    返回类型
     * @throws
     */
    public Blog findFallById(Integer blogId) {
        String sql = "SELECT b.*, u.nick_name, u.url, u.head_photo, u.signature FROM blog AS b ,user AS u WHERE b.id = ? AND b.user_id = u.id AND b.del_status = 0 AND u.del_status = 0 limit 1";
        return dao.findFirstByCache(Consts.CACHE_TIME_MINI, sql, blogId);
    }

    /**
     * 最近的5条
     * @param @return    设定文件
     * @return List<Blog>    返回类型
     * @throws
     */
    public List<Blog> findLateList(int size) {
        String sql = "SELECT b.id, b.title, b.content, b.update_time FROM blog AS b WHERE b.del_status = 0 ORDER BY b.id DESC LIMIT ?";
        return dao.findByCache(Consts.CACHE_TIME_MINI, sql, size);
    }

    /**
     * 点击最多的5条
     * @param @return    设定文件
     * @return List<Blog>    返回类型
     * @throws
     */
    public List<Blog> findHotList(int size) {
        String sql = "SELECT b.id, b.title, b.update_time, b.view_count FROM blog AS b WHERE b.del_status = 0 ORDER BY b.view_count DESC LIMIT ?";
        return dao.findByCache(Consts.CACHE_TIME_MINI, sql, size);
    }
    
    /**
     * 返回所有的未删除的博文
     * @param @return    设定文件
     * @return List<Blog>    返回类型
     * @throws
     */
    public List<Blog> find4github() {
        String sql = "SELECT b.* FROM blog AS b WHERE b.del_status = 0 ORDER BY b.id DESC";
        return dao.findByCache(Consts.CACHE_TIME_MAX, sql);
    }

    /**
     * rss List
     * @param limit
     * @return
     */
    public List<Blog> rssList(int limit) {
        String sql = "SELECT b.*, u.nick_name FROM blog AS b, user AS u WHERE  b.user_id = u.id AND b.del_status = 0 AND u.del_status = 0 ORDER BY b.id DESC limit ?";
        return dao.findByCache(Consts.CACHE_TIME_MAX, sql, limit);
    }

    /**
     * 保存blog实体
     * @param @param blog    设定文件
     * @return void    返回类型
     * @throws
     */
    public boolean saveBlog() {
        return this.set("create_time", new Date()).set("update_time", new Date()).save();
    }

    /**
     * 更新blog实体
     * @param @param blog
     * @param @return    设定文件
     * @return boolean    返回类型
     * @throws
     */
    public boolean updateBlog() {
        return this.set("update_time", new Date()).update();
    }

    /**
     * datatables 查询
     * @param @param pageNum
     * @param @param iDisplayLength
     * @param @param select
     * @param @param sqlExceptSelect
     * @param @param sEcho
     * @param @param search    设定文件
     * @return void    返回类型
     * @throws
     */
    public Map<String, Object> pageDataTables(int pageNum, int pageSize, String sEcho, String search) {
        final List<Object> parameters = new ArrayList<Object>();
        String select = "SELECT b.id, b.title, b.blog_type, b.del_status, u.nick_name, b.update_time";
        StringBuilder sqlExceptSelect = new StringBuilder(" FROM blog AS b ,user AS u WHERE b.user_id = u.id");
        if (StrKit.notBlank(search)) {
            sqlExceptSelect.append(" AND (b.title like ? or b.content like ? )");
            parameters.add("%" + search + "%");
            parameters.add("%" + search + "%");
        }
        sqlExceptSelect.append(" ORDER BY b.id DESC");
        return dao.paginateDataTables(pageNum, pageSize, select, sqlExceptSelect.toString(), sEcho, parameters.toArray());
    }

    /**
     * 查找所有的博文信息SiteMap使用
     * @param @return    设定文件
     * @return List<Blog>    返回类型
     * @throws
     */
    public List<Blog> findAll() {
        String sql = "SELECT b.id, b.update_time FROM blog b";
        return dao.findByCache(Consts.CACHE_TIME_MAX, sql);
    }

    /**
     * highcharts 博文热力图
     * @param @return    设定文件
     * @return List<List<String>>    返回类型
     * @throws
     */
    public List<List<Integer>> hostBlogList() {
        String sql = "SELECT b.id, b.view_count FROM blog b WHERE b.del_status = 0";
        List<Blog> list = dao.findByCache(Consts.CACHE_TIME_MAX, sql);
        List<List<Integer>> dataList = new ArrayList<List<Integer>>();
        for (Blog blog : list) {
            List<Integer> temp = new ArrayList<Integer>();
            temp.add(blog.getInt(Blog.ID));
            temp.add(blog.getInt(Blog.VIEW_COUNT));
            dataList.add(temp);
        }
        return dataList;
    }
}
