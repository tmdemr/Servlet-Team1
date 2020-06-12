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
    public static final int RESTORE_MAIL_COMMAND = 53;
    public static final int ADD_USER_COMMAND = 61;
    public static final int DELETE_USER_COMMAND = 62;
    public static final int USER_VERIFY_COMMAND = 63;
    public static final int FIND_ID_COMMAND = 64;
    public static final int FIND_PASSWORD_COMMAND = 65;
    public static final int CHANGE_PASSWORD_COMMAND = 66;
    public static final int CHANGE_MY_INFO = 67;
    public static final int ADD_ADDRESS = 68;
    public static final int CHANGE_ADDRESS = 69;
    public static final int DELETE_ADDRESS = 70;
    
    public static final int LOGIN = 91;
    public static final int LOGOUT = 92;
}
