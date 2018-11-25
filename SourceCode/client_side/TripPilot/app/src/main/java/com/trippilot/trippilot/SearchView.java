package com.trippilot.trippilot;

import android.content.Context;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by arden on 2018-09-20.
 */

public class SearchView extends android.support.v4.app.Fragment{
    private FragmentActivity context;
    private ViewPager viewpager;
    private SearchViewPagerAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_view,container,false);
        viewpager = (ViewPager)v.findViewById(R.id.search_viewpager);
        int type = getActivity().getIntent().getIntExtra("pager_type",1);
        adapter = new SearchViewPagerAdapter(context.getSupportFragmentManager(),type);
        viewpager.setAdapter(adapter);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        this.context = (FragmentActivity) context;
        super.onAttach(context);
    }
}
