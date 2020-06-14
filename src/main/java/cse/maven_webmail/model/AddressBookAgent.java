package cse.maven_webmail.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 * 주소록의 데이터베이스를 관리하는 모델입니다.
 * @author 박지율
 */
public class AddressBookAgent {
    private static final Logger logger = LoggerFactory.getLogger(AddressBookAgent.class);
    private String userId;
    private String nickName;
    private String email;
    private String phoneNumber;
    private String newEmail;


    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public AddressBookAgent() {
        //빈 생성자
    }

    /**
     * Datebase 연결을 반환합니다.
     * @return 데이터베이스 연결
     * @throws NamingException DBCP 실패시 반환됩니다.
     * @throws SQLException SQL 오류
     */
    private Connection getConnection() throws NamingException, SQLException {
        String name = "java:/comp/env/jdbc/JamesWebmail";
        javax.naming.Context context = new javax.naming.InitialContext();
        javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup(name);
        return dataSource.getConnection();
    }

    /**
     * 자신이 가진 주소록을 모두 삭제하는 메소드입니다.
     * @return sql 성공 여부
     */
    public boolean deleteAll() {
        boolean status;
        String sql = "DELETE FROM ADDRESSBOOK WHERE userid=?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, userId);
            preparedStatement.executeUpdate();
            status = true;
        } catch (SQLException | NamingException throwables) {
            status = false;
            logger.error(new Date() + " : " + throwables.getMessage());
        }
        return status;
    }

    /**
     * 주소록을 하나 삭제하는 메소드입니다.
     * @return sql 성공 여부
     */
    public boolean delete() {
        boolean status;
        String sql = "DELETE FROM ADDRESSBOOK WHERE userid=? AND email=?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, email);
            int rows = preparedStatement.executeUpdate();
            status = rows == 1;
        } catch (SQLException | NamingException throwables) {
            status = false;
            logger.error(new Date() + " : " + throwables.getMessage());
        }
        return status;
    }

    /**
     * 자신이 가진 주소록을 수정하는 메소드입니다.
     * @return sql 성공 여부
     */
    public boolean update() {
        boolean status;
        String sql = "UPDATE ADDRESSBOOK SET nickname=?, email=?, phoneNumber=? WHERE userid=? AND email=?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, nickName);
            preparedStatement.setString(2, newEmail);
            preparedStatement.setString(3, phoneNumber);
            preparedStatement.setString(4, userId);
            preparedStatement.setString(5, email);
            int rows = preparedStatement.executeUpdate();
            status = rows == 1;
        } catch (SQLException | NamingException throwables) {
            status = false;
            logger.error(new Date() + " : " + throwables.getMessage());
        }
        return status;
    }

    /**
     * 주소록을 삽입하는 메소드입니다.
     * @return 삽입 성공 여부
     */
    public boolean insert() {
        boolean status;
        String sql = "INSERT INTO ADDRESSBOOK(userid, nickname, email, phoneNumber) VALUES(?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, nickName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, phoneNumber);
            int rows = preparedStatement.executeUpdate();
            status = rows == 1;
        } catch (SQLException | NamingException throwables) {
            status = false;
            logger.error(new Date() + " : " + throwables.getMessage());
        }
        return status;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
