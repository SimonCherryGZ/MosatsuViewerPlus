package com.simoncherry.mosatsuviewerplus.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.simoncherry.mosatsuviewerplus.R;
import com.simoncherry.mosatsuviewerplus.activity.GalleryActivity;
import com.simoncherry.mosatsuviewerplus.activity.MosatsuActivity;
import com.simoncherry.mosatsuviewerplus.adapter.HorizontalPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HorizontalPagerFragment extends Fragment {


    public HorizontalPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_horizontal_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final HorizontalInfiniteCycleViewPager horizontalInfiniteCycleViewPager =
                (HorizontalInfiniteCycleViewPager) view.findViewById(R.id.hicvp);
        final HorizontalPagerAdapter adapter = new HorizontalPagerAdapter(getContext());
        adapter.setOnItemClickListener(new HorizontalPagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                Toast.makeText(getContext(), "click: " + String.valueOf(position), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getContext(), MosatsuActivity.class);
//                getActivity().startActivity(intent);
                Intent intent = null;
                switch (position) {
                    case 0:
                        intent = new Intent(getContext(), MosatsuActivity.class);
                        break;
                    case 1:
                        intent = new Intent(getContext(), GalleryActivity.class);
                        break;
                }
                if (intent != null) {
                    getActivity().startActivity(intent);
                }
            }
        });
        horizontalInfiniteCycleViewPager.setAdapter(adapter);
    }
}
