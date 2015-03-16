package com.gose.session;

/**
 * Created by Yuwanit on 2/24/2015.
 */
public class UserLogin {

    private static UserLogin instance;
    private static String userId;
    private static String userName;
    private static String userImage;

    private UserLogin(){}

    public static UserLogin getInstance() {
        if (instance == null) {
            instance = new UserLogin();
        }
        return instance;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        UserLogin.userId = userId;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        UserLogin.userName = userName;
    }

    public static String getUserImage() {
        return userImage;
    }

    public static void setUserImage(String userImage) {
        UserLogin.userImage = userImage;
    }
}
