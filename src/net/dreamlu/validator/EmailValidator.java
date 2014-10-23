package net.dreamlu.validator;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
 * 邮箱校验
 * @author L.cm
 * @date 2013-6-2 上午11:00:57
 */
public class EmailValidator extends Validator {

	@Override
	protected void validate(Controller c) {
		validateEmail("email", "status", "1");
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson(new String[]{"status"});
	}

}
