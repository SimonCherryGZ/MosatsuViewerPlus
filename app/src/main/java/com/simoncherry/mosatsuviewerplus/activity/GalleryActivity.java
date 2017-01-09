package com.simoncherry.mosatsuviewerplus.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.simoncherry.mosatsuviewerplus.R;
import com.simoncherry.mosatsuviewerplus.adapter.GalleryAdapter;
import com.simoncherry.mosatsuviewerplus.adapter.GridSpacingItemDecoration;
import com.simoncherry.mosatsuviewerplus.event.ImageChangeEvent;
import com.simoncherry.mosatsuviewerplus.model.GalleryBean;
import com.simoncherry.mosatsuviewerplus.realm.GalleryModel;
import com.simoncherry.mosatsuviewerplus.util.BitmapUtil;
import com.simoncherry.mosatsuviewerplus.util.FileUtil;
import com.simoncherry.mosatsuviewerplus.util.GaussBlurUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;

public class GalleryActivity extends BaseActivity {

    private final static String TAG = GalleryActivity.class.getSimpleName();
    private final static String ADD_ICON = "add_icon.png";

    @BindView(R.id.layout_root)
    LinearLayout layoutRoot;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_img)
    RecyclerView rvImg;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private Context mContext;
    private Unbinder unbinder;
    private Realm realm;

    private GalleryAdapter mAdapter;
    private List<GalleryBean> mData;
    private int clickIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        unbinder = ButterKnife.bind(this);
        mContext = GalleryActivity.this;
        EventBus.getDefault().register(this);

        setSupportActionBar(toolbar);
        initFab();
        initRecyclerView();

        realm = Realm.getDefaultInstance();
        loadGallery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        realm.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
    }

    private void initFab() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickIndex = mData.size()-1;
                GalleryBean bean = mData.get(clickIndex);
                if (bean != null) {
                    File topImg = new File(bean.getTopPath());
                    if (!topImg.exists()) {
                        topImg = FileUtil.getResourceFile(mContext, ADD_ICON);
                    }

                    View view = rvImg.getChildAt(clickIndex);
                    if (null != rvImg.getChildViewHolder(view)){
                        GalleryAdapter.MyViewHolder viewHolder = (GalleryAdapter.MyViewHolder) rvImg.getChildViewHolder(view);
                        enterImageDetails(topImg, viewHolder.getItemImage());
                    }
                }
            }
        });
    }

    private void initRecyclerView() {
        mData = new ArrayList<>();
        mAdapter = new GalleryAdapter(this, mData);
        mAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ImageView imageView) {
                clickIndex = position;
                GalleryBean bean = mData.get(position);
                if (bean != null) {
                    File topImg = new File(bean.getTopPath());
                    if (!topImg.exists()) {
                        topImg = FileUtil.getResourceFile(mContext, ADD_ICON);
                    }
                    enterImageDetails(topImg, imageView);
                }
            }
        });
        rvImg.setLayoutManager(new GridLayoutManager(mContext, 3));
        rvImg.addItemDecoration(new GridSpacingItemDecoration(3, 50, false));
        rvImg.setAdapter(mAdapter);
    }

    private void loadGallery() {
        mData.clear();
        RealmResults<GalleryModel> realmResults = realm.where(GalleryModel.class)
                .findAll();
        if (realmResults.size() > 0) {
            for (GalleryModel model : realmResults) {
                GalleryBean bean = new GalleryBean();
                bean.setIndex(model.getIndex());
                bean.setTopPath(model.getTopPath());
                bean.setBottomPath(model.getBottomPath());
                mData.add(bean);
                Log.e(TAG, bean.getTopPath());
            }
        }

        GalleryBean fakeBean = new GalleryBean();
        fakeBean.setIndex(mData.size());
        fakeBean.setTopPath("default");
        fakeBean.setBottomPath("default");
        mData.add(fakeBean);

        mAdapter.notifyDataSetChanged();
//        if (mData.size() > 0) {
//            rvImg.smoothScrollToPosition(mData.size() - 1);
//        }
    }

    private void savePath2Realm(int index, String path) {
        realm.beginTransaction();
        GalleryModel galleryModel;
        RealmResults<GalleryModel> realmResults = realm.where(GalleryModel.class)
                .equalTo("index", index)
                .findAll();
        if (realmResults.size() > 0) {
            galleryModel = realmResults.first();
            galleryModel.setTopPath(path);
            realm.copyToRealmOrUpdate(galleryModel);
        } else {
            galleryModel = new GalleryModel();
            Number maxId = realm.where(GalleryModel.class).max("id");
            AtomicLong primaryKeyValue = new AtomicLong(maxId == null ? 0 : maxId.longValue());
            galleryModel.setId(primaryKeyValue.incrementAndGet());
            galleryModel.setIndex(index);
            galleryModel.setTopPath(path);
            galleryModel.setBottomPath("default");
            realm.copyToRealm(galleryModel);
        }
        realm.commitTransaction();
    }

    public void enterImageDetails(File imageFile, final ImageView image) {
        Log.v(TAG, "enterImageDetails, imageFile " + imageFile);
        Log.v(TAG, "enterImageDetails, image.getScaleType() " + image.getScaleType());

        int[] screenLocation = new int[2];
        image.getLocationInWindow(screenLocation);

        Intent startIntent = ImageDetailActivity.getStartIntent(this, imageFile,
                screenLocation[0],
                screenLocation[1],
                image.getWidth(),
                image.getHeight(),
                image.getScaleType());

        fab.setVisibility(View.INVISIBLE);
        Bitmap screenShot = GaussBlurUtil.toBlur(BitmapUtil.takeScreenShot(this), 5);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        screenShot.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte [] bitmapByte = stream.toByteArray();
        startIntent.putExtra("bitmap", bitmapByte);
        startIntent.putExtra("index", clickIndex);

        startActivity(startIntent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ImageChangeEvent event) {
        String path = event.getPath();
        GalleryBean bean = mData.get(clickIndex);
        bean.setTopPath(path);
        mData.set(clickIndex, bean);

        if (clickIndex >= mData.size()-1) {
            GalleryBean fakeBean = new GalleryBean();
            fakeBean.setIndex(mData.size());
            fakeBean.setTopPath("default");
            fakeBean.setBottomPath("default");
            mData.add(fakeBean);
        }

        mAdapter.notifyDataSetChanged();
    }
}
