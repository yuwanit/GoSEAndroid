package com.gose.asyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gose.R;
import com.gose.httpclient.JsonFormPost;
import com.gose.route.GPSTracker;
import com.gose.route.Navigator;
import com.gose.session.GovernmentOffice;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yuwanit on 3/18/2015.
 */
public class GetGovernmentDistance extends AsyncTask<String, Integer, String> {

    private ProgressDialog progressDialog;
    private Context context;
    private String keyword_search;
    private GoogleMap googleMap;
    private LatLng latLngCurrent;
    private LatLng latLngDestination;
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
    private GovernmentOffice governmentOffice = GovernmentOffice.getInstance();
    private String language = governmentOffice.getLanguage();

    public GetGovernmentDistance(Context context, String keyword_search, GoogleMap googleMap) {
        this.context = context;
        this.keyword_search = keyword_search;
        this.googleMap = googleMap;
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

        nameValuePairs.add(new BasicNameValuePair("category_id", "0"));
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
                hashMap.put("thai_name",
                        jsonObject2.getString("thai_name"));
                hashMap.put("location", jsonObject2.getString("location"));
                hashMap.put("thai_location", jsonObject2.getString("thai_location"));
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
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if(arrayList.size() != 0) {

            GPSTracker gps = new GPSTracker(context);

            // check if GPS enabled
            if (gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                latLngCurrent = new LatLng(latitude, longitude);
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }


            double latitude = Double.parseDouble(arrayList.get(0).get("latitude"));
            double longitude = Double.parseDouble(arrayList.get(0).get("longitude"));
            String governmentName = null;
            if(language.equals("th")){
                governmentName = arrayList.get(0).get("government_name");
            }else{
                governmentName = arrayList.get(0).get("thai_name");
            }


            latLngDestination = new LatLng(latitude, longitude);


            if (latLngCurrent != null && latLngDestination != null) {

                googleMap.clear();

                CameraUpdate center = CameraUpdateFactory.newLatLng(latLngCurrent);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                googleMap.moveCamera(center);
                googleMap.animateCamera(zoom);

                googleMap.addMarker(new MarkerOptions()
                        .flat(true)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.car_icon))
                        .position(latLngCurrent));

                googleMap.addMarker(new MarkerOptions()
                        .icon(
                                BitmapDescriptorFactory.fromBitmap(ProcessingBitmap(governmentName)))
                        .position(latLngDestination)
                        .title(governmentName)).showInfoWindow();

                Navigator nav = new Navigator(googleMap, latLngCurrent, latLngDestination);
                nav.findDirections(true);
                nav.setPathColor(Color.parseColor("#f2a84c"), Color.parseColor("#00ff00"), Color.parseColor("#ff0080"));
                nav.setPathBorderColor(Color.WHITE);
            } else {
                Toast.makeText(context, "Can not found your place.", Toast.LENGTH_SHORT).show();
            }
        }

        progressDialog.dismiss();
        super.onPostExecute(result);
    }

    private Bitmap ProcessingBitmap(String captionString) {
        Bitmap bm1 = null;
        Bitmap newBitmap = null;

        if (captionString != null) {

            bm1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_icon);

            Bitmap.Config config = bm1.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }

            Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintText.setColor(Color.BLACK);
            paintText.setTextSize(30);
            paintText.setStyle(Paint.Style.FILL);
            paintText.setShadowLayer(10f, 10f, 10f, Color.BLACK);

            Rect rectText = new Rect();
            paintText.getTextBounds(captionString, 0, captionString.length(), rectText);

            newBitmap = Bitmap.createBitmap(rectText.width()+4, bm1.getHeight(), config);
            Canvas newCanvas = new Canvas(newBitmap);

            newCanvas.drawBitmap(bm1, rectText.width()/2, 0, null);

            newCanvas.drawText(captionString,
                    0, rectText.height(), paintText);
        }

        return newBitmap;
    }
}
