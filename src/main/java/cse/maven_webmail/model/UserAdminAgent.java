/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author jongmin
 */
public class UserAdminAgent implements AutoCloseable {


    boolean isConnected = false;
    private String ADMIN_ID; //  = "admin";
    private final String EOL = "\r\n";
    private  String JMX_RMI_URL_STRING;
    private String USER_BEAN_STRING;
    private ObjectName userBeanName;
    private MBeanServerConnection mBeanServerConnection;
    private JMXConnector jmxConnector;

    private String cwd;


    public UserAdminAgent(String cwd) throws Exception {
        this.cwd = cwd;
        initialize();
        userBeanName = new ObjectName(USER_BEAN_STRING);
        JMXServiceURL jmxServiceURL = new JMXServiceURL(JMX_RMI_URL_STRING);
        jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, null);
        mBeanServerConnection = jmxConnector.getMBeanServerConnection();
        //isConnected = connect();
    }

    private void initialize() {
        Properties props = new Properties();

        String propertyFile = cwd + "/WEB-INF/classes/conf/system.properties";
        propertyFile = propertyFile.replace("\\", "/");
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(propertyFile))) {
            props.load(bis);
            USER_BEAN_STRING = props.getProperty("user_bean");
            JMX_RMI_URL_STRING = props.getProperty("jmx_rmi_url");
            ADMIN_ID = props.getProperty("admin_id");
        } catch (IOException ioe) {
            System.out.println("UserAdminAgent: 초기화 실패 - " + ioe.getMessage());
        }
    }


    // return value:
    //   - true: addUser operation successful
    //   - false: addUser operation failed
    public boolean addUser(String userId, String password) {
        boolean status = false;
        byte[] messageBuffer = new byte[1024];

        System.out.println("addUser() called");
        if (!isConnected) {
            return status;
        }

        try {
            // 1: "adduser" command
            String addUserCommand = "adduser " + userId + " " + password + EOL;
            //os.write(addUserCommand.getBytes());

            // 2: response for "adduser" command
            java.util.Arrays.fill(messageBuffer, (byte) 0);
            //if (is.available() > 0) {
            //is.read(messageBuffer);
            String recvMessage = new String(messageBuffer);
            System.out.println(recvMessage);
            //}
            // 3: 기존 메일사용자 여부 확인
            if (recvMessage.contains("added")) {
                status = true;
            } else {
                status = false;
            }
            // 4: 연결 종료
            // quit();
            System.out.flush();  // for test
            //socket.close();
        } catch (Exception ex) {
            System.out.println(ex.toString());
            status = false;
        } finally {
            // 5: 상태 반환
            return status;
        }
    }  // addUser()

    public List<String> getUserList() {
        String[] users = new String[0];
        try {
            mBeanServerConnection.invoke(userBeanName, "listAllUsers", null, null);
            users = (String[]) mBeanServerConnection.invoke(userBeanName, "listAllUsers", null, null);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return Arrays.stream(users).collect(Collectors.toList());
    }  // getUserList()

    private List<String> parseUserList(String message) {
        List<String> userList = new LinkedList<String>();

        // 1: 줄 단위로 나누기
        String[] lines = message.split(EOL);
        // 2: 첫 번째 줄에는 등록된 사용자 수에 대한 정보가 있음.
        //    예) Existing accounts 7
        String[] firstLine = lines[0].split(" ");
        int numberOfUsers = Integer.parseInt(firstLine[2]);

        // 3: 두 번째 줄부터는 각 사용자 ID 정보를 보여줌.
        //    예) user: admin
        for (int i = 1; i <= numberOfUsers; i++) {
            // 3.1: 한 줄을 구분자 " "로 나눔.
            String[] userLine = lines[i].split(" ");
            // 3.2 사용자 ID가 관리자 ID와 일치하는 지 여부 확인
            if (!userLine[1].equals(ADMIN_ID)) {
                userList.add(userLine[1]);
            }
        }
        return userList;
    } // parseUserList()

    public boolean deleteUsers(String[] userList) {
        byte[] messageBuffer = new byte[1024];
        String command;
        String recvMessage;
        boolean status = false;

        if (!isConnected) {
            return status;
        }

        try {
            for (String userId : userList) {
                // 1: "deluser" 명령 송신
                command = "deluser " + userId + EOL;
                // os.write(command.getBytes());
                System.out.println(command);

                // 2: 응답 메시지 수신
                java.util.Arrays.fill(messageBuffer, (byte) 0);
                // is.read(messageBuffer);

                // 3: 응답 메시지 분석
                recvMessage = new String(messageBuffer);
                System.out.println(recvMessage);
                if (recvMessage.contains("deleted")) {
                    status = true;
                }
            }
            // quit();
        } catch (Exception ex) {
            System.err.println(ex);
        } finally {
            return status;
        }
    }  // deleteUsers()

    public boolean verify(String userId) throws ReflectionException, MBeanException, InstanceNotFoundException, IOException {
        boolean status = (boolean) mBeanServerConnection.invoke(userBeanName, "verifyExists", new Object[]{userId}, null);
        return status;
    }

    private void connect() throws IOException {
        JMXServiceURL jmxServiceURL = new JMXServiceURL(JMX_RMI_URL_STRING);
        jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, null);
        mBeanServerConnection = jmxConnector.getMBeanServerConnection();
    }

    @Override
    public void close() throws IOException {
        jmxConnector.close();
    }
}
