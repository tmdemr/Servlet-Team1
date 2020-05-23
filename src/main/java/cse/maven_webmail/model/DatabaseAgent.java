package cse.maven_webmail.model;

import cse.maven_webmail.control.CommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

public class DatabaseAgent {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseAgent.class);
    private String userId;
    private String messageId;

    public DatabaseAgent() {

    }

    public boolean sendMessageToDB(SmtpAgent smtpAgent, String messageId) throws NamingException {
        String name = "java:/comp/env/jdbc/JamesWebmail";
        javax.naming.Context context = new javax.naming.InitialContext();
        javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup(name);
        boolean status = false;
        String subject = smtpAgent.getSubj();
        String userId = smtpAgent.getUserid();
        String toUser = smtpAgent.getTo();
        String cc = smtpAgent.getCc();
        String body = smtpAgent.getBody();
        String fileString = smtpAgent.getFile1();
        int i = 1;
        String query = "INSERT INTO SENDEDMESSAGES( MESSAGE_ID, USERID, TOUSER, CC, SUBJECT, BODY,FILENAME, FILE) VALUES(?, ?, ?, ?, ?,? ,?, ?)";
        FileInputStream fileInputStream=null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setString(i++, messageId);
            preparedStatement.setString(i++, userId);
            preparedStatement.setString(i++, toUser);
            preparedStatement.setString(i++, cc);
            preparedStatement.setString(i++, subject);
            preparedStatement.setString(i++, body);
            if (fileString==null||fileString.equals("")) {
                logger.error("error");
                preparedStatement.setString(i++, null);
                preparedStatement.setNull(i++,java.sql.Types.BLOB);
            } else {
                int index = fileString.lastIndexOf('/');
                String fileName = fileString.substring(index + 1);
                File file = new File(fileString);
                fileInputStream = new FileInputStream(file);
                preparedStatement.setString(i++, fileName);
                preparedStatement.setBlob(i++, fileInputStream);
            }
            if (preparedStatement.executeUpdate() == 1) {
                status = true;
            }
        } catch (FileNotFoundException e) {
            logger.error("FileNotFoundException");
            logger.error(new Date() + e.getMessage());
        } catch (SQLException throwables) {
            logger.error("sqlException");
            logger.error(new Date() + throwables.getMessage());
        } finally {
            if(fileInputStream!=null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    logger.error(new Date()+" "+e.getMessage());
                }
            }
        }
        return status;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMySendedMessages() throws NamingException {
        String name = "java:/comp/env/jdbc/JamesWebmail";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<table border = 1>");
        stringBuilder.append("<tr> "
                + " <th> No. </td> "
                + " <th> 제목 </td>     "
                + "<th> 수신자 </td>     "
                + " <th> 보낸 날짜 </td>   "
                + " <th> 삭제 </td>   "
                + " </tr>");
        javax.naming.Context context = new javax.naming.InitialContext();
        javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup(name);
        String query = "SELECT MESSAGE_ID,SUBJECT, TOUSER, SENDED_TIME FROM SENDEDMESSAGES WHERE USERID=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setString(1, this.userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            int i = 0;
            while (resultSet.next()) {
                stringBuilder.append("<tr> " + " <td id=no>").append(i + 1).append(" </td> ");
                stringBuilder.append("<td><a href=\"show_sended_message.jsp?messageId=")
                        .append(resultSet.getString(1))
                        .append("\">").append(resultSet.getString(2)).append("</a></td>");
                stringBuilder.append("<td>").append(resultSet.getString(3)).append("</td>");
                stringBuilder.append("<td>").append(resultSet.getDate(4)).append("</td>");
                stringBuilder.append("<td><a href=\"sendedMail.do?menu=").append(CommandType.DELETE_MAIL_COMMAND).append("&userId=").append(userId)
                        .append("&messageId=").append(resultSet.getString(1)).append("\"").append(">삭제</a></td>");
                i++;
            }
        } catch (SQLException throwables) {
            logger.error(new Date() + throwables.getMessage());
        }
        stringBuilder.append("</table>");
        return stringBuilder.toString();
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() throws NamingException {
        String name = "java:/comp/env/jdbc/JamesWebmail";
        StringBuilder stringBuilder = new StringBuilder();
        javax.naming.Context context = new javax.naming.InitialContext();
        javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup(name);
        String query = "SELECT TOUSER, CC, SUBJECT, BODY, FILENAME, SENDED_TIME FROM sendedmessages WHERE MESSAGE_ID = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setString(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            stringBuilder.append("받은 사람: ").append(resultSet.getString(1)).append(" <br>");
            stringBuilder.append("Cc &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; : ").append(resultSet.getString(2)).append(" <br>");
            stringBuilder.append("보낸 날짜: ").append(resultSet.getDate(6)).append(" <br>");
            stringBuilder.append("제 &nbsp;&nbsp;&nbsp;  목: ").append(resultSet.getString(3)).append(" <br> <hr>");
            stringBuilder.append(resultSet.getString(4));
            String attachedFile = resultSet.getString(5);
            if (attachedFile != null) {
                stringBuilder.append("<br> <hr> 첨부파일: <a href=\"sendedMail.do?menu=").append(CommandType.DOWNLOAD_COMMAND).append("&userId=").append(userId)
                        .append("&messageId=").append(messageId).append("&fileName=").append(attachedFile).append("\" target=\"_top\">").append(attachedFile)
                        .append("</a>");
            }
            resultSet.close();
        } catch (SQLException throwables) {
            logger.error(new Date() + throwables.getMessage());
        }

        return stringBuilder.toString();
    }

    public void download(String directory, String fileName) throws NamingException {
        String name = "java:/comp/env/jdbc/JamesWebmail";
        javax.naming.Context context = new javax.naming.InitialContext();
        javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup(name);
        String query = "SELECT FILE FROM sendedmessages WHERE MESSAGE_ID = ? AND USERID = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setString(1, messageId);
            preparedStatement.setString(2, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            FileOutputStream fileOutputStream = new FileOutputStream(new File(directory + "/" + fileName));
            InputStream inputStream = resultSet.getBinaryStream(1);
            fileOutputStream.write(inputStream.readAllBytes());
            fileOutputStream.close();
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean delete() throws NamingException {
        boolean success = false;
        String name = "java:/comp/env/jdbc/JamesWebmail";
        javax.naming.Context context = new javax.naming.InitialContext();
        javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup(name);
        String query = "DELETE FROM sendedmessages WHERE MESSAGE_ID = ? AND USERID = ?";
        logger.info("messageId : " + messageId);
        logger.info("userId : " + userId);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setString(1, messageId);
            preparedStatement.setString(2, userId);
            int rows = preparedStatement.executeUpdate();
            if (rows == 1) {
                success = true;
            }
        } catch (SQLException throwables) {
            logger.error(new Date() + throwables.getMessage());
        }
        logger.info("삭제 : " + success);
        return success;
    }
}
