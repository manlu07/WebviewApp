package com.example.webview_s;

import android.app.DownloadManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webview_s.R;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        setupWebView();

        // Adding the download button click listener
        Button downloadButton = findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Calling a method to initiate the WebView APK download
                downloadWebViewApk();
            }
        });

        // Fetch WebView version and SHA-1 fingerprint and display in the WebView
        String webViewInfo = getWebViewInfo();
        webView.loadData(webViewInfo, "text/html", "UTF-8");
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    private String getWebViewInfo() {
        PackageManager pm = getPackageManager();

        // Checking for com.android.webview
        try {
            ApplicationInfo androidWebViewInfo = pm.getApplicationInfo("com.android.webview", 0);
            String androidWebViewVersion = pm.getPackageInfo(androidWebViewInfo.packageName, 0).versionName;
            String androidWebViewSHA1 = getSHA1(androidWebViewInfo.packageName);

            return "Android WebView version: " + androidWebViewVersion + "\n" + "Packagename : com.google.android.com" + "\n" + "SHA1: " + androidWebViewSHA1;
        } catch (PackageManager.NameNotFoundException e) {
        }

        // Checking for com.google.android.webview
        try {
            ApplicationInfo googleWebViewInfo = pm.getApplicationInfo("com.google.android.webview", 0);
            String googleWebViewVersion = pm.getPackageInfo(googleWebViewInfo.packageName, 0).versionName;
            String googleWebViewSHA1 = getSHA1(googleWebViewInfo.packageName);

            return "Google WebView version: " + googleWebViewVersion + "\n" + "Packagename : com.google.android.com" + "\n " + "SHA1: " + googleWebViewSHA1;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return "WebView information not available";
    }

    private void downloadWebViewApk() {
        // Creating the download link from APKmirror
        String apkMirrorUrlTemplate = "https://www.apkmirror.com/apk/google-inc/android-system-webview/android-system-webview-%s-release/android-system-webview-%s-android-apk-download/download/";

        // Getting the WebView version
        String webViewVersion = getWebViewVersion();

        if (webViewVersion != null) {
            String apkDownloadUrl = String.format(apkMirrorUrlTemplate, webViewVersion, webViewVersion);

            // Downloading the WebView APK
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkDownloadUrl));
            request.setTitle("WebView APK Download");
            request.setDescription("Downloading WebView APK");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS, "webview.apk");

            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);

            Toast.makeText(MainActivity.this, "Downloading WebView APK", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Failed to get WebView version", Toast.LENGTH_SHORT).show();
        }
    }

    private String getWebViewVersion() {
        PackageManager pm = getPackageManager();

        // Checking for com.android.webview
        try {
            ApplicationInfo androidWebViewInfo = pm.getApplicationInfo("com.android.webview", 0);
            return pm.getPackageInfo(androidWebViewInfo.packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }

        // Checking for com.google.android.webview
        try {
            ApplicationInfo googleWebViewInfo = pm.getApplicationInfo("com.google.android.webview", 0);
            return pm.getPackageInfo(googleWebViewInfo.packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return null;
    }

    // Utility class for calculating SHA-1
    private String getSHA1(String packageName) {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

            android.content.pm.Signature[] signatures = packageInfo.signatures;
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            for (Signature signature : signatures) {
                byte[] signatureBytes = signature.toByteArray();
                md.update(signatureBytes);
            }

            byte[] digestBytes = md.digest();
            StringBuilder sb = new StringBuilder();

            for (byte digestByte : digestBytes) {
                sb.append(Integer.toString((digestByte & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {

            e.printStackTrace();
        }

        return "SHA1 not available";
    }
}
