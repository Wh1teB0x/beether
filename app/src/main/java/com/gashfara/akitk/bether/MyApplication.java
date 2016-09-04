package com.gashfara.akitk.bether;

/**
 * Created by akitk on 2016/03/21.
 */

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MyApplication extends Application {

    private static MyApplication sInstance;
    private RequestQueue mRequestQueue;
    private List<UuidRecord> uuidRecords = new ArrayList<UuidRecord>();

    // beacon moniteringをするために、beaconManagerを使用する the gateway to interactions with Estimote Beacons.
    // Application クラスの中で宣言して使用する必要があるらしい。
    private BeaconManager beaconManager;

    //SharedPref でwallet番号取得
    private SharedPreferences pref;
    private String token;
    private boolean locked = true;

    @Override
    public void onCreate() {
        super.onCreate();

        pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
        token = pref.getString(getString(R.string.save_token),"");
        Log.d("token", token);



        //volleyの通信用のクラスを設定。
        mRequestQueue = Volley.newRequestQueue(this);
        sInstance = this;

        beaconManager = new BeaconManager(getApplicationContext());

        //final List<UuidRecord> uuidRecords = new ArrayList<UuidRecord>();

        //UUIDリストをサーバーから取ってくる。
        String url = "http://www.bether.sakura.ne.jp/uuid_json_v3.txt";
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            uuidRecords = parseUUID(response);
                            //Log.d("checkpoint2", uuidRecords.get(1).getProximity());
                            //get UUID and set it in beaconManager.
                            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                                @Override
                                public void onServiceReady(){
                                    try {
                                        for(int i=0; i<uuidRecords.size(); i++) {
                                            beaconManager.startMonitoring(new Region(
                                                    "region" + i,
                                                    UUID.fromString(uuidRecords.get(i).getProximity()),
                                                    Integer.parseInt(uuidRecords.get(i).getMajor()),
                                                    Integer.parseInt(uuidRecords.get(i).getMinor())));
                                        }
                                        System.out.println("BeaconManger Connected!");
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Unable to fetch data: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        int MY_SOCKET_TIMEOUT_MS = 20000;
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        queue.add(jsObjRequest);




        //beaconManager
        /***
        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady(){
                try {
                    beaconManager.startMonitoring(new Region(
                            "region1",
                            UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 9340, 7591));
                    beaconManager.startMonitoring(new Region(
                            "region2",
                            UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 61473, 9788));
                    beaconManager.startMonitoring(new Region(
                            "region3",
                            UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 48800, 9241));
                    System.out.println("BeaconManger Connected!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
         ***/

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                //どのbeaconを呼び出したか、UUIDを取得。そのUUIDと対応するURLをuuidRecordsから取ってくる。
                //Bundleを介してQuestionnaireActivityに渡す。
                final String receivedUUID = list.get(0).getProximityUUID().toString();

                for(int i=0; i<uuidRecords.size(); i++){
                    Log.d("uuidMinor",uuidRecords.get(i).getProximity());
                    Log.d("listMinor",list.get(0).getProximityUUID().toString().toUpperCase());
                    if (list.get(0).getProximityUUID().toString().toUpperCase().equals(uuidRecords.get(i).getProximity())
                            && uuidRecords.get(i).getMajor().equals(String.valueOf(list.get(0).getMajor()))
                            && uuidRecords.get(i).getMinor().equals(String.valueOf(list.get(0).getMinor()))) {
                        String url = uuidRecords.get(i).getUrl();


                        Intent intent = new Intent(getApplicationContext(), QuestionnaireActivity_Recycle.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("URL", url);
                        bundle.putString("token", token);

                        intent.putExtras(bundle);

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                        if(pref.getBoolean("locked", locked)){
                            pref.edit().putBoolean("locked", false).commit();
                            startActivity(intent);
                        }

//                        showNotification("Beacon Area", "Do you want to open the questionnaire page?");

                        }
                }
            }

            @Override
            public void onExitedRegion(Region region) {
                // could add an "exit" notification too if you want (-:
                System.out.println("Exited Called!!! "+ region.getProximityUUID() + " Minor : "+ region.getMinor());
                //showNotification("SmartCall","You exited!");
            }
        });

    }

    public synchronized static MyApplication getInstance() {
        return sInstance;
    }
    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }



    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }



    public List<UuidRecord> parseUUID(JSONObject json) throws JSONException {
        ArrayList<UuidRecord> records = new ArrayList<UuidRecord>();
        JSONArray jsonMessages = json.getJSONArray("UUID");

        for (int i = 0; i < jsonMessages.length(); i++) {
            JSONObject jsonMessage = jsonMessages.getJSONObject(i);
            String Proximity = jsonMessage.getString("Proximity");
            //Log.d("Proximity", Proximity);
            String Major = jsonMessage.getString("Major");
            //Log.d("Major", Major);
            String Minor = jsonMessage.getString("Minor");
            //Log.d("Minor", Minor);
            String Url = jsonMessage.getString("Url");
            //Log.d("Url", Url);
            UuidRecord record = new UuidRecord(Proximity, Major, Minor, Url);
            records.add(record);
        }
        //Log.d("checkpoint3",records.get(1).getMinor());
        return records;
    }



}