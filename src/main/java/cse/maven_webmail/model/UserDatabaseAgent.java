package cse.maven_webmail.model;

import cse.maven_webmail.control.CommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * 유저의 개인 정보 데이터베이스를 관리하는 객체입니다.
 */
public class UserDatabaseAgent {
    private String userId;
    private String name;
    private Date birthday;
    private String phoneNumber;
    private static Logger logger = LoggerFactory.getLogger(UserDatabaseAgent.class);

    public UserDatabaseAgent() {
        //빈 생성자
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setBirthdayString(String birthdayString) {
        this.birthday = Date.valueOf(birthdayString);
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * 개인정보를 삽입합니다.
     * @return 성공 여부
     */
    public boolean insert() {
        boolean status;
        String query = "INSERT INTO USERINFO(USERNAME, NAME, PHONE, BIRTHDAY) VALUES(?, ?, ?, ?)";
        try {
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query);
            ) {
                logger.trace("userId : " + userId);
                logger.trace("name : " + name);
                logger.trace("phoneNumber : " + phoneNumber);
                logger.trace("birthday : " + birthday.toString());
                preparedStatement.setString(1, userId);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, phoneNumber);
                preparedStatement.setDate(4, birthday);
                int num = preparedStatement.executeUpdate();
                status = num == 1;
            }
        } catch (SQLException | NamingException throwables) {
            logger.error(throwables.getMessage());
            status = false;
        }
        return status;
    }

    /**
     * 디비 연결을 반환합니다.
     * @return 디비 연결
     * @throws NamingException JDBC 찾기 실패했을 때
     * @throws SQLException SQL 오류
     */
    private Connection getConnection() throws NamingException, SQLException {
        String name = "java:/comp/env/jdbc/JamesWebmail";
        javax.naming.Context context = new javax.naming.InitialContext();
        javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup(name);
        return dataSource.getConnection();
    }

    /**
     * 정보를 바탕으로 아이디를 찾습니다.
     * @return 찾은 아이디
     */
    public String findId() {
        String result;
        String query = "SELECT USERNAME FROM USERINFO WHERE NAME = ? AND PHONE = ? AND BIRTHDAY = ?";
        List<String> idLists = new LinkedList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, phoneNumber);
            preparedStatement.setDate(3, birthday);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    idLists.add(resultSet.getString(1));
                }
            }
            if (idLists.isEmpty()) {
                result = "failed";
            } else {
                result = String.join(", ", idLists);
            }
        } catch (SQLException | NamingException throwables) {
            logger.error(throwables.getMessage());
            result = "database error";
        }
        logger.info("findId : {}", result);
        return result;
    }

    /**
     * 정보를 바탕으로 일치하는 회원이 있는지 찾습니다.
     * @return 찾았을 경우
     */
    public boolean findPassword() {
        boolean success = false;
        String query = "SELECT 1 FROM USERINFO WHERE NAME = ? AND PHONE = ? AND BIRTHDAY = ? AND USERNAME = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, phoneNumber);
            preparedStatement.setDate(3, birthday);
            preparedStatement.setString(4, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    success = true;
                }
            }

        } catch (SQLException | NamingException throwables) {
            logger.error(throwables.getMessage());
            success = false;
        }
        logger.info("findPassword : id {} isSuccess : {}", userId, success);
        return success;
    }

    /**
     * 정보를 바탕으로 개인정보를 변경합니다.
     * @return 개인정보 변경 성공 여부
     */
    public boolean changeMyInfo() {
        boolean success;
        String sql = "UPDATE userinfo SET NAME = ?, PHONE = ?, BIRTHDAY = ? WHERE USERNAME = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, phoneNumber);
            preparedStatement.setDate(3, birthday);
            preparedStatement.setString(4, userId);
            int updates = preparedStatement.executeUpdate();
            success = updates == 1;
        } catch (SQLException | NamingException throwables) {
            success = false;
            logger.error(throwables.getMessage());
        }
        return success;
    }

    /**
     * 개인정보를 조회할 때 개인정보를 반환합니다.
     * @return 개인정보 테이블
     */
    public String getUserData() {
        StringBuilder stringBuilder = new StringBuilder();
        String sql = "SELECT NAME, PHONE, BIRTHDAY FROM userinfo where username = ?";
        stringBuilder.append("<h2>").append(userId).append("님의 개인정보").append("</h2>");
        stringBuilder.append("<table>");
        stringBuilder.append("<form action=\"userHandle.do\" method=\"POST\">");
        stringBuilder.append("<input type=\"hidden\" name=\"menu\" value=\"");
        stringBuilder.append(CommandType.CHANGE_MY_INFO);
        stringBuilder.append("\">");
        stringBuilder.append("<input type=\"hidden\" name=\"userId\" value=\"");
        stringBuilder.append(userId).append("\">");

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, userId);
            try (ResultSet resultset = preparedStatement.executeQuery()) {
                resultset.next();
                stringBuilder.append("<tr>");
                stringBuilder.append("<td>");
                stringBuilder.append("이름");
                stringBuilder.append("</td>");
                stringBuilder.append("<td><input name=\"name\" value=\"");
                stringBuilder.append(resultset.getString(1));
                stringBuilder.append("\">");
                stringBuilder.append("</td>");
                stringBuilder.append("</tr>");
                stringBuilder.append("<tr>");
                stringBuilder.append("<td>");
                stringBuilder.append("폰번호");
                stringBuilder.append("</td>");
                stringBuilder.append("<td><input name=\"phoneNumber\" value=\"");
                stringBuilder.append(resultset.getString(2));
                stringBuilder.append("\">");
                stringBuilder.append("</td>");
                stringBuilder.append("</tr>");
                stringBuilder.append("<tr>");
                stringBuilder.append("<td>");
                stringBuilder.append("생일");
                stringBuilder.append("</td>");
                stringBuilder.append("<td><input id=\"birthday\" name=\"birthday\" value=\"");
                stringBuilder.append(resultset.getDate(3).toString());
                stringBuilder.append("\">");
                stringBuilder.append("</td>");
                stringBuilder.append("</tr>");
            }
        } catch (SQLException | NamingException throwables) {
            logger.error(throwables.getMessage());
            return "오류가 발생했습니다.";
        }
        stringBuilder.append("</table>");
        stringBuilder.append("<input type=\"submit\" value=\"변경하기\">");
        stringBuilder.append("</form>");
        stringBuilder.append("<form action=\"userHandle.do\" method=\"POST\">");
        stringBuilder.append("<input type=\"hidden\" name=\"menu\" value=\"").append(CommandType.DELETE_USER_COMMAND).append("\">");
        stringBuilder.append("<input type=\"hidden\" name=\"userId\" value=\"").append(userId).append("\">");
        stringBuilder.append("<input type=\"submit\" value=\"탈퇴하기\">");
        stringBuilder.append("</form>");
        return stringBuilder.toString();
    }

    /**
     * 회원을 삭제합니다.
     * @return 회원삭제 성공여부
     */
    public boolean deleteUser() {
        boolean status = false;
        String sql = "DELETE FROM userinfo WHERE USERNAME = ?";
        String inboxDeleteSql = "DELETE FROM inbox WHERE repository_name = ?";
        String trashDeleteSql = "DELETE FROM trash WHERE repository_name = ?";
        AddressBookAgent addressBookAgent = new AddressBookAgent();
        addressBookAgent.setUserId(userId);
        addressBookAgent.deleteAll();
        try (Connection connection = getConnection();
             PreparedStatement userInfoDeleteStatement = connection.prepareStatement(sql);
             PreparedStatement inboxDeleteStatement = connection.prepareStatement(inboxDeleteSql);
             PreparedStatement trashDeleteStatement = connection.prepareStatement(trashDeleteSql);
        ) {
            userInfoDeleteStatement.setString(1, userId);
            inboxDeleteStatement.setString(1, userId);
            trashDeleteStatement.setString(1, userId);
            int rows = userInfoDeleteStatement.executeUpdate();
            logger.info("{}가 탈퇴해서 userinfo에서 삭제됨", userId);
            int inboxDeleted = inboxDeleteStatement.executeUpdate();
            logger.info("{}가 탈퇴해서 inbox에서 {}개의 메일이 삭제됨", userId, inboxDeleted);
            int trashDeleted = trashDeleteStatement.executeUpdate();
            logger.info("{}가 탈퇴해서 휴지통에서 {}개의 메일이 삭제됨", userId, trashDeleted);
            status = true;
        } catch (SQLException | NamingException throwables) {
            logger.error(throwables.getMessage());
        }
        logger.info("{}가 탈퇴 : {}", userId, status);
        return status;
    }
}
