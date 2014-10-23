package net.dreamlu.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.ext.plugin.db.DbModel;
import com.jfinal.kit.StrKit;

/**
 * 微信留言
 * @author L.cm
 * email: 596392912@qq.com
 * site:  http://www.dreamlu.net
 * @date 2013-09-19 01:35:57
 */
public class WxLeaveMsg extends DbModel<WxLeaveMsg> {
	
	private static final long serialVersionUID = 6876954336686053233L;

	public static final String TABLE_NAME = "wx_leave_msg";
	public static final String ID = "id";
	public static final String WX_USER = "wx_user";
	public static final String MSG = "msg";
	
	public static final WxLeaveMsg dao = new WxLeaveMsg();
	
	public Map<String, Object> pageDataTables(int pageNum, int pageSize, String sEcho,
			String search) {
		final List<Object> parameters = new ArrayList<Object>();
		String select = "SELECT m.*";
		StringBuilder sqlExceptSelect = new StringBuilder(" FROM wx_leave_msg m");
		if (StrKit.notBlank(search)) {
			sqlExceptSelect.append(" AND m.msg like ?");
			parameters.add("%" + search + "%");
		}
		sqlExceptSelect.append(" ORDER BY m.id DESC");
		return dao.paginateDataTables(pageNum, pageSize, select, sqlExceptSelect.toString(), sEcho, parameters.toArray());
	}
}