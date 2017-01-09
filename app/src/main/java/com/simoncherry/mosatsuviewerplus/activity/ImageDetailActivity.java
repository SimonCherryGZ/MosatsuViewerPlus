package com.simoncherry.mosatsuviewerplus.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.simoncherry.mosatsuviewerplus.R;
import com.simoncherry.mosatsuviewerplus.event.ImageChangeEvent;
import com.simoncherry.mosatsuviewerplus.realm.GalleryModel;
import com.simoncherry.mosatsuviewerplus.util.DimenUtil;
import com.simoncherry.mosatsuviewerplus.util.FileUtil;
import com.simoncherry.mosatsuviewerplus.util.StatusBarUtil;
import com.simoncherry.mosatsuviewerplus.util.prelolipop.EnterScreenAnimations;
import com.simoncherry.mosatsuviewerplus.util.prelolipop.ExitScreenAnimations;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;

public class ImageDetailActivity extends BaseActivity {

    @BindView(R.id.layout_root)
    LinearLayout mainContainer;
    @BindView(R.id.layout_bg)
    RelativeLayout layoutBg;
    @BindView(R.id.cv_top)
    CardView cvTop;
    @BindView(R.id.iv_top)
    ImageView ivTop;
    @BindView(R.id.iv_bottom)
    ImageView ivBottom;

    private final static String TAG = ImageDetailActivity.class.getSimpleName();
    private final static String ADD_ICON = "add_icon.png";
    private final static int FILE_REQUEST_CODE = 123;

    private static final String IMAGE_FILE_KEY = "IMAGE_FILE_KEY";
    private static final String KEY_THUMBNAIL_INIT_TOP_POSITION = "KEY_THUMBNAIL_INIT_TOP_POSITION";
    private static final String KEY_THUMBNAIL_INIT_LEFT_POSITION = "KEY_THUMBNAIL_INIT_LEFT_POSITION";
    private static final String KEY_THUMBNAIL_INIT_WIDTH = "KEY_THUMBNAIL_INIT_WIDTH";
    private static final String KEY_THUMBNAIL_INIT_HEIGHT = "KEY_THUMBNAIL_INIT_HEIGHT";
    private static final String KEY_SCALE_TYPE = "KEY_SCALE_TYPE";

    private ImageView mEnlargedImage;
    private ImageView mTransitionImage;

    private Picasso mImageDownloader;

    private EnterScreenAnimations mEnterScreenAnimations;
    private ExitScreenAnimations mExitScreenAnimations;

    private Context mContext;
    private Unbinder unbinder;
    private Realm realm;

    private int clickIndex = -1;
    private boolean isTop = true;
    private boolean isShowAnim = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_image_detail);
        mContext = ImageDetailActivity.this;
        unbinder = ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        //final RelativeLayout mainContainer = (RelativeLayout) findViewById(R.id.layout_root);
        mEnlargedImage = (ImageView) findViewById(R.id.iv_top);
        mImageDownloader = Picasso.with(this);
        File imageFile = (File) getIntent().getSerializableExtra(IMAGE_FILE_KEY);

        if(savedInstanceState == null){
            // We entered activity for the first time.
            // Initialize Image view that will be transitioned
            initializeTransitionView();
        } else {
            // Activity is retrieved. Main container is invisible. Make it visible
            mainContainer.setAlpha(1.0f);
        }

        mEnterScreenAnimations = new EnterScreenAnimations(mTransitionImage, mEnlargedImage, mainContainer);
        mEnterScreenAnimations.setEnterAnimationListener(new EnterScreenAnimations.EnterAnimationListener() {
            @Override
            public void onAnimationEnd() {
                cvTop.setVisibility(View.VISIBLE);
                //StatusBarUtil.setStatusBarColor(ImageDetailActivity.this, R.color.colorPrimary);
                loadImage(clickIndex);
            }
        });
        mExitScreenAnimations = new ExitScreenAnimations(mTransitionImage, mEnlargedImage, mainContainer);
        mExitScreenAnimations.setEnterAnimationListener(new ExitScreenAnimations.ExitAnimationListener() {
            @Override
            public void onAnimationStart() {
                cvTop.setVisibility(View.INVISIBLE);
            }
        });
        initializeEnlargedImageAndRunAnimation(savedInstanceState, imageFile);

        byte [] bis = getIntent().getByteArrayExtra("bitmap");
        Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
        layoutBg.setBackground(new BitmapDrawable(bitmap));
        StatusBarUtil.setStatusBarColor(ImageDetailActivity.this, R.color.colorPrimary);

        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // prevent leaking activity if image was not loaded yet
        Picasso.with(this).cancelRequest(mEnlargedImage);
        unbinder.unbind();
        realm.close();
    }

    private void initData() {
        Intent intent = getIntent();
        clickIndex = intent.getIntExtra("index", -1);
    }

    private void loadImage(int index) {
        RealmResults<GalleryModel> realmResults = realm.where(GalleryModel.class)
                .equalTo("index", index).findAll();
        if (realmResults.size() > 0) {
            GalleryModel galleryModel = realmResults.first();
            String topPath = galleryModel.getTopPath();
            File topImage = new File(topPath);
            if (!topImage.exists()) {
                topImage = FileUtil.getResourceFile(mContext, ADD_ICON);
            }
            Picasso.with(mContext).load(topImage)
                    .fit().centerCrop()
                    .into(ivTop);

            String bottomPath = galleryModel.getBottomPath();
            File bottomImage = new File(bottomPath);
            if (!bottomImage.exists()) {
                bottomImage = FileUtil.getResourceFile(mContext, ADD_ICON);
            }
            Picasso.with(mContext).load(bottomImage)
                    .fit().centerCrop()
                    .into(ivBottom);
        } else {
            File file = FileUtil.getResourceFile(mContext, ADD_ICON);
            Picasso.with(mContext).load(file)
                    .fit().centerCrop()
                    .into(ivTop);
            Picasso.with(mContext).load(file)
                    .fit().centerCrop()
                    .into(ivBottom);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ivTop.setVisibility(View.VISIBLE);
                mTransitionImage.setVisibility(View.INVISIBLE);
                isShowAnim = false;
            }
        }, 500);
    }

    private void initializeEnlargedImageAndRunAnimation(final Bundle savedInstanceState, File imageFile) {
        Log.v(TAG, "initializeEnlargedImageAndRunAnimation");

        mImageDownloader.load(imageFile).into(mEnlargedImage, new Callback() {
            /**
             * Image is loaded when this method is called
             */
            @Override
            public void onSuccess() {
                Log.v(TAG, "onSuccess, mEnlargedImage");

                // In this callback we already have image set into ImageView and we can use it's Matrix for animation
                // But we have to wait for final measurements. We use OnPreDrawListener to be sure everything is measured

                if (savedInstanceState == null) {
                    // if savedInstanceState is null activity is started for the first time.
                    // run the animation
                    runEnteringAnimation();
                } else {
                    // activity was retrieved from recent apps. No animation needed, just load the image
                }
            }
            @Override
            public void onError() {
                // CAUTION: on error is not handled. If OutOfMemory emerged during image loading we have to handle it here
                Log.v(TAG, "onError, mEnlargedImage");
            }
        });
    }

    private void runEnteringAnimation() {
        Log.v(TAG, "runEnteringAnimation, addOnPreDrawListener");

        mEnlargedImage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            int mFrames = 0;

            @Override
            public boolean onPreDraw() {
                // When this method is called we already have everything laid out and measured so we can start our animation
                Log.v(TAG, "onPreDraw, mFrames " + mFrames);

                switch (mFrames++) {
                    case 0:
                        /**
                         * 1. start animation on first frame
                         */
                        final int[] finalLocationOnTheScreen = new int[2];
                        mEnlargedImage.getLocationOnScreen(finalLocationOnTheScreen);

                        mEnterScreenAnimations.playEnteringAnimation(
                                finalLocationOnTheScreen[0], // left
                                finalLocationOnTheScreen[1], // top
                                mEnlargedImage.getWidth(),
                                mEnlargedImage.getHeight());

                        return true;
                    case 1:
                        /**
                         * 2. Do nothing. We just draw this frame
                         */

                        return true;
                }
                /**
                 * 3.
                 * Make view on previous screen invisible on after this drawing frame
                 * Here we ensure that animated view will be visible when we make the viw behind invisible
                 */
                Log.v(TAG, "run, onAnimationStart");
                //mBus.post(new ChangeImageThumbnailVisibility(false));

                mEnlargedImage.getViewTreeObserver().removeOnPreDrawListener(this);

                Log.v(TAG, "onPreDraw, << mFrames " + mFrames);

                return true;
            }
        });
    }

    private void initializeTransitionView() {
        Log.v(TAG, "initializeTransitionView");

        FrameLayout androidContent = (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
        mTransitionImage = new ImageView(this);
        androidContent.addView(mTransitionImage);

        Bundle bundle = getIntent().getExtras();

        int thumbnailTop = bundle.getInt(KEY_THUMBNAIL_INIT_TOP_POSITION)
                - DimenUtil.getStatusBarHeight(mContext);
        int thumbnailLeft = bundle.getInt(KEY_THUMBNAIL_INIT_LEFT_POSITION);
        int thumbnailWidth = bundle.getInt(KEY_THUMBNAIL_INIT_WIDTH);

        int thumbnailHeight = bundle.getInt(KEY_THUMBNAIL_INIT_HEIGHT);

        ImageView.ScaleType scaleType = (ImageView.ScaleType) bundle.getSerializable(KEY_SCALE_TYPE);

        Log.v(TAG, "initInitialThumbnail, thumbnailTop [" + thumbnailTop + "]");
        Log.v(TAG, "initInitialThumbnail, thumbnailLeft [" + thumbnailLeft + "]");
        Log.v(TAG, "initInitialThumbnail, thumbnailWidth [" + thumbnailWidth + "]");
        Log.v(TAG, "initInitialThumbnail, thumbnailHeight [" + thumbnailHeight + "]");
        Log.v(TAG, "initInitialThumbnail, scaleType " + scaleType);

        // We set initial margins to the view so that it was situated at exact same spot that view from the previous screen were.
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mTransitionImage.getLayoutParams();
        layoutParams.height = thumbnailHeight;
        layoutParams.width = thumbnailWidth;
        layoutParams.setMargins(thumbnailLeft, thumbnailTop, 0, 0);

        File imageFile = (File) getIntent().getSerializableExtra(IMAGE_FILE_KEY);
        mTransitionImage.setScaleType(scaleType);

        mImageDownloader.load(imageFile).noFade().into(mTransitionImage);
    }

    public static Intent getStartIntent(Activity activity, File imageFile, int left, int top, int width, int height, ImageView.ScaleType scaleType) {
        Log.v(TAG, "getStartIntent, imageFile " + imageFile);

        Intent startIntent = new Intent(activity, ImageDetailActivity.class);
        startIntent.putExtra(IMAGE_FILE_KEY, imageFile);

        startIntent.putExtra(KEY_THUMBNAIL_INIT_TOP_POSITION, top);
        startIntent.putExtra(KEY_THUMBNAIL_INIT_LEFT_POSITION, left);
        startIntent.putExtra(KEY_THUMBNAIL_INIT_WIDTH, width);
        startIntent.putExtra(KEY_THUMBNAIL_INIT_HEIGHT, height);
        startIntent.putExtra(KEY_SCALE_TYPE, scaleType);

        return startIntent;
    }

    private void startFileManager() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }

    private void savePath2Realm(int index, String path, boolean isTop) {
        realm.beginTransaction();
        GalleryModel galleryModel;
        RealmResults<GalleryModel> realmResults = realm.where(GalleryModel.class)
                .equalTo("index", index)
                .findAll();
        if (realmResults.size() > 0) {
            galleryModel = realmResults.first();
            if (isTop) {
                galleryModel.setTopPath(path);
            } else {
                galleryModel.setBottomPath(path);
            }
            realm.copyToRealmOrUpdate(galleryModel);
        } else {
            galleryModel = new GalleryModel();
            Number maxId = realm.where(GalleryModel.class).max("id");
            AtomicLong primaryKeyValue = new AtomicLong(maxId == null ? 0 : maxId.longValue());
            galleryModel.setId(primaryKeyValue.incrementAndGet());
            galleryModel.setIndex(index);
            if (isTop) {
                galleryModel.setTopPath(path);
                galleryModel.setBottomPath("default");
            } else {
                galleryModel.setTopPath("default");
                galleryModel.setBottomPath(path);
            }

            realm.copyToRealm(galleryModel);
        }
        realm.commitTransaction();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == FILE_REQUEST_CODE) {
                Uri uri = data.getData();
                String path = FileUtil.getFileAbsolutePath(ImageDetailActivity.this, uri);
                if (path != null) {
                    //Toast.makeText(mContext, "path："+ path, Toast.LENGTH_SHORT).show();
                    if (clickIndex >= 0) {
                        savePath2Realm(clickIndex, path, isTop);
                        final ImageView imageView;
                        if (isTop) {
                            imageView = ivTop;
                        } else {
                            imageView = ivBottom;
                        }
                        File file = new File(path);
                        Picasso.with(mContext).load(file)
                                .fit().centerCrop()
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        if (isTop) {
                                            mEnterScreenAnimations.setTargetImageMatrixValues(imageView);
                                        }
                                    }
                                    @Override
                                    public void onError() {
                                    }
                                });

                        if (isTop) {
                            Bundle bundle = getIntent().getExtras();
                            int thumbnailLeft = bundle.getInt(KEY_THUMBNAIL_INIT_LEFT_POSITION);
                            int thumbnailTop = DimenUtil.getTitleBarHeight(mContext) + DimenUtil.getStatusBarHeight(mContext);
                            int thumbnailWidth = bundle.getInt(KEY_THUMBNAIL_INIT_WIDTH);
                            int thumbnailHeight = bundle.getInt(KEY_THUMBNAIL_INIT_HEIGHT);
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(thumbnailWidth, thumbnailHeight);
                            layoutParams.setMargins(thumbnailLeft, thumbnailTop, thumbnailLeft+thumbnailWidth, thumbnailTop+thumbnailHeight);
                            mTransitionImage.setLayoutParams(layoutParams);
                            mTransitionImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            Picasso.with(mContext).load(file)
                                    .fit().centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .into(mTransitionImage, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            mEnterScreenAnimations.setInitThumbnailMatrixValues(mTransitionImage);

                                            int width = cvTop.getWidth();
                                            int height = cvTop.getHeight();
                                            int left = cvTop.getLeft();
                                            int top = cvTop.getTop() + DimenUtil.getTitleBarHeight(mContext);
                                            FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) mTransitionImage.getLayoutParams();
                                            layoutParams1.height = height;
                                            layoutParams1.width = width;
                                            layoutParams1.setMargins(left, top, 0, 0);
                                            mTransitionImage.setLayoutParams(layoutParams1);
                                        }
                                        @Override
                                        public void onError() {
                                        }
                                    });
                            EventBus.getDefault().post(new ImageChangeEvent(path));
                        }
                    }
                } else {
                    Toast.makeText(mContext, "cannot get path", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
//        We don't call super to leave this activity on the screen when back is pressed
//        super.onBackPressed();
        Log.v(TAG, "onBackPressed");
        if (!isShowAnim) {
            mEnterScreenAnimations.cancelRunningAnimations();

            Bundle initialBundle = getIntent().getExtras();
            int toTop = initialBundle.getInt(KEY_THUMBNAIL_INIT_TOP_POSITION);
            int toLeft = initialBundle.getInt(KEY_THUMBNAIL_INIT_LEFT_POSITION);
            int toWidth = initialBundle.getInt(KEY_THUMBNAIL_INIT_WIDTH);
            int toHeight = initialBundle.getInt(KEY_THUMBNAIL_INIT_HEIGHT);

            mExitScreenAnimations.playExitAnimations(
                    toTop, toLeft, toWidth, toHeight,
                    mEnterScreenAnimations.getInitialThumbnailMatrixValues(),
                    mEnterScreenAnimations.getTargetImageMatrixValues());
        }
    }

    @OnClick({R.id.iv_top, R.id.iv_bottom})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_top:
                isTop = true;
                startFileManager();
                break;
            case R.id.iv_bottom:
                isTop = false;
                startFileManager();
                break;
        }
    }
}
