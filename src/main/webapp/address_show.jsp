
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cse.maven_webmail.control.CommandType"%>
<%@taglib tagdir="/WEB-INF/tags/" prefix="mytags"%>
<!DOCTYPE>
<html lang="kr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>주소록 화면</title>
    <link type="text/css" rel="stylesheet" href="css/main_style.css"/>
</head>

<body>
<jsp:include page="header.jsp"/>

<div id="sidebar">
    <jsp:include page="sidebar_menu.jsp"/>
</div>

<div id="main">
    <mytags:addrbook addressDeleteMenu="<%=CommandType.DELETE_ADDRESS%>" addressChangeMenu="<%=CommandType.CHANGE_ADDRESS%>" addressAddMenu="<%=CommandType.ADD_ADDRESS%>" userName="${userid}" />
        
</div>

<jsp:include page="footer.jsp"/>

</body>
</html>