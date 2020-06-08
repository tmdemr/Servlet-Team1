package cse.maven_webmail.control;

import cse.maven_webmail.model.TrashMailAgent;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class TrashHandler extends HttpServlet {
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
                try(PrintWriter out = response.getWriter()){
                    out.println("아직 휴지통 다운로드 구현안했어요.");
                }
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
    private void download(HttpServletRequest request, HttpServletResponse response){

    }
    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String messageName = request.getParameter("messageName");
        TrashMailAgent trashMailAgent = new TrashMailAgent();
        trashMailAgent.setMessageName(messageName);
        if (trashMailAgent.delete()) {
            response.sendRedirect("trash.jsp");
        } else {
            //실패
        }
    }

    private void restore(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String messageName = request.getParameter("messageName");
        TrashMailAgent trashMailAgent = new TrashMailAgent();
        trashMailAgent.setMessageName(messageName);
        if (trashMailAgent.restore()) {
            response.sendRedirect("trash.jsp");
        } else {
            //실패
        }
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
