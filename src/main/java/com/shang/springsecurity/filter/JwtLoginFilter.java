package com.shang.springsecurity.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shang.springsecurity.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Jwt 登录验证filter</p>
 *
 * @Author: 尚家朋
 * @Email: 835091332@qq.com
 * @Date: 2020/4/21 22:49
 */
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {


    /**
     * 构造方法
     * @param defaultFilterProcessesUrl
     * @param authenticationManager
     */
    public JwtLoginFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher(defaultFilterProcessesUrl));
        setAuthenticationManager(authenticationManager);
    }

    /**
     * 提取用户信息用作登录
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        //生成一个json用户
        User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        return getAuthenticationManager().authenticate(authenticationToken);
    }


    /**
     * 登录成功的回调函数
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
     FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //获取用户的登录角色
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        //如果有多个角色,把多个角色用逗号拼接起来
        StringBuffer sb = new StringBuffer();
        for (GrantedAuthority authority : authorities) {
            sb.append(authority.getAuthority()).append(",");
        }
        //生成 access_token
        String token = Jwts.builder()
                .claim("authorities",sb)  //角色
                .setSubject(authResult.getName())//主题--用户名
                .setExpiration(new Date(System.currentTimeMillis()+60*60*1000))//有效时间60分钟
                .signWith(SignatureAlgorithm.HS512,"shangjp")//前面算法
                .compact();
        //登录成功之后,使用json返回token和登录成功的msg
        Map<String,String> map =new HashMap<>();
        map.put("token",token);
        map.put("msg","登录成功");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(map));
        out.flush();
        out.close();
    }

    /**
     * 登录失败的回调函数
     * @param request
     * @param response
     * @param failed
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
     AuthenticationException failed) throws IOException, ServletException {
        //登录失败直接返回一个json错误信息
        Map<String,String> map =new HashMap<>();
        map.put("msg","登录失败");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(map));
        out.flush();
        out.close();
    }
}
