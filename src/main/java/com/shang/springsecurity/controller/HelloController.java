package com.shang.springsecurity.controller;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

/**
 * <p></p>
 *
 * @Author: 尚家朋
 * @Email: 835091332@qq.com
 * @Date: 2020/4/19 17:56
 */

@RestController
public class HelloController implements Serializable {

    /**
     * 登录成功之后跳转的路径
     *
     * @return
     */
    @RequestMapping("/success")
    public String success() {

        return "login success";
    }

    /**
     * 登录失败之后跳转的路径
     *
     * @return
     */
    @RequestMapping("/fail")
    public String fail() {
        return "login fail";
    }

    /**
     * 登录成功之后才可以访问的资源
     * @return
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello SpringSecurity";
    }

    /**
     * 只有角色为admin的用户可以访问
     * @return
     */
    @GetMapping("/admin/hello")
    public String admin() {
        return "admin";
    }

    /**
     * 只有角色为user的用户可以访问
     * @return
     */
    @GetMapping("/user/hello")
    public String user() {
        return "user";
    }


    @PostMapping("/login")
    @Transactional(rollbackFor = Exception.class)
    public String login(String username,String password,String verifyCode) {
        System.out.println("username:"+username);
        System.out.println("password:"+password);
        System.out.println("verifyCode:"+verifyCode);
        return "login success";
    }


}
