package cse.maven_webmail.control;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cse.maven_webmail.model.UserAdminAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jongmin
 */
public class UserAdminHandler extends HttpServlet {
    private String host;
    private int port;
    private String path;
    private static final Logger logger = LoggerFactory.getLogger(UserAdminHandler.class);

    /**
     * james_server.properties를 읽어서 host ip와 port를 initialize 하도록 하였음 - 남영우
     */
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
            logger.error("UserAdminHandler: 초기화 실패 - {}", ioe.getMessage());
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            String userid = (String) session.getAttribute("userid");
            if (userid == null || !userid.equals("admin")) {
                out.println("현재 사용자(" + userid + ")의 권한으로 수행 불가합니다.");
                out.println("<a href=/WebMailSystem/> 초기 화면으로 이동 </a>");
            } else {

                request.setCharacterEncoding(StandardCharsets.UTF_8.name());
                int select = Integer.parseInt(request.getParameter("menu"));

                switch (select) {
                    case CommandType.ADD_USER_COMMAND:
                        addUser(request, out);
                        break;

                    case CommandType.DELETE_USER_COMMAND:
                        deleteUser(request, response);
                        break;

                    default:
                        out.println("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
                        break;
                }
            }
        } catch (Exception ex) {
            logger.error("{}", ex.getMessage());
        }
    }

    /**
     * 사용자를 추가합니다.
     * @param request 요청
     * @param out PrintWriter
     */
    private void addUser(HttpServletRequest request, PrintWriter out) {
        try {
            UserAdminAgent agent = new UserAdminAgent();
            agent.setServer(host);
            agent.setCwd(path);
            agent.setPort(port);
            String userid = request.getParameter("id");  // for test
            String password = request.getParameter("password");// for test
            out.println("userid = " + userid + "<br>");
            out.println("password = " + password + "<br>");
            out.flush();
            // else 사용자 등록 실패 팝업창
            if (agent.addUser(userid, password)) {
                out.println(getUserRegistrationSuccessPopUp());
            } else {
                out.println(getUserRegistrationFailurePopUp());
            }
            out.flush();
        } catch (Exception ex) {
            out.println("시스템 접속에 실패했습니다.");
        }
    }

    /**
     * 사용자 추가 성공 알람입니다.
     * @return 사용자 추가 성공 javascript 문자열
     */
    private String getUserRegistrationSuccessPopUp() {
        String alertMessage = "사용자 등록이 성공했습니다.";
        StringBuilder successPopUp = new StringBuilder();
        successPopUp.append("<html>");
        successPopUp.append("<head>");

        successPopUp.append("<title>메일 전송 결과</title>");
        successPopUp.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"css/main_style.css\" />");
        successPopUp.append("</head>");
        successPopUp.append("<body onload=\"goMainMenu()\">");
        successPopUp.append("<script type=\"text/javascript\">");
        successPopUp.append("function goMainMenu() {");
        successPopUp.append("alert(\"");
        successPopUp.append(alertMessage);
        successPopUp.append("\"); ");
        successPopUp.append("window.location = \"admin_menu.jsp\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
    }
    /**
     * 사용자 추가 실패 알람입니다.
     * @return 사용자 추가 실패 javascript 문자열
     */
    private String getUserRegistrationFailurePopUp() {
        String alertMessage = "사용자 등록이 실패했습니다.";
        StringBuilder successPopUp = new StringBuilder();
        successPopUp.append("<html>");
        successPopUp.append("<head>");

        successPopUp.append("<title>메일 전송 결과</title>");
        successPopUp.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"css/main_style.css\" />");
        successPopUp.append("</head>");
        successPopUp.append("<body onload=\"goMainMenu()\">");
        successPopUp.append("<script type=\"text/javascript\">");
        successPopUp.append("function goMainMenu() {");
        successPopUp.append("alert(\"");
        successPopUp.append(alertMessage);
        successPopUp.append("\"); ");
        successPopUp.append("window.location = \"admin_menu.jsp\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
    }

    /**
     * 회원을 삭제하는 함수입니다.
     * @param request 요청
     * @param response 응답
     */
    private void deleteUser(HttpServletRequest request, HttpServletResponse response) {

        try {
            UserAdminAgent agent = new UserAdminAgent();
            agent.setCwd(path);
            agent.setPort(port);
            agent.setServer(host);
            String[] deleteUserList = request.getParameterValues("selectedUsers");
            agent.deleteUsers(deleteUserList);
            response.sendRedirect("admin_menu.jsp");
        } catch (Exception ex) {
            logger.error("UserAdminHandler.deleteUser : exception = {}", ex.getMessage());
        }
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
