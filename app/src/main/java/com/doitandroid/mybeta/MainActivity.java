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

    androidx.fragment.app.Fragment fragment1;
    androidx.fragment.app.Fragment fragment2;
    androidx.fragment.app.Fragment fragment3;
    androidx.fragment.app.Fragment fragment4;

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


        toolbar = findViewById(R.id.toolbar);
        /*button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
            }
        });*/

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FrameLayout home = findViewById(R.id.toolbar_home);
        FrameLayout certification = findViewById(R.id.toolbar_certification);
        FrameLayout search = findViewById(R.id.toolbar_search);
        FrameLayout user = findViewById(R.id.toolbar_user);

        fragment1 = new MyFragment1();
        fragment2 = new MyFragment2();
        fragment3 = new MyFragment3();
        fragment4 = new MyFragment4();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.main_frame, fragment2).commit();

        fragmentManager.beginTransaction().add(R.id.main_frame, fragment1).commit();
        fragmentManager.beginTransaction().show(fragment2).commit();
        fragmentManager.beginTransaction().hide(fragment1).commit();


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fragment1 == null){
                    fragment1 = new MyFragment1();
                    fragmentManager.beginTransaction().add(R.id.main_frame, fragment1).commit();

                }

                if(fragment1 != null){
                    fragmentManager.beginTransaction().show(fragment1).commit();
                }

                if(fragment2 != null){
                    fragmentManager.beginTransaction().hide(fragment2).commit();
                }
//                interface_set.setVisibility(View.VISIBLE);




/*                getFragmentManager().beginTransaction().hide(fragment2).commit();
                getFragmentManager().beginTransaction().hide(fragment1).commit();
                getFragmentManager().beginTransaction().hide(fragment3).commit();
                getFragmentManager().beginTransaction().hide(fragment4).commit();
//
//                getFragmentManager().beginTransaction().add(R.id.main_frame, fragment1).commit();
                getFragmentManager().beginTransaction().show(fragment1).commit();
                // getFragmentManager().beginTransaction().replace(R.id.main_frame, fragment1).commit();*/
            }
        });
        certification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fragment2 == null){
                    fragment2 = new MyFragment2();
                    fragmentManager.beginTransaction().add(R.id.main_frame, fragment2).commit();

                }
                if(fragment1 != null){
                    fragmentManager.beginTransaction().hide(fragment1).commit();
                }

                if(fragment2 != null){
                    fragmentManager.beginTransaction().show(fragment2).commit();
                }

//                interface_set.setVisibility(View.INVISIBLE);
//                getFragmentManager().beginTransaction().replace(R.id.main_frame, new MyFragment2()).commit();
            }
        });

/*        getFragmentManager().beginTransaction().add(R.id.main_frame, fragment1).commit();
        getFragmentManager().beginTransaction().add(R.id.main_frame, fragment2).commit();
        getFragmentManager().beginTransaction().add(R.id.main_frame, fragment3).commit();
        getFragmentManager().beginTransaction().add(R.id.main_frame, fragment4).commit();*/
/*
        if(current_fragment.equals("first")){
            if(fragment1 == null){
                fragment1 = new MyFragment1();
                fragmentManager.beginTransaction().add(R.id.main_frame, fragment1).commit();

            }
            if(fragment1 != null){
                fragmentManager.beginTransaction().show(fragment1).commit();
            }

            if(fragment2 != null){
                fragmentManager.beginTransaction().hide(fragment2).commit();
            }

            if(fragment3 != null){
                fragmentManager.beginTransaction().hide(fragment3).commit();
            }

            if(fragment4 != null){
                fragmentManager.beginTransaction().hide(fragment4).commit();
            }
            // getFragmentManager().beginTransaction().replace(R.id.main_frame, fragment1).commit();

        } else if (current_fragment.equals("second")){
            getFragmentManager().beginTransaction().replace(R.id.main_frame, fragment2).commit();

        } else if (current_fragment.equals("third")){
            getFragmentManager().beginTransaction().replace(R.id.main_frame, fragment3).commit();

        } else if (current_fragment.equals("fourth")){
            getFragmentManager().beginTransaction().replace(R.id.main_frame, fragment4).commit();

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
