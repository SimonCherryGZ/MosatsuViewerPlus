package com.simoncherry.mosatsuviewerplus.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.simoncherry.mosatsuviewerplus.R;


/**
 * Created by Simon on 2017/1/4.
 */

public class BitmapUtil {

    public static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.i("TAG", "" + statusBarHeight);

        // 获取toolBar高度
        int toolBarHeight = (int) activity.getResources().getDimension(R.dimen.global_tool_bar_height);
        Log.i("TAG", "" + toolBarHeight);

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉状态栏，如果需要的话
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight+toolBarHeight, width, height-statusBarHeight-toolBarHeight);
        view.destroyDrawingCache();
        return b;
    }
}
