package cse.maven_webmail.model;

public class UserDataBean {
    private String userId;
    private String result;
    public UserDataBean() {

    }

    public void setUserId(String userId) {
        this.userId = userId;
        findData();
    }
    public void findData(){
        UserDatabaseAgent userDatabaseAgent = new UserDatabaseAgent();
        userDatabaseAgent.setUserId(userId);
        result = userDatabaseAgent.getUserData();
    }

    public String getResult() {
        return result;
    }
}
