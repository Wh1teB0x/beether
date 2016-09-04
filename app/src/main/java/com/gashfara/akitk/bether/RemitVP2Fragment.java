package com.gashfara.akitk.bether;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;


/**
 * A simple {@link Fragment} subclass.
 */
public class RemitVP2Fragment extends Fragment {

    // Store instance variables
    private String title;
    private int page;

    //Wallet address用のSharedPref
    private SharedPreferences pref;
    private String token;


    public static RemitVP2Fragment newInstance(int page, String title) {
        RemitVP2Fragment fragmentSecond = new RemitVP2Fragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentSecond.setArguments(args);
        return fragmentSecond;
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
        View view =  inflater.inflate(R.layout.fragment_remit_vp2, container, false);
        TextView tvLabel2 = (TextView) view.findViewById(R.id.vp2);
        tvLabel2.setText(page + " -- " + title);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);

        pref = getContext().getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
        token = pref.getString(getString(R.string.save_token), "");

        TextView yourAddress = (TextView) view.findViewById(R.id.walletAddress);
        yourAddress.setText(token);

        ImageView walletImage = (ImageView) view.findViewById(R.id.walletImage);

        try {
            Bitmap bitmap = encodeAsBitmap(token);
            walletImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }



    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 600;
    public final static int HEIGHT = 600;

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
    }

}
