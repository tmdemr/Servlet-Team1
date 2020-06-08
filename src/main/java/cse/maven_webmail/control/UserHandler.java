package cse.maven_webmail.control;

import cse.maven_webmail.model.UserDatabaseAgent;
import cse.maven_webmail.model.UserAdminAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class UserHandler extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);
    private String path;
    private String host;
    private int port;

    @Override
    public void init() {
        Properties props = new Properties();
        path = getServletContext().getRealPath(".");
        String propertyFile = path + "/WEB-INF/classes/config/james_server.properties";
        propertyFile = propertyFile.replace("\\", "/");
        logger.info("prop path = {}", propertyFile);
        try (BufferedInputStream bis =
                     new BufferedInputStream(
                             new FileInputStream(propertyFile))) {
            props.load(bis);
            host = props.getProperty("host");
            port = Integer.parseInt(props.getProperty("port"));
            logger.trace("host = {}\nport = {}\n", host, port);
        } catch (IOException ioe) {
            logger.error("UserHandler: 초기화 실패 - {}", ioe.getMessage());
        }
    }


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        int menu = Integer.parseInt(request.getParameter("menu"));
        logger.info(String.valueOf(menu));
        switch (menu) {
            case CommandType.USER_VERIFY_COMMAND:
                userVerify(request, response);
                break;
            case CommandType.ADD_USER_COMMAND:
                addUser(request, response);
                break;
            case CommandType.FIND_ID_COMMAND:
                findID(request, response);
                break;
            case CommandType.FIND_PASSWORD_COMMAND:
                findPassword(request, response);
                break;
            case CommandType.CHANGE_PASSWORD_COMMAND:
                changePassword(request, response);
                break;
            case CommandType.CHANGE_MY_INFO:
                changeMyInfo(request, response);
            case CommandType.DELETE_USER_COMMAND:
                userDelete(request, response);
            default:
                try (PrintWriter out = response.getWriter()) {
                    out.println("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
                }
                break;
        }

    }

    private void userDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = request.getParameter("userId");
        UserDatabaseAgent userDatabaseAgent = new UserDatabaseAgent();
        userDatabaseAgent.setUserId(userId);
        if (userDatabaseAgent.deleteUser()) {
            UserAdminAgent userAdminAgent = new UserAdminAgent();
            userAdminAgent.setCwd(path);
            userAdminAgent.setPort(port);
            userAdminAgent.setServer(host);
            if (userAdminAgent.deleteUser(userId)) {
                try (PrintWriter out = response.getWriter()) {
                    out.println("<head>\n" +
                            "    <meta charset=\"UTF-8\">\n" +
                            "    <title>회원탈퇴 성공</title>\n" +
                            "    <script type=\"text/javascript\">\n" +
                            "        function gohome() {\n" +
                            "            window.location = \"/maven_webmail/\"\n" +
                            "        }\n" +
                            "    </script>\n" +
                            "</head>\n" +
                            "<body onload=\"setTimeout('gohome()', 5000)\">\n" +
                            userId + "님 회원탈퇴되었습니다.\n" +
                            "\n" +
                            "5초후 메인화면으로 돌아갑니다." +
                            "</body>");
                }
                //성공
            } else {
                try (PrintWriter out = response.getWriter()) {
                    out.println("<head>\n" +
                            "    <meta charset=\"UTF-8\">\n" +
                            "    <title>회원탈퇴 실패</title>\n" +
                            "    <script type=\"text/javascript\">\n" +
                            "        function gohome() {\n" +
                            "            window.location = \"/maven_webmail/\"\n" +
                            "        }\n" +
                            "    </script>\n" +
                            "</head>\n" +
                            "<body onload=\"setTimeout('gohome()', 5000)\">\n" +
                            userId + "님 회원탈퇴에 실패하였습니다... 서버에 오류가 있습니다...\n" +
                            "\n" +
                            "5초후 메인화면으로 돌아갑니다." +
                            "</body>");
                }
                //제임스 서버 오류
            }
        } else {
            try (PrintWriter out = response.getWriter()) {
                out.println("<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>회원탈퇴 실패</title>\n" +
                        "    <script type=\"text/javascript\">\n" +
                        "        function gohome() {\n" +
                        "            window.location = \"/maven_webmail/\"\n" +
                        "        }\n" +
                        "    </script>\n" +
                        "</head>\n" +
                        "<body onload=\"setTimeout('gohome()', 5000)\">\n" +
                        userId + "님 회원탈퇴에 실패하였습니다... 서버에 오류가 있습니다...\n" +
                        "\n" +
                        "5초후 메인화면으로 돌아갑니다." +
                        "</body>");
            }
            //디비 오류
        }
    }

    private void changeMyInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserDatabaseAgent userDatabaseAgent = new UserDatabaseAgent();
        String phoneNumber = request.getParameter("phoneNumber");
        String name = request.getParameter("name");
        String birthdayString = request.getParameter("birthday");
        String userId = request.getParameter("userId");
        userDatabaseAgent.setBirthdayString(birthdayString);
        userDatabaseAgent.setName(name);
        userDatabaseAgent.setPhoneNumber(phoneNumber);
        userDatabaseAgent.setUserId(userId);
        if (userDatabaseAgent.changeMyInfo()) {
            response.sendRedirect("my_info.jsp");
        } else {
            try (PrintWriter out = response.getWriter()) {
                out.println("오류가 발생했습니다.");
            }
        }
    }

    private void addUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserAdminAgent userAdminAgent = new UserAdminAgent();
        userAdminAgent.setCwd(path);
        userAdminAgent.setPort(port);
        userAdminAgent.setServer(host);
        String phoneNumber = request.getParameter("phoneNumber");
        String name = request.getParameter("name");
        String birthdayString = request.getParameter("birthday");
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        UserDatabaseAgent userDatabaseAgent = new UserDatabaseAgent();
        if (userAdminAgent.addUser(userId, password)) {
            try (PrintWriter out = response.getWriter()) {
                userDatabaseAgent.setBirthdayString(birthdayString);
                userDatabaseAgent.setName(name);
                userDatabaseAgent.setPhoneNumber(phoneNumber);
                userDatabaseAgent.setUserId(userId);
                if (userDatabaseAgent.insert()) {
                    out.print("register success.");
                } else {
                    out.print("register database error.");
                }
            }
        } else {
            try (PrintWriter out = response.getWriter()) {
                out.print("register server failed.");
            }
        }
    }

    private void userVerify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = request.getParameter("userId");
        logger.info(userId);
        UserAdminAgent userAdminAgent = new UserAdminAgent();
        userAdminAgent.setCwd(path);
        userAdminAgent.setPort(port);
        userAdminAgent.setServer(host);
        boolean status = userAdminAgent.verify(userId);
        try (PrintWriter out = response.getWriter()) {
            if (status) {
                out.print("fail");
            } else {
                out.print("success");
            }
        }
    }

    private void findID(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String phoneNumber = request.getParameter("phoneNumber");
        String name = request.getParameter("name");
        String birthdayString = request.getParameter("birthday");
        UserDatabaseAgent userDatabaseAgent = new UserDatabaseAgent();
        userDatabaseAgent.setPhoneNumber(phoneNumber);
        userDatabaseAgent.setName(name);
        userDatabaseAgent.setBirthdayString(birthdayString);
        try (PrintWriter out = response.getWriter()) {
            out.print(userDatabaseAgent.findId());
        }
    }

    private void findPassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserDatabaseAgent userDatabaseAgent = new UserDatabaseAgent();
        String phoneNumber = request.getParameter("phoneNumber");
        String name = request.getParameter("name");
        String birthdayString = request.getParameter("birthday");
        String userId = request.getParameter("userId");
        userDatabaseAgent.setPhoneNumber(phoneNumber);
        userDatabaseAgent.setName(name);
        userDatabaseAgent.setBirthdayString(birthdayString);
        userDatabaseAgent.setUserId(userId);
        try (PrintWriter out = response.getWriter()) {
            out.print("findPassword " + userDatabaseAgent.findPassword());
        }
    }

    private void changePassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        UserAdminAgent userAdminAgent = new UserAdminAgent();
        userAdminAgent.setCwd(path);
        userAdminAgent.setPort(port);
        userAdminAgent.setServer(host);
        try (PrintWriter out = response.getWriter()) {
            if (userAdminAgent.setPassword(userId, password)) {
                out.print("password change success");
            } else {
                out.print("password change failed");
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
