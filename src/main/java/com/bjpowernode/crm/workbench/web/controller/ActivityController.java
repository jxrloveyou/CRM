package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workbench/activity")
public class ActivityController {

    @Resource
    private UserService userService;

    @Resource
    private ActivityService activityService;

    @RequestMapping("/getUserList.do")
    @ResponseBody
    private List<User> getUserList() {
        System.out.println("取得用户信息列表");
        List<User> users = userService.getUserList();
        return users;
    }

    @RequestMapping("/save.do")
    @ResponseBody
    private boolean save(HttpServletRequest request, Activity activity) {
        System.out.println("执行市场活动的添加操作");

        String id = UUIDUtil.getUUID();
        // 创建时间，当前系统时间
        String createTime = DateTimeUtil.getSysTime();
        // 创建人：当前用户
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        activity.setId(id);
        activity.setCreateTime(createTime);
        activity.setCreateBy(createBy);

        return activityService.save(activity);
    }

    @RequestMapping("/pageList.do")
    @ResponseBody
    private Object pageList(Activity activity, Integer pageSize, Integer pageNo) {
        System.out.println("进入到查询市场活动操作");
        String name = activity.getName();
        String owner = activity.getOwner();
        String startDate = activity.getStartDate();
        String endDate = activity.getEndDate();

        // 计算略过的记录数
        int skipCount = (pageNo-1)*pageSize;

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("owner", owner);
        map.put("startDate", startDate);
        map.put("endDate", endDate);

        // 因为以下两条信息不在domain类中,所以选择使用map进行传值(<parameterType>传值不能使用vo类,<resultType>传值可以使用vo类)
        map.put("pageSize", pageSize);
        map.put("skipCount", skipCount);

        return activityService.pageList(map);
        /*
         * 前端要: 市场活动信息列表
         *          查询的总条数
         *
         *          业务层拿到了以上两项信息之后,如何做返回
         *          map
         *          map.put("dataList",dataList);
         *          map.put("total",total);
         *          PrintJson map --> json
         *          {"total":100,"dataList":[{市场活动1},{2},{3}...]
         *
         *          vo
         *          PaginationVo<T>
         *              private int total;
         *              private List<T> dataList;
         *
         *          PaginationVo<Activity> vo = new PaginationVo<>();
         *          vo.setTotal(total);
         *          vo.setDataList(dataList);
         *          PrintJson vo --> json
         *          {"total":100,"dataList":[{市场活动1},{2},{3}...]}
         *
         *          将来分页查询: 每个模块都有,所以我们选择使用一个通用的vo,操作起来比较方便
         */
    }

    @RequestMapping("/delete.do")
    @ResponseBody
    private boolean delete(@RequestParam(value = "id") String[] ids) {// 前端传过来的参数是id,后端变量名称变成ids使用
        System.out.println("执行市场活动的删除");
        return activityService.delete(ids);
    }

    @RequestMapping("/getUserListAndActivity.do")
    @ResponseBody
    private Object getUserListAndActivity(String id) {
        System.out.println("进入到查询用户信息列表和根据市场活动id查询单条记录操作");
        return activityService.getUserListAndActivity(id);
    }

    @RequestMapping("/update.do")
    @ResponseBody
    private boolean update(HttpServletRequest request, Activity activity) {
        System.out.println("执行市场活动修改操作");
        // 修改时间，当前系统时间
        String editTime = DateTimeUtil.getSysTime();
        // 修改人：当前用户
        String editBy = ((User)request.getSession().getAttribute("user")).getName();


        activity.setEditTime(editTime);
        activity.setEditBy(editBy);

        return activityService.update(activity);
    }

    @RequestMapping("/detail.do")
    private ModelAndView detail(String id) {
        System.out.println("进入到跳转详细信息页的操作");
        ModelAndView mv = new ModelAndView();
        Activity activity = activityService.detail(id);
        mv.addObject("a", activity);

        mv.setViewName("/workbench/activity/detail.jsp");
        return mv;
    }

    @RequestMapping("/getRemarkListByAid.do")
    @ResponseBody
    private List<ActivityRemark> getRemarkListByAid(String activityId) {
        System.out.println("根据市场活动，取得备注列表信息");
        return activityService.getRemarkListByAid(activityId);
    }

    @RequestMapping("/deleteRemark.do")
    @ResponseBody
    private boolean deleteRemark(String id) {
        System.out.println("删除备注操作");
        return activityService.deleteRemark(id);
    }

    @RequestMapping("/saveRemark.do")
    @ResponseBody
    private Map<String, Object> saveRemark(HttpServletRequest request, ActivityRemark ar) {
        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "0";
        System.out.println("Id " + id);

        ar.setId(id);
        ar.setCreateTime(createTime);
        ar.setCreateBy(createBy);
        ar.setEditFlag(editFlag);
        System.out.println(ar);

        boolean flag = activityService.saveRemark(ar);
        Map<String, Object> map = new HashMap<>();
        map.put("success", flag);
        map.put("ar", ar);

        return map;
    }

    @RequestMapping("/updateRemark.do")
    @ResponseBody
    private Map<String, Object> updateRemark(HttpServletRequest request, ActivityRemark ar) {
        System.out.println("执行修改备注的操作");

        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "1";

        ar.setEditTime(editTime);
        ar.setEditBy(editBy);
        ar.setEditFlag(editFlag);

        boolean flag = activityService.updateRemark(ar);
        Map<String, Object> map = new HashMap<>();
        map.put("success", flag);
        map.put("ar", ar);
        return map;

    }
}
