/*
 * File: Pop3Agent.java
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import java.util.Properties;

/**
 * @author jongmin
 */
public class Pop3Agent {
    private static final Logger logger = LoggerFactory.getLogger(Pop3Agent.class);
    private String host;
    private String userid;
    private String password;
    private Session session;
    private Store store;
    private int pageNo;
    private String exceptionType;
    private static final int MAX_PAGE_MESSAGE = 10;
    private static final int MAX_PAGE = 5;

    public Pop3Agent() {
    }

    public Pop3Agent(String host, String userid, String password) {
        this.host = host;
        this.userid = userid;
        this.password = password;
    }

    public boolean validate() {
        boolean status;

        try {
            status = connectToStore();
            store.close();
        } catch (Exception ex) {
            logger.error("Pop3Agent.validate() error : {}", ex.getMessage());
            status = false;  // for clarity
        }
        return status;

    }

    public boolean deleteMessage(int msgid, boolean really_delete) {
        boolean status = false;
        logger.info("deleteMessage 값 : {}", msgid);

        if (!connectToStore()) {
            return false;
        }

        try {
            // Folder 설정
//            Folder folder = store.getDefaultFolder();
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            // Message에 DELETED flag 설정
            Message msg = folder.getMessage(msgid);

            logger.info("message id : {}", msg.getHeader("Message-ID"));
            logger.info("message number : {}", msg.getMessageNumber());
            logger.info("{}", msg.getSubject());
            msg.setFlag(Flags.Flag.DELETED, really_delete);

            // 폴더에서 메시지 삭제
            // Message [] expungedMessage = folder.expunge();
            // <-- 현재 지원 안 되고 있음. 폴더를 close()할 때 expunge해야 함.
            folder.close(true);  // expunge == true
            store.close();
            status = true;
        } catch (Exception ex) {
            logger.error("deleteMessage() error: {}", ex.getMessage());
        }
        return status;

    }

    /*
     * 페이지 단위로 메일 목록을 보여주어야 함.
     */
    public String getMessageList() {
        String result = "";
        Message[] messages;

        if (!connectToStore()) {  // 3.1
            System.err.println("POP3 connection failed!");
            return "POP3 연결이 되지 않아 메일 목록을 볼 수 없습니다.";
        }
        int counts = 0;
        try {
            // 메일 폴더 열기
            Folder folder = store.getFolder("INBOX");  // 3.2
            folder.open(Folder.READ_ONLY);  // 3.3

            // 현재 수신한 메시지 모두 가져오기
            counts = folder.getMessageCount();
            int start =counts - (pageNo) * MAX_PAGE_MESSAGE;

            int end = start + MAX_PAGE_MESSAGE;
            
            end = Math.min(counts, end);
            
            logger.info("start : {} end : {}", start, end);
            
            start = Math.max(start,1);

            messages = folder.getMessages(start, end);      // 3.4
            FetchProfile fp = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, fp);
            
            /*
            System.out.println(start);//시작
            System.out.println(end);//끝
            System.out.println(counts);//메시지 수
            System.out.println(pageNo);//현재페이지
*/
            
            MessageFormatter formatter = new MessageFormatter(userid);  //3.5

            result = formatter.getMessageTable(messages);   // 3.6

            folder.close(true);  // 3.7
            store.close();       // 3.8
        } catch (Exception ex) {
            logger.error("Pop3Agent.getMessageList() : exception = {}", ex.getMessage());
            result = "Pop3Agent.getMessageList() : exception = " + ex;
        }
        StringBuilder stringBuilder = new StringBuilder(result);
        if (counts < MAX_PAGE_MESSAGE) {

        } else {
            int totalPage = counts / MAX_PAGE_MESSAGE + (counts%MAX_PAGE_MESSAGE == 0 ? 0 : 1);
            if (pageNo == 1) {
                stringBuilder.append("첫페이지로");
                stringBuilder.append("&nbsp;");
                stringBuilder.append("&lt;");
            } else {
                stringBuilder.append("<a href=\"main_menu.jsp?pageNo=1\">첫페이지로</a>");
                stringBuilder.append("&nbsp;");
                stringBuilder.append("<a href=\"main_menu.jsp?pageNo=").append(pageNo - 1).append("\">&lt;</a>");
            }
            stringBuilder.append("&nbsp;");
            
            int startPage = ((pageNo/MAX_PAGE)-(pageNo%MAX_PAGE==0 ? 1 : 0)) * MAX_PAGE + 1;
            int endPage = startPage + MAX_PAGE -1;
            
            
            endPage = Math.min(endPage, totalPage);
            
            for (int i = startPage; i <= endPage; i++) {
                if (i == pageNo) {
                    stringBuilder.append(i);
                } else {
                    stringBuilder.append("<a href=\"main_menu.jsp?pageNo=").append(i).append("\">").append(i).append("</a>");
                }
                stringBuilder.append("&nbsp;");
            }
            if (pageNo == totalPage) {
                stringBuilder.append("&gt;");
                stringBuilder.append("&nbsp;");
                stringBuilder.append("마지막페이지로");
                
            } else {
                stringBuilder.append("<a href=\"main_menu.jsp?pageNo=").append(pageNo + 1).append("\">&gt;</a>");
                stringBuilder.append("&nbsp;");
                stringBuilder.append("<a href=\"main_menu.jsp?pageNo=").append(totalPage).append("\">마지막페이지로</a>");
            }
        }
        return stringBuilder.toString();
        
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getMessage(int n) {
        String result = "POP3  서버 연결이 되지 않아 메시지를 볼 수 없습니다.";

        if (!connectToStore()) {
            System.err.println("POP3 connection failed!");
            return result;
        }

        try {
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            Message message = folder.getMessage(n);

            MessageFormatter formatter = new MessageFormatter(userid);
            result = formatter.getMessage(message);

            folder.close(true);
            store.close();
        } catch (Exception ex) {
            logger.error("Pop3Agent.getMessageList() : exception = {}", ex.getMessage());
            result = "Pop3Agent.getMessage() : exception = " + ex;
        }
        return result;

    }

    private boolean connectToStore() {
        boolean status = false;
        Properties props = System.getProperties();
        props.setProperty("mail.pop3.host", host);
        props.setProperty("mail.pop3.user", userid);
        props.setProperty("mail.pop3.apop.enable", "false");
        props.setProperty("mail.pop3.disablecapa", "true");  // 200102 LJM - added cf. https://javaee.github.io/javamail/docs/api/com/sun/mail/pop3/package-summary.html
        props.setProperty("mail.debug", "true");

        session = Session.getInstance(props);
        session.setDebug(true);

        try {
            store = session.getStore("pop3");
            store.connect(host, userid, password);
            status = true;
        } catch (Exception ex) {
            exceptionType = ex.toString();
        }
        return status;

    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}  // class Pop3Agent

