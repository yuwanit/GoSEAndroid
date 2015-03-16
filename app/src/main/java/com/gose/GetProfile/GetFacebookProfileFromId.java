package com.gose.GetProfile;

import android.content.Context;
import android.util.Log;

import com.gose.httpclient.JsonFormGet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Yuwanit on 3/5/2015.
 */
public class GetFacebookProfileFromId {

    private static final String TAG = GetFacebookProfileFromId.class.getSimpleName();
    private String userImageProfile = null;
    private String userDisplayName = null;

    public GetFacebookProfileFromId(String userFId, Context context) {
        String userId = userFId.replace("F", "");

        String url_profile = null;

        try {
            url_profile = "https://graph.facebook.com/v2.2/"+userId+"?access_token=1615450702008202"+URLEncoder.encode("|", "UTF-8")+"DfiGLP8Xbhj8bozzCVVnaG57BMM";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JsonFormGet getJson = new JsonFormGet();
        String result = getJson.connect(url_profile);

        try {
            JSONObject jsonObject = new JSONObject(result);
            userDisplayName = jsonObject.getString("name");
            Log.e(TAG, "Photo URL >>> "+userDisplayName);

            userImageProfile = "https://graph.facebook.com/"+userId+"/picture?type=large";
            Log.e(TAG, "Photo URL >>> "+userImageProfile);



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
