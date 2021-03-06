package cse.maven_webmail.model;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 책임: enctype이 multipart/form-data인 HTML 폼에 있는 각 필드와 파일 정보 추출
 *
 * @author jongmin
 */
public class FormParser {
    private static final Logger logger = LoggerFactory.getLogger(FormParser.class);
    private HttpServletRequest request;
    private String toAddress = null;
    private String ccAddress = null;
    private String subject = null;
    private String body = null;
    private String fileName = null;
    private static final String UPLOAD_TARGET_DIR = "C:/temp/upload";

    public FormParser(HttpServletRequest request) {
        this.request = request;
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

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
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

    private void checkFolder() {
        File uploadTargetFolder = new File(UPLOAD_TARGET_DIR);
        if (!uploadTargetFolder.exists()) {
            uploadTargetFolder.mkdirs();
        }
    }

    /**
     * request를 파싱하는 메소드입니다.
     */
    public void parse() {
        checkFolder(); // 폴더 없을 경우 만들게 추가했음 - 남영우
        try {
            request.setCharacterEncoding(StandardCharsets.UTF_8.name());

            // 1. 디스크 기반 파일 항목에 대한 팩토리 생성
            DiskFileItemFactory diskFactory = new DiskFileItemFactory();
            // 2. 팩토리 제한사항 설정
            diskFactory.setSizeThreshold(10 * 1024 * 1024);
            diskFactory.setRepository(new File(UPLOAD_TARGET_DIR));
            // 3. 파일 업로드 핸들러 생성
            ServletFileUpload upload = new ServletFileUpload(diskFactory);

            // 4. request 객체 파싱
            List<FileItem> fileItems = upload.parseRequest(request);
            for (FileItem fi : fileItems) {
                if (fi.isFormField()) {  // 5. 폼 필드 처리
                    String fieldName = fi.getFieldName();
                    String item = fi.getString(StandardCharsets.UTF_8.name());

                    if (fieldName.equals("to")) {
                        setToAddress(item);  // 200102 LJM - @ 이후의 서버 주소 제거
                    } else if (fieldName.equals("cc")) {
                        setCcAddress(item);
                    } else if (fieldName.equals("subj")) {
                        setSubject(item);
                    } else if (fieldName.equals("body")) {
                        setBody(item);
                    }
                } else {  // 6. 첨부 파일 처리
                    if (!(fi.getName() == null || fi.getName().equals(""))) {
                        String fieldName = fi.getFieldName();
                        logger.info("ATTACHED FILE : {} = {}", fieldName, fi.getName());

                        // 절대 경로 저장
                        setFileName(UPLOAD_TARGET_DIR + File.separator + fi.getName());
                        File fn = new File(fileName);
                        // upload 완료. 추후 메일 전송후 해당 파일을 삭제하도록 해야 함.
                        if (fileName != null) {
                            fi.write(fn);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("FormParser.parse() : exception = {}", ex.getMessage());
        }
    }  // parse()
}
