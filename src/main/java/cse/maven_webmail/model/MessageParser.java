package cse.maven_webmail.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.MimeUtility;

/**
 * @author jongmin
 */
public class MessageParser {
    private static final Logger logger = LoggerFactory.getLogger(MessageParser.class);
    private Message message;
    private String toAddress;
    private String fromAddress;
    private String ccAddress;
    private String sentDate;
    private String subject;
    private String body;
    private String fileName;
    private static final String DOWNLOAD_TEMP_DIR = "C:/temp/download/";
    private String userid;

    /**
     * 메세지를 파싱하는 클래스의 생성자입니다.
     * @param message 메세지
     * @param userid 사용자 아이디
     */
    public MessageParser(Message message, String userid) {
        this.message = message;
        this.userid = userid;
    }

    /**
     * 파싱을 하는 메소드입니다.
     * @param parseBody body를 파싱할 지 여부입니다. 목록화인지 메일 보기인지...
     * @return 파싱 성공 여부입니다.
     */
    public boolean parse(boolean parseBody) {
        boolean status = false;

        try {
            getEnvelope();
            if (parseBody) {
                getPart(message);
            }
            printMessage(parseBody);
            //  예외가 발생하지 않았으므로 정상적으로 동작하였음.
            status = true;
        } catch (Exception ex) {
            logger.error("MessageParser.parse() - Exception : {}", ex.getMessage());
        }
        return status;

    }

    /**
     * 메세지에서 각각 요소를 뽑아내는 메소드입니다.
     * @throws MessagingException
     */
    private void getEnvelope() throws MessagingException {
        fromAddress = message.getFrom()[0].toString();  // 101122 LJM : replaces getMyFrom2()
        toAddress = getAddresses(message.getRecipients(Message.RecipientType.TO));
        Address[] addr = message.getRecipients(Message.RecipientType.CC);
        if (addr != null) {
            ccAddress = getAddresses(addr);
        } else {
            ccAddress = "";
        }
        subject = message.getSubject();
        sentDate = message.getSentDate().toString();
        sentDate = sentDate.substring(0, sentDate.length() - 8);  // 8 for "KST 20XX"
    }

    // ref: http://www.oracle.com/technetwork/java/faq-135477.html#readattach

    /**
     * 메세지에서 파트를 처리하는 메소드입니다.
     * @param p 파트
     * @throws Exception
     */
    private void getPart(Part p) throws Exception {
        String disp = p.getDisposition();

        if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT)
                || disp.equalsIgnoreCase(Part.INLINE))) {  // 첨부 파일
            fileName = MimeUtility.decodeText(p.getFileName());
            if (fileName != null) {
                logger.trace("MessageParser.getPart() : file = {}", fileName);
                // 첨부 파일을 서버의 내려받기 임시 저장소에 저장
                String tempUserDir = DOWNLOAD_TEMP_DIR + this.userid;
                File dir = new File(tempUserDir);
                if (!dir.exists()) {  // tempUserDir 생성
                    dir.mkdirs();
                }

                String filename = MimeUtility.decodeText(p.getFileName());
                // 파일명에 " "가 있을 경우 서블릿에 파라미터로 전달시 문제 발생함.
                // " "를 모두 "_"로 대체함.
                DataHandler dh = p.getDataHandler();
                FileOutputStream fos = new FileOutputStream(tempUserDir + "/" + filename);
                dh.writeTo(fos);
                fos.flush();
                fos.close();
            }
        } else {  // 메일 본문
            if (p.isMimeType("text/*")) {
                body = (String) p.getContent();
                if (p.isMimeType("text/plain")) {
                    body = body.replaceAll("\r\n", " <br>");
                }
            } else if (p.isMimeType("multipart/alternative")) {
                // html text보다  plain text 선호
                Multipart mp = (Multipart) p.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    Part bp = mp.getBodyPart(i);
                    if (bp.isMimeType("text/plain")) {  // "text/html"도 있을 것임.
                        getPart(bp);
                    }
                }
            } else if (p.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) p.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    getPart(mp.getBodyPart(i));
                }
            }
        }
    }

    /**
     * 출력해보는 메소드입니다.
     * @param printBody 출력할지 여부입니다.
     */
    private void printMessage(boolean printBody) {
        logger.trace("From: {}", fromAddress);
        logger.trace("To: {}", toAddress);
        logger.trace("CC: {}", ccAddress);
        logger.trace("Date: {}", sentDate);
        logger.trace("Subject: {}", subject);

        if (printBody) {
            logger.trace("본 문");
            logger.trace("---------------------------------");
            logger.trace(body);
            logger.trace("---------------------------------");
            logger.trace("첨부파일: {}", fileName);
        }
    }

    /**
     * 주소들을 String으로 바꿔주는 메소드입니다.
     * @param addresses 주소들
     * @return ", "로 연결된 주소
     */
    private String getAddresses(Address[] addresses) {
        String[] addressStrings = Arrays.stream(addresses).map(Address::toString).distinct().toArray(String[]::new); // java 8
        return String.join(", ", addressStrings); // java 9
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCcAddress() {
        return ccAddress;
    }

    public void setCcAddress(String ccAddress) {
        this.ccAddress = ccAddress;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }
}
