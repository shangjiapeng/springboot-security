package com.shang.springsecurity.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * <p>这个filter主要是校验用户访问接口时携带的token</p>
 *
 * @Author: 尚家朋
 * @Email: 835091332@qq.com
 * @Date: 2020/4/22 13:07
 */
public class JwtFilter extends GenericFilterBean {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
         FilterChain filterChain) throws IOException, ServletException {
        //类型装换
        HttpServletRequest request =(HttpServletRequest) servletRequest;
        //从请求头中获取token
        String access_token = request.getHeader("authorization");
        Jws<Claims> jws = Jwts.parser().setSigningKey("shangjp")
                .parseClaimsJws(access_token.replace("Bearer", ""));
        //获取用户声明实体
        Claims claims = jws.getBody();
        //用户名
        String username = claims.getSubject();
        //角色
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get("authorization"));
        //new 一个token
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, authorities);
        //设置token
        SecurityContextHolder.getContext().setAuthentication(token);
        //让过滤器继续往下走
        filterChain.doFilter(servletRequest,servletResponse);

    }
}
