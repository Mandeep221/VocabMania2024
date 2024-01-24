package com.msarangal.vocabmania;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by Mandeep on 30/6/2015.
 */
public class LearnMoreFav extends AppCompatActivity {

    WebView webView;
    Toolbar toolbar;
    TextView tvTitle;
    View v;
    private LinearLayout appbar_container;
    int exit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.learnmorefav);

        setProgressBarIndeterminateVisibility(true);
        setProgressBarVisibility(true);


        appbar_container = (LinearLayout) findViewById(R.id.app_bar);
        toolbar = (Toolbar) appbar_container.findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        webView = (WebView) findViewById(R.id.webView);
        webView.goBack();
        webView.goForward();
        webView.setWebViewClient(new MyWebView());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        Intent i = getIntent();
        String url = i.getStringExtra("link");
        final boolean isPrivacy = i.getBooleanExtra("isPrivacy", false);
        webView.setWebViewClient(new MyWebView() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                setProgress(newProgress * 100);
                if (newProgress < 100) {
                    SpannableString s = new SpannableString("Loading...");
                    s.setSpan(new TypefaceSpan(LearnMoreFav.this, "Roboto-Medium.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    // Update the action bar title with the TypefaceSpan instance
                    getSupportActionBar().setTitle(s);
                }
                if (newProgress == 100) {
                    setProgressBarIndeterminateVisibility(false);
                    setProgressBarVisibility(false);
                    SpannableString s = new SpannableString(isPrivacy? "Privacy policy" : "Learn more");
                    s.setSpan(new TypefaceSpan(LearnMoreFav.this, "Nunito-Light.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    // Update the action bar title with the TypefaceSpan instance
                    getSupportActionBar().setTitle(s);
                }
            }


        });
        webView.loadUrl(url);

    }

    private class MyWebView extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.card_slide_left_in, R.anim.card_slide_right_out);
    }
}
