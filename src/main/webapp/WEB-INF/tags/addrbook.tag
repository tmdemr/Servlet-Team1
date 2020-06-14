<%@tag description="addressbook tag file" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@attribute required="true" name="userName" %>
<%@attribute required="true" name="addressAddMenu" type="java.lang.Integer" %>
<%@attribute required="true" name="addressDeleteMenu" type="java.lang.Integer" %>
<%@attribute required="true" name="addressChangeMenu" type="java.lang.Integer" %>
<%@attribute name="deleteAllAddressMenu" required="true" type="java.lang.Integer" %>
<sql:query var="rs" dataSource="jdbc/JamesWebmail">
    SELECT userid, nickname, email, phoneNumber FROM ADDRESSBOOK WHERE userid = '${userName}'
</sql:query>

<table border="1">
    <thread>
        <tr>
            <th>이름</th>
            <th>이메일</th>
            <th>전화번호</th>
            <th>수정</th>
            <th>삭제</th>
            <th>메일보내기</th>
        </tr>
    </thread>
    <tbody>
    <c:forEach var="row" items="${rs.rows}">
        <tr>
        <form action="address.do" method="POST">
            <input type="hidden" name="menu" value="${addressChangeMenu}"/>
            <input type="hidden" name="user" value="${userName}"/>
            <input type="hidden" name="email" value="${row.email}"/>
            <td><input type="text" name="name" value="${row.nickname}"/></td>
                <td><input type="text" name="newEmail" value="${row.email}"/></td>
                <td><input type="text" name="phoneNumber" value="${row.phoneNumber}"/></td>
                <td>
                    <input type="submit" value="수정하기"/>
                </td>

        </form>

        <td>
            <form action="address.do" method="POST">
                <input type="hidden" name="menu" value="${addressDeleteMenu}"/>
                <input type="hidden" name="user" value="${userName}"/>
                <input type="hidden" name="email" value="${row.email}"/>
                <input type="submit" value="삭제하기"/>
            </form>
        </td>
        <td>
            <form action="write_mail.jsp" method="POST">
                <input type="hidden" name="fromAddress" value="${row.email}">
                <input type="submit" value="메일 보내기">
            </form>
        </td>
        </tr>


    </c:forEach>
    </tbody>

</table>
<h2>주소록 추가하기</h2>
<form action="address.do" method="POST">
    <input type="hidden" name="menu" value="${addressAddMenu}"/>
    <input type="hidden" name="user" value="${userName}"/>
    이름 : <input type="text" name="name"/><br>
    이메일 : <input type="email" name="email"/><br>
    휴대폰 번호 : <input type="text" name="phoneNumber"/><br>
    <input type="submit" value="추가하기"/>
</form>
<form action="address.do" method="POST">
    <input type="hidden" name="menu" value="${deleteAllAddressMenu}"/>
    <input type="hidden" name="user" value="${userName}"/>
    <input type="submit" value="전부 삭제하기">
</form>