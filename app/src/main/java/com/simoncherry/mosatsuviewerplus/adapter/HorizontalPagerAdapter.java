package com.simoncherry.mosatsuviewerplus.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simoncherry.mosatsuviewerplus.R;

/**
 * Created by Simon on 2017/1/1.
 */

public class HorizontalPagerAdapter extends PagerAdapter {

    private String[] titleArray = {"妄撮", "图库", "设置"};
    private int[] imgArray = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public HorizontalPagerAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view = mLayoutInflater.inflate(R.layout.item_entrance, container, false);
        final TextView txt = (TextView) view.findViewById(R.id.tv_title);
        txt.setText(titleArray[position]);

        final ImageView img = (ImageView) view.findViewById(R.id.iv_bg);
        img.setImageResource(imgArray[position]);

        final View layoutCard = view.findViewById(R.id.layout_card);
        layoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }
}
