<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
    Document   : delete_user.jsp
    Author     : jongmin
--%>

<%@page import="cse.maven_webmail.control.CommandType" %>
<%@page import="cse.maven_webmail.model.UserAdminAgent" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<script type="text/javascript">
    function getConfirmResult() {
        var result = confirm("사용자를 정말로 삭제하시겠습니까?");
        return result;
    }
</script>

<!DOCTYPE>
<html lang="kr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>사용자 제거 화면</title>
    <link type="text/css" rel="stylesheet" href="css/main_style.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>
<jsp:useBean id="userAdminAgent" scope="page" class="cse.maven_webmail.model.UserAdminAgent"/>
<jsp:setProperty name="userAdminAgent" property="cwd" value="${pageContext.servletContext.getRealPath('.')}"/>
<jsp:setProperty name="userAdminAgent" property="port" value="4555"/>
<jsp:setProperty name="userAdminAgent" property="server" value="localhost"/>
<% userAdminAgent.initialize();%>
<div id="sidebar">
    <%-- 사용자 추가때와 동일하므로 같은 메뉴 사용함. --%>
    <jsp:include page="sidebar_admin_previous_menu.jsp"/>
</div>

<div id="main">
    <h2> 삭제할 사용자를 선택해 주세요. </h2> <br>

    <!-- 아래 코드는 위와 같이 Java Beans와 JSTL을 이용하는 코드로 바꾸어져야 함 -->

    <form name="DeleteUser" action="UserAdmin.do?menu=<%=CommandType.DELETE_USER_COMMAND%>"
          method="POST">
        <c:forEach items="${userAdminAgent.userList}" var="item">
            <input type="checkbox" name="seletedUsers" value="${item}">${item}<br>
        </c:forEach>

        <br>
        <input type="submit" value="제거" name="delete_command" onClick="return getConfirmResult()"/>
        <input type="reset" value="선택 전부 취소"/>
    </form>
</div>

<jsp:include page="footer.jsp"/>
</body>
</html>
