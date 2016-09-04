package com.gashfara.akitk.bether;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.List;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


/**
 * A simple {@link Fragment} subclass.
 */
public class WalletFragment extends Fragment {

    static SQLiteDatabase db;
    EditText etBunnyName;
    Button btnAdd;
    ListView lvBunnies;
    ArrayAdapter<String> bunnyAdapter;
    ArrayList<Bunny> bunnyArray;
    ArrayList<String> bunnyNameArray;


    private SharedPreferences pref;
    private String token;

    ProgressDialog pd = null;


    public static WalletFragment newInstance() {
        WalletFragment fragment = new WalletFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    public WalletFragment(){
        // singleton
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wallet, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //SQLite
        //init views
        etBunnyName = (EditText) view.findViewById(R.id.edit_text);
        btnAdd = (Button) view.findViewById(R.id.button2);
        lvBunnies = (ListView) view.findViewById(R.id.listView);

        // setup database
        PracticeDatabaseHelper dbHelper = new PracticeDatabaseHelper(getContext());
        // dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1, 2);
        db = dbHelper.getWritableDatabase();

        // here is where you associate the name array.
        bunnyNameArray = getAllBunniesNames();

        bunnyAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, bunnyNameArray) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);

                text1.setText("Wallet: " + bunnyArray.get(position).getName());
                return view;
            }
        };


        //Active Walletの表示設定
        pref = getContext().getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
        token = pref.getString(getString(R.string.save_token), "");
        TextView activeWallet = (TextView) view.findViewById(R.id.active_wallet);
        activeWallet.setText(token);



        // clicking this button adds a new bunny with your chosen name to the database
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = etBunnyName.getText().toString();
                //ここにvolleyでウィレット作成処理を書く。
                final String[] s = new String[1];
                // ここに、ユーザー登録の処理を書く。
                final RequestQueue queue = Volley.newRequestQueue(getContext());
                String url = "http://160.16.204.8:8080/?adduser=" + password;//2016May14th sakura vps
                //URLアクセス、JSONを受け取って処理する。
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String jsonMessage = response.getString("wallet");
                                    s[0] = jsonMessage;

                                    if (!s[0].isEmpty()) {
                                        Bunny b = new Bunny(s[0]);
                                        cupboard().withDatabase(db).put(b);
                                        bunnyArray.add(b);
                                        bunnyAdapter.add(b.getName());
                                        bunnyAdapter.notifyDataSetChanged();
                                        // empty the edit text
                                        etBunnyName.setText("");
                                        pd.dismiss();

                                    } else {
                                        Toast.makeText(getContext(), "Please input password of new wallet", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e){
                                    e.printStackTrace();
                                    if (pd != null) {
                                        if (pd.isShowing()) {
                                            pd.dismiss();
                                        }
                                        pd = null;
                                    }
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
                            }
                        });

                int MY_SOCKET_TIMEOUT_MS = 20000;
                jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                        MY_SOCKET_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(jsObjRequest);
                pd = new ProgressDialog(getContext());
                pd.setTitle("Loading...");
                pd.setMessage("Please wait.");
                pd.setCancelable(false);
                pd.show();

            }
        });

        lvBunnies.setAdapter(bunnyAdapter);

        // long clicking on a list item will remove the bunny from the list AND from the db
        lvBunnies.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {

                Bunny b = bunnyArray.get(pos);
                cupboard().withDatabase(db).delete(Bunny.class, b.get_id());
                bunnyArray.remove(pos);
                bunnyNameArray.remove(pos);
                bunnyAdapter.notifyDataSetChanged();

                return false;
            }
        });

        //clicking on the bunny updates their cuteness value to VERYCUTE and persists that change
        lvBunnies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // as an example of updating a value, and persisting it back to the db
                Bunny b = bunnyArray.get(pos);
                String temp = b.getName();
                b.setName(token);
                token = temp;

                b.setCuteValue(85);
                b.setCutenessTypeEnum(Bunny.cutenessType.VERYCUTE);

                cupboard().withDatabase(db).put(b);
                bunnyAdapter.notifyDataSetChanged();

            }
        });
    }



    /* Private Methods */
    private static List<Bunny> getListFromQueryResultIterator(QueryResultIterable<Bunny> iter) {
        final List<Bunny> bunnies = new ArrayList<Bunny>();
        for (Bunny bunny : iter) {
            bunnies.add(bunny);
        }
        iter.close();
        return bunnies;
    }

    public ArrayList<String> getAllBunniesNames() {
        final QueryResultIterable<Bunny> iter = cupboard().withDatabase(db).query(Bunny.class).query();
        bunnyArray = (ArrayList<Bunny>) getListFromQueryResultIterator(iter);

        ArrayList<String> bunnyNameArray = new ArrayList<String>();
        for (Bunny b : bunnyArray) {
            bunnyNameArray.add(b.getName());
        }
        return bunnyNameArray;
    }

}
