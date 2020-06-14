package cse.maven_webmail.control;

import cse.maven_webmail.model.TrashMailAgent;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 휴지통을 구성하는 클래스입니다.
 * @author 남영우
 */
public class TrashHandler extends HttpServlet {
    private static final String TEMP_DOWNLOAD_DIR = "C:/temp/upload";
    private static final String MESSAGE_NAME = "messageName";

    /**
     * 요청을 처리합니다.
     * @param request 요청
     * @param response 응답
     * @throws IOException PrintWriter로 인해 발생할 수 있습니다.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        int menu = Integer.parseInt(request.getParameter("menu"));
        switch (menu) {
            case CommandType.DELETE_MAIL_COMMAND:
                delete(request, response);
                //delete
                break;
            case CommandType.DOWNLOAD_COMMAND:
                download(request, response);
                //download
                break;
            case CommandType.RESTORE_MAIL_COMMAND:
                restore(request, response);
                //restore
                break;
            default:
                try (PrintWriter out = response.getWriter()) {
                    out.println("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
                }
                break;
        }
    }

    /**
     * 휴지통의 파일 다운로드를 수행합니다.
     * @param request 요청
     * @param response 응답
     * @throws IOException PrintWriter로 인해 발생할 수 있습니다.
     */
    private void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String messageName = request.getParameter(MESSAGE_NAME);
        try (ServletOutputStream sos = response.getOutputStream();
        ) {
            TrashMailAgent trashMailAgent = new TrashMailAgent();
            trashMailAgent.setMessageName(messageName);
            trashMailAgent.setDir(TEMP_DOWNLOAD_DIR);
            trashMailAgent.download();
            String fileName = trashMailAgent.getFileName();
            response.setHeader("Content-Disposition", "attachment; filename="
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + ";");
            try (FileInputStream fileInputStream = new FileInputStream(TEMP_DOWNLOAD_DIR + "/" + fileName)) {
                sos.write(fileInputStream.readAllBytes());
            }
        }
    }

    /**
     * 휴지통의 메일을 완전 삭제하는 메소드입니다.
     * @param request 요청
     * @param response 응답
     * @throws IOException PrintWriter로 인해 발생할 수 있습니다.
     */
    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String messageName = request.getParameter(MESSAGE_NAME);
        TrashMailAgent trashMailAgent = new TrashMailAgent();
        trashMailAgent.setMessageName(messageName);
        if (trashMailAgent.delete()) {
            response.sendRedirect("trash.jsp");
        } else {
            try (PrintWriter out = response.getWriter()) {
                out.println("메일 삭제중 오류가 발생하였습니다.");
            }
        }
    }

    /**
     * 휴지통의 메일을 복원하는 함수입니다.
     * @param request 요청
     * @param response 응답
     * @throws IOException PrintWriter로 인해 발생할 수 있습니다.
     */
    private void restore(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String messageName = request.getParameter(MESSAGE_NAME);
        TrashMailAgent trashMailAgent = new TrashMailAgent();
        trashMailAgent.setMessageName(messageName);
        if (trashMailAgent.restore()) {
            response.sendRedirect("trash.jsp");
        } else {
            try (PrintWriter out = response.getWriter()) {
                out.println("메일 복구중 오류가 발생하였습니다.");
            }
        }
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
