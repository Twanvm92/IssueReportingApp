package com.example.justin.verbeterjegemeente.ui.adapters;

import android.annotation.SuppressLint;
import android.databinding.BindingAdapter;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;

import com.example.justin.verbeterjegemeente.ui.BredaMapInterface;

/**
 * Created by twanv on 28-1-2018.
 */

public class WebviewBindingAdapter {
    @SuppressLint("AddJavascriptInterface")
    @BindingAdapter({"javascriptInterface", "webviewClient"})
    public static void bindJavascriptInterface(WebView webview, BredaMapInterface bredaMapInterface,
                                               WebViewClient webViewClient) {

        if (bredaMapInterface != null) {
            webview.addJavascriptInterface(bredaMapInterface, "Android");
        }
        if (webViewClient != null) {
            webview.setWebViewClient(webViewClient);
        }

        webview.setVerticalScrollBarEnabled(false);
        webview.setHorizontalScrollBarEnabled(false);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setAllowFileAccessFromFileURLs(true);
        webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAllowFileAccess( true );
    }

    @BindingAdapter({"loadUrl"})
    public static void bindJavascriptInterface(WebView webview, String url) {

        if (url != null && !url.equals("")) {
            webview.loadUrl(url);
        }
    }
}
