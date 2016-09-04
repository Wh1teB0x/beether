package com.gashfara.akitk.bether;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
public class QuestionnaireActivity_Recycle extends AppCompatActivity {

    private QuestionnaireRecordAdapter_Recycle qAdapter;
    private ArrayList<QuestionnaireRecord_Recycle> questionnaireRecords_recycle = new ArrayList<QuestionnaireRecord_Recycle>();
    private RecyclerView rvLists;

    private SharedPreferences pref;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_recycle);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_quest);
        collapsingToolbar.setTitle("評価ページ");//ここに

        rvLists = (RecyclerView) findViewById(R.id.rvLists);
        rvLists.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvLists.setLayoutManager(llm);

        //UserのWalletIDを渡している
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        final String token = bundle.getString("token");


        //
        pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
        pref.edit().putBoolean("locked", true).commit();

        fetch();

//        ImageView imageView = (ImageView) findViewById(R.id.backdrop);
//        imageView.setImageResource(R.drawable.blockchain);
        //palette使ってみた。結局色を取り出すのには便利だけど、デザインは自分でやらないといけないことがわかった。
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.eth76cob);
//        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
//            @Override
//            public void onGenerated(Palette palette) {
//                TextView mVibrantTextView = (TextView) findViewById(R.id.q_detail);
//                setViewSwatch( mVibrantTextView, palette.getVibrantSwatch() );
//            }
//        });



        //下記はsubmitボタンを押したときの処理を書く。
        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Map<String, String> jsonParam = new HashMap<String, String>();
                JSONArray jAnswers = new JSONArray();
                // {"id":[{"qNum":"1","qAns":"4"},{"qNum":"2","qAns":"3"}]}
                try {
                    for (int i = 0; i < questionnaireRecords_recycle.size(); i++) {
                        JSONObject list1 = new JSONObject();
                        list1.put("qNum", String.valueOf(i + 1));
                        list1.put("qAns", questionnaireRecords_recycle.get(i).toString());
                        list1.put("token", token);
                        jAnswers.put(list1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonParam.put("UUID", jAnswers.toString());
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

                jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 1,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                requestQueue.add(jsObjRequest);

                //POSTの結果を受け取ってからIntent移動処理を行うべき。
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                pref.edit().putBoolean("locked", true).commit();

                finish();


            }
        });



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
                            List<QuestionnaireRecord_Recycle> questionnaireRecords = parse(jsonObject);
                            questionnaireRecords_recycle = (ArrayList) questionnaireRecords;
                            qAdapter = new QuestionnaireRecordAdapter_Recycle(questionnaireRecords_recycle);
                            rvLists.setAdapter(qAdapter);
                            qAdapter.notifyDataSetChanged();

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

    private List<QuestionnaireRecord_Recycle> parse(JSONObject json) throws JSONException {
        ArrayList<QuestionnaireRecord_Recycle> records = new ArrayList<QuestionnaireRecord_Recycle>();
        JSONArray jsonMessages = json.getJSONArray("messages");
        for (int i = 0; i < jsonMessages.length(); i++) {
            JSONObject jsonMessage = jsonMessages.getJSONObject(i);
            String number = jsonMessage.getString("question");//questionは質問番号のこと
            String quest = jsonMessage.getString("comment");//commentは質問内容のこと
            QuestionnaireRecord_Recycle record = new QuestionnaireRecord_Recycle(number, quest);
            records.add(record);
        }
        return records;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }




    public void setViewSwatch( TextView view, Palette.Swatch swatch ) {
        if( swatch != null ) {
            view.setTextColor( swatch.getTitleTextColor() );
            view.setBackgroundColor( swatch.getRgb() );
            view.setVisibility( View.VISIBLE );
        } else {
            view.setVisibility( View.GONE );
        }
    }
}




