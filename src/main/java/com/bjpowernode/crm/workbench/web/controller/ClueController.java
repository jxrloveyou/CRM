package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Controller
@RequestMapping("/workbench/clue")
public class ClueController {
    @Resource
    private ClueService clueService;

    @Resource
    private UserService userService;

    @Resource
    private ActivityService activityService;

    @RequestMapping("/getUserList.do")
    @ResponseBody
    private List<User> getUserList() {
        System.out.println("取得用户信息列表");
        return userService.getUserList();
    }

    @RequestMapping("/save.do")
    @ResponseBody
    private boolean save(HttpServletRequest request, Clue clue) {
        System.out.println("执行线索的添加");

        String id = UUIDUtil.getUUID();// 创建时间，当前系统时间
        String createTime = DateTimeUtil.getSysTime();
        // 创建人：当前用户
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        clue.setId(id);
        clue.setCreateTime(createTime);
        clue.setCreateBy(createBy);

        return clueService.save(clue);
    }

    @RequestMapping("/pageList.do")
    @ResponseBody
    private PaginationVO<Clue> pageList(Integer pageNo, Integer pageSize, Clue clue) {
        System.out.println("展现线索列表");
        int skipCount = (pageNo-1)*pageSize;

        Map<String, Object> map = new HashMap<>();
        map.put("pageSize", pageSize);
        map.put("skipCount", skipCount);

        map.put("fullname",clue.getFullname());
        map.put("owner",clue.getOwner());
        map.put("company",clue.getCompany());
        map.put("phone",clue.getPhone());
        map.put("mphone",clue.getMphone());
        map.put("state",clue.getState());
        map.put("source",clue.getSource());

        return clueService.pageList(map);
    }

    @RequestMapping("/detail.do")
    public ModelAndView detail(String id) {
        System.out.println("跳转到详细信息页");
        ModelAndView mv = new ModelAndView();
        Clue c = clueService.detail(id);
        mv.addObject("c", c);
        mv.setViewName("/workbench/clue/detail.jsp");
        return mv;
    }

    @RequestMapping("/getActivityListByClueId.do")
    @ResponseBody
    private List<Activity> getActivityListByClueId(String clueId) {
        return activityService.getActivityListByClueId(clueId);
    }

    @RequestMapping("/unbund.do")
    @ResponseBody
    private boolean unbund(String id) {
        return clueService.unbund(id);
    }

    @RequestMapping("/getActivityListByNameAndNotByClueId.do")
    @ResponseBody
    private List<Activity> getActivityListByNameAndNotByClueId(String aName, String clueId ) {
        System.out.println("查询市场活动列表-排除掉已关联市场活动");
        return activityService.getActivityListByNameAndNotByClueId(aName, clueId);
    }

//    @RequestMapping("/bund.do")
//    @ResponseBody
//    private boolean bund(String cid, String[] aids) {
//        return clueService.bund(cid, aids);
//    }

    @RequestMapping("/bund.do")
    @ResponseBody
    private boolean bund(HttpServletRequest request) {
        System.out.println("进入到线索控制器");

        String cid = request.getParameter("cid");
        String[] aids = request.getParameterValues("aid");

        return clueService.bund(cid, aids);
    }

    @RequestMapping("/getActivityListByName.do")
    @ResponseBody
    private List<Activity> getActivityListByName(String aname) {
        return activityService.getActivityListByName(aname);
    }

    @RequestMapping("/convert.do")
    private ModelAndView convert(HttpServletRequest req) {
        System.out.println("执行线索转换的操作");
        ModelAndView mv = new ModelAndView();

        String flag = req.getParameter("flag");
        String clueId = req.getParameter("clueId");
        String createBy = ((User)req.getSession().getAttribute("user")).getName();

        Tran t = null;

        if("a".equals(flag)) {
            // 接收交易表单的参数
            t = new Tran();

            // 接收交易表单中的参数
            String money = req.getParameter("money");
            String name = req.getParameter("name");
            String expectedDate = req.getParameter("expectedDate");
            String stage = req.getParameter("stage");
            String activityId = req.getParameter("activityId");
            String id = UUIDUtil.getUUID();
            String createTime = DateTimeUtil.getSysTime();

            t = new Tran();

            t.setId(id);
            t.setMoney(money);
            t.setName(name);
            t.setExpectedDate(expectedDate);
            t.setStage(stage);
            t.setActivityId(activityId);
            t.setCreateTime(createTime);
            t.setCreateBy(createBy);

        }
        boolean flag2 = clueService.convert(clueId, t, createBy);

        if(flag2) {
            mv.setViewName("redirect:/workbench/clue/index.jsp");
        }

        return mv;
    }
}
