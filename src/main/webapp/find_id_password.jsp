<%@ page import="cse.maven_webmail.control.CommandType" %><%--
  Created by IntelliJ IDEA.
  User: Java
  Date: 2020-06-03
  Time: 오후 1:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE>
<html lang="ko">
<head>
    <title>아이디/비밀번호 찾기</title>
    <link type="text/css" rel="stylesheet" href="css/main_style.css"/>
    <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
</head>
<script>
    var isIdFind = true;
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
        $('#birthday').datepicker('setDate', '-20Y');
        $("#passBirthday").datepicker({
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
        $('#passBirthday').datepicker('setDate', '-20Y');
    });

    function change(idFind) {
        if (idFind) {
            $('.passFind').each(function (i, obj) {
                obj.setAttribute("style", "display:none;")
            })
            $('.idFind').each(function (i, obj) {
                obj.setAttribute("style", "display:block;")
            })
            isIdFind = true;
        } else {
            $('.passFind').each(function (i, obj) {
                obj.setAttribute("style", "display:block;")
            })
            $('.idFind').each(function (i, obj) {
                obj.setAttribute("style", "display:none;")
            })
            isIdFind = false;
        }
    }

    $(document).ready(function () {
        change(true)
        $('#submit').on('click', function (e) {
            if (isIdFind) {
                $.ajax({
                    type: "POST"
                    ,
                    url: "/maven_webmail/userHandle.do"
                    ,
                    data: {
                        menu: <%=CommandType.FIND_ID_COMMAND%>,
                        name: $('#nameValue').val(),
                        phoneNumber: $('#phoneValue').val(),
                        birthday: $('#birthday').val(),
                    }
                    ,
                    success: function (res) {
                        if (res === 'failed') {
                            alert("해당 정보로 등록된 회원이 없습니다.")
                        } else if (res === 'database error') {
                            alert("오류가 발생했습니다.")
                        } else {
                            alert("회원가입 된 아이디는 " + res + " 입니다.")
                        }
                    }
                });
            } else {
                $.ajax({
                    type: "POST"
                    ,
                    url: "/maven_webmail/userHandle.do"
                    ,
                    data: {
                        menu: <%=CommandType.FIND_PASSWORD_COMMAND%>,
                        userId: $('#passIdValue').val(),
                        name: $('#passNameValue').val(),
                        phoneNumber: $('#passPhoneValue').val(),
                        birthday: $('#passBirthday').val(),
                    }
                    ,
                    success: function (res) {
                        if (res === 'findPassword false') {
                            alert("해당 정보로 등록된 회원이 없습니다.")
                        } else if (res === 'findPassword true') {
                            const newPassword = prompt($('#passNameValue').val() + "님" + " 새 비밀번호를 입력해주세요!");
                            $.ajax({
                                type: "POST"
                                ,
                                url: "/maven_webmail/userHandle.do"
                                ,
                                data: {
                                    menu: <%=CommandType.CHANGE_PASSWORD_COMMAND%>,
                                    password: newPassword,
                                    userId: $('#passIdValue').val()
                                }
                                ,
                                success: function (res) {
                                    if (res === 'password change success') {
                                        alert("비밀번호 변경 성공!")
                                    } else if (res === 'password change failed') {
                                        alert("비밀번호 변경에 실패하였습니다.")
                                    } else {
                                        alert("오류가 발생하였습니다...")
                                    }
                                }
                            });
                        } else {
                            alert("알 수 없는 오류가 발생했습니다.")
                        }
                    }
                });
            }
        })
    });

    function reset() {
        if (isIdFind) {
            $('#nameValue').val('')
            $('#phoneValue').val('')
            $('#birthday').datepicker('setDate', '-20Y');
        } else {
            $('#passIdValue').val('')
            $('#passNameValue').val('')
            $('#passPhoneValue').val('')
            $('#passBirthday').datepicker('setDate', '-20Y');
        }
    }
</script>
<body>
<h1>아이디/비밀번호 찾기!</h1>
<table id="formTable" border="1">
    <tr>
        <th colspan="2">
            <a onclick="change(true)">아이디 찾기</a>
            <a onclick="change(false)">비밀번호 찾기</a>
        </th>
    </tr>
    <tr class="idFind">
        <td class="idFind">이름</td>
        <td class="idFind"><input class="idFind" type="text" id="nameValue"></td>
    </tr>
    <tr class="idFind">
        <td class="idFind">폰번호</td>
        <td class="idFind"><input class="idFind" type="text" id="phoneValue"></td>
    </tr>
    <tr class="idFind">
        <td class="idFind">생일</td>
        <td class="idFind"><input class="idFind" type="text" name="birthday" id="birthday" value=""/></td>
    </tr>

    <tr class="passFind">
        <td class="passFind">아이디</td>
        <td class="passFind"><input class="passFind" type="text" id="passIdValue"></td>
    </tr>
    <tr class="passFind">
        <td class="passFind">이름</td>
        <td class="passFind"><input class="passFind" type="text" id="passNameValue"></td>
    </tr>
    <tr class="passFind">
        <td class="passFind">폰번호</td>
        <td class="passFind"><input class="passFind" type="text" id="passPhoneValue"></td>
    </tr>
    <tr class="passFind">
        <td class="passFind">생일</td>
        <td class="passFind"><input class="passFind" type="text" name="birthday" id="passBirthday" value=""/></td>
    </tr>

    <tr>
        <td colspan="2">
            <button id="submit">찾기</button>
            <button onclick="reset()">초기화</button>
        </td>
    </tr>
</table>
</body>
</html>
