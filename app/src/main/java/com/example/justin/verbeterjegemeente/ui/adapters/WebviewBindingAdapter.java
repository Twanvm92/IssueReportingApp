package com.example.justin.verbeterjegemeente.ui.adapters;

import android.annotation.SuppressLint;
import android.databinding.BindingAdapter;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;

import com.example.justin.verbeterjegemeente.ui.BredaMapInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by twanv on 28-1-2018.
 */

public class WebviewBindingAdapter {
    @SuppressLint("AddJavascriptInterface")
    @BindingAdapter("webviewClient")
    public static void bindJavascriptInterface1(WebView webview, WebViewClient webViewClient) {


        if (webViewClient != null) {
            webview.setWebViewClient(webViewClient);
        }

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setAllowFileAccessFromFileURLs(true);
        webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAllowFileAccess( true );
        webview.setScrollbarFadingEnabled(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);

    }

    @BindingAdapter("javascriptInterface")
    public static void bindJavascriptInterface(WebView webview, BredaMapInterface bredaMapInterface) {

        if (bredaMapInterface != null) {// only load map URL if Javascript Interface was initialized
            webview.addJavascriptInterface(bredaMapInterface, "Android");
            loadMapUrl(webview);
        }

    }

    private static void loadMapUrl(WebView webView) {
        StringBuilder buf = new StringBuilder();
        InputStream json;
        try {
            json = webView.getContext().getAssets().open("html/bredaKaart.html");

            BufferedReader in;

            in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str=in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        webView.loadDataWithBaseURL("file:///android_asset/", buf.toString(), "text/html", "utf-8", null);
    }
}
