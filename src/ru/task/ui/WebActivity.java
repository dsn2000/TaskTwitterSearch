package ru.task.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import ru.task.R;
import ru.task.utils.Message;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 30.11.12
 * Time: 13:39
 * To change this template use File | Settings | File Templates.
 */
public class WebActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);

        Intent intent = getIntent();
        String url = intent.getStringExtra(Message.EXTRA_MESSAGE_URL);

        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(url);
    }
}
