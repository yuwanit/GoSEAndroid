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
        String title = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("url");
            title = bundle.getString("title");
        }
        bundle.clear();

        getActionBar().setTitle(title+" Website");

        WebView wv = (WebView) findViewById(R.id.webView);
        wv.loadUrl(url);
    }

}
