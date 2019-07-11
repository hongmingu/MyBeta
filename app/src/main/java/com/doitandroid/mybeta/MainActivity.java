package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.doitandroid.mybeta.fragment.HomeFragment;
import com.doitandroid.mybeta.fragment.MyFragment1;
import com.doitandroid.mybeta.fragment.MyFragment2;
import com.doitandroid.mybeta.fragment.MyFragment3;
import com.doitandroid.mybeta.fragment.MyFragment4;
import com.doitandroid.mybeta.utils.UtilsCollection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String current_fragment = "home";
    Toolbar toolbar;

    FrameLayout btn_home, btn_noti, btn_search, btn_user;

    FrameLayout ping_wrapper;

    LottieAnimationView lav_home, lav_noti, lav_search, lav_user;

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


        toolbar = findViewById(R.id.tb_tb);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btn_home = findViewById(R.id.tb_fl_home);
        btn_noti = findViewById(R.id.tb_fl_notification);
        btn_search = findViewById(R.id.tb_fl_search);
        btn_user = findViewById(R.id.tb_fl_user);

        btn_home.setOnClickListener(this);
        btn_noti.setOnClickListener(this);

        lav_home = findViewById(R.id.tb_lav_home);
        lav_noti = findViewById(R.id.tb_lav_notification);
        lav_search =findViewById(R.id.tb_lav_search);
        lav_user = findViewById(R.id.tb_lav_user);

        fragment_home = new HomeFragment();
        fragment_notification = new MyFragment2();
        fragment_search = new MyFragment3();
        fragment_user = new MyFragment4();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().add(R.id.main_frame, fragment_notification).commit();
        fragmentManager.beginTransaction().add(R.id.main_frame, fragment_home).commit();

//        fragmentManager.beginTransaction().show(fragment_notification).commit();
//        fragmentManager.beginTransaction().hide(fragment_home).commit();


        ping_wrapper = findViewById(R.id.main_ping_wrapper);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        // fragment init

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
    private void lottie_tb_released_case(String tb_tab_before){
        switch (tb_tab_before){
            case "home":
                lottie_tb_play(lav_home, 2.4f);
                break;
            case "noti":
                lottie_tb_play(lav_noti, 2.4f);
                break;
            case "search":
                break;
            case "user":
                break;
            default:
                break;
        }
    }

    private void lottie_tb_clicked_case(String tb_tab) {

        switch (tb_tab){
            case "home":
                lottie_tb_play(lav_home, -2.4f);
                break;
            case "noti":
                lottie_tb_play(lav_noti, -2.4f);
                break;
            case "search":
                break;
            case "user":
                break;
            default:
                break;
        }
    }

    private void lottie_tb_play(LottieAnimationView lottieAnimationView, Float speed) {
        lottieAnimationView.pauseAnimation();
        lottieAnimationView.setMinAndMaxProgress(0f, 1f);
        lottieAnimationView.setSpeed(speed);
        lottieAnimationView.playAnimation();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.tb_fl_home: /** Start a new Activity MyCards.java */
                if(current_fragment.equals("home")){
                    return;
                }

                Log.d("MainActivityOnClick", "home");

                lottie_tb_released_case(current_fragment);
                lottie_tb_clicked_case("home");

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

                current_fragment = "home";
                break;

            case R.id.tb_fl_notification: /** AlerDialog when click on Exit */


                if(current_fragment.equals("noti")){
                    return;
                }

                Log.d("MainActivityOnClick", "noti");
                lottie_tb_released_case(current_fragment);
                lottie_tb_clicked_case("noti");

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

                current_fragment = "noti";
                break;
            default:

        }
    }

}
