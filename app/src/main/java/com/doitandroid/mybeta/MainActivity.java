package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.doitandroid.mybeta.fragment.HomeFragment;
import com.doitandroid.mybeta.fragment.MyFragment1;
import com.doitandroid.mybeta.fragment.MyFragment2;
import com.doitandroid.mybeta.fragment.MyFragment3;
import com.doitandroid.mybeta.fragment.MyFragment4;
import com.doitandroid.mybeta.utils.UtilsCollection;

public class MainActivity extends AppCompatActivity {

    String current_fragment = "first";
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    Button button;

    FrameLayout btn_home, btn_certi, btn_search, btn_user;

    RecyclerView recyclerView;

    androidx.fragment.app.Fragment fragment_home;
    androidx.fragment.app.Fragment fragment_notification;
    androidx.fragment.app.Fragment fragment_search;
    androidx.fragment.app.Fragment fragment_user;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        UtilsCollection utilsCollection = new UtilsCollection(this);
        utilsCollection.makeStatusBarColor("#cccccc");

        ////////////////////////////////////////////////////////////////////////////////////////////
        // 자동 로그인 구현
        SharedPreferences sp = getSharedPreferences("init_app", MODE_PRIVATE);
        if(sp.getInt("auto_login", 0) == 0){
            Intent intent = new Intent(this, FrontActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            /*intent.addFlags(
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );*/
            startActivity(intent);

            // not auto login
        } else {

            // auto login
        }




        // getFragmentManager().beginTransaction().replace(R.id.main_frame, new MyFragment1()).commit();


        toolbar = findViewById(R.id.tb_tb);
        /*button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
            }
        });*/

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FrameLayout framelayout_home = findViewById(R.id.tb_fl_home);
        FrameLayout framelayout_notification = findViewById(R.id.tb_fl_notification);
        FrameLayout search = findViewById(R.id.tb_fl_search);
        FrameLayout user = findViewById(R.id.tb_fl_user);

        fragment_home = new HomeFragment();
        fragment_notification = new MyFragment2();
        fragment_search = new MyFragment3();
        fragment_user = new MyFragment4();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().add(R.id.main_frame, fragment_notification).commit();
        fragmentManager.beginTransaction().add(R.id.main_frame, fragment_home).commit();

        fragmentManager.beginTransaction().show(fragment_notification).commit();
        fragmentManager.beginTransaction().hide(fragment_home).commit();


        framelayout_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragment_home == null){
                    fragment_home = new HomeFragment();
                    fragmentManager.beginTransaction().add(R.id.main_frame, fragment_home).commit();
                }

                if(fragment_home != null){
                    fragmentManager.beginTransaction().show(fragment_home).commit();
                }

                if(fragment_notification != null){
                    fragmentManager.beginTransaction().hide(fragment_notification).commit();
                }
            }
        });
        framelayout_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fragment_notification == null){
                    fragment_notification = new MyFragment2();
                    fragmentManager.beginTransaction().add(R.id.main_frame, fragment_notification).commit();

                }
                if(fragment_home != null){
                    fragmentManager.beginTransaction().hide(fragment_home).commit();
                }

                if(fragment_notification != null){
                    fragmentManager.beginTransaction().show(fragment_notification).commit();
                }

//                interface_set.setVisibility(View.INVISIBLE);
//                getFragmentManager().beginTransaction().replace(R.id.main_frame, new MyFragment2()).commit();
            }
        });

/*        getFragmentManager().beginTransaction().add(R.id.main_frame, fragment_home).commit();
        getFragmentManager().beginTransaction().add(R.id.main_frame, fragment_notification).commit();
        getFragmentManager().beginTransaction().add(R.id.main_frame, fragment_search).commit();
        getFragmentManager().beginTransaction().add(R.id.main_frame, fragment_user).commit();*/
/*
        if(current_fragment.equals("first")){
            if(fragment_home == null){
                fragment_home = new MyFragment1();
                fragmentManager.beginTransaction().add(R.id.main_frame, fragment_home).commit();

            }
            if(fragment_home != null){
                fragmentManager.beginTransaction().show(fragment_home).commit();
            }

            if(fragment_notification != null){
                fragmentManager.beginTransaction().hide(fragment_notification).commit();
            }

            if(fragment_search != null){
                fragmentManager.beginTransaction().hide(fragment_search).commit();
            }

            if(fragment_user != null){
                fragmentManager.beginTransaction().hide(fragment_user).commit();
            }
            // getFragmentManager().beginTransaction().replace(R.id.main_frame, fragment_home).commit();

        } else if (current_fragment.equals("second")){
            getFragmentManager().beginTransaction().replace(R.id.main_frame, fragment_notification).commit();

        } else if (current_fragment.equals("third")){
            getFragmentManager().beginTransaction().replace(R.id.main_frame, fragment_search).commit();

        } else if (current_fragment.equals("fourth")){
            getFragmentManager().beginTransaction().replace(R.id.main_frame, fragment_user).commit();

        }*/

//        search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getFragmentManager().beginTransaction().replace(R.id.main_frame, new MyFragment3()).commit();
//            }
//        });
//        user.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getFragmentManager().beginTransaction().replace(R.id.main_frame, new MyFragment4()).commit();
//            }
//        });




    }
}
