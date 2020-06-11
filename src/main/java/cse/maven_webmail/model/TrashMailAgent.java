package cse.maven_webmail.model;

import cse.maven_webmail.control.CommandType;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class TrashMailAgent {
    private static final Logger logger = LoggerFactory.getLogger(TrashMailAgent.class);
    private String userId;
    private String messageName;
    private String result;
    private String dir;
    private String fileName;
    public TrashMailAgent() {

    }

    public String getFileName() {
        return fileName;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setMessageName(String messageName) {
        logger.info("messageName : {}", messageName);
        this.messageName = messageName;
    }

    public String getResult() {
        String sql = "SELECT message_body FROM trash WHERE message_name = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, messageName);
            logger.info("{}", preparedStatement.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    TrashMessageFormatter trashMessageFormatter = new TrashMessageFormatter();
                    MimeStreamParser parser = new MimeStreamParser();
                    parser.setContentHandler(trashMessageFormatter);
                    try (InputStream inputStream = resultSet.getBinaryStream(1)) {
                        parser.parse(inputStream);
                    }
                    stringBuilder.append("보낸 사람 : ").append(trashMessageFormatter.getFromAddress()).append("<br>");
                    stringBuilder.append("받은 사람 : ").append(trashMessageFormatter.getToAddress()).append("<br>");
                    stringBuilder.append("Cc : ").append(trashMessageFormatter.getCc() == null ? "" : trashMessageFormatter.getCc()).append("<br>");
                    stringBuilder.append("보낸 날짜 : ").append(trashMessageFormatter.getDate()).append("<br>");
                    stringBuilder.append("제목 : ").append(trashMessageFormatter.getSubject()).append("<br>");
                    stringBuilder.append("<hr>");
                    stringBuilder.append(trashMessageFormatter.getBodyString());
                    stringBuilder.append("<hr>");
                    if (trashMessageFormatter.getFileName() != null) {
                        stringBuilder.append("<form action=\"trash.do\" method=\"POST\">");
                        stringBuilder.append("<input type=\"hidden\" name=\"menu\" value=\"").append(CommandType.DOWNLOAD_COMMAND).append("\"/>");
                        stringBuilder.append("<input type=\"hidden\" name=\"messageName\" value=\"").append(messageName).append("\"/>");
                        stringBuilder.append("<input type=\"submit\" value=\"");
                        stringBuilder.append("파일 : ").append(trashMessageFormatter.getFileName());
                        stringBuilder.append("\"/>");
                        stringBuilder.append("</form>");
                    }
                    result = stringBuilder.toString();
                } else {
                    return "데이터베이스 오류가 발생했습니다.";
                }
            }
        } catch (SQLException | NamingException | IOException | MimeException throwables) {
            logger.error(throwables.getMessage());
            return "오류가 발생했습니다." + throwables.getMessage();
        }
        return result;
    }

    public void download() {

        String sql = "SELECT message_body FROM trash WHERE message_name = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, messageName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    try (InputStream inputStream = resultSet.getBinaryStream(1)) {
                        TrashMessageFormatter trashMessageFormatter = new TrashMessageFormatter();
                        MimeStreamParser parser = new MimeStreamParser();
                        parser.setContentHandler(trashMessageFormatter);
                        parser.parse(inputStream);
                        fileName = trashMessageFormatter.getFileName();
                        try (InputStream fileStream = trashMessageFormatter.getFileStream();
                             FileOutputStream fileOutputStream = new FileOutputStream(dir + "/" + fileName);
                        ) {
                            byte[] decoded = fileStream.readAllBytes();
                            fileOutputStream.write(decoded);
                        }
                    }
                } else {
                    logger.error("messageName을 못찾았음 : {}", messageName);
                }
            }
        } catch (SQLException | NamingException | MimeException | IOException throwables) {
            logger.error(throwables.getMessage());
        }
    }

    public List<String> getResults() {
        List<String> results = new LinkedList<>();
        String sql = "SELECT message_name, message_body FROM trash WHERE repository_name = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String messageName = resultSet.getString(1);
                    TrashMessageFormatter trashMessageFormatter = new TrashMessageFormatter();
                    MimeStreamParser parser = new MimeStreamParser();
                    parser.setContentHandler(trashMessageFormatter);
                    try (InputStream inputStream = resultSet.getBinaryStream(2)) {
                        parser.parse(inputStream);
                    }
                    stringBuilder.append("<tr>");
                    stringBuilder.append("<td>");
                    stringBuilder.append(resultSet.getRow());
                    stringBuilder.append("</td>");
                    stringBuilder.append("<td>");
                    stringBuilder.append(trashMessageFormatter.getFromAddress());
                    stringBuilder.append("</td>");
                    stringBuilder.append("<td>");
                    stringBuilder.append("<form action=\"show_trash_message.jsp\" method=\"POST\">");
                    stringBuilder.append("<input type=\"hidden\" name=\"messageName\" value=\"");
                    stringBuilder.append(URLEncoder.encode(messageName, StandardCharsets.UTF_8)).append("\"/>");
                    stringBuilder.append("<input type=\"submit\" class=\"submitLink\" value=\"");
                    stringBuilder.append(trashMessageFormatter.getSubject());
                    stringBuilder.append("\"/>");
                    stringBuilder.append("</form>");
                    stringBuilder.append("</td>");
                    stringBuilder.append("<td>");
                    stringBuilder.append(trashMessageFormatter.getDate());
                    stringBuilder.append("</td>");
                    stringBuilder.append("<td>");
                    stringBuilder.append("<form action=\"trash.do\" method=\"POST\">");
                    stringBuilder.append("<input type=\"hidden\" name=\"menu\" value=\"");
                    stringBuilder.append(CommandType.DELETE_MAIL_COMMAND).append("\"/>");
                    stringBuilder.append("<input type=\"hidden\" name=\"messageName\" value=\"");
                    stringBuilder.append(messageName).append("\"/>");
                    stringBuilder.append("<input type=\"submit\" class=\"submitLink\" value=\"");
                    stringBuilder.append("삭제");
                    stringBuilder.append("\"/>");
                    stringBuilder.append("</form>");
                    stringBuilder.append("</td>");
                    stringBuilder.append("<td>");
                    stringBuilder.append("<form action=\"trash.do\" method=\"POST\">");
                    stringBuilder.append("<input type=\"hidden\" name=\"menu\" value=\"");
                    stringBuilder.append(CommandType.RESTORE_MAIL_COMMAND).append("\"/>");
                    stringBuilder.append("<input type=\"hidden\" name=\"messageName\" value=\"");
                    stringBuilder.append(messageName).append("\"/>");
                    stringBuilder.append("<input type=\"submit\" class=\"submitLink\" value=\"");
                    stringBuilder.append("복구");
                    stringBuilder.append("\"/>");
                    stringBuilder.append("</form>");
                    stringBuilder.append("</td>");
                    stringBuilder.append("</tr>");
                    results.add(stringBuilder.toString());
                }
            }
        } catch (SQLException | NamingException | IOException | MimeException throwables) {
            logger.error(throwables.getMessage());
        }
        return results;
    }

    public boolean restore() {
        boolean status = false;
        String firstSql = "INSERT INTO inbox SELECT * FROM trash WHERE message_name= ?";
        String second = "DELETE FROM trash WHERE message_name = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(firstSql);
             PreparedStatement secondStatement = connection.prepareStatement(second);
        ) {
            preparedStatement.setString(1, messageName);
            int rows = preparedStatement.executeUpdate();
            if (rows == 1) {
                secondStatement.setString(1, messageName);
                int secondRow = secondStatement.executeUpdate();
                status = secondRow == 1;
            }
        } catch (SQLException | NamingException throwables) {
            status = false;
            logger.error(throwables.getMessage());
        }
        return status;
    }

    public boolean delete() {
        boolean status;
        String sql = "DELETE FROM trash WHERE message_name = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, messageName);
            int rows = preparedStatement.executeUpdate();
            status = rows == 1;
        } catch (SQLException | NamingException throwables) {
            status = false;
            logger.error(throwables.getMessage());
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
