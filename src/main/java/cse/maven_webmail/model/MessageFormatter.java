/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import cse.maven_webmail.control.CommandType;

import javax.mail.Message;

/**
 * @author jongmin
 */
public class MessageFormatter {

    private String userid;  // 파일 임시 저장 디렉토리 생성에 필요
    private int pageNo;
    private static final int MAX_PAGE_MESSAGE = 10;
    private static final int MAX_PAGE = 5;

    public MessageFormatter(String userid) {
        this.userid = userid;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getMessageTable(Message[] messages) {
        StringBuilder buffer = new StringBuilder();

        // 메시지 제목 보여주기
        buffer.append("<table>");  // table start
        buffer.append("<tr> "
                + " <th> No. </th> "
                + " <th> 보낸 사람 </th>"
                + " <th> 제목 </th>     "
                + " <th> 보낸 날짜 </th>   "
                + " <th> 삭제 </th>   "
                + " </tr>");

        for (int i = messages.length - 1 - (pageNo - 1) * MAX_PAGE_MESSAGE; i > messages.length - 1 - pageNo * MAX_PAGE_MESSAGE; i--) {
            if (i < 0) {
                break;
            }
            MessageParser parser = new MessageParser(messages[i], userid);
            parser.parse(false);  // envelope 정보만 필요
            // 메시지 헤더 포맷
            // 추출한 정보를 출력 포맷 사용하여 스트링으로 만들기
            buffer.append("<tr> " + " <td id=\"no\">").append(i + 1).append(" </td> ")
                    .append(" <td id=\"sender\">").append(parser.getFromAddress())
                    .append("</td>").append(" <td id=\"subject\"> ")
                    .append(" <a href=show_message.jsp?msgid=").append(i + 1)
                    .append(" title=\"메일 보기\"> ").append(parser.getSubject())
                    .append("</a> </td>").append(" <td id=\"date\">").append(parser.getSentDate())
                    .append("</td>").append(" <td id=delete>").append("<a href=ReadMail.do?menu=")
                    .append(CommandType.DELETE_MAIL_COMMAND).append("&msgid=").append(i + 1)
                    .append("> 삭제 </a>").append("</td>").append(" </tr>");
        }
        buffer.append("</table>");
        if (messages.length < MAX_PAGE_MESSAGE) {
            return buffer.toString();
        } else {
            int totalPage = (int) Math.ceil(messages.length / MAX_PAGE_MESSAGE) + 1;
            if (pageNo == 1) {
                buffer.append("첫페이지로");
                buffer.append("&nbsp;");
                buffer.append("&lt;");
            } else {
                buffer.append("<a href=\"main_menu.jsp?pageNo=1\">첫페이지로</a>");
                buffer.append("&nbsp;");
                buffer.append("<a href=\"main_menu.jsp?pageNo=").append(pageNo - 1).append("\">&lt;</a>");
            }
            buffer.append("&nbsp;");
            int endPage = (int) (Math.ceil(pageNo / (double) MAX_PAGE) * MAX_PAGE);
            int startPage = (endPage - MAX_PAGE) + 1;
            endPage = Math.min(endPage, totalPage);
            for (int i = startPage; i <= endPage; i++) {
                if (i == pageNo) {
                    buffer.append(i);
                } else {
                    buffer.append("<a href=\"main_menu.jsp?pageNo=").append(i).append("\">").append(i).append("</a>");
                }
                buffer.append("&nbsp;");
            }
            if (pageNo == totalPage) {
                buffer.append("&gt;");
                buffer.append("&nbsp;");
                buffer.append("마지막페이지로");
            } else {
                buffer.append("<a href=\"main_menu.jsp?pageNo=").append(pageNo + 1).append("\">&gt;</a>");
                buffer.append("&nbsp;");
                buffer.append("<a href=\"main_menu.jsp?pageNo=").append(totalPage).append("\">마지막페이지로</a>");
            }
        }
        return buffer.toString();
    }

    public String getMessage(Message message) {
        StringBuilder buffer = new StringBuilder();
        MessageParser parser = new MessageParser(message, userid);
        parser.parse(true);
        buffer.append("보낸 사람: ").append(parser.getFromAddress()).append(" <br>");
        buffer.append("받은 사람: ").append(parser.getToAddress()).append(" <br>");
        buffer.append("Cc &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; : ").append(parser.getCcAddress()).append(" <br>");
        buffer.append("보낸 날짜: ").append(parser.getSentDate()).append(" <br>");
        buffer.append("제 &nbsp;&nbsp;&nbsp;  목: ").append(parser.getSubject()).append(" <br> <hr>");
        buffer.append(parser.getBody());
        String attachedFile = parser.getFileName();
        if (attachedFile != null) {
            buffer.append("<br> <hr> 첨부파일: <a href=ReadMail.do?menu=" + CommandType.DOWNLOAD_COMMAND + "&userid=")
                    .append(this.userid).append("&filename=").append(attachedFile.replaceAll(" ", "%20"))
                    .append(" target=_top> ").append(attachedFile).append("</a> <br>");
        }
        buffer.append("<form action=\"write_mail.jsp\" method=\"POST\">   "
                + "<input type=\"hidden\" name=\"fromAddress\" value=\"" + parser.getFromAddress() + "\"/>"
                + "<input type=\"hidden\" name=\"toAddress\" value=\"" + parser.getToAddress() + "\"/>"
                + "<input type=\"hidden\" name=\"subject\" value=\"" + parser.getSubject() + "\"/>"
                + "<input type=\"hidden\" name=\"body\" value=\"" + parser.getBody() + "\"/>"
                + "<button type=\"submit\">답장하기</button>"
                + "  </form>");
        return buffer.toString();
    }
}
