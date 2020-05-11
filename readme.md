tomcat server.xml 추가
#
<Resource name="jdbc/JamesWebmail" auth="Container"
                    type="javax.sql.DataSource"
                    maxTotal="8" maxIdle="2" maxWaitMillis="5000"
                    username="james_webmail" password="123456"
                    url="jdbc:mysql://localhost:3306/jamesdb?serverTimezone=Asia/Seoul"/>


#
mysql 아이디 james_webmail, 비번 123456, 스키마 jamesdb로 하였음 바꿨으면 바꿔야 함
sqls.sql 실행
#

