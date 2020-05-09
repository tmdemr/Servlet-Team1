/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cse.maven_webmail.control;

public class CommandType {
    /**
     * 생성하지 마시오 - 남영우
     */
    private CommandType() {

    }

    public static final int READ_MENU = 1;
    public static final int WRITE_MENU = 2;

    public static final int ADD_USER_MENU = 3;
    public static final int DELETE_USER_MENU = 4;

    public static final int SEND_MAIL_COMMAND = 21;
    public static final int DELETE_MAIL_COMMAND = 41;
    public static final int DOWNLOAD_COMMAND = 51;

    public static final int ADD_USER_COMMAND = 61;
    public static final int DELETE_USER_COMMAND = 62;

    public static final int LOGIN = 91;
    public static final int LOGOUT = 92;
}
