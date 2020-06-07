<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Java
  Date: 2020-06-07
  Time: 오후 2:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="userData" class="cse.maven_webmail.model.UserDataBean" scope="page"/>
<c:set var="userId" value="${userid}"/>
<jsp:setProperty name="userData" property="userId" value="${userId}"/>

<!DOCTYPE>
<html lang="ko">
<head>
    <link type="text/css" rel="stylesheet" href="css/main_style.css"/>
    <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <title>내 정보보기</title>
    <script>
        $(function () {
            $("#birthday").datepicker({
                dateFormat: 'yy-mm-dd',
                showOn: "both",
                changeMonth: true,
                changeYear: true,
                nextText: 'next',
                prevText: 'prev',
                buttonImage: "http://jqueryui.com/resources/demos/datepicker/images/calendar.gif",
                buttonImageOnly: true,
                buttonText: "선택",
                yearSuffix: "년",
                monthNamesShort: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
                monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
                dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
                dayNames: ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일']
            });
        })
    </script>
</head>
<body>
<jsp:include page="header.jsp"/>
<div id="sidebar">
    <jsp:include page="sidebar_menu.jsp"/>
</div>

<div id="main">
    ${userData.result}
</div>

<jsp:include page="footer.jsp"/>

</body>
</html>
