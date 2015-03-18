package com.gose.asyncTask;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.SQLException;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gose.DetailGovernment;
import com.gose.R;
import com.gose.database.DatabaseHelper;
import com.gose.database.FavoriteGovernmentOffice;
import com.gose.database.FavoriteGovernmentOfficeData;
import com.gose.httpclient.JsonFormGet;
import com.gose.session.GovernmentOffice;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yuwanit on 2/26/2015.
 */
public class GetLocation extends AsyncTask<String, Integer, String> {

    private static final String TAG = GetLocation.class.getSimpleName();

    private ProgressDialog progressDialog;
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
    List<String> governmentNameList = new ArrayList<>();
    private Context context;
    private String tel, government_id, imageDesc,governmentNameThai, location, imagePath, head_agency,thai_head_agency,
            offices_hours_start, offices_hours_end, latitude, longitude, category_name,thai_category_name, category_image, fax;

    private static Dialog dialog = null;
    private static GoogleMap googleMap;
    private LayoutInflater inflater;
    private ImageView img_category, img_government;
    private ProgressBar progressBar_img_category, progressBar_img_government;

    private ImageView imageView_favorite;
    private  boolean status_favorite = false;
    private DatabaseHelper databaseHelper;

    public GetLocation(Context context, LayoutInflater inflater, GoogleMap googleMap) {
        this.context = context;
        this.inflater = inflater;
        this.googleMap = googleMap;
    }

    @Override
    protected void onPreExecute() {

        databaseHelper = new DatabaseHelper(context);

        if (dialog == null) {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_on_map);

            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));

            //HomeSectionFragment.this.startActivity(intent);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            lp.width = size.x;
            lp.height = size.y;
            // This makes the dialog take up the full width
            window.setAttributes(lp);
        }

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

        JsonFormGet getJson = new JsonFormGet();
        String result = getJson
                .connect("http://gose.esy.es/administrator/json/search_government.html");

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
                governmentNameList.add(jsonObject2.getString("government_name"));
                governmentNameList.add(jsonObject2.getString("thai_name"));
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;

    }

    @Override
    protected void onPostExecute(String result) {

        GovernmentOffice governmentOffice = GovernmentOffice.getInstance();
        governmentOffice.setGovernmentList(arrayList);
        governmentOffice.setGovernmentNameList(governmentNameList);

        for (int i = 0; i < arrayList.size(); i++) {

            googleMap.addMarker(new MarkerOptions()
                    .icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.marker_icon))
                    .position(
                            new LatLng(Double.valueOf(arrayList.get(i).get(
                                    "latitude")), Double.valueOf(arrayList.get(i)
                                    .get("longitude"))))
                    .title(
                            arrayList.get(i).get("ImageDesc")));
        }

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                // Getting view from the layout file info_window_layout
                View v = inflater.inflate(R.layout.map_information, null);

                TextView tv_government_name = (TextView) v.findViewById(R.id.tv_government_name);
                imageDesc = marker.getTitle();

                tv_government_name.setText(imageDesc);

                // Returning the view containing InfoWindow contents
                return v;
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {

                final String title = marker.getTitle();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).get("ImageDesc").equals(title)) {

                        progressBar_img_category = (ProgressBar) dialog.findViewById(R.id.progressBar_img_category);
                        progressBar_img_government = (ProgressBar) dialog.findViewById(R.id.progressBar_img_government);
                        img_category = (ImageView) dialog.findViewById(R.id.img_category);
                        img_government = (ImageView) dialog.findViewById(R.id.img_government);
                        TextView tv_government = (TextView) dialog.findViewById(R.id.tv_government);
                        TextView tv_location = (TextView) dialog.findViewById(R.id.tv_location);
                        TextView tv_offices_hours = (TextView) dialog.findViewById(R.id.tv_offices_hours);
                        TextView tv_tel = (TextView) dialog.findViewById(R.id.tv_tel);
                        Button btn_read_more = (Button) dialog.findViewById(R.id.btn_read_more);
                        imageView_favorite = (ImageView) dialog.findViewById(R.id.imageView_favorite);

                        government_id = arrayList.get(i).get("government_id");
                        imageDesc = arrayList.get(i).get("ImageDesc");
                        governmentNameThai = arrayList.get(i).get("thai_name");
                        location = arrayList.get(i).get("location");
                        imagePath = arrayList.get(i).get("ImagePath");
                        head_agency = arrayList.get(i).get("head_agency");
                        thai_head_agency = arrayList.get(i).get("thai_head_agency");
                        offices_hours_start = arrayList.get(i).get("offices_hours_start");
                        String offices_hours_start_show = offices_hours_start.substring(0, offices_hours_start.length() - 3);
                        offices_hours_end = arrayList.get(i).get("offices_hours_end");
                        String offices_hours_end_show = offices_hours_end.substring(0, offices_hours_end.length() - 3);
                        latitude = arrayList.get(i).get("latitude");
                        longitude = arrayList.get(i).get("longitude");
                        category_name = arrayList.get(i).get("category_name");
                        thai_category_name = arrayList.get(i).get("thai_category_name");
                        category_image = arrayList.get(i).get("category_image");
                        tel = arrayList.get(i).get("tel");
                        fax = arrayList.get(i).get("fax");

                        checkFavorite();
                        imageView_favorite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (status_favorite == true) {
                                    imageView_favorite.setImageResource(R.drawable.favorite_icon_off);
                                    status_favorite = false;

                                    RuntimeExceptionDao<FavoriteGovernmentOffice, Integer> canteenDao = databaseHelper.getfavoriteGovernmentofficesDao();
                                    DeleteBuilder<FavoriteGovernmentOffice, Integer> deleteBuilder = canteenDao.deleteBuilder();

                                    try {
                                        deleteBuilder.where().eq(FavoriteGovernmentOffice.COLUMN_NAME_GOVERNMENT_ID, government_id);
                                        canteenDao.delete(deleteBuilder.prepare());
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    } catch (java.sql.SQLException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    imageView_favorite.setImageResource(R.drawable.favorite_icon);
                                    status_favorite = true;

                                    RuntimeExceptionDao<FavoriteGovernmentOffice, Integer> favoriteGovernmentofficesDao = databaseHelper.getfavoriteGovernmentofficesDao();
                                    FavoriteGovernmentOffice favoriteGovernmentoffices = new FavoriteGovernmentOffice(Integer.parseInt(government_id), imageDesc, governmentNameThai);
                                    favoriteGovernmentofficesDao.create(favoriteGovernmentoffices);

                                }
                            }
                        });

                        tv_government.setText(imageDesc);
                        tv_location.setText(location);
                        tv_offices_hours.setText(offices_hours_start_show + " - " + offices_hours_end_show);
                        tv_tel.setText(tel);

                        tv_tel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:" + Uri.encode(tel.trim())));
                                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(callIntent);
                            }
                        });

                        String img_category_name = arrayList.get(i).get("category_image").replace(" ", "%20");
                        String img_government_name = arrayList.get(i).get("ImagePath").replace(" ", "%20");

                        new ImageLoadTaskWithProgressbar(img_category_name, img_category, progressBar_img_category).execute();
                        new ImageLoadTaskWithProgressbar(img_government_name, img_government, progressBar_img_government).execute();

                        btn_read_more.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context,
                                        DetailGovernment.class);

                                intent.putExtra("government_id", government_id);
                                intent.putExtra("imageDesc", imageDesc);
                                intent.putExtra("thai_name", governmentNameThai);
                                intent.putExtra("location", location);
                                intent.putExtra("ImagePath", imagePath);
                                intent.putExtra("head_agency", head_agency);
                                intent.putExtra("thai_head_agency", thai_head_agency);
                                intent.putExtra("offices_hours_start", offices_hours_start);
                                intent.putExtra("offices_hours_end", offices_hours_end);
                                intent.putExtra("latitude", latitude);
                                intent.putExtra("longitude", longitude);
                                intent.putExtra("category_name", category_name);
                                intent.putExtra("thai_category_name", thai_category_name);
                                intent.putExtra("category_image", category_image);
                                intent.putExtra("tel", tel);
                                intent.putExtra("fax", fax);

                                context.startActivity(intent);
                            }
                        });

                        ImageView btnCancel = (ImageView) dialog.findViewById(R.id.icon_close);
                        // if button is clicked, close the custom dialog
                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                }

            }
        });

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
                7.894654, 98.352212));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);

        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);

        progressDialog.dismiss();
        super.onPostExecute(result);
    }

    private void checkFavorite(){
        FavoriteGovernmentOfficeData favoriteGovernmentOfficeData = new FavoriteGovernmentOfficeData();
        try {
            List<FavoriteGovernmentOffice> favoriteList = favoriteGovernmentOfficeData.getFavoriteGovernmentOfficeByGovernmentId(databaseHelper, government_id);

            Log.e(TAG, "favoriteList >>> " + favoriteList + " | size >>> " + favoriteList.size());

            if(favoriteList.size() != 0){
                status_favorite = true;
                imageView_favorite.setImageResource(R.drawable.favorite_icon);
            }else {
                status_favorite = false;
                imageView_favorite.setImageResource(R.drawable.favorite_icon_off);
            }

            Log.e(TAG, favoriteList.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
}

