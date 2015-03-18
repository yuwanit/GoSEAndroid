package com.gose.asyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.gose.DetailGovernment;
import com.gose.R;
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
 * Created by APATTA-PU on 16/3/2558.
 */
public class FavoriteList extends AsyncTask<String, Integer, String> {

    private ProgressDialog progressDialog;
    private Context context;
    private int goverment_id;
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

    public FavoriteList(Context context, int goverment_id) {
        this.context = context;
        this.goverment_id = goverment_id;
    }

    @Override
    protected void onPreExecute() {
        Resources resources = context.getResources();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(resources.getString(R.string.progress_processing));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... arg0) {

        JsonFormPost post = new JsonFormPost();

        String url = "http://gose.esy.es/administrator/json/search_government_by_id.html";

        List<NameValuePair> nameValuePairs = new ArrayList<>();

        nameValuePairs.add(new BasicNameValuePair("government_id", String.valueOf(goverment_id)));

        String result = post.connect(url, nameValuePairs);

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = new JSONArray(
                    jsonObject.getString("GovernmentOffice"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject2 = new JSONObject(
                        jsonArray.getString(i));
                final HashMap<String, String> hashMap = new HashMap<String, String>();

                hashMap.put("government_id",
                        jsonObject2.getString("government_id"));
                hashMap.put("ImageDesc",
                        jsonObject2.getString("government_name"));
                hashMap.put("thai_name",
                        jsonObject2.getString("thai_name"));
                hashMap.put("location", jsonObject2.getString("location"));
                hashMap.put("ImagePath",
                        "http://gose.esy.es/administrator/uploads/pic_government/"
                                + jsonObject2.getString("image"));
                hashMap.put("head_agency",
                        jsonObject2.getString("head_agency"));
                hashMap.put("thai_head_agency",
                        jsonObject2.getString("thai_head_agency"));
                hashMap.put("offices_hours_start",
                        jsonObject2.getString("offices_hours_start"));
                hashMap.put("offices_hours_end",
                        jsonObject2.getString("offices_hours_end"));
                hashMap.put("latitude", jsonObject2.getString("latitude"));
                hashMap.put("longitude", jsonObject2.getString("longitude"));
                hashMap.put("category_name",
                        jsonObject2.getString("category_name"));
                hashMap.put("thai_category_name",
                        jsonObject2.getString("thai_category_name"));
                hashMap.put("tel", jsonObject2.getString("tel"));
                hashMap.put("category_image",
                        "http://gose.esy.es/administrator/uploads/pic_categories/"
                                + jsonObject2.getString("category_image"));

                arrayList.add(hashMap);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Log.d("", result);
        return null;

    }

    @Override
    protected void onPostExecute(String result) {

        Log.e("xxxx", "size : "+arrayList.size());
        if(arrayList.size() != 0) {
            Intent intent = new Intent(context,
                    DetailGovernment.class);

            intent.putExtra("government_id", arrayList
                    .get(0).get("government_id"));
            intent.putExtra("imageDesc", arrayList
                    .get(0).get("ImageDesc"));
            intent.putExtra("thai_name", arrayList
                    .get(0).get("thai_name"));
            intent.putExtra("location", arrayList.get(0)
                    .get("location"));
            intent.putExtra("imagepath", arrayList
                    .get(0).get("ImagePath"));
            intent.putExtra("head_agency",
                    arrayList.get(0).get("head_agency"));
            intent.putExtra("thai_head_agency",
                    arrayList.get(0).get("thai_head_agency"));
            intent.putExtra("offices_hours_start", arrayList
                    .get(0).get("offices_hours_start"));
            intent.putExtra(
                    "offices_hours_end",
                    arrayList.get(0).get(
                            "offices_hours_end"));
            intent.putExtra("latitude", arrayList.get(0)
                    .get("latitude"));
            intent.putExtra("longitude", arrayList
                    .get(0).get("longitude"));
            intent.putExtra("category_name",
                    arrayList.get(0)
                            .get("category_name"));
            intent.putExtra("thai_category_name",
                    arrayList.get(0)
                            .get("thai_category_name"));
            intent.putExtra("category_image",
                    arrayList.get(0)
                            .get("category_image"));
            intent.putExtra("tel",
                    arrayList.get(0).get("tel"));
            intent.putExtra("fax",
                    arrayList.get(0).get("fax"));

            context.startActivity(intent);
        }

        progressDialog.dismiss();
        super.onPostExecute(result);
    }
}