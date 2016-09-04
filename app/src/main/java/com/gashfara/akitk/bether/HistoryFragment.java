package com.gashfara.akitk.bether;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    public HistoryFragment(){
        // singleton
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }




}
