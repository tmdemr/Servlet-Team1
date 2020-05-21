package cse.maven_webmail.control;

import cse.maven_webmail.model.DatabaseAgent;
import cse.maven_webmail.model.Pop3Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class SendedMailHandler extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SendedMailHandler.class);

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        int select = Integer.parseInt(request.getParameter("menu"));

        switch (select) {
            case CommandType.DELETE_MAIL_COMMAND:
                deleteMessage(request);
                response.sendRedirect("sended_message_menu.jsp");
                break;

            case CommandType.DOWNLOAD_COMMAND: // 파일 다운로드 처리
                download(request, response);
                break;

            default:
                try (PrintWriter out = response.getWriter()) {
                    out.println("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
                }
                break;

        }
    }

    private void download(HttpServletRequest request, HttpServletResponse response) { //throws IOException {
        response.setContentType("application/octet-stream");

        ServletOutputStream sos;

        try {
            request.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String userId = request.getParameter("userId");
            String messageId = request.getParameter("messageId");
            String fileName = request.getParameter("fileName");
            logger.info("userId = " + userId);
            logger.info("messageId = " + messageId);
            logger.info("fileName = " + fileName);
            DatabaseAgent databaseAgent = new DatabaseAgent();
            databaseAgent.setMessageId(messageId);
            databaseAgent.setUserId(userId);
            String downloadDir = "C:/temp/download/";
            databaseAgent.download(downloadDir, fileName);
            response.setHeader("Content-Disposition", "attachment; filename="
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + ";");
            File f = new File(downloadDir + "/" + fileName);
            byte[] b = new byte[(int) f.length()];
            try (FileInputStream fis = new FileInputStream(f)) {
                fis.read(b);
            }
            sos = response.getOutputStream();
            sos.write(b);
            sos.flush();
            sos.close();
            f.delete();
        } catch (Exception ex) {
            logger.error("====== DOWNLOAD exception : {}", ex.getMessage());
        }
    }

    private boolean deleteMessage(HttpServletRequest request) {
        boolean success = false;
        String messageId = request.getParameter("messageId");
        String userId = request.getParameter("userId");
        DatabaseAgent databaseAgent = new DatabaseAgent();
        databaseAgent.setUserId(userId);
        databaseAgent.setMessageId(messageId);
        try {
            success = databaseAgent.delete();
        } catch (Exception e) {
            logger.error(new Date() + e.getMessage());
        }
        return success;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
