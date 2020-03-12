package com.nature.thenature;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private WebView webView;
    private ImageView mIconBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        mIconBack = findViewById(R.id.iv_toolbar_start);
        mIconBack.setImageResource(R.drawable.ic_left_arrow);
        mIconBack.setVisibility(View.VISIBLE);
        mIconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PrivacyPolicyActivity.this,AboutAcivity.class));
                finish();
            }
        });
        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/privacy_policy.html");
    }
}
