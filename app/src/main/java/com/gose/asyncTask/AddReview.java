package com.gose.asyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.Toast;

import com.gose.R;
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
 * Created by Yuwanit on 3/1/2015.
 */
public class AddReview  extends AsyncTask<String, Integer, String> {

    private static final String TAG = AddReview.class.getSimpleName();

    private Context context;
    private String government_id;
    private String user_id;
    private String review;

    private ProgressDialog progressDialog;

    public AddReview(Context context, String government_id, String user_id, String review){
        this.context = context;
        this.government_id = government_id;
        this.user_id = user_id;
        this.review = review;
    }

    @Override
    protected void onPreExecute() {
        Resources resources = context.getResources();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(resources.getString(R.string.progress_please_wait));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        JsonFormPost post = new JsonFormPost();

        String url = "http://gose.esy.es/administrator/json/add_review.html";

        List<NameValuePair> nameValuePairs = new ArrayList<>();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());

        nameValuePairs.add(new BasicNameValuePair("government_id",government_id));
        nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
        nameValuePairs.add(new BasicNameValuePair("review", review));
        nameValuePairs.add(new BasicNameValuePair("create_date", date));

        String result = post.connect(url, nameValuePairs);

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String message = jsonObject.getString("message");
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog.dismiss();
        super.onPostExecute(result);
    }
}
