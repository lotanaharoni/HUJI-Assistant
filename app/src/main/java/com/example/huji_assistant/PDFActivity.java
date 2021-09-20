package com.example.huji_assistant;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PDFActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        webView = findViewById(R.id.webview);

        Intent i=this.getIntent();
        String path=i.getExtras().getString("PATH");

        webView.getSettings().setJavaScriptEnabled(true);
        String url = "";
        try {
            url= URLEncoder.encode(path,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + url);
    }
}