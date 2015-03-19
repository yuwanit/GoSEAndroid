package com.gose;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


public class WebsiteActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);

        String url = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("url");
        }
        bundle.clear();

        WebView wv = (WebView) findViewById(R.id.webView);
        wv.loadUrl(url);
    }

}
