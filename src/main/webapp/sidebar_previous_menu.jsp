<%-- 
    Document   : sidebar_adduser_menu
    Author     : jongmin
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>JSP Page</title>
</head>
<body>
<br> <br>

<span style="color: indigo">
            <strong>사용자: <%= session.getAttribute("userid") %> </strong>
        </span> <br> <br>

<a href="javascript:history.back()"> 이전 메뉴로 </a>
</body>
</html>
