package com.shang.springsecurity.controller;

import com.shang.springsecurity.util.VerifyCodeUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * <p>验证码图片生成控制器</p>
 *
 * @Author: shangjp
 * @Email: shangjp@163.com
 * @Date: 2020/4/25 17:50
 */
@RestController
public class VerifyCodeController {

    @GetMapping("/verifyCode")
    public void code(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //实例化一个验证码图片生成工具类
        VerifyCodeUtil util = new VerifyCodeUtil();
        BufferedImage image = util.createImage();
        String text = util.getText();
        HttpSession session = req.getSession();
        session.setAttribute("verify_code",text);
        VerifyCodeUtil.output(image,resp.getOutputStream());

    }
}
