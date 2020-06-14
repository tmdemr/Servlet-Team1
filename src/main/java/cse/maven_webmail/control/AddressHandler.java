/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import cse.maven_webmail.model.AddressBookAgent;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 주소록 핸들러로서 주소록 관련 제어를 수행합니다.
 * 실제 삽입과 수정과 삭제는 AddressBookAgent에서 일어납니다.
 * @see AddressBookAgent
 * @author 박지율
 */
public class AddressHandler extends HttpServlet {
    private static final String SEND_REDIRECT_URL = "address_show.jsp";
    private static final String EMAIL = "email";

    /**
     * 요청을 처리합니다.
     * @param request 요청
     * @param response 응답
     * @throws IOException PrintWriter로 인해 발생할 수 있습니다.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        int menu = Integer.parseInt(request.getParameter("menu"));

        switch (menu) {
            case CommandType.ADD_ADDRESS:
                insert(request, response);
                break;
            case CommandType.DELETE_ADDRESS:
                delete(request, response);
                break;
            case CommandType.CHANGE_ADDRESS:
                update(request, response);
                break;
            case CommandType.DELETE_ALL_ADDRESS:
                deleteAll(request, response);
                break;
            default:
                try (PrintWriter out = response.getWriter()) {
                    out.println("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
                }
                break;
        }

    }

    /**
     * 모든 주소록을 삭제하는 메소드입니다.
     * @param request 요청
     * @param response 응답
     * @throws IOException PrintWriter로 인해 IOException이 발생할 수 있습니다.
     */
    private void deleteAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String user = request.getParameter("user");
        AddressBookAgent addressBookAgent = new AddressBookAgent();
        addressBookAgent.setUserId(user);
        try (PrintWriter out = response.getWriter()) {
            if (addressBookAgent.deleteAll()) {
                response.sendRedirect(SEND_REDIRECT_URL);
            } else {
                out.println("주소록 삭제 중에 오류가 발생했습니다.");
            }
        }
    }

    /**
     * 주소록을 변경하는 메소드입니다.
     * @param request 요청
     * @param response 응답
     * @throws IOException PrintWriter로 인해 IOException이 발생할 수 있습니다.
     */
    private void update(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String user = request.getParameter("user");
        String email = request.getParameter(EMAIL);
        String name = request.getParameter("name");
        String phoneNumber = request.getParameter("phoneNumber");
        String newEmail = request.getParameter("newEmail");
        AddressBookAgent addressBookAgent = new AddressBookAgent();
        addressBookAgent.setUserId(user);
        addressBookAgent.setEmail(email);
        addressBookAgent.setNickName(name);
        addressBookAgent.setPhoneNumber(phoneNumber);
        addressBookAgent.setNewEmail(newEmail);
        try (PrintWriter out = response.getWriter()) {
            if (addressBookAgent.update()) {
                response.sendRedirect(SEND_REDIRECT_URL);
            } else {
                out.println("주소록 수정 중에 오류가 발생했습니다.");
            }
        }
    }

    /**
     * 주소록을 하나 삭제하는 메소드입니다.
     * @param request 요청
     * @param response 응답
     * @throws IOException PrintWriter로 인해 IOException이 발생할 수 있습니다.
     */
    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String user = request.getParameter("user");
        String email = request.getParameter(EMAIL);
        AddressBookAgent addressBookAgent = new AddressBookAgent();
        addressBookAgent.setEmail(email);
        addressBookAgent.setUserId(user);
        try (PrintWriter out = response.getWriter()) {
            if (addressBookAgent.delete()) {
                response.sendRedirect(SEND_REDIRECT_URL);
            } else {
                out.println("주소록 삭제 중에 오류가 발생했습니다.");
            }
        }
    }

    /**
     * 주소록을 삽입하는 메소드입니다.
     * @param request 요청
     * @param response 응답
     * @throws IOException PrintWriter로 인해 IOException이 발생할 수 있습니다.
     */
    private void insert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String user = request.getParameter("user");
        String name = request.getParameter("name");
        String email = request.getParameter(EMAIL);
        String phoneNumber = request.getParameter("phoneNumber");
        AddressBookAgent addressBookAgent = new AddressBookAgent();
        addressBookAgent.setUserId(user);
        addressBookAgent.setNickName(name);
        addressBookAgent.setEmail(email);
        addressBookAgent.setPhoneNumber(phoneNumber);
        try (PrintWriter out = response.getWriter()) {
            if (addressBookAgent.insert()) {
                response.sendRedirect(SEND_REDIRECT_URL);
            } else {
                out.println("주소록 추가 중에 오류가 발생했습니다.");
            }
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        processRequest(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }


    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
