package cse.maven_webmail.model;

/**
 * 유저 데이터 빈입니다.
 */
public class UserDataBean {
    private String userId;
    private String result;
    public UserDataBean() {
        // 빈 생성자
    }

    /**
     * 유저 아이디를 설정하고 해당 개인 정보를 찾습니다.
     * @param userId 유저 아이디
     */
    public void setUserId(String userId) {
        this.userId = userId;
        findData();
    }

    /**
     * 데이터베이스 모델 객체를 생성하여 찾은 데이터를 result에 넣습니다.
     */
    public void findData(){
        UserDatabaseAgent userDatabaseAgent = new UserDatabaseAgent();
        userDatabaseAgent.setUserId(userId);
        result = userDatabaseAgent.getUserData();
    }

    /**
     * 결과를 반환합니다.
     * @return 결과
     */
    public String getResult() {
        return result;
    }
}
