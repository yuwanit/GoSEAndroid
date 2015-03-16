package com.gose.asyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gose.httpclient.JsonFormPost;
import com.gose.session.UserLogin;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yuwanit on 3/3/2015.
 */
public class ShowEvaluation extends AsyncTask<String, Integer, String> {

    private static final String TAG = ShowEvaluation.class.getSimpleName();

    private Context context;
    private String government_id, total_people, sum_evaluation;
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
    private UserLogin sessoinLogin = UserLogin.getInstance();
    private RatingBar ratingBar;
    private Button btn_evaluation;
    private ImageView img_edit_evaluation;
    private TextView tv_evaluation_rate, tv_total_people;
    private RelativeLayout layout_show_evaluation;
    private LinearLayout layout_evaluation;

    public ShowEvaluation(Context context, String government_id, RatingBar ratingBar, Button btn_evaluation, ImageView img_edit_evaluation,
                          TextView tv_evaluation_rate, TextView tv_total_people,
                          RelativeLayout layout_show_evaluation, LinearLayout layout_evaluation){
        this.context = context;
        this.government_id = government_id;
        this.ratingBar = ratingBar;
        this.btn_evaluation = btn_evaluation;
        this.img_edit_evaluation = img_edit_evaluation;
        this.tv_evaluation_rate = tv_evaluation_rate;
        this.tv_total_people = tv_total_people;
        this.layout_show_evaluation = layout_show_evaluation;
        this.layout_evaluation = layout_evaluation;
    }

    @Override
    protected void onPreExecute() {
        layout_evaluation.setVisibility(View.INVISIBLE);
        layout_show_evaluation.setVisibility(View.INVISIBLE);
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        JsonFormPost post = new JsonFormPost();

        String url = "http://gose.esy.es/administrator/json/show_evaluation.html";

        List<NameValuePair> nameValuePairs = new ArrayList<>();

        nameValuePairs.add(new BasicNameValuePair("government_id",government_id));
        nameValuePairs.add(new BasicNameValuePair("user_id", sessoinLogin.getUserId()));

        String result = post.connect(url, nameValuePairs);

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = new JSONArray(
                    jsonObject.getString("Evaluation"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject2 = new JSONObject(
                        jsonArray.getString(i));
                final HashMap<String, String> hashMap = new HashMap<String, String>();

                hashMap.put("evaluation_id",
                        jsonObject2.getString("evaluation_id"));
                hashMap.put("evaluation",
                        jsonObject2.getString("evaluation"));

                arrayList.add(hashMap);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        url = "http://gose.esy.es/administrator/json/calculate_evaluation.html";

        nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("government_id",government_id));

        result = post.connect(url, nameValuePairs);

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = new JSONArray(
                    jsonObject.getString("Evaluation"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject2 = new JSONObject(
                        jsonArray.getString(i));
                final HashMap<String, String> hashMap = new HashMap<String, String>();

                total_people = jsonObject2.getString("total_people");
                sum_evaluation = jsonObject2.getString("sum_evaluation");

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if(arrayList.size() != 0) {
            float evaluation = Float.parseFloat(arrayList.get(0).get("evaluation"));
            Log.e(TAG, "evaluation >>> "+evaluation);
            ratingBar.setRating(evaluation);
            ratingBar.setClickable(false);
            btn_evaluation.setVisibility(View.GONE);
            img_edit_evaluation.setVisibility(View.VISIBLE);
        }else{
            ratingBar.setClickable(true);
            btn_evaluation.setVisibility(View.VISIBLE);
            img_edit_evaluation.setVisibility(View.GONE);
        }

        tv_evaluation_rate.setText(sum_evaluation);
        tv_total_people.setText(total_people);

        layout_show_evaluation.setVisibility(View.VISIBLE);
        if(sessoinLogin.getUserId() != null) {
            layout_evaluation.setVisibility(View.VISIBLE);
        }
        super.onPostExecute(result);
    }
}
