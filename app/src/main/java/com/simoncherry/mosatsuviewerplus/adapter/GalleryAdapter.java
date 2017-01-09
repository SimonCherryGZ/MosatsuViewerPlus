package com.simoncherry.mosatsuviewerplus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.simoncherry.mosatsuviewerplus.R;
import com.simoncherry.mosatsuviewerplus.model.GalleryBean;
import com.simoncherry.mosatsuviewerplus.util.FileUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by Simon on 2017/1/2.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder>{

    private final static String ADD_ICON = "add_icon.png";

    private Context mContext;
    private List<GalleryBean> mData;

    public GalleryAdapter(Context mContext, List<GalleryBean> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ImageView view);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_gallery, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        GalleryBean bean = mData.get(position);
        String filePath = bean.getTopPath();
        if (filePath != null) {
            File file = new File(filePath);
            if (!file.exists()) {
                file = FileUtil.getResourceFile(mContext, ADD_ICON);
            }
            Picasso.with(mContext)
                    .load(file)
                    .fit().centerCrop()
                    .into(holder.itemImage);
        }

        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, holder.itemImage);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mData != null && mData.size() > 0) {
            return mData.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;

        public ImageView getItemImage() {
            return itemImage;
        }

        public MyViewHolder(View itemView) {
            super(itemView);
            itemImage = (ImageView) itemView.findViewById(R.id.item_img);
        }
    }
}
