package com.gashfara.akitk.bether;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by akitk on 2016/04/10.
 * 2016/04/24 これは使わない方針となった。
 */
public class WebQuestActivity extends AppCompatActivity {

    TextView textView = null;
    Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_webquest);

        WebView webView = (WebView)findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        String url = "http://bether.sakura.ne.jp/gs/json.txt";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                response.toString();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });


        webView.addJavascriptInterface(new Object() {
            public void pushLink() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("Bye");
                    }
                });
            }
        }, "app");


        webView.loadUrl("file:///android_asset/questionnair.html");
    }
}
