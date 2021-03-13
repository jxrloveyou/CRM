package com.bjpowernode.crm.web.listener;

import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInitListener implements ServletContextListener {
    /*
        该方法是用来监听上下文域对象的方法，当服务器启动，上下文作用域对象创建
        完毕后，马上执行该方法

        event：该参数能够取得监听的对象
            监听的什么对象，就可以通过参数取得什么对象

     */

    public void contextInitialized(ServletContextEvent event) {
        System.out.println("上下文作用域创建了");
        ServletContext application = event.getServletContext();

        // 取数据字典
        /*
            应该向业务层要7个list
                可以打包为一个map
         */
        DicService dicService = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext()).getBean(DicService.class);

        Map<String, List<DicValue>> map = dicService.getAll();

        // 将map解析为上下文域对象中键值对
        Set<String> set = map.keySet();
        for(String key:set) {
            application.setAttribute(key, map.get(key));
        }

        //------------------------------------
        // 数据字典处理完毕后，处理可能性
        /*
            步骤：解析该文件，将属性中的键值对关系处理成给java中键值对关系
         */
        // 解析properties文件
        Map<String, String> pMap = new HashMap<>();

        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");

        Enumeration<String> e = rb.getKeys();

        while(e.hasMoreElements()) {
            String key = e.nextElement();
            String value = rb.getString(key);
            pMap.put(key, value);
        }

        // 将pMap保存到服务器缓存
        application.setAttribute("pMap", pMap);
    }

    public void contextDestroyed(ServletContextEvent event) {
    }
}
