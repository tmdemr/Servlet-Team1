/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jongmin
 */
public class UserAdminAgent {
    private static final Logger logger = LoggerFactory.getLogger(UserAdminAgent.class); // 로거
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private boolean isConnected = false;
    private String rootId;  //  = "root";
    private String rootPassword;  // = "root";
    private String adminId; //  = "admin";
    private static final String EOL = "\r\n";
    private String cwd;
    private String server;
    private int port;

    public UserAdminAgent() {
        // 빈 생성자
    }

    /**
     * path를 설정합니다.
     * @param cwd
     */
    public void setCwd(String cwd) {
        this.cwd = cwd;
    }

    /**
     * 서버를 설정하고 연결합니다.
     * @param server 서버
     * @throws Exception
     */
    public void setServer(String server) throws Exception {
        this.server = server;
        initialize();
    }

    /**
     * 포트를 설정합니다.
     * @param port 포트
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 프로퍼티를 열어서 설정 값들을 가져오고, 연결합니다.
     * @throws Exception
     */
    public void initialize() throws Exception {
        Properties props = new Properties();
        String propertyFile = this.cwd + "/WEB-INF/classes/config/system.properties";
        propertyFile = propertyFile.replace("\\", "/");
        logger.info("prop path = {}", propertyFile);
        try (BufferedInputStream bis =
                     new BufferedInputStream(
                             new FileInputStream(propertyFile))) {
            props.load(bis);
            rootId = props.getProperty("root_id");
            rootPassword = props.getProperty("root_password");
            adminId = props.getProperty("admin_id");
            logger.info("ROOT_ID = {}\nROOT_PASS = {}\n", rootId, rootPassword);
        } catch (IOException ioe) {
            logger.error("UserAdminAgent: 초기화 실패 - {}", ioe.getMessage());
        }
        socket = new Socket(server, port);
        is = socket.getInputStream();
        os = socket.getOutputStream();

        isConnected = connect();
    }

    /**
     * 사용자를 추가합니다.
     * @param userId 아이디
     * @param password 비밀번호
     * @return 사용자 추가 성공여부
     */
    public boolean addUser(String userId, String password) {
        boolean status = false;
        logger.info("addUser() called");
        if (!isConnected) {
            return status;
        }
        try {
            // 1: "adduser" command
            String addUserCommand = "adduser " + userId + " " + password + EOL;
            String recvMessage = sendCommand(addUserCommand);
            logger.trace(recvMessage);
            //}
            // 3: 기존 메일사용자 여부 확인
            status = recvMessage.contains("added");

        } catch (Exception ex) {
            logger.error("addUser failed : {}", ex.getMessage());
            status = false;
        }
        return status; // 상태 반환
    }  // addUser()

    /**
     * 단일 사용자 제거를 합니다.
     * @param userId 사용자아이디
     * @return 사용자 제거 성공여부
     */
    public boolean deleteUser(String userId) {
        boolean status = false;
        String command = "deluser " + userId + EOL;
        if (!isConnected) {
            return status;
        }
        try {
            String recvMessage = sendCommand(command);
            logger.trace(recvMessage);
            status = recvMessage.contains("deleted");

        } catch (Exception ex) {
            logger.error("deleteUser failed : {}", ex.getMessage());
            status = false;
        }
        return status; // 상태 반환
    }

    /**
     * 사용자 리스트를 반환합니다.
     * @return 사용자 리스트
     */
    public List<String> getUserList() {
        List<String> userList = new LinkedList<>();
        if (!isConnected) {
            return userList;
        }
        try {
            // 1: "listusers" 명령 송신
            String command = "listusers " + EOL;
            // 3: 응답 메시지 처리
            String recvMessage = sendCommand(command);
            logger.info(recvMessage);
            userList = parseUserList(recvMessage);
        } catch (Exception ex) {
            logger.error("getUserList error : {}", ex.getMessage());
        }
        return userList;

    }  // getUserList()

    /**
     * 사용자 리스트를 파싱합니다.
     * @param message 받은 모든 결과
     * @return 파싱된 리스트
     */
    private List<String> parseUserList(String message) {
        // 1: 줄 단위로 나누기
        String[] lines = message.split(EOL);
        // 2: 첫 번째 줄에는 등록된 사용자 수에 대한 정보가 있음.
        //    예) Existing accounts 7
        String[] firstLine = lines[0].split(" ");
        int numberOfUsers = Integer.parseInt(firstLine[2]);
        String[] accountLines = new String[numberOfUsers];
        System.arraycopy(lines, 1, accountLines, 0, numberOfUsers);
        return Arrays.stream(accountLines).map(line -> { // java 8의 스트림으로 변경해보았음.
            if (line.split(" ")[1].equals(adminId))
                return null;
            else
                return line.split(" ")[1];
        }).filter(Objects::nonNull).collect(Collectors.toList());

        // 3: 두 번째 줄부터는 각 사용자 ID 정보를 보여줌.
        //    예) user: admin
        /*
        for (int i = 1; i <= numberOfUsers; i++) {
            // 3.1: 한 줄을 구분자 " "로 나눔.
            String[] userLine = lines[i].split(" ");
            // 3.2 사용자 ID가 관리자 ID와 일치하는 지 여부 확인
            if (!userLine[1].equals(ADMIN_ID)) {
                userList.add(userLine[1]);
            }
        }

         */
        //return userList;
    } // parseUserList()

    /**
     * 사용자 여럿을 제거합니다.
     * @param userList 선택된 사용자들
     * @return 제거 성공 여부
     */
    public boolean deleteUsers(String[] userList) {
        byte[] messageBuffer = new byte[1024];
        String command;
        String recvMessage;
        boolean status = false;
        UserDatabaseAgent userDatabaseAgent = new UserDatabaseAgent();
        logger.info("deleteUsers Connected... : {}",isConnected);
        if (!isConnected) {
            return false;
        }
        logger.info("selected users : {}",String.join(", ", userList));
        try {
            for (String userId : userList) {
                userDatabaseAgent.setUserId(userId);
                userDatabaseAgent.deleteUser();
                // 1: "deluser" 명령 송신
                command = "deluser " + userId + EOL;
                os.write(command.getBytes());
                logger.trace(command);

                // 2: 응답 메시지 수신
                java.util.Arrays.fill(messageBuffer, (byte) 0);
                int bytes = is.read(messageBuffer);
                logger.trace("deluser : {}byte", bytes);
                // 3: 응답 메시지 분석
                recvMessage = new String(messageBuffer);
                logger.trace(recvMessage);
                if (recvMessage.contains("deleted")) {
                    status = true;
                }
            }
            quit();
        } catch (Exception ex) {
            logger.error("delete users error : {}", ex.getMessage());
        }
        return status;

    }

    /**
     * 사용자가 존재하는지 검사합니다.
     * @param userid 검사할 아이디
     * @return 사용자 존재 여부
     */
    public boolean verify(String userid) {
        boolean status = false;
        try {
            // --> verify userid
            String verifyCommand = "verify " + userid + EOL;
            String recvMessage = sendCommand(verifyCommand);
            if (recvMessage.contains("exists")) {
                status = true;
            }
            logger.info(recvMessage);
        } catch (IOException ex) {
            logger.error("verify error : {}", ex.getMessage());
        }
        return status;

    }

    /**
     * 원격 관리 도구에 접속하는 메소드입니다.
     * @return 연결 성공 여부
     * @throws Exception socket이라서
     */
    private boolean connect() throws Exception {
        byte[] messageBuffer = new byte[1024];
        boolean returnVal;
        int bytes;
        String sendMessage;
        logger.info("UserAdminAgent.connect() called...");
        // root 인증: id, passwd - default: root
        // 1: Login Id message 수신
        bytes = is.read(messageBuffer);
        logger.info("connect : {}bytes",bytes);
        String recvMessage = new String(messageBuffer);
        logger.trace(recvMessage);
        // 2: rootId 송신
        sendMessage = rootId + EOL;
        os.write(sendMessage.getBytes());

        // 3: Password message 수신
        java.util.Arrays.fill(messageBuffer, (byte) 0);
        bytes = is.read(messageBuffer);
        logger.info("connect : {}bytes",bytes);
        recvMessage = new String(messageBuffer);
        logger.trace(recvMessage);
        // 4: rootPassword 송신
        sendMessage = rootPassword + EOL;
        os.write(sendMessage.getBytes());

        // 5: welcome message 수신
        java.util.Arrays.fill(messageBuffer, (byte) 0);
        bytes = is.read(messageBuffer);
        logger.info("connect : {}bytes",bytes);
        recvMessage = new String(messageBuffer);
        logger.trace(recvMessage);
        returnVal = recvMessage.contains("Welcome");
        logger.info("connecntion : " + returnVal);
        return returnVal;
    }  // connect()

    /**
     * 원격 제어 도구를 종료하는 메소드입니다.
     * @return 종료 성공 여부
     */
    public boolean quit() {
        byte[] messageBuffer = new byte[1024];
        boolean status = false;
        // quit
        try {
            // 1: quit 명령 송신
            String quitCommand = "quit" + EOL;
            os.write(quitCommand.getBytes());
            // 2: quit 명령에 대한 응답 수신
            java.util.Arrays.fill(messageBuffer, (byte) 0);
            int bytes = is.read(messageBuffer);
            logger.info("나가기는 {}바이트", bytes);
            // 3: 메시지 분석
            String recvMessage = new String(messageBuffer);
            logger.info(recvMessage);
            status = recvMessage.contains("Bye"); // Bye인걸로 확인됨 그 전의 closed는 잘못된 거임
            socket.close();
        } catch (IOException ex) {
            logger.error("UserAdminAgent.quit() error : {}", ex.getMessage());
        }
        return status;

    }

    /**
     * 명령을 보낼 때 사용하는 메소드입니다. 사용자 리스트의 경우 끝이 어딘지 알 수 없고 얼마나 많은
     * 바이트를 받을지 알 수 없어서 만든 메소드입니다.
     * @param command 보낼 명령
     * @return 명령에  대한 결과
     * @throws IOException 소켓이라...
     */
    private String sendCommand(String command) throws IOException {
        os.write(command.getBytes());
        String quitCommand = "quit" + EOL;
        os.write(quitCommand.getBytes());
        byte[] allResults = is.readAllBytes();
        byte[] quitResults = Arrays.copyOfRange(allResults, allResults.length - 6, allResults.length - 1); // Bye일거임
        logger.info("quit의 Bye일거임 : " + new String(quitResults));
        byte[] commandResults = Arrays.copyOfRange(allResults, 0, allResults.length - 7);
        logger.info("정보일거임 : " + new String(commandResults));
        socket.close();
        return new String(commandResults);
    }

    /**
     * 사용자의 비밀번호를 바꾸는 메소드입니다.
     * @param userId 바꿀 사용자 아이디
     * @param newPassword 새로운 비밀번호
     * @return 비밀번호 변경 성공 여부
     */
    public boolean setPassword(String userId, String newPassword) {
        boolean status = false;
        try {
            String setPasswordCommand = "setpassword " + userId + " " + newPassword + EOL;
            String recvMessage = sendCommand(setPasswordCommand);
            if (recvMessage.contains("Password")) {
                status = true;
            }
            logger.info(recvMessage);
        } catch (IOException ex) {
            logger.error("setPassword error : {}", ex.getMessage());
        }
        return status;
    }
}
