package com.shang.springsecurity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shang.springsecurity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
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
//@Configuration
public class SecurityConfig1 extends WebSecurityConfigurerAdapter {

//    /**
//     * Security 自带的密码加密工具
//     * 对123 进行两次加密
//     * $2a$10$YdjRrM5aeAP55dRI2aT.BuZOiM7cty8dlZMFaPtKFsjrUBYssni7y
//     * $2a$10$HVwMu0An5uBvV/tnLJFz/eEZEsMUgzFYTVDJ7ZhWc5o.Ew/YyV0hC
//     * @return
//     */
//    @Bean
//    PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }

    /**
     * 不对密码进行加密,使用明文存储;
     *
     * @return
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

//    /**
//     * 配置基于内存的默认的用户信息
//     *
//     * @param auth
//     * @throws Exception
//     */
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                //配置基于内存的用户名,密码,角色
//                .withUser("shang")
//                .password("12345")
//                .roles("admin")
//                .and()
//                .withUser("kong")
//                .password("456")
//                .roles("user");
//    }

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
     * 将用户的数据写入数据库
     *
     * @return JdbcUserDetailsManager
     */
//    @Autowired
//    DataSource dataSource;
//
//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        //创建两个内存用户
//        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
//        //为了避免每次添加相同的用户信息
//        if (!manager.userExists("shang")) {
//            manager.createUser(User.withUsername("shang").password("123").roles("admin").build());
//        }
//        if (!manager.userExists("kong")) {
//            manager.createUser(User.withUsername("kong").password("456").roles("user").build());
//        }
//        return manager;
//    }

    /**
     * 把 SpringSecurity 接入数据库
     * @param auth
     * @throws Exception
     */
    @Resource
    UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

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
        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
    }

    /**
     * 配置http访问接口相关
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")//配置需要某种角色才可以访问的路径
                .antMatchers("/user/**").hasRole("user")
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .anyRequest().authenticated()  //任何的访问请求都需要通过认证
                .and()//and() 表示一段分配置的结束
                .formLogin()
                //设置登录页面
                .loginPage("/login.html")//表单页面中的action默认也是这个(隐藏属性)
                .loginProcessingUrl("/login")//专门配置登录接口-必须是post请求(如果不配置,默认和loginPage是一样的)
                .usernameParameter("username")//专门定制登录表单的参数名
                .passwordParameter("password")
//                .successForwardUrl("/success") //登录成功之后的跳转地址(服务端数据跳转)
//                .defaultSuccessUrl("/success")//登录成功之后,重定向到之前的页面
                .successHandler((req, resp, authentication) -> {//前后端分离的项目登录成功需要使用处理器去返回前端一个json
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter writer = resp.getWriter();
                    writer.write(new ObjectMapper().writeValueAsString(authentication.getPrincipal()));
                    writer.flush();
                    writer.close();
                })
//                .failureForwardUrl("/fail") //登录失败之后的跳转地址
//                .failureUrl("/fail") //登录失败之后,前端url重定向到错误页面
                .failureHandler((req, resp, exception) -> {//前后端分离的项目登录失败之后的,一个回调的配置(失败的原因可能只不同的)
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter writer = resp.getWriter();
//                    writer.write(new ObjectMapper().writeValueAsString(exception.getMessage()));//直接返回异常信息
                    String msg = null;//返回json
                    if (exception instanceof LockedException) {
                        msg = "账户被锁定,请联系管理员";
                    } else if (exception instanceof CredentialsExpiredException) {
                        msg = "密码过期,请联系管理员";
                    } else if (exception instanceof AccountExpiredException) {
                        msg = "账户过期,请联系管理员";
                    } else if (exception instanceof DisabledException) {
                        msg = "账户被禁用,请联系管理员";
                    } else if (exception instanceof BadCredentialsException) {
                        msg = "用户名或者密码错误,请重新输入";
                    }
                    writer.write(new ObjectMapper().writeValueAsString(msg));//根据异常类型返回不同的信息
                    writer.flush();
                    writer.close();
                })
                .permitAll()//只要是跟登录页面相关的东西都放行

                .and()
                .logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout","POST"))//自定义POST请求的退出路径
                .logoutUrl("/logout")//自定义注销路径(限于Get请求)
//                .logoutSuccessUrl("/login.html") //配置注销成功之后跳转的页面
                .logoutSuccessHandler((req, resp, authentication) -> {//前后端分离的项目,是指注销成功之后返回json
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter writer = resp.getWriter();
                    writer.write(new ObjectMapper().writeValueAsString("注销登录成功"));
                    writer.flush();
                    writer.close();
                })
                .invalidateHttpSession(true)//默认就位true,目的是让session失效
                .clearAuthentication(true) //默认就位true,目的是清除认证信息

                .and()
                .csrf().disable()//关闭csrf

                .exceptionHandling()//前后端分离项目,当客户端没有登录就去访问资源,进行一个异常处理,返回json
                .authenticationEntryPoint((req, resp, exception) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    resp.setStatus(401);//没有权限的状态码
                    PrintWriter writer = resp.getWriter();
                    writer.write(new ObjectMapper().writeValueAsString("请先进行登录"));
                    writer.flush();
                    writer.close();
                })
                ;

    }
}
