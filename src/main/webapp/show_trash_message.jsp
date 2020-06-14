<%@ page import="cse.maven_webmail.control.CommandType" %><%--
  Created by IntelliJ IDEA.
  User: Java
  Date: 2020-06-08
  Time: 오전 11:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="trashAgent" class="cse.maven_webmail.model.TrashMailAgent" scope="page"/>
<jsp:setProperty name="trashAgent" property="messageName" value="${param.messageName}"/>
<!DOCTYPE>
<html lang="ko">
<head>
    <title>휴지통 내용 보기</title>
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
    <jsp:include page="sidebar_previous_menu.jsp"/>
</div>

<div id="msgBody">
    ${trashAgent.result}
    <form action="trash.do" method="POST">
        <input type="hidden" name="menu" value="<%=CommandType.RESTORE_MAIL_COMMAND%>">
        <input type="hidden" name="messageName" value="${param.messageName}">
        <input type="submit" value="복구"/>
    </form>
    <form action="trash.do" method="POST">
        <input type="hidden" name="menu" value="<%=CommandType.DELETE_MAIL_COMMAND%>">
        <input type="hidden" name="messageName" value="${param.messageName}">
        <input type="submit" value="삭제"/>
    </form>
</div>
<jsp:include page="footer.jsp"/>
</body>
</html>
