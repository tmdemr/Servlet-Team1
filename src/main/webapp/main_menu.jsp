<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
    Document   : main_menu.jsp
    Author     : jongmin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="pop3" scope="page" class="cse.maven_webmail.model.Pop3Agent"/>
<jsp:setProperty name="pop3" property="host" value="${host}"/>
<jsp:setProperty name="pop3" property="userid" value="${userid}"/>
<jsp:setProperty name="pop3" property="password" value="${password}"/>
<c:if test="${not empty param.pageNo}">
    <jsp:setProperty name="pop3" property="pageNo" value="${param.pageNo}"/>
</c:if>
<c:if test="${empty param.pageNo}">
    <jsp:setProperty name="pop3" property="pageNo" value="1"/>
</c:if>
<!DOCTYPE>
<html lang="kr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>주메뉴 화면</title>
    <link type="text/css" rel="stylesheet" href="css/main_style.css"/>
</head>

<body>
<jsp:include page="header.jsp"/>

<div id="sidebar">
    <jsp:include page="sidebar_menu.jsp"/>
</div>

<div id="main">
    <%= pop3.getMessageList() %>
</div>

<jsp:include page="footer.jsp"/>

</body>
</html>
