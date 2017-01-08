package com.simoncherry.mosatsuviewerplus.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.simoncherry.mosatsuviewerplus.R;
import com.simoncherry.mosatsuviewerplus.util.StatusBarUtil;

/**
 * Created by Simon on 2017/1/4.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        StatusBarUtil.setStatusBarColor(this, R.color.colorPrimary);
    }
}
