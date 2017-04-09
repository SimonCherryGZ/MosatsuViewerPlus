package com.simoncherry.mosatsuviewerplus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.simoncherry.mosatsuviewerplus.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.layout_mosatsu)
    RelativeLayout layoutMosatsu;
    @BindView(R.id.layout_gallery)
    RelativeLayout layoutGallery;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    @OnClick({R.id.layout_mosatsu, R.id.layout_gallery})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_mosatsu:
                startActivity(MosatsuActivity.class);
                break;
            case R.id.layout_gallery:
                startActivity(GalleryActivity.class);
                break;
        }
    }
}
