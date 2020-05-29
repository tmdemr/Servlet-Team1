package cse.maven_webmail.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterDatabaseAgent {
    private String userId;
    private String name;
    private Date birthday;
    private String phoneNumber;
    private static Logger logger = LoggerFactory.getLogger(RegisterDatabaseAgent.class);

    public RegisterDatabaseAgent() {

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

    private Connection getConnection() throws NamingException, SQLException {
        String name = "java:/comp/env/jdbc/JamesWebmail";
        javax.naming.Context context = new javax.naming.InitialContext();
        javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup(name);
        return dataSource.getConnection();
    }


}
