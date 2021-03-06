/*
 * File: Pop3Agent.java
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Properties;

/**
 * @author jongmin
 */
public class Pop3Agent {
    private static final Logger logger = LoggerFactory.getLogger(Pop3Agent.class);
    private static final String INBOX = "INBOX";
    private String host;
    private String userid;
    private String password;
    private Store store;
    private int pageNo;
    private static final int MAX_PAGE_MESSAGE = 10;
    private static final int MAX_PAGE = 5;
    private String searchType;
    private String searchKeyword;

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public Pop3Agent() {
        // 빈생성자
    }

    public Pop3Agent(String host, String userid, String password) {
        this.host = host;
        this.userid = userid;
        this.password = password;
    }

    /**
     * 사용자를 인증하는 메소드입니다.
     * @return 사용자 존재 여부
     */
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

    /**
     * 메세지를 삭제하는 메소드입니다.
     * @param msgid 메세지 번호
     * @param reallyDelete 진짜 삭제할 건지
     * @return 삭제 성공 여부
     */
    public boolean deleteMessage(int msgid, boolean reallyDelete) {
        boolean status = false;
        logger.info("deleteMessage 값 : {}", msgid);

        if (!connectToStore()) {
            return false;
        }

        try {
            // Folder 설정
            Folder folder = store.getFolder(INBOX);
            folder.open(Folder.READ_WRITE);
            // Message에 DELETED flag 설정
            Message msg = folder.getMessage(msgid);

            logger.info("message number : {}", msg.getMessageNumber());
            logger.info("{}", msg.getSubject());
            msg.setFlag(Flags.Flag.DELETED, reallyDelete);

            // 폴더에서 메시지 삭제
            // <-- 현재 지원 안 되고 있음. 폴더를 close()할 때 expunge해야 함.
            folder.close(true);  // expunge == true
            store.close();
            status = true;
        } catch (Exception ex) {
            logger.error("deleteMessage() error: {}", ex.getMessage());
        }
        return status;

    }

    /**
     * 메세지를 페이징하여 표현하는 메소드입니다.
     * @return 페이징된 String
     */
    public String getMessageList() {
        String result = "";
        Message[] messages;

        if (!connectToStore()) {  // 3.1
            logger.error("POP3 connection failed!");
            return "POP3 연결이 되지 않아 메일 목록을 볼 수 없습니다.";
        }
        logger.info("타입 : {}", searchType);
        logger.info("키워드 : {}", searchKeyword);
        int counts = 0;
        int start;
        int end;
        try {
            // 메일 폴더 열기
            Folder folder = store.getFolder(INBOX);  // 3.2
            folder.open(Folder.READ_ONLY);  // 3.3
            if (searchKeyword != null && !searchKeyword.equals("")) {
                if (searchType.equals("subject")) {
                    counts = (int) Arrays.stream(folder.getMessages()).parallel().filter(message -> {
                        String subject;
                        try {
                            subject = message.getSubject();
                        } catch (MessagingException e) {
                            return false;
                        }
                        return subject.contains(searchKeyword);
                    }).count();
                    messages = Arrays.stream(folder.getMessages()).filter(message -> {
                        String subject;
                        try {
                            subject = message.getSubject();
                        } catch (MessagingException e) {
                            return false;
                        }
                        return subject.contains(searchKeyword);
                    }).sorted(Comparator.comparing(Message::getMessageNumber).reversed()).skip((pageNo - 1) * MAX_PAGE_MESSAGE).limit(10)
                            .toArray(Message[]::new);
                    for (int i = 0; i < messages.length / 2; i++) {
                        Message message = messages[i];
                        messages[i] = messages[messages.length - i - 1];
                        messages[messages.length - i - 1] = message;
                    }
                } else if (searchType.equals("from")) {
                    counts = (int) Arrays.stream(folder.getMessages()).parallel().filter(message -> {
                        String from;
                        try {
                            from = String.join(", ", Arrays.stream(message.getFrom()).map(Address::toString).toArray(String[]::new));
                        } catch (MessagingException e) {
                            return false;
                        }
                        return from.contains(searchKeyword);
                    }).count();
                    messages = Arrays.stream(folder.getMessages()).filter(message -> {
                        String from;
                        try {
                            from = String.join(", ", Arrays.stream(message.getFrom()).map(Address::toString).toArray(String[]::new));
                        } catch (MessagingException e) {
                            return false;
                        }
                        return from.contains(searchKeyword);
                    }).sorted(Comparator.comparing(Message::getMessageNumber).reversed()).skip((pageNo - 1) * MAX_PAGE_MESSAGE).limit(10)
                            .toArray(Message[]::new);
                    for (int i = 0; i < messages.length / 2; i++) {
                        Message message = messages[i];
                        messages[i] = messages[messages.length - i - 1];
                        messages[messages.length - i - 1] = message;
                    }
                } else {
                    counts = folder.getMessageCount();
                    start = counts + 1 - (pageNo) * MAX_PAGE_MESSAGE;
                    end = start - 1 + MAX_PAGE_MESSAGE;
                    end = Math.min(counts, end);
                    logger.info("start : {} end : {}", start, end);
                    start = Math.max(start, 1);
                    messages = folder.getMessages(start, end);      // 3.4
                }
            } else {
                counts = folder.getMessageCount();
                start = counts + 1 - (pageNo) * MAX_PAGE_MESSAGE;
                end = start - 1 + MAX_PAGE_MESSAGE;
                end = Math.min(counts, end);
                logger.info("start : {} end : {}", start, end);
                start = Math.max(start, 1);
                messages = folder.getMessages(start, end);      // 3.4
            }
            FetchProfile fp = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, fp);


            MessageFormatter formatter = new MessageFormatter(userid);  //3.5

            result = formatter.getMessageTable(messages);   // 3.6

            folder.close(true);  // 3.7
            store.close();       // 3.8
        } catch (Exception ex) {
            logger.error("Pop3Agent.getMessageList() : exception = {}", ex.getMessage());
            result = "Pop3Agent.getMessageList() : exception = " + ex;
        }
        StringBuilder stringBuilder = new StringBuilder(result);
        logger.info("메일  : {}개", counts);
        if (counts < MAX_PAGE_MESSAGE) {

        } else {
            int totalPage = counts / MAX_PAGE_MESSAGE + (counts % MAX_PAGE_MESSAGE == 0 ? 0 : 1);
            if (pageNo == 1) {
                stringBuilder.append("첫페이지로");
                stringBuilder.append("&nbsp;");
                stringBuilder.append("&lt;");
            } else {
                stringBuilder.append("<a href=\"main_menu.jsp?pageNo=1").append("&searchType=").append(searchType)
                        .append("&keyword=").append(URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8)).append("\">첫페이지로</a>");
                stringBuilder.append("&nbsp;");
                stringBuilder.append("<a href=\"main_menu.jsp?pageNo=").append(pageNo - 1).append("&searchType=").append(searchType)
                        .append("&keyword=").append(URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8)).append("\">&lt;</a>");
            }
            stringBuilder.append("&nbsp;");

            int startPage = ((pageNo / MAX_PAGE) - (pageNo % MAX_PAGE == 0 ? 1 : 0)) * MAX_PAGE + 1;
            int endPage = startPage + MAX_PAGE - 1;


            endPage = Math.min(endPage, totalPage);

            for (int i = startPage; i <= endPage; i++) {
                if (i == pageNo) {
                    stringBuilder.append(i);
                } else {
                    stringBuilder.append("<a href=\"main_menu.jsp?pageNo=").append(i).append("&searchType=").append(searchType)
                            .append("&keyword=").append(URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8)).append("\">").append(i).append("</a>");
                }
                stringBuilder.append("&nbsp;");
            }
            if (pageNo == totalPage) {
                stringBuilder.append("&gt;");
                stringBuilder.append("&nbsp;");
                stringBuilder.append("마지막페이지로");

            } else {
                stringBuilder.append("<a href=\"main_menu.jsp?pageNo=").append(pageNo + 1).append("&searchType=").append(searchType)
                        .append("&keyword=").append(URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8)).append("\">&gt;</a>");
                stringBuilder.append("&nbsp;");
                stringBuilder.append("<a href=\"main_menu.jsp?pageNo=").append(totalPage).append("&searchType=").append(searchType)
                        .append("&keyword=").append(URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8)).append("\">마지막페이지로</a>");
            }
        }
        return stringBuilder.toString();
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * 해당 메세지 번호에 해당하는 메세지를 가져옵니다.
     * @param n 메세지 번호
     * @return 포매팅된 메세지 String
     */
    public String getMessage(int n) {
        String result = "POP3  서버 연결이 되지 않아 메시지를 볼 수 없습니다.";

        if (!connectToStore()) {
            logger.error("POP3 connection failed!");
            return result;
        }

        try {
            Folder folder = store.getFolder(INBOX);
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

    /**
     * POP3Store에 연결합니다.
     * @return 연결 성공 여부
     */
    private boolean connectToStore() {
        boolean status = false;
        Properties props = System.getProperties();
        props.setProperty("mail.pop3.host", host);
        props.setProperty("mail.pop3.user", userid);
        props.setProperty("mail.pop3.apop.enable", "false");
        props.setProperty("mail.pop3.disablecapa", "true");  // 200102 LJM - added cf. https://javaee.github.io/javamail/docs/api/com/sun/mail/pop3/package-summary.html
        props.setProperty("mail.debug", "true");

        Session session = Session.getInstance(props);
        session.setDebug(true);

        try {
            store = session.getStore("pop3");
            store.connect(host, userid, password);
            status = true;
        } catch (Exception ex) {
            logger.error(new Date() + " : " + ex.getMessage());
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

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
}  // class Pop3Agent

