package com.jxr.settings.test;

import com.bjpowernode.crm.utils.MD5Util;

public class Test1 {
    public static void main(String[] args) {
        // 验证失效时间
//        String expireTime = "2020-10-10 10:10:10";
//
//        String currentTime = DateTimeUtil.getSysTime();
//
//        int n = expireTime.compareTo(currentTime);
//        System.out.println(n);

//        String lockState = "0";
//        if("0".equals(lockState)) {
//            System.out.println("账号已锁定");
//        }

//        String ip = "192.168.1.1";
//        String allowIps = "192.168.1.1,192.168.1.1";
//        if(allowIps.contains(ip)) {
//            System.out.println("有效的ip地址，允许访问系统");
//        } else {
//            System.out.println("ip地址受限，请联系管理员");
//        }

        String pwd = "cs032997";
        String pwdl = MD5Util.getMD5(pwd);
        System.out.println(pwdl);
    }
}
