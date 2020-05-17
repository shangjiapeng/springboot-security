package com.shang.springsecurity.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import sun.misc.Contended;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>
 * 自定义过滤器继承自 GenericFilterBean，并实现其中的 doFilter 方法，
 * 在 doFilter 方法中，当请求方法是 POST，并且请求地址是 /doLogin 时，
 * 获取参数中的 code 字段值，该字段保存了用户从前端页面传来的验证码，
 * 然后获取 session 中保存的验证码，
 * 如果用户没有传来验证码，则抛出验证码不能为空异常，
 * 如果用户传入了验证码，则判断验证码是否正确，
 * 如果不正确则抛出异常，否则执行 chain.doFilter(request, response);
 * 使请求继续向下走
 * </p>
 *
 * @Author: shangjp
 * @Email: shangjp@163.com
 * @Date: 2020/4/25 18:03
 */
@Component
public class VerifyCodeFilter extends GenericFilterBean {

    private String defaultFilterProcessUrl = "/login";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //强转两个类型
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if ("POST".equalsIgnoreCase(request.getMethod()) && defaultFilterProcessUrl.equals(request.getServletPath())) {
            //验证码比对
            String verifyCode = request.getParameter("verifyCode");
            HttpSession session = request.getSession();
            String verify_code = (String) session.getAttribute("verify_code");
            //如果出错返回错误信息
            if (StringUtils.isEmpty(verifyCode)) {
//                throw new AuthenticationServiceException("验证码不能为空");
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.write(new ObjectMapper().writeValueAsString("验证码不能为空"));
                out.flush();
                out.close();
            } else if (!verify_code.equalsIgnoreCase(verifyCode)) {
//                throw new AuthenticationServiceException("验证码错误");
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.write(new ObjectMapper().writeValueAsString("验证码错误"));
                out.flush();
                out.close();
            }else {
                //走到这一步说明验证码正确,放行
                filterChain.doFilter(request, response);
            }
        }
        //走到这一步说明不是访问登录的接口,不用验证
        filterChain.doFilter(request, response);
    }
}
