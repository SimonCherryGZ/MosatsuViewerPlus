package com.simoncherry.mosatsuviewerplus.util;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.simoncherry.mosatsuviewerplus.R;

/**
 * Created by Simon on 2017/1/7.
 */

public class DimenUtil {

    private final static String TAG = DimenUtil.class.getSimpleName();

    public static int getTitleBarHeight(Context context) {
        int titleBarHeight = (int) context.getResources().getDimension(R.dimen.global_tool_bar_height);
        Logger.t(TAG).e("getTitleBarHeight: " + String.valueOf(titleBarHeight));
        return titleBarHeight;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        Logger.t(TAG).e("getStatusBarHeight: " + String.valueOf(result));
        return result;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
