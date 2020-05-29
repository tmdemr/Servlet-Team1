package cse.maven_webmail.control;

import cse.maven_webmail.model.RegisterDatabaseAgent;
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

        String userId = request.getParameter("userId");
        UserAdminAgent userAdminAgent = new UserAdminAgent(host, port, path);
        switch (menu) {
            case CommandType.USER_VERIFY_COMMAND:
                logger.info(userId);
                boolean status = userAdminAgent.verify(userId);
                try (PrintWriter out = response.getWriter()) {
                    System.out.println(status);
                    if (status) {
                        out.print("fail");
                    } else {
                        out.print("success");
                    }
                }
                break;
            case CommandType.ADD_USER_COMMAND:
                String password = request.getParameter("password");
                String phoneNumber = request.getParameter("phoneNumber");
                String name = request.getParameter("name");
                String birthdayString = request.getParameter("birthday");
                if (userAdminAgent.addUser(userId, password)) {
                    try (PrintWriter out = response.getWriter()) {
                        RegisterDatabaseAgent registerDatabaseAgent = new RegisterDatabaseAgent();
                        registerDatabaseAgent.setBirthdayString(birthdayString);
                        registerDatabaseAgent.setName(name);
                        registerDatabaseAgent.setPhoneNumber(phoneNumber);
                        registerDatabaseAgent.setUserId(userId);
                        if (registerDatabaseAgent.insert()) {
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

                break;
            default:
                try (PrintWriter out = response.getWriter()) {
                    out.println("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
                }
                break;
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
