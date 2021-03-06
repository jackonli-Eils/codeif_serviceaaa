package com.ibole.auth.validate.impl.sms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibole.auth.validate.AbstractValidateCodeProcessor;
import com.ibole.auth.validate.impl.ValidateCode;
import com.ibole.constant.CommonConst;
import com.ibole.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.HashMap;

/**
 * 短信验证码处理器
 * 
 * @author zhailiang
 *
 */
@Component
public class SmsValidateCodeProcessor extends AbstractValidateCodeProcessor<ValidateCode> {

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * 短信验证码发送器
	 */
	@Autowired
	private SmsCodeSender smsCodeSender;
	
	@Override
	protected void send(ServletWebRequest request, ValidateCode validateCode) throws Exception {
        String paramName = CommonConst.DEFAULT_PARAMETER_NAME_PHONE;
        String phone = ServletRequestUtils.getRequiredStringParameter(request.getRequest(), paramName);
        smsCodeSender.send(phone, validateCode.getCode());

        HashMap<Object, Object> map = new HashMap<>();
        map.put("deviceId", request.getHeader("DEVICE-ID"));
        // TODO 使用模拟手机验证码，配置短信发送器后可再使用真实的
        map.put("tempCode", validateCode.getCode());

        request.getResponse().setContentType("application/json;charset=UTF-8");
        request.getResponse().getWriter().write(objectMapper.writeValueAsString(JsonData.success(map)));
    }

}
