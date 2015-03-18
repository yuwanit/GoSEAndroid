package com.gose.asyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.gose.httpclient.JsonFormGet;
import com.gose.session.GovernmentOffice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yuwanit on 3/9/2015.
 */
public class CategorySpinnerSearch extends AsyncTask<String, Integer, String> {

    private static String TAG = CategorySpinnerSearch.class.getSimpleName();

    private Context context;
    private Spinner spinner;

    private static ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

    public CategorySpinnerSearch(Context context, Spinner spinner){
        this.context = context;
        this.spinner = spinner;
    }

    @Override
    protected String doInBackground(String... params) {
        JsonFormGet getJson = new JsonFormGet();
        String result = getJson
                .connect("http://gose.esy.es/administrator/json/all_category.html");

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = new JSONArray(
                    jsonObject.getString("Categories"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject2 = new JSONObject(
                        jsonArray.getString(i));
                final HashMap<String, String> hashMap = new HashMap<String, String>();

                hashMap.put("category_id",
                        jsonObject2.getString("category_id"));
                hashMap.put("category_image",
                        jsonObject2.getString("category_image"));
                hashMap.put("category_name",
                        jsonObject2.getString("category_name"));
                hashMap.put("thai_category_name",
                        jsonObject2.getString("thai_category_name"));

                arrayList.add(hashMap);
            }

            Log.e(TAG, "array >>> "+arrayList.toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;

    }

    List<String> categoryIdList;
    @Override
    protected void onPostExecute(String s) {
        List<String> categoryNameList = new ArrayList<String>();
        categoryIdList = new ArrayList<String>();
        categoryNameList.add("All");
        categoryIdList.add("0");
        for (int i = 0; i < arrayList.size(); i++){
            categoryNameList.add(arrayList.get(i).get("category_name"));
            categoryIdList.add(arrayList.get(i).get("category_id"));
        }

        final GovernmentOffice governmentOffice = GovernmentOffice.getInstance();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categoryNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                governmentOffice.setCategoryId(categoryIdList.get(position));
                Log.e(TAG, "category_id >>> "+(categoryIdList.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelection(0);
            }
        });

        super.onPostExecute(s);
    }
}
