package com.gashfara.akitk.bether;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * 2016/04/24 これは使わない方針となった
 */
public class QuestionnaireFragment extends WebViewFragment {

    TextView textView = null;
    Handler handler = new Handler();
    WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView.findViewById(R.id.web_view);
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


    public QuestionnaireFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_questionnaire, container, false);
    }





}
