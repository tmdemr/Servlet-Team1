package cse.maven_webmail.control;

import cse.maven_webmail.model.SendMailDatabaseAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;

/**
 * 보낸메일함의 제어클래스입니다.
 * @see SendMailDatabaseAgent
 * @author 남영우
 */
public class SendedMailHandler extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SendedMailHandler.class);

    /**
     * 요청을 처리합니다.
     * @param request 요청
     * @param response 응답
     * @throws IOException PrintWriter로 인해 오류가 발생할 수 있습니다.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
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

    /**
     * 보낸 메일함의 파일 다운로드를 수행합니다.
     * @param request 요청
     * @param response 응답
     */
    private void download(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        ServletOutputStream sos;
        try {
            request.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String userId = request.getParameter("userId");
            String messageId = request.getParameter("messageId");
            messageId = messageId.replace("&lt;","<").replace("&gt;",">");
            String fileName = request.getParameter("fileName");
            logger.info("userId = " + userId);
            logger.info("messageId = " + messageId);
            logger.info("fileName = " + fileName);
            SendMailDatabaseAgent sendMailDatabaseAgent = new SendMailDatabaseAgent();
            sendMailDatabaseAgent.setMessageId(messageId);
            sendMailDatabaseAgent.setUserId(userId);
            String downloadDir = "C:/temp/download/";
            sendMailDatabaseAgent.download(downloadDir, fileName);
            response.setHeader("Content-Disposition", "attachment; filename="
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + ";");
            File f = new File(downloadDir + File.separator + fileName);
            byte[] b;
            try (FileInputStream fis = new FileInputStream(f)) {
                b = fis.readAllBytes();
            }
            sos = response.getOutputStream();
            sos.write(b);
            sos.flush();
            sos.close();
            Files.delete(f.toPath());
        } catch (Exception ex) {
            logger.error("====== DOWNLOAD exception : {}", ex.getMessage());
        }
    }

    /**
     * 보낸 메일함의 메일을 삭제합니다.
     * @param request 요청
     * @return 삭제 성공여부
     */
    private boolean deleteMessage(HttpServletRequest request) {
        boolean success = false;
        String messageId = request.getParameter("messageId");
        messageId = messageId.replace("&lt;","<").replace("&gt;",">");
        String userId = request.getParameter("userId");
        SendMailDatabaseAgent sendMailDatabaseAgent = new SendMailDatabaseAgent();
        sendMailDatabaseAgent.setUserId(userId);
        sendMailDatabaseAgent.setMessageId(messageId);
        try {
            success = sendMailDatabaseAgent.delete();
        } catch (Exception e) {
            logger.error(new Date() + e.getMessage());
        }
        return success;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        processRequest(req, resp);
    }
}
