package net.dreamlu.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jetbrick.template.web.jfinal.JetTemplateRenderFactory;
import net.dreamlu.handler.HttpCacheHandler;
import net.dreamlu.handler.QRcodeHandler;
import net.dreamlu.handler.SessionIdHandler;
import net.dreamlu.handler.XmlHandler;
import net.dreamlu.handler.XssHandler;
import net.dreamlu.interceptor.CookieLoginInterceptor;
import net.dreamlu.interceptor.InstallInterceptor;
import net.dreamlu.interceptor.OptionsInterceptor;
import net.dreamlu.interceptor.SessionInterceptor;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.RenderingTimeHandler;
import com.jfinal.ext.plugin.config.ConfigKit;
import com.jfinal.ext.plugin.config.ConfigPlugin;
import com.jfinal.ext.plugin.db.DbModel;
import com.jfinal.ext.plugin.redis.JedisPlugin;
import com.jfinal.ext.plugin.tablebind.AutoTableBindPlugin;
import com.jfinal.ext.plugin.tablebind.SimpleNameStyles;
import com.jfinal.ext.route.AutoBindRoutes;
import com.jfinal.plugin.activerecord.SqlReporter;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.plugin.druid.IDruidStatViewAuth;

/**
 * 项目主要配置部分
 * @author L.cm
 * email: 596392912@qq.com
 * site:  http://www.dreamlu.net
 * @date 2013-5-29 下午9:12:23
 */
public class JFWebConfig extends JFinalConfig {

    /**
     * 常量配置
     */
    @Override
    public void configConstant(Constants me) {
        // 扒皮的config插件
        new ConfigPlugin(".*.txt").reload(false).start();
        // 开发模式
        me.setDevMode(ConfigKit.getBoolean("isdev", false));
        // 设置JetTemplate模板
        me.setMainRenderFactory(new JetTemplateRenderFactory());
        // Template end
        me.setError404View("/error/404.vm");
        me.setError500View("/error/500.vm");
    }

    /**
     * 路由配置
     */
    @Override
    public void configRoute(Routes me) {
        me.add(new AutoBindRoutes().autoScan(false));
    }

    /**
     * 全局拦截器
     */
    @Override
    public void configInterceptor(Interceptors me) {
        me.add(new InstallInterceptor());
        me.add(new CookieLoginInterceptor());
        me.add(new SessionInterceptor());
        me.add(new OptionsInterceptor());
    }

    /**
     * 配置处理器
     */
    @Override
    public void configHandler(Handlers me) {
        me.add(new RenderingTimeHandler());
        me.add(new SessionIdHandler());
        me.add(new HttpCacheHandler());
        me.add(new XmlHandler());
        me.add(new QRcodeHandler());
        // Druid监控
        DruidStatViewHandler druidViewHandler = new DruidStatViewHandler(
                "/admin/druid", new IDruidStatViewAuth() {
            public boolean isPermitted(HttpServletRequest request) {
                HttpSession session = request.getSession();
                Object user = session.getAttribute(Consts.USER_SESSION);
                return user != null;
            }
        });
        me.add(druidViewHandler);
        me.add(new XssHandler("/admin")); // `/admin*`为排除的目录
    }
    
    /**
     * 配置插件
     */
    @SuppressWarnings("unchecked")
    @Override
    public void configPlugin(Plugins me) {
        // 配置Druid数据库连接池插件
        DruidPlugin dp = new DruidPlugin(ConfigKit.getStr("db.jdbcUrl"), ConfigKit.getStr("db.user"), ConfigKit.getStr("db.password"));
        dp.setTestWhileIdle(true).setTestOnBorrow(true).setTestOnReturn(true);
        dp.addFilter(new StatFilter());
        WallFilter wall = new WallFilter();
        wall.setDbType("mysql");
        dp.addFilter(wall);
        me.add(dp);
        
        boolean isDev = ConfigKit.getBoolean("isdev", false);

        // 扒皮的自动绑定插件
        AutoTableBindPlugin atbp = new AutoTableBindPlugin(dp, SimpleNameStyles.LOWER_UNDERLINE);
        atbp.addExcludeClasses(DbModel.class);
        atbp.setShowSql(isDev);
        me.add(atbp);
        // sql记录
        SqlReporter.setLogger(isDev);
        // redis
        me.add(new JedisPlugin());
        // 添加Quartz定时调度
//        me.add(new QuartzPlugin());
    }

    /**
     * 运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
     */
    public static void main(String[] args) {
        JFinal.start("WebContent", 8011, "/", 10);
    }
}
