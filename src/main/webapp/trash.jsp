<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Java
  Date: 2020-06-07
  Time: 오후 8:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="trashAgent" class="cse.maven_webmail.model.TrashMailAgent"/>
<jsp:setProperty name="trashAgent" property="userId" value="${userid}"/>
<html>
<head>
    <title>휴지통</title>
    <link type="text/css" rel="stylesheet" href="css/main_style.css"/>
    <style type="text/css">
        .submitLink {
            background-color: transparent;
            text-decoration: underline;
            border: none;
            color: blue;
            cursor: pointer;
        }
        submitLink:focus {
            outline: none;
        }
    </style>
</head>
<body>
<jsp:include page="header.jsp"/>
<div id="sidebar">
    <jsp:include page="sidebar_menu.jsp"/>
</div>

<div id="main">
    <table border="1">
        <c:forEach items="${trashAgent.results}" var="item">
            ${item}
        </c:forEach>
    </table>
</div>
<jsp:include page="footer.jsp"/>
</body>
</html>
