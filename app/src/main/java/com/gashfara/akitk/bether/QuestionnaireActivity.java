package com.gashfara.akitk.bether;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by akitk on 2016/04/02.
 * QuestionnaireActivityを呼び出すときに、BundleでUUIDの情報を渡して、fetchを行うときにUUIDの情報と紐付いたURLを開くようにする。
 */
public class QuestionnaireActivity extends AppCompatActivity {

    private QuestionnaireRecordAdapter qAdapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;

    ArrayList<QuestionnaireRecord> questionnaireRecords = new ArrayList<QuestionnaireRecord>();


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);


        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Title");

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        final String token = bundle.getString("token");

        final ArrayList<QuestionnaireRecord> questionnaireRecords = new ArrayList<QuestionnaireRecord>();
        qAdapter = new QuestionnaireRecordAdapter(this, questionnaireRecords);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(qAdapter);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView.setNestedScrollingEnabled(true);
        }



        //下記はsubmitボタンを押したときの処理を書く。
        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final Map<String, String> jsonParam = new HashMap<String, String>();
                JSONArray jAnswers = new JSONArray();
                    // {"id":[{"qNum":"1","qAns":"4"},{"qNum":"2","qAns":"3"}]}
                    try{
                        for(int i = 0; i<questionnaireRecords.size(); i++){
                            JSONObject list1 = new JSONObject();
                            list1.put("qNum", String.valueOf(i+1));
                            list1.put("qAns", questionnaireRecords.get(i).toString());
                            list1.put("token", token);
                            jAnswers.put(list1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                jsonParam.put("UUID",jAnswers.toString());
                String url = "http://160.16.204.8:8080/"; // sakura vps

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, jsonParam,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );

                jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*1,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                requestQueue.add(jsObjRequest);

                //POSTの結果を受け取ってからIntent移動処理を行うべき。
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        fetch();

    }


    private void fetch() {
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        String url = bundle.getString("URL");
        JsonObjectRequest request = new JsonObjectRequest(
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            List<QuestionnaireRecord> questionnaireRecords = parse(jsonObject);//parseは下で定義
                            qAdapter.setMessageRecords(questionnaireRecords);// setMessageRecordsはQuestionnaireRecordAdapterの中で定義
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Unable to parse data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        MyApplication.getInstance().getRequestQueue().add(request);
    }

    private List<QuestionnaireRecord> parse(JSONObject json) throws JSONException {
        ArrayList<QuestionnaireRecord> records = new ArrayList<QuestionnaireRecord>();
        JSONArray jsonMessages = json.getJSONArray("messages");
        for (int i = 0; i < jsonMessages.length(); i++) {
            JSONObject jsonMessage = jsonMessages.getJSONObject(i);
            String number = jsonMessage.getString("question");//questionは質問番号のこと
            String quest = jsonMessage.getString("comment");//commentは質問内容のこと
            QuestionnaireRecord record = new QuestionnaireRecord(number, quest);
            records.add(record);
        }
        return records;
    }




    @Override
    public void onStart() {
        super.onStart();
/***
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Questionnaire Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.gashfara.akitk.bether/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
 ***/
    }

    @Override
    public void onStop() {
        super.onStop();
/***
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Questionnaire Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.gashfara.akitk.bether/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
 *///
    }
}




