package net.dreamlu.validator;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class PwdValidator extends Validator {

    @Override
    protected void validate(Controller c) {
        validateString("pwd", true, 6, 24, "status", "1");
    }

    @Override
    protected void handleError(Controller c) {
    	c.renderJson(new String[]{"status"});
    }

}
