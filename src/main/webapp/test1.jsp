<%--
  Created by IntelliJ IDEA.
  User: Y
  Date: 2021/3/6
  Time: 20:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    $.ajax({
    url:"",
    data:{

    },
    type:"",
    dataType:"json",
    success:function (data) {

    }
    })

    // 创建时间，当前系统时间
    String createTime = DateTimeUtil.getSysTime();
    // 创建人：当前用户
    String createBy = ((User)request.getSession().getAttribute("user")).getName();


    // 规定输入日期格式
    $(".time").datetimepicker({
    minView: "month",
    language:  'zh-CN',
    format: 'yyyy-mm-dd',
    autoclose: true,
    todayBtn: true,
    pickerPosition: "bottom-left"
    });
</head>
<body>

</body>
</html>
