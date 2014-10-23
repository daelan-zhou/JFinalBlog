package net.dreamlu.controller.admin;

import net.dreamlu.config.Consts;
import net.dreamlu.interceptor.AdminInterceptor;
import net.dreamlu.utils.QiniuUtil;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;
import com.jfinal.upload.UploadFile;

/**
 * 百度编辑器
 * 
 * @author L.cm
 * @date Nov 27, 2013 9:48:07 PM
 */
@Before(AdminInterceptor.class)
@ControllerBind(controllerKey="/ueditor")
public class UeditorController extends Controller {

	private static final Logger logger = Logger.getLogger(UeditorController.class);

	// 上传图片
	public void uploadImg() {
		// 选择保存目录
		String fetch = getPara("fetch");
		if (StrKit.notBlank(fetch)) {
			renderJavascript("updateSavePath( [\"" + Consts.QINIU_BUCKET + "\"] );");
			return;
		}
		// 临时目录
		String tmpfsPath = Consts.TMP_FS_PATH;
		try {
			UploadFile file = getFile("upfile", tmpfsPath);
			String fileName = file.getFileName();
			boolean isFail = QiniuUtil.checkFileType(fileName, true);
			if (isFail) {
				setAttr("state", "图片类型不支持！");
			} else {
				// 上传文件
				logger.error("上传文件");
				setAttr("url", QiniuUtil.uploadImg(file.getFile().getPath(), fileName));
				setAttr("state", "SUCCESS");

			}
			setAttr("original", fileName);
		} catch (Exception e) {
			logger.error(e.getMessage());
			setAttr("state", "图片上传失败，请稍后重试！");
		}
		// "SUCCESS", "SUCCESS"
		setAttr("title", getPara("pictitle"));
		renderJson(new String[] { "original", "url", "title", "state" });
		// "{'original':'"+up.getOriginalName()+"','url':'"+up.getUrl()+"','title':'"+up.getTitle()+"','state':'"+up.getState()+"'}"
	}

	// 上传文件
	public void uploadFile() {
		String tmpfsPath = Consts.TMP_FS_PATH;
		UploadFile file = getFile("upfile", tmpfsPath);
		String fileName = file.getFileName();
		boolean isFail = QiniuUtil.checkFileType(fileName, false);
		if (isFail) {
			setAttr("state", "文件类型不支持！");
		} else {
			try {
				setAttr("url", QiniuUtil.uploadFile(file.getFile().getPath(),
						fileName));
				setAttr("state", "SUCCESS");
			} catch (Exception e) {
				logger.error(e.getMessage());
				setAttr("state", "文件上传失败，请稍后重试！");
			}
		}
		setAttr("original", fileName);
		setAttr("fileType", QiniuUtil.getFileType(fileName));
		renderJson(new String[] { "url", "fileType", "state", "original" });
		// {'url':'"+up.getUrl()+"','fileType':'"+up.getType()+"','state':'"+up.getState()+"','original':'"+up.getOriginalName()+"'}"
	}

	// 涂鸦上传
	public void uploadScrawl() throws Exception {
		String tmpfsPath = Consts.TMP_FS_PATH;
		// jfinal必须先getfile,
		try {
			UploadFile file = getFile("upfile", tmpfsPath, 1024 * 1024);
			String url = QiniuUtil.uploadImg(file.getFile().getPath(), file.getFileName());
			// <script>parent.ue_callback('" + up.getUrl() + "','" +
			// up.getState() + "')</script>
			renderText("<script>parent.ue_callback('" + url + "','SUCCESS')</script>");
		} catch (Exception e) {
			logger.error(e.getMessage());
			String content = getPara("content");
			setAttr("url", QiniuUtil.uploadBase64(content, tmpfsPath));
			setAttr("state", "SUCCESS");
			renderJson(new String[] { "url", "state" });
			// {'url':'" + up.getUrl()+"',state:'"+up.getState()+"'}
		}
	}

	// 图片管理
	public void imageManager() {
		renderText(QiniuUtil.listObject(50));
	}
}
