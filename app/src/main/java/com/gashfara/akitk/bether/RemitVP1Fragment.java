package com.gashfara.akitk.bether;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RemitVP1Fragment extends Fragment {

    // Store instance variables
    private String title;
    private int page;


    public static RemitVP1Fragment newInstance(int page, String title) {
        RemitVP1Fragment fragmentFirst = new RemitVP1Fragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_remit_vp1, container, false);
        TextView tvLabel1 = (TextView) view.findViewById(R.id.vp1);
        tvLabel1.setText(page + " -- " + title);
        return view;
    }

}
