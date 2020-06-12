
<%@tag description="addressbook tag file" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@attribute  required="true" name="userName"%>
<%@attribute  required="true" name="addressAddMenu" %>
<%@attribute  required="true" name="addressDeleteMenu" %>
<%@attribute  required="true" name="addressChangeMenu" %>
<sql:query var="rs" dataSource="jdbc/JamesWebmail">
    SELECT * FROM ${table}
</sql:query>

<table border="1">
    <thread>
        <tr>
            <th> 이름  </th>
            <th> 이메일  </th>
            <th> 전화번호  </th>
            <th>수정</th>
            <th>삭제</th>

        </tr>
    </thread>
    <tbody>
        <c:forEach var="row" items="${rs.rows}">
            <tr>
                <td> ${row.name} </td>
                <td> ${row.email} </td>
                <td> ${row.phoneNumber} </td>
                <td>
                    <form action="address.do" method="POST">
                        <input type="hidden" name="menu" value="${addressChangeMenu}"/>
                        <input type="hiddden" name="user" value="${userName}"/>
                        <input type="hidden" name="email" value="${row.email}"/>
                        <input type="submit" value="수정하기"/>
                    </form>
                </td>
                <td>
                    <form action="address.do" method="POST">
                        <input type="hidden" name="menu" value="${addressDeleteMenu}"/>
                        <input type="hiddden" name="user" value="${userName}"/>
                        <input type="hidden" name="email" value="${row.email}"/>
                        <input type="submit" value="삭제하기"/>
                    </form>
                </td>
            </tr>

        </c:forEach>
    </tbody>

</table>
<h2>주소록 추가하기</h2>
<form action="address.do" method="POST">
    <input type="hidden" name="menu" value="${addressAddMenu}"/>
    <input type="hiddden" name="user" value="${userName}"/>
    이름 : <input type="text" name="name"/><br>
    이메일 : <input type="email" name="email"/><br>
    휴대폰 번호 : <input type="text" name="phoneNumber"/><br>
    <input type="submit" value="추가하기"/>
</form>