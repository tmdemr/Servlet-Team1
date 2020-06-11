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



    public MessageFormatter(String userid) {
        this.userid = userid;
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

        for (int i = messages.length-1; i > -1; i--) {
            MessageParser parser = new MessageParser(messages[i], userid);
            parser.parse(false);  // envelope 정보만 필요
            // 메시지 헤더 포맷
            // 추출한 정보를 출력 포맷 사용하여 스트링으로 만들기
            buffer.append("<tr> " + " <td id=\"no\">").append(messages[i].getMessageNumber()).append(" </td> ")
                    .append(" <td id=\"sender\">").append(parser.getFromAddress())
                    .append("</td>").append(" <td id=\"subject\"> ")
                    .append(" <a href=show_message.jsp?msgid=").append(messages[i].getMessageNumber())
                    .append(" title=\"메일 보기\"> ").append(parser.getSubject())
                    .append("</a> </td>").append(" <td id=\"date\">").append(parser.getSentDate())
                    .append("</td>").append(" <td id=delete>").append("<a href=ReadMail.do?menu=")
                    .append(CommandType.DELETE_MAIL_COMMAND).append("&msgid=").append(messages[i].getMessageNumber())
                    .append("> 삭제 </a>").append("</td>").append(" </tr>");
        }
        buffer.append("</table>");

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
