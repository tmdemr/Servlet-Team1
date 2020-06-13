<%--
  Created by IntelliJ IDEA.
  User: Java
  Date: 2020-06-13
  Time: 오후 8:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<html>
<head>
    <title>오류가 발생했습니다.</title>
    <link type="text/css" rel="stylesheet" href="css/main_style.css"/>
</head>
<body>
    오류가 발생했습니다.<br>
    오류 내용 : <%=exception.getMessage()%>
<%
    session.invalidate();
%>
</body>
</html>
