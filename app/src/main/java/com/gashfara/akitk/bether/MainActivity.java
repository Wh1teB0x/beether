package com.gashfara.akitk.bether;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.github.orangegangsters.lollipin.lib.PinCompatActivity;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class MainActivity extends PinCompatActivity {
    private BeaconManager beaconManager;
    private Region region;
    private static final Map<String, List<String>> PLACES_BY_BEACONS;

    private DrawerLayout mDrawerLayout;//code path ではmDrawerになっていたけどこっちの方がわかりやすいし。
    private Toolbar toolbar;
    private NavigationView nvDrawer; //code path ではなかったけど、エラーが起こったので追加した。

    private View mDrawerView;
    private ActionBarDrawerToggle mDrawerToggle;

    private String[] mNavigationTitles;

    private GoogleApiClient client;
    private SharedPreferences pref;
    private String token;

    private static final int REQUEST_CODE_ENABLE = 11;//PinCodeプログラム用のstatic変数

    static SQLiteDatabase db;   //SQLite用
    ArrayAdapter<String> bunnyAdapter;
    ArrayList<Bunny> bunnyArray;
    ArrayList<String> bunnyNameArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
        token = pref.getString(getString(R.string.save_token), "");

        pref.edit().putBoolean("locked", true).commit();

        if(token == ""){
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
            startActivity(intent);

            finish();
        }

        //この下記3行をどこに置くかは非常に重要である。PinCodeのプログラム。
       // Intent intent = new Intent(getApplicationContext(), CustomPinActivity.class);
       // intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
       // startActivityForResult(intent, REQUEST_CODE_ENABLE);



        Fragment fragment = null;
        Class fragmentClass;
        fragmentClass = FirstFragment.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.flContent, fragment).commit();
        }

        //Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Find out drawer view
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);

        //v7 では3番目の引数にtoolbarを置くらしい
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerOpened(drawerView);
                // onPrepareOptionmenu()が呼ばれるようにする
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // onPrepareOptionsMenu()が呼ばれるようにする
                invalidateOptionsMenu();
            }
        };



        //NavigationHeaderにWallet
        View header = nvDrawer.getHeaderView(0);
        TextView wallet = (TextView) header.findViewById(R.id.header_wallet);
        wallet.setText(token);
        wallet.setTextSize(10);




        //Estimote Beacon Ranging code
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    List<String> places = placesNearBeacon(nearestBeacon); //List<String> placesNearBeacon
                    // TODO: update the UI here
                    //Dialog fragment managerの設定
//                    FragmentManager manager = getSupportFragmentManager();
  //                  MainFragmentDialog dialog = new MainFragmentDialog();
    //                dialog.show(manager, "dialog");
                    beaconManager.disconnect();
                }
            }
        });
        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    //Estimote Beacon ranging beacon
    //Beacon と そのBeaconが保有する要素をArrayListで渡している
    static {
        Map<String, List<String>> placesByBeacons = new HashMap<>();
        placesByBeacons.put("9340:7591", new ArrayList<String>() {{
            add("Heavenly Sandwiches");//closest
            add("Green & Green Salads");//nextclosest
            add("Mini Panini");//the further away
        }});
        placesByBeacons.put("61473:9788", new ArrayList<String>() {{
            add("Mini Panini");
            add("Green & Green Salads");
            add("Heavenly Sandwiches");
        }});
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }

    private List<String> placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (PLACES_BY_BEACONS.containsKey(beaconKey)) {
            return PLACES_BY_BEACONS.get(beaconKey);
        }
        return Collections.emptyList();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    selectDrawerItem(menuItem);
                    return true;
                }
            });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragmentClass = FirstFragment.class;
                break;
            case R.id.nav_remit_fragment:
                fragmentClass = RemitFragment.class;
                break;
            case R.id.nav_wallet_fragment:
                fragmentClass = WalletFragment.class;
                break;
            case R.id.nav_history_fragment:
                fragmentClass = HistoryFragment.class;
                break;
            case R.id.nav_about_fragment:
                fragmentClass = AboutFragment.class;
                break;
            default:
                fragmentClass = FirstFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager(); //
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by navigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawerLayout.closeDrawers();
    }


    @Override
    public void onStop() {
        super.onStop();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.gashfara.akitk.bether/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /*** DialogFragmentの設定ここから ***/
    //MainFragmentDialogの設定
    public static class MainFragmentDialog extends DialogFragment {
        private MainActivity mActivity;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            //MainActivity 以外に所属する場合、Exception スローを出して前に進ませない
            if (activity instanceof MainActivity == false) {
                throw new UnsupportedOperationException("MainActivity以外からコールされている");
            }
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mActivity = null;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Ether受取申請");
            builder.setMessage("申請しますか？");
            builder.setPositiveButton("はい",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity activity = (MainActivity) getActivity();
                            activity.doPositiveClick();
                        }
                    });
            builder.setNegativeButton("いいえ",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity activity = (MainActivity) getActivity();
                            activity.doNegativeClick();
                        }
                    });

            return builder.create();
        }
    }

    public void doPositiveClick() {
        Toast.makeText(this, "OKボタンがクリックされました", Toast.LENGTH_SHORT).show();

        final TextView mTextView = (TextView) findViewById(R.id.text1);
        final RequestQueue queue = Volley.newRequestQueue(this);

        // Ether get するために、URLにアクセス。
        String url = "http://192.168.11.22:8080/?ether=10";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mTextView.setText("Response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(jsObjRequest);
    }

    public void doNegativeClick() {
        Toast.makeText(this, "Cancelボタンがクリックされました", Toast.LENGTH_SHORT).show();
        final Intent intent = new Intent(this, WebQuestActivity.class);
        startActivity(intent);
    }
    /*** DialogFragmentの設定ここまで ***/



    /*** Navigation Drawer の設定ここから ***/
    // onPostCreate called when activity start-up is complete after onStart()
    // Note! Make sure to override the method with only a single Bundle argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        // sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        // pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    //後編
    private void setupNavDrawerList(){
        mNavigationTitles = getResources().getStringArray(R.array.drawer_items);
        ListView listview = (ListView) mDrawerView;

        listview.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mNavigationTitles));

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
    }

    private void selectItem(int position) {

//        Fragment fragment = MainFragmentDialog.(position);
//        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        mDrawerLayout.closeDrawer(mDrawerView);

    }


    /*** Navigation Drawer の設定ここまで ***/

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.gashfara.akitk.bether/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    //Estimote Beacon用
    @Override
    protected void onResume() {
        super.onResume();
        // Location Permissionを追加するために必要らしい・・・Estimote SDK Android Part1に載っていた。
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }


    //メニュー用。まだ触っていない
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //pin code のためのOverride
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_ENABLE:
                Toast.makeText(this, "PinCode enabled", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}

