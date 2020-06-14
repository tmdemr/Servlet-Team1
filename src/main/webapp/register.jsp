<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="cse.maven_webmail.control.CommandType" %><%--
  Created by IntelliJ IDEA.
  User: Java
  Date: 2020-05-29
  Time: 오후 3:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE>
<html lang="ko">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>회원가입</title>
    <link type="text/css" rel="stylesheet" href="css/main_style.css"/>

    <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <script>
        var isDoubleChecked = false;
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

            $('#doubleCheck').on('click', function (e) {
                $.ajax({
                    type: "POST"
                    ,
                    url: "/maven_webmail/userHandle.do"
                    ,
                    data: {
                        menu: <%=CommandType.USER_VERIFY_COMMAND%>,
                        userId: $('#userId').val()
                    }
                    ,
                    success: function (res) {
                        if (res === 'success') {
                            isDoubleChecked = true;
                            alert("아이디 중복체크 완료!")
                        } else if (res === 'fail') {
                            isDoubleChecked = false;
                            alert("아이디가 이미 존재합니다!")
                        } else {
                            isDoubleChecked = false;
                            alert("예상치 못한 오류가 발생했습니다!")
                        }
                    }
                });
            });

            $('#submit').on('click', function (e) {
                if (check()) {
                    $.ajax({
                        type: "POST"
                        ,
                        url: "/maven_webmail/userHandle.do"
                        ,
                        data: {
                            menu: <%=CommandType.ADD_USER_COMMAND%>,
                            userId: $('#userId').val(),
                            password: $('#password').val(),
                            name: $('#name').val(),
                            birthday: $('#birthday').val(),
                            phoneNumber: $('#phoneNumber').val()
                        }
                        ,
                        success: function (res) {
                            if (res === 'register success.') {
                                alert("회원가입 완료")
                                window.location.href = 'index.jsp'
                            } else if (res === 'register database error.') {
                                alert("데이터베이스에서 오류가 발생했습니다...")
                            } else if (res === 'register server failed.') {
                                alert("메일 서버에 오류가 있습니다.")
                            } else {
                                alert("예상치 못한 오류가 발생했습니다!")
                            }
                        }
                    });
                }

            });
            $('#userId').on('change', function () {
                isDoubleChecked = false;
            });
        });

        function check() {
            if (isDoubleChecked === false) {
                alert("아이디 중복 확인을 해주세요.")
                return false
            }
            const pass1 = $('#password').val();
            if (pass1 === '') {
                alert("비밀번호를 입력해주세요.")
                return false
            }
            const pass2 = $('#passwordAuth').val();
            const name = $('#name').val();
            if (name === '') {
                alert("이름을 입력해주세요.")
                return false
            }
            const phone = $('#phoneNumber').val();
            if (phone === '') {
                alert("휴대전화번호를 입력해주세요!")
                return false;
            }
            const birthday = $('#birthday').val();
            if (birthday === '') {
                alert("생일을 입력해주세요.")
                return false;
            }
            if (pass1 !== pass2) {
                alert("비밀번호를 확인해주세요.")
            }

            return pass1 === pass2
        }


        function characterCheck() {
            var RegExp = /[ \{\}\[\]\/?.,;:|\)*~`!^\-_+┼<>@\#$%&\'\"\\\(\=]/gi;//정규식 구문
            var obj = document.getElementsByName("id")[0]
            var obj2 = document.getElementsByName("password")[0]
            var obj3 = document.getElementsByName("passwordAuth")[0]
            var obj4 = document.getElementsByName("name")[0]
            if (RegExp.test(obj.value) ) {
                alert("특수문자는 입력하실 수 없습니다.");
                obj.value = obj.value.substring(0, obj.value.length - 1);//특수문자를 지우는 구문
    
            }
                if (RegExp.test(obj2.value)) {
                alert("특수문자는 입력하실 수 없습니다.");

                obj2.value = obj2.value.substring(0, obj2.value.length - 1);//특수문자를 지우는 구문

            }
            
                if (RegExp.test(obj3.value)) {
                alert("특수문자는 입력하실 수 없습니다.");
                obj3.value = obj3.value.substring(0, obj3.value.length - 1);//특수문자를 지우는 구문
            }
            
                if (RegExp.test(obj4.value)) {
                alert("특수문자는 입력하실 수 없습니다.");
                obj4.value = obj4.value.substring(0, obj4.value.length - 1);//특수문자를 지우는 구문
            }
        }


        $(document).ready(function () {
            $('.length').keyup(function () {
                if ($(this).val().length > $(this).attr('maxlength')) {
                    alert('제한길이 초과');
                    $(this).val($(this).val().substr(0, $(this).attr('maxlength')));
                }
            });
        });


    </script>
    <style>
        .btn {
            display: inline-block;
            background: #000;
            border-radius: 4px;
            font-size: 14px;
            color: #FFF;
            padding: 8px 12px;
            cursor: pointer;
        }
    </style>
</head>
<body>
<h1>회원가입</h1>
<input type="hidden" name="menu" value="${CommandType.ADD_USER_COMMAND}"/>
<table border="0" align="left">
    <caption>회원가입</caption>
    <tr>
        <th id="user">사용자 ID</th>
        <td><input type="text" id="userId" name="id" onkeyup="characterCheck()" maxlength="10"
                   onkeydown="characterCheck()" value="" size="20"/></td>
        <td>
            <a id="doubleCheck" name="doubleCheck" class="btn">아이디 중복 확인</a>
        </td>
    </tr>
    <tr>
        <td>암호</td>
        <td><input type="password" name="password" onkeyup="characterCheck()" maxlength="10"
                   onkeydown="characterCheck()" id="password" value=""/></td>
    </tr>
    <tr>
        <td>암호확인</td>
        <td><input type="password" name="passwordAuth" onkeyup="characterCheck()" maxlength="10"
                   onkeydown="characterCheck()" id="passwordAuth" value=""/></td>
    </tr>
    <tr>
        <td>이름</td>
        <td><input type="text" name="name" onkeyup="characterCheck()" maxlength="10" onkeydown="characterCheck()"
                   id="name" value=""/></td>
    </tr>
    <tr>
        <td>생일</td>
        <td><input type="text" name="birthday" id="birthday" value=""/></td>
    </tr>
    <tr>
        <td>휴대전화번호</td>
        <td><input type="text" name="phoneNumber" id="phoneNumber" value=""/></td>
    </tr>
    <tr>
        <td colspan="2">
            <button type="submit" id="submit">회원가입</button>
            <input type="reset" value="초기화" name="reset"/>
        </td>
    </tr>
</table>


</body>
</html>
