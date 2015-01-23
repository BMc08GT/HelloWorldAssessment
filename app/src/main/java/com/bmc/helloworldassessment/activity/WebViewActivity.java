package com.bmc.helloworldassessment.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.bmc.helloworldassessment.BaseActivity;
import com.bmc.helloworldassessment.R;

public class WebViewActivity extends BaseActivity {

    private static final String TAG = WebViewActivity.class.getSimpleName();

    public static final String EXTRA_URL = "webview_url";
    public static final String EXTRA_TITLE = "oss_title";
    public static final String EXTRA_RUN_JS = "extra_run_js";
    public static final String EXTRA_JS_PARAM = "extra_js_param";
    public static final String EXTRA_LOG_CONSOLE = "extra_log_console";

    private WebView webView;
    private ProgressBar progressBar;

    private boolean mRunJavascriptOnPageFinish;
    private boolean mShowConsoleLog;
    private String mJavascriptParam;

    @Override
    public int getLayoutResource() {
        return R.layout.webview;
    }

    @Override
    public String getTitleResource() {
        return getIntent().getStringExtra(EXTRA_TITLE);
    }

    @Override
    public boolean setHomeAsUpEnabled() {
        return true;
    }

    @SuppressLint("SetJavascriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        mRunJavascriptOnPageFinish = intent.getBooleanExtra(EXTRA_RUN_JS, false);
        mJavascriptParam = intent.getStringExtra(EXTRA_JS_PARAM);
        mShowConsoleLog = intent.getBooleanExtra(EXTRA_LOG_CONSOLE, false);

        setupWebview(intent);

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
    }

    private void setupWebview(Intent intent) {
        String url = intent.getStringExtra(EXTRA_URL);
        webView = (WebView) findViewById(R.id.webView1);
        webView.setWebViewClient(new WebClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                setProgress(progress * 100);
            }

            @Override
            public boolean onConsoleMessage(@NonNull ConsoleMessage cm) {
                if (mShowConsoleLog) {
                    Log.d(TAG, cm.message() + " -- From line "
                            + cm.lineNumber() + " of "
                            + cm.sourceId());
                }
                return true;
            }
        });
        webView.loadUrl(url);
    }

    private class WebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            if (mRunJavascriptOnPageFinish){
                webView.loadUrl("javascript:init(" + mJavascriptParam + ")");
            }
        }
    }
}
