<%--
  Created by IntelliJ IDEA.
  User: Java
  Date: 2020-05-21
  Time: 오후 6:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="sendMessageHandler" scope="page" class="cse.maven_webmail.model.SendMailDatabaseAgent"/>
<%
    sendMessageHandler.setUserId((String) session.getAttribute("userid"));
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>보낸 메일함</title>
    <link type="text/css" rel="stylesheet" href="css/main_style.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<div id="sidebar">
    <jsp:include page="sidebar_menu.jsp"/>
</div>
<div id="main">
    <%= sendMessageHandler.getMySendedMessages() %>
</div>
<jsp:include page="footer.jsp"/>
</body>
</html>
