<%@ page import="cse.maven_webmail.control.CommandType" %><%--
    Document   : show_message.jsp
    Author     : jongmin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<jsp:useBean id="pop3" scope="page" class="cse.maven_webmail.model.Pop3Agent"/>
<jsp:setProperty name="pop3" property="host" value="${host}"/>
<jsp:setProperty name="pop3" property="password" value="${password}"/>
<jsp:setProperty name="pop3" property="userid" value="${userid}"/>
<!DOCTYPE>
<html lang="kr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>메일 보기 화면</title>
    <link type="text/css" rel="stylesheet" href="css/main_style.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>

<div id="sidebar">
    <jsp:include page="sidebar_previous_menu.jsp"/>
</div>

<div id="msgBody">
    <%= pop3.getMessage(Integer.parseInt(request.getParameter("msgid")))%>
    <form action="ReadMail.do" method="POST">
        <input type="hidden" name="menu" value="<%=CommandType.DELETE_MAIL_COMMAND%>">
        <input type="hidden" name="msgid" value="${param.msgid}">
        <input type="submit" value="삭제">
    </form>
</div>


<jsp:include page="footer.jsp"/>
</body>
</html>
