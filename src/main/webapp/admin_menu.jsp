<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
    Document   : admin_menu.jsp
    Author     : jongmin
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="userAdminAgent" scope="page" class="cse.maven_webmail.model.UserAdminAgent"/>
<jsp:setProperty name="userAdminAgent" property="cwd" value="${pageContext.servletContext.getRealPath('.')}"/>
<jsp:setProperty name="userAdminAgent" property="port" value="4555"/>
<jsp:setProperty name="userAdminAgent" property="server" value="localhost"/>

<!DOCTYPE>
<html lang="kr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>사용자 관리 메뉴</title>
    <link type="text/css" rel="stylesheet" href="css/main_style.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>

<div id="sidebar">
    <jsp:include page="sidebar_admin_menu.jsp"/>
</div>

<div id="main">
    <h2> 메일 사용자 목록 </h2>
    <!-- 아래 코드는 위와 같이 Java Beans와 JSTL을 이용하는 코드로 바꾸어져야 함 -->
    <ul>
        <c:forEach items="${userAdminAgent.userList}" var="item">
            <li>${item}</li>
        </c:forEach>
    </ul>
</div>

<jsp:include page="footer.jsp"/>
</body>
</html>
