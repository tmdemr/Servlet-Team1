<%-- 
    Document   : sidebar_menu.jsp
    Author     : jongmin
--%>

<%@page import="cse.maven_webmail.control.CommandType" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>웹메일 시스템 메뉴</title>
</head>
<body>
<br> <br>

<span style="color: indigo"> <strong>사용자: <%= session.getAttribute("userid") %> </strong> </span> <br>

<p><a href="main_menu.jsp"> 메일 읽기 </a></p>
<p><a href="write_mail.jsp"> 메일 쓰기 </a></p>
<p><a href="sended_message_menu.jsp">보낸메일함</a></p>
<p><a href="my_info.jsp">내 정보보기</a></p>
<p><a href="trash.jsp">휴지통</a></p>
<p><a href="address_show.jsp">주소록</a></p>
<p><a href="Login.do?menu=<%= CommandType.LOGOUT %>">로그아웃</a></p>
</body>
</html>
