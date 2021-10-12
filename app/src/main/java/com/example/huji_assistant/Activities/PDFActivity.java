package com.example.huji_assistant.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.huji_assistant.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PDFActivity extends AppCompatActivity {

    WebView webView;
    Activity activity ;
    private ProgressDialog progDailog;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        // Set layout in 'rtl' direction
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        activity = this;

        // Show ProgressDialog when the image is loading
        progDailog = ProgressDialog.show(activity, getString(R.string.loading),getString(R.string.please_wait_message), true);
        progDailog.setCancelable(false);

        webView = findViewById(R.id.webview);

        // Get the image's path
        Intent i = this.getIntent();
        String path = i.getExtras().getString("PATH");

        // Set webView settings
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progDailog.show();
                view.loadUrl(url);

                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                progDailog.dismiss();
            }
        });

        String url = "";
        try {
            // Convert the Firestore url to 'UTF-8' url
            url= URLEncoder.encode(path,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // Show the image in the webView
        webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + url);
    }
}