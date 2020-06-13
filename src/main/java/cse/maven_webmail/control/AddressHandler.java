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


public class AddressHandler extends HttpServlet {


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
            default:
                try (PrintWriter out = response.getWriter()) {
                    out.println("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
                }
                break;
        }

    }

    private void deleteAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String user = request.getParameter("user");
        AddressBookAgent addressBookAgent = new AddressBookAgent();
        try (PrintWriter out = response.getWriter()) {
            if (addressBookAgent.deleteAll()) {
                response.sendRedirect("address_show.jsp");
            } else {
                out.println("주소록 삭제 중에 오류가 발생했습니다.");
            }
        }
    }

    private void update(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String user = request.getParameter("user");
        String email = request.getParameter("email");
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
                response.sendRedirect("address_show.jsp");
            } else {
                out.println("주소록 삭제 중에 오류가 발생했습니다.");
            }
        }
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String user = request.getParameter("user");
        String email = request.getParameter("email");
        AddressBookAgent addressBookAgent = new AddressBookAgent();
        addressBookAgent.setEmail(email);
        addressBookAgent.setUserId(user);
        try (PrintWriter out = response.getWriter()) {
            if (addressBookAgent.delete()) {
                response.sendRedirect("address_show.jsp");
            } else {
                out.println("주소록 삭제 중에 오류가 발생했습니다.");
            }
        }
    }

    private void insert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String user = request.getParameter("user");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        AddressBookAgent addressBookAgent = new AddressBookAgent();
        addressBookAgent.setUserId(user);
        addressBookAgent.setNickName(name);
        addressBookAgent.setEmail(email);
        addressBookAgent.setPhoneNumber(phoneNumber);
        try (PrintWriter out = response.getWriter()) {
            if (addressBookAgent.insert()) {
                response.sendRedirect("address_show.jsp");
            } else {
                out.println("주소록 추가 중에 오류가 발생했습니다.");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
