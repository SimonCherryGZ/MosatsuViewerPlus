package com.simoncherry.mosatsuviewerplus.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.simoncherry.mosatsuviewerplus.R;
import com.simoncherry.mosatsuviewerplus.fragment.HorizontalPagerFragment;

public class MainActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HorizontalPagerFragment horizontalPagerFragment = new HorizontalPagerFragment();
        fragmentTransaction.add(R.id.layout_root, horizontalPagerFragment);
        fragmentTransaction.commit();
    }
}
