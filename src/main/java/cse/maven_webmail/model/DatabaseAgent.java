package cse.maven_webmail.model;

import javax.naming.NamingException;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

public class DatabaseAgent {

    public DatabaseAgent() {

    }

    public boolean sendMessageToDB(SmtpAgent smtpAgent) throws NamingException {
        String name = "java:/comp/env/jdbc/JamesWebmail";
        boolean status = false;
        javax.naming.Context context = new javax.naming.InitialContext();
        javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup(name);

        String subject = smtpAgent.getSubj();
        String userId = smtpAgent.getUserid();
        String toUser = smtpAgent.getTo();
        String cc = smtpAgent.getCc();
        String body = smtpAgent.getBody();
        String file = smtpAgent.getFile1();
        System.out.println(file);
        String query = "INSERT INTO SENDEDMESSAGES( USERID, TOUSER, CC, SUBJECT, BODY,FILENAME, FILE) VALUES( ?, ?, ?, ?,? ,?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            FileInputStream fileInputStream = null;
            if(file!=null){
                fileInputStream = new FileInputStream(new File(file));
            }
            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, toUser);
            preparedStatement.setString(3, cc);
            preparedStatement.setString(4, subject);
            preparedStatement.setString(5, body);
            int index = file.lastIndexOf('/');
            String fileName = file.substring(index + 1);
            preparedStatement.setString(6, fileName);
            preparedStatement.setBlob(7, fileInputStream);
            if (preparedStatement.executeUpdate() == 1) {
                status = true;
            }
            if(fileInputStream!=null){
                fileInputStream.close();
            }
        } catch (Exception e) {
            System.err.println(DatabaseAgent.class.getName() + e.getMessage());
            e.printStackTrace();
        }
        return status;
    }
}
