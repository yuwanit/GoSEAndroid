package com.gose.asyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.gose.R;
import com.gose.httpclient.JsonFormPost;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuwanit on 3/1/2015.
 */
public class AddEvaluation extends AsyncTask<String, Integer, String> {

    private static final String TAG = AddEvaluation.class.getSimpleName();

    private Context context;
    private String government_id;
    private String user_id;
    private float rating;

    private ProgressDialog progressDialog;

    public AddEvaluation(Context context, String government_id, String user_id, float rating){
        this.context = context;
        this.government_id = government_id;
        this.user_id = user_id;
        this.rating = rating;

        Log.e(TAG, "government_id, user_id, rating >>> "+government_id+", "+user_id+", "+rating);
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

        String url = "http://gose.esy.es/administrator/json/add_evaluation.html";

        List<NameValuePair> nameValuePairs = new ArrayList<>();

        nameValuePairs.add(new BasicNameValuePair("government_id",government_id));
        nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
        nameValuePairs.add(new BasicNameValuePair("evaluation", String.valueOf(rating)));

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
