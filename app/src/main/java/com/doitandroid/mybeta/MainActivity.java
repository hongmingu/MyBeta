package com.doitandroid.mybeta;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.doitandroid.mybeta.fragment.NotiFragment;
import com.doitandroid.mybeta.fragment.SearchFragment;
import com.doitandroid.mybeta.fragment.UserFragment;
import com.doitandroid.mybeta.homeping.HomePingAdapater;
import com.doitandroid.mybeta.fragment.HomeFragment;
import com.doitandroid.mybeta.ping.PingShownItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.LoggedInAPIClient;
import com.doitandroid.mybeta.utils.UtilsCollection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "MainActivityTAG";

    final static int REQUEST_ADD_POST = 10001;
    final static int REQUEST_PING_SEARCH = 10002;

    Context context;

    String current_fragment = "home";
    String current_ping_num;
    Boolean currentPingClicked, currentPingIsWide;

    int window_width, ping_small_wrapper_width, space_width, pingXdiffer, pingXdifferPx, pingAndSpacePx, pingPxDp;
    Toolbar toolbar;

    FrameLayout btn_home, btn_noti, btn_search, btn_user;

    CoordinatorLayout btn_add_post;

    LinearLayoutCompat main_ping_for_you_ll, main_ping_recommend_ll;
    AppCompatTextView main_ping_tv;

    CoordinatorLayout main_ping_fl, main_ping_swiping_text_cl, main_whole_wrapper, main_dim_wrapper, main_ping_progress_bar_cl, main_ping_search;

    LottieAnimationView lav_home, lav_noti, lav_search, lav_user;

    androidx.fragment.app.Fragment fragment_home;
    androidx.fragment.app.Fragment fragment_notification;
    androidx.fragment.app.Fragment fragment_search;
    androidx.fragment.app.Fragment fragment_user;

    FragmentManager fragmentManager;

    ArrayList<PingShownItem> forYouPingShownItemArrayList, allPingShownItemArrayList, recommendPingShownItemArrayList;
    ArrayList<View> wrapperVisibleArrayList;
    HomePingAdapater homePingAdapater;

    APIInterface apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_display();
        ////////////////////////////////////////////////////////////////////////////////////////////

        // 자동 로그인 구현
        auto_login();
        set_toolbar();
        findViews();

        apiInterface = getApiInterface();
        context = this;

        currentPingClicked = false;
        currentPingIsWide = false;

        setFragments();


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        window_width = displayMetrics.widthPixels;

        // make ping wrapper hide


        // ArrayList Init
        arrayListInit();

        // make ping list
        refreshForYouPings();
        refreshRecommendPings();


        // main whole wrapper cover on


    }

    private void refreshRecommendPings(){
        Call<JsonObject> call = apiInterface.refresh_recommend_pings();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        int rc = jsonObject.get("rc").getAsInt();
                        JsonObject content = jsonObject.get("content").getAsJsonObject();

                        if (rc != ConstantIntegers.SUCCEED_RESPONSE) {
                            // sign up 실패
                            call.cancel();
                            return;
                        }
                        JsonArray jsonArray = content.getAsJsonArray("ping_ids");
                        for (JsonElement item : jsonArray) {
                            String gotPingID = item.getAsString();
                            List<PingItem> pingConstant = ConstantAnimations.pingList;
                            // Constant 리스트에서 정보를 파악함.
                            for (PingItem pingItem : pingConstant) {
                                if (pingItem.getPingID().equals(gotPingID)) {
                                    PingShownItem pingShownItem = new PingShownItem(gotPingID);
                                    allPingShownItemArrayList.add(pingShownItem);
                                    recommendPingShownItemArrayList.add(pingShownItem);
                                }
                            }


                        }


                        int default_ping_index = 0;

                        for (final PingShownItem pingShownItem : recommendPingShownItemArrayList) {

                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.home_ping_recyclerview_item, main_ping_recommend_ll, false);
                            pingShownItem.setView(view);
                            pingShownItem.getLottieAnimationView().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    removeAllClicked();
                                    pingShownItem.setIsClicked(true);
                                    pingShownItem.playAnimation();
                                }
                            });

                            default_ping_index++;

                            main_ping_recommend_ll.addView(view);
                            if (default_ping_index != 5) {
                                Space space = new Space(getApplicationContext());
                                space.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.home_ping_space), ViewGroup.LayoutParams.MATCH_PARENT));
                                main_ping_recommend_ll.addView(space);
                            }
                        }
                        // 접속 성공.

                    }

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();

            }
        });
    }

    private void arrayListInit() {
        allPingShownItemArrayList = new ArrayList<>();
        forYouPingShownItemArrayList = new ArrayList<>();
        recommendPingShownItemArrayList = new ArrayList<>();
        wrapperVisibleArrayList = new ArrayList<>();
    }

    private void setFragments() {
        fragment_home = new HomeFragment();
        fragment_notification = new NotiFragment();
        fragment_search = new SearchFragment();
        fragment_user = new UserFragment();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().add(R.id.main_frame, fragment_user).commit();
        fragmentManager.beginTransaction().add(R.id.main_frame, fragment_search).commit();
        fragmentManager.beginTransaction().add(R.id.main_frame, fragment_notification).commit();
        fragmentManager.beginTransaction().add(R.id.main_frame, fragment_home).commit();


        if (fragment_home == null) {
            fragment_home = new HomeFragment();
            fragmentManager.beginTransaction().add(R.id.main_frame, fragment_home).commit();
        }

        if (fragment_home != null) {
            fragmentManager.beginTransaction().show(fragment_home).commit();

        }

        if (fragment_notification != null) {
            fragmentManager.beginTransaction().hide(fragment_notification).commit();
        }
        if (fragment_search != null) {
            fragmentManager.beginTransaction().hide(fragment_search).commit();
        }
        if (fragment_user != null) {
            fragmentManager.beginTransaction().hide(fragment_user).commit();
        }
    }

    private APIInterface getApiInterface(){
        SharedPreferences sp = getSharedPreferences(ConstantStrings.INIT_APP, MODE_PRIVATE);
        String auth_token = sp.getString(ConstantStrings.TOKEN, ConstantStrings.REMOVE_TOKEN);
        APIInterface apiInterface = LoggedInAPIClient.getClient(auth_token).create(APIInterface.class);
        return apiInterface;
    }

    private void refreshForYouPings() {

        Call<JsonObject> call = apiInterface.refresh_for_you_pings();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        int rc = jsonObject.get("rc").getAsInt();
                        JsonObject content = jsonObject.get("content").getAsJsonObject();

                        if (rc != ConstantIntegers.SUCCEED_RESPONSE) {
                            // sign up 실패
                            call.cancel();
                            return;
                        }
                        JsonArray jsonArray = content.getAsJsonArray("ping_ids");
                        for (JsonElement item : jsonArray) {
                            String gotPingID = item.getAsString();
                            List<PingItem> pingConstant = ConstantAnimations.pingList;
                            // Constant 리스트에서 정보를 파악함.
                            for (PingItem pingItem : pingConstant) {
                                if (pingItem.getPingID().equals(gotPingID)) {
                                    PingShownItem pingShownItem = new PingShownItem(gotPingID);
                                    allPingShownItemArrayList.add(pingShownItem);
                                    forYouPingShownItemArrayList.add(pingShownItem);
                                }
                            }


                        }


                        int default_ping_index = 0;

                        for (final PingShownItem pingShownItem : forYouPingShownItemArrayList) {

                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.home_ping_recyclerview_item, main_ping_for_you_ll, false);
                            pingShownItem.setView(view);
                            pingShownItem.getLottieAnimationView().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    removeAllClicked();
                                    pingShownItem.setIsClicked(true);
                                    pingShownItem.playAnimation();
                                }
                            });

                            default_ping_index++;

                            main_ping_for_you_ll.addView(view);
                            if (default_ping_index != 5) {
                                Space space = new Space(getApplicationContext());
                                space.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.home_ping_space), ViewGroup.LayoutParams.MATCH_PARENT));
                                main_ping_for_you_ll.addView(space);
                                if (default_ping_index == 1){
                                    wrapperVisibleArrayList.add(view);
                                    wrapperVisibleArrayList.add(space);
                                } else if (default_ping_index == 4){
                                    wrapperVisibleArrayList.add(space);
                                }
                            } else {
                                wrapperVisibleArrayList.add(view);
                            }
                        }
                        // 접속 성공.

                    }

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();

            }
        });

    }

    public void removeAllClicked() {
        for (PingShownItem item: allPingShownItemArrayList){
            item.setIsClicked(false);
        }
    }

    private void set_toolbar() {
        toolbar = findViewById(R.id.tb_tb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void auto_login() {
        SharedPreferences sp = getSharedPreferences(ConstantStrings.INIT_APP, MODE_PRIVATE);

        if (sp.getInt(ConstantStrings.AUTO_LOGIN, ConstantIntegers.IS_NOT_LOGINED) != ConstantIntegers.IS_LOGINED) {
            Intent intent = new Intent(this, FrontActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            // not auto login
        }
    }

    private void init_display() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        UtilsCollection utilsCollection = new UtilsCollection(this);
        utilsCollection.makeStatusBarColor(ConstantStrings.TRAY_COLOR);
    }

    private void findViews() {
        btn_home = findViewById(R.id.tb_fl_home);
        btn_noti = findViewById(R.id.tb_fl_notification);
        btn_search = findViewById(R.id.tb_fl_search);
        btn_user = findViewById(R.id.tb_fl_user);

        btn_home.setOnClickListener(this);
        btn_noti.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        btn_user.setOnClickListener(this);

        // main ping ll
        main_ping_for_you_ll = findViewById(R.id.main_ping_for_you_ll);
        main_ping_recommend_ll = findViewById(R.id.main_ping_recommend_ll);

        main_ping_fl = findViewById(R.id.main_ping_fl);

        main_ping_search = findViewById(R.id.main_dim_wrapper_search_cl);

        main_ping_search.setOnClickListener(this);

        // main layouts
        main_whole_wrapper = findViewById(R.id.main_whole_wrapper);
        main_dim_wrapper = findViewById(R.id.main_dim_wrapper);
        main_ping_progress_bar_cl = findViewById(R.id.main_ping_progress_bar_cl);

        main_whole_wrapper.setOnClickListener(this);
        main_dim_wrapper.setOnClickListener(this);


//        rv_ping = findViewById(R.id.main_ping_rv);


        // add post main button
        btn_add_post = findViewById(R.id.main_addpost);
        btn_add_post.setOnClickListener(this);

        lav_home = findViewById(R.id.tb_lav_home);
        lav_noti = findViewById(R.id.tb_lav_notification);
        lav_search = findViewById(R.id.tb_lav_search);
        lav_user = findViewById(R.id.tb_lav_user);


    }

    public void pingWrapperNarrower() {
        CoordinatorLayout.LayoutParams pingLineNarrowParams = new CoordinatorLayout.LayoutParams(ping_small_wrapper_width, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        pingLineNarrowParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        pingLineNarrowParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.home_ping_bottom_margin);

        main_ping_fl.setLayoutParams(pingLineNarrowParams);
        main_ping_fl.setElevation(getResources().getDimensionPixelSize(R.dimen.home_pings_elevation));
        main_ping_fl.setOutlineProvider(null);
        // make ping wrapper line space
        LinearLayout.LayoutParams narrowParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
/*        space1.setLayoutParams(narrowParams);
        space2.setLayoutParams(narrowParams);*/

        main_whole_wrapper.setVisibility(View.INVISIBLE);
        main_dim_wrapper.setVisibility(View.INVISIBLE);
        // 단순히 여기서 하면 안된다. 쓰레드시에 프로그레스바 필요한 경우에 보이게 해야 한다. 그리고 쓰레드를 따로 돌려서 푸시가 아니면
        // 지워지게 하는 것을 구현해놔야 한다. 안 사라지는 경우가 있기 때문.
        main_ping_progress_bar_cl.setVisibility(View.INVISIBLE);

        currentPingClicked = false;


    }

    public void pingWrapperWider() {

        CoordinatorLayout.LayoutParams ping_line_params = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        ping_line_params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        ping_line_params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.home_ping_bottom_margin);
        main_ping_fl.setLayoutParams(ping_line_params);
        main_ping_fl.setElevation(getResources().getDimensionPixelSize(R.dimen.home_pings_elevation));

        main_whole_wrapper.setVisibility(View.VISIBLE);
        main_dim_wrapper.setVisibility(View.VISIBLE);
        main_ping_progress_bar_cl.setVisibility(View.VISIBLE);

    }


    private void lottie_tb_released_case(String tb_tab_before) {
        switch (tb_tab_before) {
            case "home":
                lottie_tb_play(lav_home, 2.4f);
                break;
            case "noti":
                lottie_tb_play(lav_noti, 2.4f);
                break;
            case "search":
                lottie_tb_play(lav_search, 2.4f);

                break;
            case "user":
                lottie_tb_play(lav_user, 2.4f);
                break;
            default:
                break;
        }
    }

    private void lottie_tb_clicked_case(String tb_tab) {

        switch (tb_tab) {
            case "home":
                lottie_tb_play(lav_home, -2.4f);
                break;
            case "noti":
                lottie_tb_play(lav_noti, -2.4f);
                break;
            case "search":
                lottie_tb_play(lav_search, -2.4f);
                break;
            case "user":
                lottie_tb_play(lav_user, -2.4f);

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

    private void ping_side_invisible() {
        for (View view: wrapperVisibleArrayList){
            view.setVisibility(View.INVISIBLE);
        }
    }

    private void ping_side_visible() {
        for (View view: wrapperVisibleArrayList){
            view.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tb_fl_home:
//                home_btn_clicked();
                tb_btn_clicked(ConstantStrings.FRAGMENT_HOME);
                ping_side_invisible();

                break;

            case R.id.tb_fl_notification:
                tb_btn_clicked(ConstantStrings.FRAGMENT_NOTI);
                ping_side_visible();
//                noti_btn_clicked();
                break;

            case R.id.tb_fl_user:
                tb_btn_clicked(ConstantStrings.FRAGMENT_USER);

                // narrower 일 때 space비롯해서 invisible 걸어버리자. tag 붙이면 됨.
                break;
            case R.id.tb_fl_search:
                tb_btn_clicked(ConstantStrings.FRAGMENT_SEARCH);
                break;


            case R.id.main_addpost: /* add post activity open */
                Intent intent_add_post = new Intent(this, AddPostActivity.class);

                intent_add_post.putExtra(ConstantStrings.INTENT_HAS_PING, currentPingClicked);
                if (currentPingClicked) {
                    intent_add_post.putExtra(ConstantStrings.INTENT_PING_NUM, current_ping_num);
                }

                startActivityForResult(intent_add_post, REQUEST_ADD_POST);
                overridePendingTransition(R.anim.slide_up, R.anim.stay);

                break;
            case R.id.main_whole_wrapper:
                // pingWrapperNarrower();
                Log.d(TAG, "mainWholeWrapper");
                break;

            case R.id.main_dim_wrapper_search_cl:
                Intent intent_ping_search = new Intent(this, PingSearchActivity.class);

                intent_ping_search.putExtra("got_ping", currentPingClicked);
                if (currentPingClicked) {
                    intent_ping_search.putExtra("ping_num", current_ping_num);
                }

                startActivityForResult(intent_ping_search, REQUEST_PING_SEARCH);
                overridePendingTransition(0, 0); // 아무것도 없는 전환

                break;
            default:

        }
    }


    private void tb_btn_clicked(String clicked) {
        if (current_fragment.equals(clicked)) {
            return;
        }

        lottie_tb_released_case(current_fragment);
        lottie_tb_clicked_case(clicked);
        // 이게 추가된 부분.

        ArrayList<Fragment> fragments = null;


        switch (clicked) {
            case ConstantStrings.FRAGMENT_HOME:

                if (fragment_home == null) {
                    fragment_home = new HomeFragment();
                    fragmentManager.beginTransaction().add(R.id.main_frame, fragment_home).commit();
                }
                fragments = (ArrayList<Fragment>) fragmentManager.getFragments();
                for (Fragment fragment : fragments) {
                    if (fragment != null && fragment.isVisible()) {
                        fragmentManager.beginTransaction().hide(fragment).commit();

                    }
                }
                if (fragment_home != null) {
                    fragmentManager.beginTransaction().show(fragment_home).commit();
                }
                break;
            case ConstantStrings.FRAGMENT_NOTI:
                if (fragment_notification == null) {
                    fragment_notification = new NotiFragment();
                    fragmentManager.beginTransaction().add(R.id.main_frame, fragment_notification).commit();
                }
                fragments = (ArrayList<Fragment>) fragmentManager.getFragments();
                for (Fragment fragment : fragments) {
                    if (fragment != null && fragment.isVisible()) {
                        fragmentManager.beginTransaction().hide(fragment).commit();

                    }
                }
                if (fragment_notification != null) {
                    fragmentManager.beginTransaction().show(fragment_notification).commit();
                }
                break;
            case ConstantStrings.FRAGMENT_SEARCH:
                if (fragment_search == null) {
                    fragment_search = new SearchFragment();
                    fragmentManager.beginTransaction().add(R.id.main_frame, fragment_search).commit();
                }
                fragments = (ArrayList<Fragment>) fragmentManager.getFragments();
                for (Fragment fragment : fragments) {
                    if (fragment != null && fragment.isVisible()) {
                        fragmentManager.beginTransaction().hide(fragment).commit();

                    }
                }
                if (fragment_search != null) {
                    fragmentManager.beginTransaction().show(fragment_search).commit();
                }
                break;
            case ConstantStrings.FRAGMENT_USER:
                if (fragment_user == null) {
                    fragment_user = new UserFragment();
                    fragmentManager.beginTransaction().add(R.id.main_frame, fragment_user).commit();
                }
                fragments = (ArrayList<Fragment>) fragmentManager.getFragments();
                for (Fragment fragment : fragments) {
                    if (fragment != null && fragment.isVisible()) {
                        fragmentManager.beginTransaction().hide(fragment).commit();

                    }
                }
                if (fragment_user != null) {
                    fragmentManager.beginTransaction().show(fragment_user).commit();
                }
                break;
        }

//        Fragment current_frag = fragmentManager.findFragmentById(R.id.main_frame);
//        Log.d("Main_Fragment", current_frag.getClass().getSimpleName());
//                fragmentManager.beginTransaction().hide(current_frag).commit();

        current_fragment = clicked;
    }


    public void openSettingActivity() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivityForResult(intent, ConstantIntegers.REQUEST_SETTING_ACTIVITY);
        overridePendingTransition(R.anim.slide_left_in, R.anim.stay); //

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case ConstantIntegers.REQUEST_SETTING_ACTIVITY:

                    if (data.getIntExtra(ConstantStrings.INTENT_LOGOUT_INFO, ConstantIntegers.RESULT_CANCELED) == ConstantIntegers.RESULT_LOGOUTTED) {
                        //logout됨
                        logout();
                        Toast.makeText(this, "is logout", Toast.LENGTH_SHORT).show();

                    } else {
                        //logout 안됨
                    }


                    break;
                default:
                    break;
            }
        }
    }

    private void logout() {

        SharedPreferences sp = getSharedPreferences(ConstantStrings.INIT_APP, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(ConstantStrings.AUTO_LOGIN, ConstantIntegers.IS_NOT_LOGINED);
        editor.putString(ConstantStrings.TOKEN, ConstantStrings.REMOVE_TOKEN);
        editor.commit();

        Intent intent = new Intent(this, FrontActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }


    public static int dpToPx(Context context, int dp) {
        return dp * (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int pxToDp(Context context, int px) {
        return px / (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


}


/**
 * // make ping list
 * ArrayList<Integer> rawArrayList = new ArrayList<>();
 * <p>
 * while (rawArrayList.size()<8){
 * Integer randomInt = utilsCollection.randomInt(0, ConstantAnimations.list.size() - 1);
 * Integer rawInteger = ConstantAnimations.list.get(randomInt);
 * if(!rawArrayList.contains(rawInteger)){
 * rawArrayList.add(rawInteger);
 * }
 * }
 * <p>
 * homePingItemArrayList = new ArrayList<>();
 * <p>
 * for (Integer rawInteger: rawArrayList){
 * homePingItemArrayList.add(new HomePingItem(rawInteger, false));
 * }
 * <p>
 * main_ping_for_you_ll = findViewById(R.id.main_hsv_ll);
 * <p>
 * <p>
 * int index = 0;
 * for (final HomePingItem addItem: homePingItemArrayList){
 * <p>
 * LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 * View view = inflater.inflate(R.layout.home_ping_recyclerview_item, main_ping_for_you_ll, false);
 * <p>
 * addItem.setView(view);
 * <p>
 * addItem.setAnimation();
 * LottieAnimationView lottieAnimationView = addItem.getLottieAnimationView();
 * <p>
 * lottieAnimationView.setOnClickListener(new View.OnClickListener() {
 *
 * @Override public void onClick(View v) {
 * if(!addItem.getClicked()){
 * for(HomePingItem addedItem: homePingItemArrayList){
 * addedItem.setClicked(false);
 * }
 * <p>
 * addItem.play();
 * addItem.setClicked(true);
 * <p>
 * currentPingClicked = true;
 * currentPingIsWide = true;
 * pingWrapperWider();
 * <p>
 * }
 * }
 * });
 * <p>
 * if (index != 0){
 * Space space = new Space(this);
 * space.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.home_ping_space), ViewGroup.LayoutParams.MATCH_PARENT));
 * main_ping_for_you_ll.addView(space, 1);
 * }
 * main_ping_for_you_ll.addView(view, 1);
 * <p>
 * index++;
 * <p>
 * <p>
 * }
 **/