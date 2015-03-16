package com.gose.GetProfile;

import android.util.Log;

import com.gose.httpclient.JsonFormGet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yuwanit on 3/5/2015.
 */
public class GetGooglePlusProfileFromId {

    private static final String TAG = GetGooglePlusProfileFromId.class.getSimpleName();
    private String userImageProfile = null;
    private String userDisplayName = null;

    public GetGooglePlusProfileFromId(String userGId) {
        String userId = userGId.replace("G", "");

        String url_profile = "http://picasaweb.google.com/data/entry/api/user/"+userId+"?alt=json";

        JsonFormGet getJson = new JsonFormGet();
        String result = getJson.connect(url_profile);

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject jsonObjectEntry = new JSONObject(jsonObject.getString("entry"));
            JSONObject jsonObjectThumbnail = new JSONObject(jsonObjectEntry.getString("gphoto$thumbnail"));
            userImageProfile = jsonObjectThumbnail.getString("$t");
            Log.e(TAG, "Photo URL >>> "+userImageProfile);

            JSONArray jsonArrayAuthor = new JSONArray(jsonObjectEntry.getString("author"));
            JSONObject jsonObjectAuthor = new JSONObject(jsonArrayAuthor.getString(0));
            JSONObject jsonObjectName = new JSONObject(jsonObjectAuthor.getString("name"));
            userDisplayName = jsonObjectName.getString("$t");
            Log.e(TAG, "Photo URL >>> "+userDisplayName);



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserImageProfile() {
        return userImageProfile;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }
}
