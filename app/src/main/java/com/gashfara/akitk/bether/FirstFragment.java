package com.gashfara.akitk.bether;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class FirstFragment extends Fragment implements OnClickListener {

    CardView mCardView;
    Button myButton;

    private SwipeRefreshLayout swipeContainer;

    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance){
        super.onViewCreated(view, savedInstance);
        mCardView = (CardView) view.findViewById(R.id.card_view_first);

//        myButton = (Button) view.findViewById(R.id.button);
//        myButton.setOnClickListener(this);

        final TextView mTextView = (TextView) view.findViewById(R.id.balance_info);
        mTextView.setText("loading...");
        final TextView accountTextView = (TextView) view.findViewById(R.id.account_info);


        SharedPreferences pref = this.getActivity().getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
        final String token = pref.getString(getString(R.string.save_token), "");
        final String url = "http://160.16.204.8:8080/?account=" + token; // sakura vps
        accountTextView.setText(token);



        final RequestQueue queue = Volley.newRequestQueue(getContext());// thisからgetContext()に変えた

        //swipe to refresh
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTextView.setText("loading...");
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String jsonMessage = response.getString("balance");
                                    mTextView.setText(jsonMessage);
                                    Log.d("balance", "Success");
                                    swipeContainer.setRefreshing(false);
                                } catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                queue.add(jsObjRequest);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);






        //URLアクセス、JSONを受け取って処理する。
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String jsonMessage = response.getString("balance");
                            mTextView.setText(jsonMessage);
                            Log.d("balance", "Success");
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue.add(jsObjRequest);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {// 複数のボタンを登録する際は、getIdでviewのIDを取り出し、switchで分岐させると良い
/***            case R.id.button:
                Intent intent = new Intent(getActivity(), QuestionnaireActivity.class);
                startActivity(intent);
                break;
 ***/
        }
    }
}
