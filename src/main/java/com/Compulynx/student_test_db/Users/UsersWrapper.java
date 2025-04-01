package com.Compulynx.student_test_db.Users;

public class UsersWrapper {
    private Long userID;
    private final String userName;
    private final String password;

    public UsersWrapper(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }






}
