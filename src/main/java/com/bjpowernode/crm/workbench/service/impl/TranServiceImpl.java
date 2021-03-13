package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.ContactsDao;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.dao.TranHistoryDao;
import com.bjpowernode.crm.workbench.domain.Contacts;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.TranService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.annotation.Resources;
import java.util.*;

@Service
public class TranServiceImpl implements TranService {
    @Resource
    private TranDao tranDao;
    @Resource
    private TranHistoryDao tranHistoryDao;
    @Resource
    private CustomerDao customerDao;
    @Resource
    private ContactsDao contactsDao;


    @Override
    public boolean save(Tran tran, String customerName) {
        boolean flag = true;

        /*
            判断customerName，根据客户名在客户表进行精确查询
                如果有该客户，则取出这个客户的id，封装到t对象中
                如果没有这个客户，则再客户表新建一条客户信息，然后将新建的客户的id取出，封装到t对象中
         */

        Customer customer = customerDao.getCustomerByName(customerName);
        if(customer==null) {
            // 创建客户
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(customerName);
            customer.setCreateBy(tran.getCreateBy());
            customer.setCreateTime(DateTimeUtil.getSysTime());
            customer.setContactSummary(tran.getContactSummary());
            customer.setNextContactTime(tran.getNextContactTime());
            customer.setOwner(tran.getOwner());

            // 添加客户
            int count1 = customerDao.save(customer);
            if(count1 != 1) {
                flag = false;
            }
        }

        // 将客户id封装到tran中
        tran.setCustomerId(customer.getId());

        // 添加交易
        int count2 = tranDao.save(tran);
        if(count2 != 1) {
            flag = false;
        }

        // 添加交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setTranId(tran.getId());
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setCreateTime(DateTimeUtil.getSysTime());
        tranHistory.setCreateBy(tran.getCreateBy());

        int count3 = tranHistoryDao.save(tranHistory);
        if(count3 != 1) {
            flag = false;
        }
        return flag;
    }

    @Override
    public PaginationVO<Tran> pageList(Map<String, Object> map, String customerName, String contactsName) {
        boolean flagCustomer = false;
        boolean flagContacts = false;
        Customer customer = null;
        Contacts contacts = null;
        if(customerName!=null && !"".equals(customerName)) {
            customer = customerDao.getCustomerByName(customerName);
            flagCustomer = true;
        }
        if(contactsName!=null && !"".equals(contactsName)) {
            contacts = contactsDao.getContactsByName(contactsName);
            flagContacts = true;
        }
        PaginationVO<Tran> p = new PaginationVO<>();
        p.setDataList(new ArrayList<>());

        if(flagCustomer && customer==null) {
            return p;
        }

        if(flagContacts && contacts==null) {
            return p;
        }

        String customerId = "";
        String contactsId = "";
        if(customer!=null) {
            customerId = customer.getId();
        }

        if(contacts!=null) {
            contactsId = contacts.getId();
        }
        map.put("customerId", customerId);
        map.put("contactsId", contactsId);

        int total = tranDao.getTotalByCondition(map);

        List<Tran> list = tranDao.getTranListByCondition(map);

        p.setTotal(total);
        p.setDataList(list);
        return p;
    }

    @Override
    public Tran detail(String id) {
        return tranDao.detail(id);
    }

    @Override
    public List<TranHistory> getHistoryListByTranId(String tranId) {
        return tranHistoryDao.getHistoryListByTranId(tranId);
    }

    @Override
    public boolean changeStage(Tran t) {
        boolean flag = true;

        int count1 = tranDao.changeStage(t);
        if(count1 != 1) {
            flag = false;
        }

        // 生成交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setCreateBy(t.getEditBy());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setExpectedDate(t.getExpectedDate());
        th.setMoney(t.getMoney());
        th.setTranId(t.getId());
        th.setStage(t.getStage());

        int count2 = tranHistoryDao.save(th);
        if(count2 != 1) {
            flag = false;
        }

        return flag;
    }

    @Override
    public Map<String, Object> getCharts() {
        int total = tranDao.getTotal();

        List<Map<String,Object>> dataList = tranDao.getCharts();

        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("dataList",dataList);

        return map;
    }
}
