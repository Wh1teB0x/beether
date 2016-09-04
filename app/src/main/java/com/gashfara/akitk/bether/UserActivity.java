package com.gashfara.akitk.bether;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akitk on 2016/05/01.
 */
public class UserActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private String token;

    private EditText mPasswordField;

    ProgressDialog pd = null;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
        token = pref.getString(getString(R.string.save_token),"");
        CreateMyView(savedInstance);
    }

    public void CreateMyView(Bundle savedInstance){
        setContentView(R.layout.activity_user);
        mPasswordField = (EditText) findViewById(R.id.password_field);
        mPasswordField.setTransformationMethod(new PasswordTransformationMethod());
        mPasswordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        Button signupBtn = (Button) findViewById(R.id.signup_button);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignupButtonClicked(v);
            }
        });
    }

    //登録処理
    public void onSignupButtonClicked(View v) {
        //IMEを閉じる
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        //入力文字を得る
        String password = mPasswordField.getText().toString();

        // ここに、ユーザー登録の処理を書く。
        final RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://160.16.204.8:8080/?adduser=" + password;//2016May14th sakura vps
        //URLアクセス、JSONを受け取って処理する。

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String jsonMessage = response.getString("wallet");
                    Log.d("wallet", "Success");
                    pref.edit().putString(getString(R.string.save_token), jsonMessage).apply();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                } catch (JSONException e){
                    e.printStackTrace();
                    if (pd != null) {
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                        pd = null;
                    }
                    Toast.makeText(getApplicationContext(), "Unable to parse data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
                @Override
                    public void onErrorResponse(VolleyError error) {
                        if (pd != null) {
                            if (pd.isShowing()) {
                                pd.dismiss();
                            }
                            pd = null;
                        }
                        Toast.makeText(getApplicationContext(), "Unable to fetch data: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                );

        int MY_SOCKET_TIMEOUT_MS = 20000;
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsObjRequest);
        pd = new ProgressDialog(UserActivity.this);
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.show();

    }
}

