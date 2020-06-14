package cse.maven_webmail.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cse.maven_webmail.model.Pop3Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jongmin
 */
public class ReadMailHandler extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ReadMailHandler.class);

    /**
     * 요청을 처리합니다.
     *
     * @param request  요청
     * @param response 응답
     * @throws IOException PrintWriter로 인해 발생할 수 있습니다.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        int select = Integer.parseInt(request.getParameter("menu"));

        switch (select) {
            case CommandType.DELETE_MAIL_COMMAND:
                deleteMessage(request);
                response.sendRedirect("main_menu.jsp");
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
     * 메일의 파일 다운로드를 처리합니다.
     *
     * @param request  요청
     * @param response 응답
     */
    private void download(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        try (ServletOutputStream sos = response.getOutputStream()) {
            request.setCharacterEncoding(StandardCharsets.UTF_8.name());
            // LJM 041203 - 아래와 같이 해서 한글파일명 제대로 인식되는 것 확인했음.
            String fileName = request.getParameter("filename");
            logger.info(">>>>>> DOWNLOAD: file name = {}", fileName);
            // fileName에 있는 ' '는 '+'가 파라미터로 전송되는 과정에서 변한 것이므로
            // 다시 변환시켜줌.
            String userid = request.getParameter("userid");
            // download할 파일 읽기
            // LJM 090430 : 수정해야 할 부분 - start ------------------
            // 리눅스 서버 사용시

            // 윈도우즈 환경 사용시
            String downloadDir = "C:/temp/download/";
            // LJM 090430 : 수정해야 할 부분 - end   ------------------
            response.setHeader("Content-Disposition", "attachment; filename="
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + ";");

            File f = new File(downloadDir + File.separator + userid + File.separator + fileName);
            byte[] b;
            // try-with-resource 문은 fis를 명시적으로 close해 주지 않아도 됨.
            try (FileInputStream fis = new FileInputStream(f)) {
                b = fis.readAllBytes();
            }
            // 다운로드
            sos.write(b);
            sos.flush();
            Files.delete(f.toPath());
        } catch (Exception ex) {
            logger.error("====== DOWNLOAD exception : {}", ex.getMessage());
        }
    }

    /**
     * 메일을 삭제합니다.
     *
     * @param request 요청
     * @return 삭제 성공여부
     */
    private boolean deleteMessage(HttpServletRequest request) {
        int msgid = Integer.parseInt(request.getParameter("msgid"));

        HttpSession httpSession = request.getSession();
        String host = (String) httpSession.getAttribute("host");
        String userid = (String) httpSession.getAttribute("userid");
        String password = (String) httpSession.getAttribute("password");

        Pop3Agent pop3 = new Pop3Agent(host, userid, password);
        return pop3.deleteMessage(msgid, true);
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
