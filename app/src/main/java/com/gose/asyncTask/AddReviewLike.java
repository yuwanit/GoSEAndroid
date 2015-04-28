package com.gose.asyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.gose.httpclient.JsonFormPost;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Yuwanit on 4/27/2015.
 */
public class AddReviewLike extends AsyncTask<String, Integer, String> {

    private static final String TAG = AddReview.class.getSimpleName();

    private Context context;
    private String review_id;
    private String user_id;

    public AddReviewLike(Context context, String review_id, String user_id){
        this.context = context;
        this.review_id = review_id;
        this.user_id = user_id;
    }

    @Override
    protected String doInBackground(String... params) {

        JsonFormPost post = new JsonFormPost();

        String url = "http://gose.esy.es/administrator/json/add_review_like.html";

        List<NameValuePair> nameValuePairs = new ArrayList<>();

        nameValuePairs.add(new BasicNameValuePair("review_id", review_id));
        nameValuePairs.add(new BasicNameValuePair("user_id", user_id));

        String result = post.connect(url, nameValuePairs);

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String message = jsonObject.getString("message");
            Log.d(TAG, message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onPostExecute(result);
    }
}
