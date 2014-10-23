package net.dreamlu.model;


import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;

import com.jfinal.plugin.activerecord.Model;

public class MailVerify extends Model<MailVerify> {
    
    private static final long serialVersionUID = -2208881735662609833L;
    
    public static final MailVerify dao = new MailVerify();

    public static final String TABLE_NAME = "mail_verify";
    public static final String ID = "id";					// id
    public static final String USER_ID = "user_id";			// user id,绑定的其他的id，wbid
	public static final String VERIFY_CODE = "verify_code";	// 验证码
	public static final String CREATE_TIME = "create_time"; // 添加时间
	
	/**
	 * 根据code查找
	 * @param code
	 * @return
	 */
	public MailVerify getByCode(String code) {
		String sql = "SELECT m.* FROM mail_verify m WHERE hour(TIMEDIFF(NOW(), m.create_time)) < 24 AND m.verify_code = ? limit 1";
		return dao.findFirst(sql, code);
	}

	/**
	 * createVerify
	 * @param @return    设定文件
	 * @return MailVerify    返回类型
	 * @throws
	 */
	public boolean createVerify(MailVerify verify) {
		String value = DigestUtils.md5Hex(System.currentTimeMillis() + "");
		verify.set(MailVerify.CREATE_TIME, new Date());
		verify.set(MailVerify.VERIFY_CODE, value);
		return verify.save();
	}
}
