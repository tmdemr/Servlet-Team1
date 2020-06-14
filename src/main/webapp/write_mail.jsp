<%-- 
    Document   : write_mail.jsp
    Author     : jongmin
--%>

<%@page import="java.nio.charset.StandardCharsets"%>
<%@page import="cse.maven_webmail.control.CommandType" %>


<%-- @taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setCharacterEncoding(StandardCharsets.UTF_8.name());%>
<!DOCTYPE>
<html lang="kr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>메일 쓰기 화면</title>
    <link type="text/css" rel="stylesheet" href="css/main_style.css"/>
</head>
<body>
<jsp:include page="header.jsp"/>

<div id="sidebar">
    <jsp:include page="sidebar_previous_menu.jsp"/>
</div>

<div id="main">
    <%-- <jsp:include page="mail_send_form.jsp" /> --%>
    <form enctype="multipart/form-data" method="POST"
          action="WriteMail.do?menu=<%= CommandType.SEND_MAIL_COMMAND%>">
        <table>
            <caption>메일 쓰기</caption>
            <tr>
                <th id="to"> 수신</th>
                <td><input type="text" name="to" size="80"
                           value="<%=request.getParameter("fromAddress")==null ?"":request.getParameter("fromAddress")%>"/></td>
            </tr>
            <tr>
                <th id="cc">참조</th>
                <td><input type="text" name="cc" size="80"></td>
            </tr>
            <tr>
                <th id="subject"> 메일 제목</th>
                <td><input type="text" name="subj" size="80" value="${param.subject}"></td>
            </tr>
            <tr>
                <th id="area" colspan="2">본 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 문</th>
            </tr>
            <tr>  <%-- TextArea    --%>
                <td colspan="2"><textarea rows="15" name="body" cols="80">${param.body}</textarea></td>
            </tr>
            <tr>
                <th id="file">첨부 파일</th>
                <td><input type="file" name="file1" size="80"></td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="submit" value="메일 보내기">
                    <input type="reset" value="다시 입력">
                </td>
            </tr>
        </table>
    </form>
</div>

<jsp:include page="footer.jsp"/>
</body>
</html>
