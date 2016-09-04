package com.gashfara.akitk.bether;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class RemitFragment extends Fragment {
    FragmentPagerAdapter adapterViewPager;

    public RemitFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_remit, container, false);

        ViewPager vpPager = (ViewPager) view.findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getActivity().getSupportFragmentManager(), getContext());
        vpPager.setAdapter(adapterViewPager);


        // Attach the page change listener inside the activity
        vpPager.addOnPageChangeListener(new OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
//                Toast.makeText(getActivity(), "Selected page position: " + position, Toast.LENGTH_SHORT).show();
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });


        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);

        return view;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);
        ViewPager vpPager = (ViewPager) getActivity().findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getActivity().getSupportFragmentManager(), getContext());
        vpPager.setAdapter(adapterViewPager);

        // Attach the page change listener inside the activity
        vpPager.addOnPageChangeListener(new OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
//                Toast.makeText(getActivity(), "Selected page position: " + position, Toast.LENGTH_SHORT).show();
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);
    }


    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;
        private String tabTitles[] = new String[] { "送金処理", "着金処理"};
        private Context context;

        public MyPagerAdapter(FragmentManager fragmentManager, Context context) {
            super(fragmentManager);
            this.context = context;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return RemitVP1Fragment.newInstance(0, "Page # 1");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return RemitVP2Fragment.newInstance(1, "Page # 2");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
