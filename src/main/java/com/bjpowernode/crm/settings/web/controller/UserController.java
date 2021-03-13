package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.utils.MD5Util;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/settings/user")
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping("/login.do")
    @ResponseBody
    public Object login(HttpServletRequest request, String loginAct, String loginPwd) {
        System.out.println("进入登陆验证操作");
        loginPwd = MD5Util.getMD5(loginPwd);
        // 获取浏览器端ip地址
        String ip = request.getRemoteAddr();
        System.out.println("------ip:" + ip);
        Map<Object, Object> map = new HashMap<>();
        try {
            User user = userService.login(loginAct, loginPwd, ip);
            /*
                登录成功
                {
                    "success" :true,

                }
             */
            request.getSession().setAttribute("user", user);
            map.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            /*
                登录失败
                {
                    "success":false,

                }
             */
            String msg = e.getMessage();
            map.put("success", false);
            map.put("msg", msg);
        }
        return map;
    }



}
