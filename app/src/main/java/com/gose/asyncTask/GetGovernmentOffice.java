package com.gose.asyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gose.DetailGovernment;
import com.gose.R;
import com.gose.adapters.ImageAdapterForSearch;
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
 * Created by Yuwanit on 2/26/2015.
 */
public class GetGovernmentOffice extends AsyncTask<String, Integer, String> {

    private ProgressDialog progressDialog;
    private Context context;
    private String keyword_search, category_id;
    private ListView listView;
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

    public GetGovernmentOffice(Context context, String keyword_search, String category_id, ListView listView){
        this.context = context;
        this.keyword_search = keyword_search;
        this.category_id = category_id;
        this.listView = listView;
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

        String url = "http://gose.esy.es//administrator/json/search_government.html";

        List<NameValuePair> nameValuePairs = new ArrayList<>();

        nameValuePairs.add(new BasicNameValuePair("category_id",category_id));
        nameValuePairs.add(new BasicNameValuePair("keyword_search", keyword_search));

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
                hashMap.put("location", jsonObject2.getString("location"));
                hashMap.put("ImagePath",
                        "http://gose.esy.es/administrator/uploads/pic_government/"
                                + jsonObject2.getString("image"));
                hashMap.put("head_agency",
                        jsonObject2.getString("head_agency"));
                hashMap.put("offices_hours_start",
                        jsonObject2.getString("offices_hours_start"));
                hashMap.put("offices_hours_end",
                        jsonObject2.getString("offices_hours_end"));
                hashMap.put("latitude", jsonObject2.getString("latitude"));
                hashMap.put("longitude", jsonObject2.getString("longitude"));
                hashMap.put("category_name",
                        jsonObject2.getString("category_name"));
                hashMap.put("category_name",
                        jsonObject2.getString("category_name"));
                hashMap.put("tel", jsonObject2.getString("tel"));
                hashMap.put("category_image",
                        "http://gose.esy.es/administrator/uploads/pic_categories/"
                                + jsonObject2.getString("category_image"));

                arrayList.add(hashMap);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int position, long arg3) {

                        Intent intent = new Intent(context,
                                DetailGovernment.class);

                        intent.putExtra("government_id", arrayList
                                .get(position).get("government_id"));
                        intent.putExtra("imageDesc", arrayList
                                .get(position).get("ImageDesc"));
                        intent.putExtra("location", arrayList.get(position)
                                .get("location"));
                        intent.putExtra("imagepath", arrayList
                                .get(position).get("ImagePath"));
                        intent.putExtra("head_agency",
                                arrayList.get(position).get("head_agency"));
                        intent.putExtra("offices_hours_start", arrayList
                                .get(position).get("offices_hours_start"));
                        intent.putExtra(
                                "offices_hours_end",
                                arrayList.get(position).get(
                                        "offices_hours_end"));
                        intent.putExtra("latitude", arrayList.get(position)
                                .get("latitude"));
                        intent.putExtra("longitude", arrayList
                                .get(position).get("longitude"));
                        intent.putExtra("category_name",
                                arrayList.get(position)
                                        .get("category_name"));
                        intent.putExtra("category_image",
                                arrayList.get(position)
                                        .get("category_image"));
                        intent.putExtra("tel",
                                arrayList.get(position).get("tel"));
                        intent.putExtra("fax",
                                arrayList.get(position).get("fax"));

                        context.startActivity(intent);

                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    protected void onPostExecute(String result) {

        listView.setAdapter(new ImageAdapterForSearch(context, arrayList ));
        progressDialog.dismiss();
        super.onPostExecute(result);
    }
}
