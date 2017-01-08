package com.simoncherry.mosatsuviewerplus;

import android.app.Application;

import com.simoncherry.mosatsuviewerplus.util.FileUtil;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Simon on 2017/1/2.
 */

public class MyApplication extends Application {

    private final static String ADD_ICON = "add_icon.png";

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);

        FileUtil.writeResourceToFile(this, R.raw.add_icon, ADD_ICON);
    }
}
