package com.gose.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yuwanit on 3/18/2015.
 */
public class GovernmentOffice {

    private static GovernmentOffice instance;
    private static ArrayList<HashMap<String, String>> governmentList;
    private static List<String> governmentNameList;
    private static String categoryId;

    private GovernmentOffice(){}

    public static GovernmentOffice getInstance() {
        if (instance == null) {
            instance = new GovernmentOffice();
        }
        return instance;
    }

    public static ArrayList<HashMap<String, String>> getGovernmentList() {
        return governmentList;
    }

    public static void setGovernmentList(ArrayList<HashMap<String, String>> governmentList) {
        GovernmentOffice.governmentList = governmentList;
    }

    public static List<String> getGovernmentNameList() {
        return governmentNameList;
    }

    public static void setGovernmentNameList(List<String> governmentNameList) {
        GovernmentOffice.governmentNameList = governmentNameList;
    }

    public static String getCategoryId() {
        return categoryId;
    }

    public static void setCategoryId(String categoryId) {
        GovernmentOffice.categoryId = categoryId;
    }
}
