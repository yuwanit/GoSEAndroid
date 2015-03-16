package com.gose.asyncTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Yuwanit on 2/26/2015.
 */
public class ImageLoadTaskWithProgressbar extends AsyncTask<Void, Void, Bitmap> {

    private String url;
    private ImageView imageView;
    private ProgressBar progressBar;

    public ImageLoadTaskWithProgressbar() {}

    public ImageLoadTaskWithProgressbar(String url, ImageView imageView, ProgressBar progressBar) {
        this.url = url;
        this.imageView = imageView;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        imageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlConnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        imageView.setImageBitmap(result);
        progressBar.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);

    }
}
