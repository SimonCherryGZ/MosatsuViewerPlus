package com.simoncherry.mosatsuviewerplus.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.clock.scratch.ScratchView;
import com.konifar.fab_transformation.FabTransformation;
import com.mingle.sweetpick.BlurEffect;
import com.mingle.sweetpick.CustomDelegate;
import com.mingle.sweetpick.SweetSheet;
import com.orhanobut.logger.Logger;
import com.simoncherry.mosatsuviewerplus.R;
import com.simoncherry.mosatsuviewerplus.adapter.GalleryAdapter;
import com.simoncherry.mosatsuviewerplus.adapter.GridSpacingItemDecoration;
import com.simoncherry.mosatsuviewerplus.model.GalleryBean;
import com.simoncherry.mosatsuviewerplus.realm.GalleryModel;
import com.simoncherry.mosatsuviewerplus.util.DimenUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;

public class MosatsuActivity extends AppCompatActivity {

    private final static String TAG = MosatsuActivity.class.getSimpleName();

    @BindView(R.id.layout_root)
    RelativeLayout layoutRoot;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.scratch_view)
    ScratchView scratchView;
    @BindView(R.id.iv_bottom)
    ImageView ivBottom;

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.layout_tool)
    RelativeLayout layoutTool;
    @BindView(R.id.iv_gallery)
    ImageView ivGallery;
    @BindView(R.id.iv_reset)
    ImageView ivReset;
    @BindView(R.id.iv_effect)
    ImageView ivEffect;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;

    private SweetSheet mSweetSheet;
    private RecyclerView rvImg;
    private GalleryAdapter mAdapter;
    private List<GalleryBean> mData;

    private Context mContext;
    private Unbinder unbinder;
    private Realm realm;

    private boolean isTransforming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mosatsu);
        unbinder = ButterKnife.bind(this);
        mContext = MosatsuActivity.this;
        realm = Realm.getDefaultInstance();

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void init() {
        setSupportActionBar(toolbar);
        initView();
        setupSweetSheet();
        loadGallery();
    }

    private void initView() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                scratchView.setEraserSize(60.0f * (progress+1)/5);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        scratchView.setMaskImage(R.drawable.sample1_a);
        ivBottom.setImageResource(R.drawable.sample1_b);
    }

    private void setupSweetSheet() {
        mSweetSheet = new SweetSheet(layoutRoot);
        CustomDelegate customDelegate = new CustomDelegate(true,
                CustomDelegate.AnimationType.DuangLayoutAnimation);
        customDelegate.setSweetSheetColor(Color.WHITE);

        View view = LayoutInflater.from(this).inflate(R.layout.layout_img_grid, null, false);
        rvImg = (RecyclerView) view.findViewById(R.id.rv_img);

        mData = new ArrayList<>();
        mAdapter = new GalleryAdapter(this, mData);
        mAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ImageView view) {
                setScratchView(position);
                mSweetSheet.dismiss();
            }
        });
        rvImg.setLayoutManager(new GridLayoutManager(mContext, 3));
        rvImg.addItemDecoration(new GridSpacingItemDecoration(3, 50, false));
        rvImg.setAdapter(mAdapter);

        customDelegate.setCustomView(view);
        mSweetSheet.setDelegate(customDelegate);
        mSweetSheet.setBackgroundEffect(new BlurEffect(8));
        //mSweetSheet.setBackgroundEffect(new DimEffect(1.5f));

        mSweetSheet.setOnSweetSheetToggleListener(new SweetSheet.OnSweetSheetToggleListener() {
            @Override
            public void onShow() {
                //btnGallery.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onHide() {
                //btnGallery.setVisibility(View.VISIBLE);
            }
        });
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

        mAdapter.notifyDataSetChanged();
    }

    private void setScratchView(int position) {
        if (mData != null && mData.size() > position) {
            GalleryBean bean = mData.get(position);
            String topPath = bean.getTopPath();
            String bottomPath = bean.getBottomPath();

            Logger.t(TAG).e("get topPath: " + topPath);
            Logger.t(TAG).e("get bottomPath: " + bottomPath);

            Picasso.with(mContext).load(new File(bottomPath))
                    .fit().centerCrop()
                    .into(ivBottom);

            Picasso.with(mContext).load(new File(topPath))
                    .resize(DimenUtil.dip2px(mContext, 240.0f), DimenUtil.dip2px(mContext, 360.0f)).centerCrop()
                    .into(target);
        }
    }

    final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Logger.t(TAG).e("onBitmapLoaded");
            scratchView.setMaskImage(bitmap);
            scratchView.reset();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Logger.t(TAG).e("onBitmapFailed");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    @Override
    public void onBackPressed() {
        if (mSweetSheet.isShow()) {
            mSweetSheet.toggle();
        } else {
            if (fab.getVisibility() != View.VISIBLE) {
                FabTransformation.with(fab).transformFrom(layoutTool);
                return;
            }
            super.onBackPressed();
        }
    }

    @OnClick({R.id.fab, R.id.layout_tool, R.id.layout_root, R.id.iv_gallery, R.id.iv_reset, R.id.iv_effect})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                if (fab.getVisibility() == View.VISIBLE) {
                    FabTransformation.with(fab).transformTo(layoutTool);
                }
                break;
            case R.id.layout_tool:
                break;
            case R.id.layout_root:
                if (fab.getVisibility() != View.VISIBLE && !isTransforming) {
                    FabTransformation.with(fab)
                            .setListener(new FabTransformation.OnTransformListener() {
                                @Override
                                public void onStartTransform() {
                                    isTransforming = true;
                                }
                                @Override
                                public void onEndTransform() {
                                    isTransforming = false;
                                }
                            })
                            .transformFrom(layoutTool);
                }
                break;
            case R.id.iv_gallery:
                if (mSweetSheet != null) {
                    mSweetSheet.toggle();
                }
                break;
            case R.id.iv_reset:
                scratchView.reset();
                break;
            case R.id.iv_effect:
                scratchView.toggleEdgeEffect();
                if (scratchView.isEdgeEffectEnable()) {
                    ivEffect.setImageResource(R.drawable.ic_radio_button_on_white_48dp);
                } else {
                    ivEffect.setImageResource(R.drawable.ic_radio_button_off_white_48dp);
                }
                break;
        }
    }
}
