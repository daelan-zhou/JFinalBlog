package net.dreamlu.kit;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import jetbrick.template.JetEngine;
import jetbrick.template.JetTemplate;
import jetbrick.template.web.JetWebEngineLoader;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.ext.plugin.config.ConfigKit;
import com.jfinal.log.Logger;

/**
 * 邮件发送
 * @author L.cm
 * @date 2013-6-2 下午3:15:55
 * <url>http://developer.baidu.com/bae/bms/list/</url>
 */
public class MailKit {
	
	private static final Logger logger = Logger.getLogger(MailKit.class);
	
	private static final String emailAddressPattern = "\\b(^['_A-Za-z0-9-]+(\\.['_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";

	private static final String HOST		= ConfigKit.getStr("mail.smtp.host");				// 163免费企业邮箱的smtp服务器
	private static final String FROM		= ConfigKit.getStr("mail.stmp.username");			// 我的企业邮箱地址
	private static final int PORT			= ConfigKit.getInt("mail.smtp.port", 25);			// 邮件端口
	private static final int TIMEOUT		= ConfigKit.getInt("mail.smtp.timeout", 2000);		// 邮件超时
	private static final String MAIL_NAME	= ConfigKit.getStr("mail.stmp.title");				// 邮件title
	private static final String USERNAME	= ConfigKit.getStr("mail.stmp.username");			// 用户名
	private static final String PASSWORD	= ConfigKit.getStr("mail.stmp.pwd");				// 密码
	private static final boolean AUTH		= ConfigKit.getBoolean("mail.smtp.auth", true);		// 时候校验
	private static final boolean DEBUG		= ConfigKit.getBoolean("mail.smtp.debug", false);	// 是否debug
	
	private static final int FAILED_RESEND_TIMES = 3; // 重试次数
	
	private static final int THREAD_SLEEP = 3000; // 休眠时间 threadsleep
	
	/* 函数共用字段 */
	private static Message message = initMessage();
	
	private static final Message initMessage() {
		Properties props = new Properties();
		props.put("mail.smtp.host", HOST);
		props.put("mail.smtp.auth", AUTH);
		props.put("mail.smtp.port", PORT);
		props.put("mail.stmp.timeout", TIMEOUT);
		// getDefaultInstance 会造成JFinal debug模式的自动重启报错
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USERNAME, PASSWORD);
			}
		});
		session.setDebug(DEBUG);
		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(MimeUtility.encodeText(MAIL_NAME)+ "<"+ FROM + ">"));
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return message;
	}
	
	/**
	 * 同步发送邮件
	 * @param subject
	 * @param content
	 * @param to
	 * @throws MessagingException 
	 */
	private static final synchronized void sendMail(String subject, String content, String... to) throws MessagingException {
		to = validMails(to);
		// 检验过滤结果
		if (ArrayUtils.isEmpty(to)) {
			return;
		}
		message.setSubject(subject);
		message.setContent(content, "text/html;charset=UTF-8");
		List<Address> list = new ArrayList<Address>();
		for (String mail : to) {
			list.add(new InternetAddress(mail));
		}
		Transport.send(message, list.toArray(new Address[0]));
	}
	
	/**
	 * 校验邮箱地址
	 * @param mails
	 * @return
	 */
	public static String[] validMails(String... mails) {
		// 没有任何接收邮箱
		if (ArrayUtils.isEmpty(mails)) {
			return null;
		}
		// 验证邮箱格式合法性
		Pattern pattern = Pattern.compile(emailAddressPattern);
		List<String> mailList = new ArrayList<String>();
		for (String tmp : mails) {
			if (StringUtils.isEmpty(tmp)) {
				continue;
			}
			Matcher matcher = pattern.matcher(tmp);
			if (matcher.matches()) {
				mailList.add(tmp);
			}
		}
		// 只给合法的地址发送邮件
		if (mailList.size() != mails.length) {
			if (mailList.size() == 0) {
				return null;
			}
			return mailList.toArray(new String[0]);
		}
		// 所有邮箱均合法
		return mails;
	}
	
	/**
	 * 邮件发送线程
	 */
	static final class MailSendThend extends Thread {
		private String subject;
		private String content;
		private String[] mails;

		public MailSendThend(String subject, String content, String[] mails) {
			this.subject = subject;
			this.content = content;
			this.mails = mails;
		}

		@Override
		public void run() {
			int sendTimes = 0; // 重试次数
			boolean isTryAgain = false, isFirst = true;
			do {
				try {
					while (isFirst || isTryAgain) {
						sendMail(subject, content, mails);
						isTryAgain = isFirst = false;
						Thread.sleep(THREAD_SLEEP);
					}
				} catch (Exception e) {
					e.printStackTrace();
					try {
						Thread.sleep(THREAD_SLEEP);
					} catch (Exception e1) {
						logger.error(e1.getMessage(), e1);
					}
					isTryAgain = true;
				}
			} while (sendTimes++ < FAILED_RESEND_TIMES);
		}
	}
	
	/**
	 * 同步发送邮件
	 * @param subject
	 * @param content
	 * @param to
	 */
	public static void syncSend(String subject, String content, String... to) {
		int sendTimes = 0; // 重试次数
		boolean isTryAgain = false, isFirst = true;
		do {
			try {
				while (isFirst || isTryAgain) {
					sendMail(subject, content, to);
					isTryAgain = isFirst = false;
					Thread.sleep(THREAD_SLEEP);
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(THREAD_SLEEP);
				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
				isTryAgain = true;
			}
		} while (sendTimes++ < FAILED_RESEND_TIMES);
	}
	
	/**
	 * 异步发送邮件
	 * @param subject
	 * @param content
	 * @param to
	 */
	public static void asynSend(String subject, String content, String... to) {
		new MailSendThend(subject, content, to).start();
	}

	/**
	 * 发送模板邮件
	 * @param subject
	 * @param to
	 * @param model
	 * @param tempname
	 * @return
	 */
	public static boolean sendTemplateMail(String subject, String to, Map<String, Object> model, String tempname){
		try {
			// 创建一个 JetEngine
			JetEngine engine = JetWebEngineLoader.getJetEngine();
			// 获取一个模板对象
			JetTemplate template = engine.getTemplate("/mail/" + tempname);
			// 创建 context 对象
			StringWriter writer = new StringWriter();
			template.render(model, writer);
			// 解决标题乱码
			subject = "=?UTF-8?B?" + Base64.encodeBase64String(subject.getBytes("UTF-8")) + "?=";
			syncSend(subject, writer.toString(), to);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}