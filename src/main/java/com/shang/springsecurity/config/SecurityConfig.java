package com.shang.springsecurity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shang.springsecurity.filter.JwtFilter;
import com.shang.springsecurity.filter.JwtLoginFilter;
import com.shang.springsecurity.filter.VerifyCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.PrintWriter;

/**
 * <p>
 * 使用配置类的方式配置Security的默认用户名和密码
 * 注意:使用配置类再代码中的配置的优先级高于配置文件中的配置
 * </p>
 *
 * @Author: 尚家朋
 * @Email: 835091332@qq.com
 * @Date: 2020/4/19 18:23
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    /**
     * Security 自带的密码加密工具
     * 对123 进行两次加密
     * $2a$10$YdjRrM5aeAP55dRI2aT.BuZOiM7cty8dlZMFaPtKFsjrUBYssni7y
     * $2a$10$HVwMu0An5uBvV/tnLJFz/eEZEsMUgzFYTVDJ7ZhWc5o.Ew/YyV0hC
     *
     * @return
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置基于内存的默认的用户信息
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                //配置基于内存的用户名,密码,角色
                .withUser("shang")
                .password("$2a$10$YdjRrM5aeAP55dRI2aT.BuZOiM7cty8dlZMFaPtKFsjrUBYssni7y")
                .roles("admin")
                .and()
                .withUser("kong")
                .password("$2a$10$HVwMu0An5uBvV/tnLJFz/eEZEsMUgzFYTVDJ7ZhWc5o.Ew/YyV0hC")
                .roles("user");
    }

//    /**
//     * 也可以使用如下的方式定义内存用户
//     *
//     * @return InMemoryUserDetailsManager
//     */
//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        //创建两个内存用户
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(User.withUsername("shang").password("123").roles("admin").build());
//        manager.createUser(User.withUsername("kong").password("456").roles("user").build());
//        return manager;
//    }


    /**
     * 配置角色等级,实现角色继承(即上级自动拥有下级的权限)
     */
    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return roleHierarchy;
    }

    /**
     * 配置放行前端样式相关的文件
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**","/login.html");
    }

    /**
     * 配置http访问接口相关
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Spring Security 的配置中，配置过滤器
        http.addFilterBefore(new VerifyCodeFilter(),UsernamePasswordAuthenticationFilter.class);
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")//配置需要某种角色才可以访问的路径
                .antMatchers("/user/**").hasRole("user")
                .antMatchers("/verifyCode").permitAll()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .anyRequest().authenticated()  //任何的访问请求都需要通过认证

                .and()
//                .addFilterBefore(new JwtLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(new JwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();
    }


}
