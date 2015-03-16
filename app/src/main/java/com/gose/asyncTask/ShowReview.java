package com.gose.asyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gose.GetProfile.GetFacebookProfileFromId;
import com.gose.GetProfile.GetGooglePlusProfileFromId;
import com.gose.adapter.ReviewAdapter;
import com.gose.httpclient.JsonFormPost;

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
public class ShowReview extends AsyncTask<String, Integer, String> {

    private static final String TAG = ShowReview.class.getSimpleName();

    private Context context;
    private String government_id;
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

    private ListView listView;
    private static String max_review_id = "0";
    private final String limit = "5";
    private ProgressBar progressBar_reviews;
    private TextView tv_no_review;

    public ShowReview(){}

    public ShowReview(Context context, String government_id, ListView listView, ProgressBar progressBar_reviews, TextView tv_no_review){
        this.context = context;
        this.government_id = government_id;
        this.listView = listView;
        this.progressBar_reviews = progressBar_reviews;
        this.tv_no_review = tv_no_review;
    }

    @Override
    protected void onPreExecute() {
        progressBar_reviews.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        JsonFormPost post = new JsonFormPost();

        String url = "http://gose.esy.es/administrator/json/show_review.html";

        List<NameValuePair> nameValuePairs = new ArrayList<>();

        nameValuePairs.add(new BasicNameValuePair("government_id",government_id));
        nameValuePairs.add(new BasicNameValuePair("max_review_id", max_review_id));
        nameValuePairs.add(new BasicNameValuePair("limit", limit));

        Log.e(TAG, "max_review_id query >>> "+max_review_id);
        Log.e(TAG, "government_id query >>> "+government_id);
        Log.e(TAG, "Limit query >>> "+limit);

        String result = post.connect(url, nameValuePairs);

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = new JSONArray(
                    jsonObject.getString("Review"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject2 = new JSONObject(
                        jsonArray.getString(i));
                final HashMap<String, String> hashMap = new HashMap<String, String>();

                String user_id = jsonObject2.getString("user_id");
                String type = String.valueOf(user_id.charAt(0));

                String user_img = null;
                String user_name = null;

                if(type.equals("G")){
                    GetGooglePlusProfileFromId getGooglePlusProfileFromId = new GetGooglePlusProfileFromId(user_id);
                    user_img = getGooglePlusProfileFromId.getUserImageProfile();
                    user_name = getGooglePlusProfileFromId.getUserDisplayName();
                }else {
                    GetFacebookProfileFromId getFacebookProfileFromId = new GetFacebookProfileFromId(user_id, context);
                    user_img = getFacebookProfileFromId.getUserImageProfile();
                    user_name = getFacebookProfileFromId.getUserDisplayName();
                }

                hashMap.put("review_id",
                        jsonObject2.getString("review_id"));
                hashMap.put("user_id",
                        user_id);
                hashMap.put("user_img",
                        user_img);
                hashMap.put("user_name",
                        user_name);
                hashMap.put("review",
                        jsonObject2.getString("review"));
                hashMap.put("create_date",
                        jsonObject2.getString("create_date"));
                hashMap.put("evaluation",
                        jsonObject2.getString("evaluation"));

                arrayList.add(hashMap);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if(arrayList.size() != 0 && arrayList != null) {
            max_review_id = arrayList.get(arrayList.size() - 1).get("review_id");
            listView.setAdapter(new ReviewAdapter(context, arrayList));

            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                // pre-condition
                return;
            }

            int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                if (listItem instanceof ViewGroup) {
                    listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                }
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);

        }else{
            tv_no_review.setVisibility(View.VISIBLE);
        }

        progressBar_reviews.setVisibility(View.GONE);

        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        max_review_id = "0";
        super.onCancelled();
    }
}
