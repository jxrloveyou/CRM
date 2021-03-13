package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workbench/transaction")
public class TranController {
    @Resource
    private TranService tranService;
    @Resource
    private UserService userService;
    @Resource
    private CustomerService customerService;

    @RequestMapping("/add.do")
    private ModelAndView add(HttpServletRequest request) {
        System.out.println("进入到添加交易的页面");
        List<User> list = userService.getUserList();
        ModelAndView mv = new ModelAndView();
        mv.addObject("uList", list);
        mv.setViewName("/workbench/transaction/save.jsp");
        return mv;
    }

    @RequestMapping("/getCustomerName.do")
    @ResponseBody
    private List<String> getCustomerName(String name) {
        System.out.println("取得客户名称列表");
        return customerService.getCustomerName(name);
    }

    @RequestMapping("/save.do")
    private ModelAndView save(HttpServletRequest request, Tran tran) {
        ModelAndView mv = new ModelAndView();
        String customerName = request.getParameter("customerName");
        tran.setId(UUIDUtil.getUUID());
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        tran.setCreateBy(createBy);
        tran.setCreateTime(createTime);

        boolean flag = tranService.save(tran, customerName);

        if(flag) {
            mv.setViewName("redirect:/workbench/transaction/index.jsp");
        }

        return mv;
    }

    @RequestMapping("/pageList.do")
    @ResponseBody
    private PaginationVO<Tran> pageList(Integer pageNo, Integer pageSize, Tran t, String customerName, String contactsName) {
        System.out.println("展现交易列表");

        // 计算略过的记录数
        int skipCount = (pageNo-1)*pageSize;

        Map<String, Object> map = new HashMap<>();

        // 因为以下两条信息不在domain类中,所以选择使用map进行传值(<parameterType>传值不能使用vo类,<resultType>传值可以使用vo类)
        map.put("pageSize", pageSize);
        map.put("skipCount", skipCount);
        map.put("owner", t.getOwner());
        map.put("name", t.getName());
        map.put("stage", t.getStage());
        map.put("type", t.getType());
        map.put("source", t.getSource());

        return tranService.pageList(map,customerName,contactsName);
    }

    @RequestMapping("/detail.do")
    private ModelAndView detail(HttpServletRequest request, String id) {
        System.out.println(id);
        ModelAndView mv = new ModelAndView();
        Tran t = tranService.detail(id);

        // 处理可能性
        String stage = t.getStage();
        Map<String, String> pMap = (Map<String, String>) request.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);
        t.setPossibility(possibility);


        mv.addObject("t", t);
        mv.setViewName("/workbench/transaction/detail.jsp");
        return mv;
    }

    @RequestMapping("/getHistoryListByTranId.do")
    @ResponseBody
    public List<TranHistory> getHistoryListByTranId(HttpServletRequest request, String tranId) {
        System.out.println("根据交易id取得相应的历史列表");

        List<TranHistory> tranHistoryList = tranService.getHistoryListByTranId(tranId);

        for(TranHistory tranHistory:tranHistoryList) {
            String stage = tranHistory.getStage();
            Map<String, String> pMap = (Map<String, String>) request.getServletContext().getAttribute("pMap");
            String possibility = pMap.get(stage);
            tranHistory.setPossibility(possibility);
        }

        return tranHistoryList;
    }

    @RequestMapping("/changeStage.do")
    @ResponseBody
    public Map<String, Object> changeStage(HttpServletRequest request, Tran t) {
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        t.setEditBy(editBy);
        t.setEditTime(editTime);

        boolean flag = tranService.changeStage(t);

        Map<String, String> pMap = (Map<String, String>) request.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(t.getStage());
        t.setPossibility(possibility);

        Map<String, Object> map = new HashMap<>();
        map.put("success", flag);
        map.put("t", t);

        return map;

    }

    @RequestMapping("/getCharts.do")
    @ResponseBody
    public Map<String, Object> getCharts() {
        System.out.println("取统计图表的参数");

        return tranService.getCharts();
    }
}

