package cse.maven_webmail.model;

import org.apache.james.mime4j.dom.Header;
import org.apache.james.mime4j.message.SimpleContentHandler;
import org.apache.james.mime4j.stream.BodyDescriptor;
import org.apache.james.mime4j.stream.Field;

import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TrashMessageFormatter extends SimpleContentHandler {
    private String toAddress;
    private String fromAddress;
    private String subject;
    private String date;
    private String cc;
    private int i = 0;
    private String bodyString;
    private static final String ENCODED_PART_REGEX_PATTERN = "=\\?([^?]+)\\?([^?]+)\\?([^?]+)\\?=";
    private String fileName;
    private InputStream fileStream;
    private boolean file;
    public String getCc() {
        return cc;
    }

    public String getToAddress() {
        return toAddress;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public String getBodyString() {
        return bodyString;
    }

    public String getFileName() {
        return fileName;
    }

    public InputStream getFileStream() {
        return fileStream;
    }

    @Override
    public void headers(Header arg0) {
        List<Field> fields = arg0.getFields();
        Map<String, String> mapFields =
                fields.stream().collect(Collectors.toMap(Field::getName,
                        Field::getBody));
        mapFields.forEach((k, v) -> System.out.println(k + " " + v));
        if (i == 0) {
            toAddress = mapFields.get("To");
            fromAddress = mapFields.get("From");
            subject = decode(mapFields.get("Subject"));
            date = mapFields.get("Date");
            cc = mapFields.get("Cc");
            i++;
        }
        if (mapFields.get("Content-Disposition") != null) {
            String contentDisposition = mapFields.get("Content-Disposition");
            file = true;
            fileName = decode(contentDisposition.split("=")[1]);
        }
    }

    @Override
    public void body(BodyDescriptor bd, InputStream is) {
        if (!file) {
            try {
                String encoded = new String(is.readAllBytes());
                bodyString = decode(encoded);
            } catch (IOException e) {

            }
        } else {
            fileStream = is;
            System.out.println(fileStream);
            file = false;
        }
    }



    public void startMessage() {

    }


    public void endMessage() {

    }


    public void startBodyPart() {

    }


    public void endBodyPart() {

    }


    public void preamble(InputStream is) {

    }


    public void epilogue(InputStream is) {

    }


    public void startMultipart(BodyDescriptor bd) {

    }


    public void endMultipart() {

    }


    public void raw(InputStream is) {

    }

    private String decode(String s) {
        Pattern pattern = Pattern.compile(ENCODED_PART_REGEX_PATTERN);
        Matcher m = pattern.matcher(s);
        ArrayList<String> encodedParts = new ArrayList<>();
        while (m.find()) {
            encodedParts.add(m.group(0));

        }
        if (encodedParts.size() > 0) {
            try {
                for (String encoded : encodedParts) {
                    s = s.replace(encoded, MimeUtility.decodeText(encoded));
                }
                return s;

            } catch (Exception ex) {
                return s;
            }
        } else
            return s;
    }
}
