package com.trippilot.trippilot;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.AttrRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arden on 2018-09-11.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    FrameLayout main_container;
    ImageButton home_menu,my_plan_menu,accountbook_menu,search_menu,option_menu;
    long backKeyPressedTime = 0;
    int clicked_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.main_nav);
        actionBar.setElevation(10);
        main_container = (FrameLayout)findViewById(R.id.main_container);
        home_menu = (ImageButton)findViewById(R.id.home_menu);
        my_plan_menu = (ImageButton)findViewById(R.id.my_plan_menu);
        accountbook_menu = (ImageButton)findViewById(R.id.accountbook_menu);
        search_menu = (ImageButton)findViewById(R.id.search_menu);
        option_menu = (ImageButton)findViewById(R.id.option_menu);
        home_menu.setOnClickListener(this);
        my_plan_menu.setOnClickListener(this);
        accountbook_menu.setOnClickListener(this);
        search_menu.setOnClickListener(this);
        option_menu.setOnClickListener(this);
        clicked_id = R.id.home_menu;
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (clicked_id){
            case R.id.home_menu:
                home_menu.setSelected(true);
                my_plan_menu.setSelected(false);
                accountbook_menu.setSelected(false);
                search_menu.setSelected(false);
                option_menu.setSelected(false);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new HomeView()).commit();
                clicked_id = R.id.home_menu;
                break;
            case R.id.my_plan_menu:
                home_menu.setSelected(false);
                my_plan_menu.setSelected(true);
                accountbook_menu.setSelected(false);
                search_menu.setSelected(false);
                option_menu.setSelected(false);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new PlanListView()).commit();
                clicked_id = R.id.my_plan_menu;
                break;
            case R.id.accountbook_menu:
                home_menu.setSelected(false);
                my_plan_menu.setSelected(false);
                accountbook_menu.setSelected(true);
                search_menu.setSelected(false);
                option_menu.setSelected(false);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new AccountBookListView()).commit();
                clicked_id = R.id.accountbook_menu;
                break;
            case R.id.search_menu:
                home_menu.setSelected(false);
                my_plan_menu.setSelected(false);
                accountbook_menu.setSelected(false);
                search_menu.setSelected(true);
                option_menu.setSelected(false);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new SearchView()).commit();
                clicked_id = R.id.search_menu;
                break;
            case R.id.option_menu:
                home_menu.setSelected(false);
                my_plan_menu.setSelected(false);
                accountbook_menu.setSelected(false);
                search_menu.setSelected(false);
                option_menu.setSelected(true);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new OptionView()).commit();
                clicked_id = R.id.option_menu;
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(clicked_id ==R.id.home_menu) {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                Toast.makeText(this, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                finish();
            }
        }
        else{
            home_menu.setSelected(true);
            my_plan_menu.setSelected(false);
            accountbook_menu.setSelected(false);
            search_menu.setSelected(false);
            option_menu.setSelected(false);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new HomeView()).commit();
            clicked_id = R.id.home_menu;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.home_menu:
                if(clicked_id ==R.id.home_menu)
                    break;
                home_menu.setSelected(true);
                my_plan_menu.setSelected(false);
                accountbook_menu.setSelected(false);
                search_menu.setSelected(false);
                option_menu.setSelected(false);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new HomeView()).commit();
                clicked_id = R.id.home_menu;
                break;
            case R.id.my_plan_menu:
                if(clicked_id ==R.id.my_plan_menu)
                    break;
                home_menu.setSelected(false);
                my_plan_menu.setSelected(true);
                accountbook_menu.setSelected(false);
                search_menu.setSelected(false);
                option_menu.setSelected(false);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new PlanListView()).commit();
                clicked_id = R.id.my_plan_menu;
                break;
            case R.id.accountbook_menu:
                if(clicked_id ==R.id.accountbook_menu)
                    break;
                home_menu.setSelected(false);
                my_plan_menu.setSelected(false);
                accountbook_menu.setSelected(true);
                search_menu.setSelected(false);
                option_menu.setSelected(false);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new AccountBookListView()).commit();
                clicked_id = R.id.accountbook_menu;
                break;
            case R.id.search_menu:
                if(clicked_id ==R.id.search_menu)
                    break;
                home_menu.setSelected(false);
                my_plan_menu.setSelected(false);
                accountbook_menu.setSelected(false);
                search_menu.setSelected(true);
                option_menu.setSelected(false);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new SearchView()).commit();
                clicked_id = R.id.search_menu;
                break;
            case R.id.option_menu:
                if(clicked_id ==R.id.option_menu)
                    break;
                home_menu.setSelected(false);
                my_plan_menu.setSelected(false);
                accountbook_menu.setSelected(false);
                search_menu.setSelected(false);
                option_menu.setSelected(true);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new OptionView()).commit();
                clicked_id = R.id.option_menu;
                break;
        }
    }
}


