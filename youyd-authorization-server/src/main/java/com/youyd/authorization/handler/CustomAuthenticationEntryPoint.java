package com.youyd.authorization.handler;

import com.youyd.enums.StatusEnum;
import com.youyd.utils.JsonData;
import com.youyd.utils.JsonUtil;
import com.youyd.utils.LogBack;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户没有登录时返回给前端的数据
 * @author : LGG
 * @create : 2019/6/10 2:03:05
 **/

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
		httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
//		httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		// 如果这里状态改为HttpServletResponse.SC_UNAUTHORIZED 会导致feign之间调用异常 see https://xujin.org/sc/sc-feign-4xx/
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		LogBack.error("用户没有登录时返回给前端的数据");
		JsonData jsonData =  new JsonData(StatusEnum.LOGIN_EXPIRED);
		httpServletResponse.getWriter().write(JsonUtil.toJsonString(jsonData));
	}
}
