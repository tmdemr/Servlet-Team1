/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import cse.maven_webmail.model.Pop3Agent;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author jongmin
 */
public class LoginHandler extends HttpServlet {

    private static final String ADMINISTRATOR = "admin";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        HttpSession session = request.getSession();
        response.setContentType("text/html;charset=UTF-8");

        int selectedMenu = Integer.parseInt(request.getParameter("menu"));


        switch (selectedMenu) {
            case CommandType.LOGIN:
                String host = (String) request.getSession().getAttribute("host");
                String userid = request.getParameter("userid");
                String password = request.getParameter("passwd");

                // Check the login information is valid using <<model>>Pop3Agent.
                Pop3Agent pop3Agent = new Pop3Agent(host, userid, password);
                boolean isLoginSuccess = pop3Agent.validate();

                // Now call the correct page according to its validation result.
                if (isLoginSuccess) {
                    session.setAttribute("userid", userid);
                    if (isAdmin(userid)) {
                        // HttpSession 객체에 userid를 등록해 둔다.
                        response.sendRedirect("admin_menu.jsp");
                    } else {
                        // HttpSession 객체에 userid와 password를 등록해 둔다.
                        session.setAttribute("password", password);
                        response.sendRedirect("main_menu.jsp");
                    }
                } else {
                    RequestDispatcher view = request.getRequestDispatcher("login_fail.jsp");
                    view.forward(request, response);
                }
                break;
            case CommandType.LOGOUT:
                session.invalidate();
                response.sendRedirect(getServletContext().getInitParameter("HomeDirectory"));
                break;
            default:
                break;
        }


    }

    /**
     * 사용자 아이디가 운영자 계정인지 반환합니다.
     * @param userid 사용자 아이디
     * @return 운영자 여부
     */
    protected boolean isAdmin(String userid) {
        boolean status = false;

        if (userid.equals(ADMINISTRATOR)) {
            status = true;
        }

        return status;
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
            throws ServletException,
            IOException {
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
            throws ServletException,
            IOException {
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
