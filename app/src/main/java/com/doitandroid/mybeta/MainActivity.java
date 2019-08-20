package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Space;

import com.airbnb.lottie.LottieAnimationView;
import com.doitandroid.mybeta.homeping.HomePingAdapater;
import com.doitandroid.mybeta.fragment.HomeFragment;
import com.doitandroid.mybeta.fragment.MyFragment2;
import com.doitandroid.mybeta.fragment.MyFragment3;
import com.doitandroid.mybeta.fragment.MyFragment4;
import com.doitandroid.mybeta.homeping.HomePingItem;
import com.doitandroid.mybeta.homeping.HomePingItemDecoration;
import com.doitandroid.mybeta.homeping.HomePingItemNarrowDecoration;
import com.doitandroid.mybeta.utils.UtilsCollection;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "MainActivityTAG";

    final static int REQUEST_ADD_POST = 10001;
    final static int REQUEST_PING_SEARCH = 10002;

    String current_fragment = "home";
    String current_ping_num;
    Boolean currentPingClicked, currentPingIsWide;

    int window_width, ping_small_wrapper_width, space_width, pingXdiffer, pingXdifferPx, pingAndSpacePx, pingPxDp;
    Toolbar toolbar;

    FrameLayout btn_home, btn_noti, btn_search, btn_user;

    CoordinatorLayout btn_add_post;

    LinearLayout home_ping_ll, home_ping_recent_ll;
    AppCompatTextView home_ping_tv;

    CoordinatorLayout main_ping_fl, main_ping_swiping_text_cl, main_whole_wrapper, main_dim_wrapper, main_ping_progress_bar_cl, main_ping_search;


    RecyclerView rv_ping;
    Space space1, space2;

    LottieAnimationView lav_home, lav_noti, lav_search, lav_user;

    RecyclerView recyclerView;
    HorizontalScrollView horizontalScrollView;


    androidx.fragment.app.Fragment fragment_home;
    androidx.fragment.app.Fragment fragment_notification;
    androidx.fragment.app.Fragment fragment_search;
    androidx.fragment.app.Fragment fragment_user;

    FragmentManager fragmentManager;


    ArrayList<HomePingItem> homePingItemArrayList, homePingItemRecentArrayList;
    HomePingAdapater homePingAdapater;

    HomePingItemDecoration homePingItemDecoration;
    HomePingItemNarrowDecoration homePingItemNarrowDecoration;


    UtilsCollection utilsCollection;
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
        if (sp.getInt("auto_login", 0) == 0) {
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

        findViews();


        fragment_home = new HomeFragment();
        fragment_notification = new MyFragment2();
        fragment_search = new MyFragment3();
        fragment_user = new MyFragment4();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().add(R.id.main_frame, fragment_notification).commit();
        fragmentManager.beginTransaction().add(R.id.main_frame, fragment_home).commit();

//        fragmentManager.beginTransaction().show(fragment_notification).commit();
//        fragmentManager.beginTransaction().hide(fragment_home).commit();


        currentPingClicked = false;
        currentPingIsWide = false;
        // fragment init

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


        utilsCollection = new UtilsCollection(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        horizontalScrollView = findViewById(R.id.main_hsv);

        window_width = displayMetrics.widthPixels;
        ping_small_wrapper_width = (getResources().getDimensionPixelSize(R.dimen.home_ping_width) * 3) + (getResources().getDimensionPixelSize(R.dimen.home_ping_space) * 2);
        space_width = (window_width - ping_small_wrapper_width) / 2;


        pingXdiffer = utilsCollection.pxToDp(this, horizontalScrollView.getScrollX()) % 100;
        pingXdifferPx = utilsCollection.dpToPx(this, pingXdiffer);

        pingPxDp = getResources().getDimensionPixelSize(R.dimen.home_ping_width);

        pingAndSpacePx = utilsCollection.dpToPx(this, 72);

        Log.d(TAG, utilsCollection.pxToDp(this, window_width)+"dp");


        space1 = findViewById(R.id.space_1);
        space2 = findViewById(R.id.space_2);

        // make ping wrapper narrower
        pingWrapperNarrower();
        //


        // make ping list
        ArrayList<Integer> rawArrayList = new ArrayList<>();

        homePingItemArrayList = new ArrayList<>();
        homePingItemRecentArrayList = new ArrayList<>();

        while (rawArrayList.size()<8){
            Integer randomInt = utilsCollection.randomInt(0, ConstantAnimations.map.size() - 1);

            for (Map.Entry<String, Integer> entry: ConstantAnimations.map.entrySet()){
                if (entry.getKey().equals((randomInt+1)+"")){
                    Integer rawInteger = entry.getValue();
                    if(!rawArrayList.contains(rawInteger)){
                        rawArrayList.add(rawInteger);

                        homePingItemArrayList.add(new HomePingItem(rawInteger, entry.getKey(), false));
                        homePingItemRecentArrayList.add(new HomePingItem(rawInteger, entry.getKey(), false));

                    }
                }
            }
        }

        home_ping_ll = findViewById(R.id.main_hsv_ll);


        int index = 0;
        for (final HomePingItem addItem: homePingItemArrayList){

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.home_ping_recyclerview_item, home_ping_ll, false);

            addItem.setView(view);

            addItem.setAnimation();
            LottieAnimationView lottieAnimationView = addItem.getLottieAnimationView();

            lottieAnimationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!addItem.getClicked()){
                        for(HomePingItem addedItem: homePingItemArrayList){
                            addedItem.setClicked(false);
                        }
                        for(HomePingItem addedItem: homePingItemRecentArrayList){
                            addedItem.setClicked(false);
                        }
                        addItem.play();
                        addItem.setClicked(true);
                        for (Map.Entry<String, String> entry: ConstantAnimations.textMap.entrySet()){
                            if (entry.getKey().equals(addItem.getNumString())){
                                home_ping_tv.setText(entry.getValue());
                            }
                        }

                        currentPingClicked = true;

                        currentPingIsWide = true;

                        current_ping_num = addItem.getNumString();

                        pingWrapperWider();

                    }
                }
            });

            AppCompatImageView imageView = addItem.getImageView();

            if (index != 0){
                Space space = new Space(this);
                space.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.home_ping_space), ViewGroup.LayoutParams.MATCH_PARENT));
                home_ping_ll.addView(space, 1);
            }
            home_ping_ll.addView(view, 1);

            index++;


        }


        home_ping_recent_ll = findViewById(R.id.main_recent_hsv_ll);
        int recentIndex = 0;
        for (final HomePingItem addItem: homePingItemRecentArrayList){

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.home_ping_recyclerview_item, home_ping_recent_ll, false);

            addItem.setView(view);

            addItem.setAnimation();
            LottieAnimationView lottieAnimationView = addItem.getLottieAnimationView();

            lottieAnimationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!addItem.getClicked()){

                        for(HomePingItem addedItem: homePingItemRecentArrayList){
                            addedItem.setClicked(false);
                        }
                        for(HomePingItem addedItem: homePingItemArrayList){
                            addedItem.setClicked(false);
                        }

                        addItem.play();
                        addItem.setClicked(true);
                        for (Map.Entry<String, String> entry: ConstantAnimations.textMap.entrySet()){
                            if (entry.getKey().equals(addItem.getNumString())){
                                home_ping_tv.setText(entry.getValue());

                            }
                        }

                        currentPingClicked = true;
                        currentPingIsWide = true;

                        current_ping_num = addItem.getNumString();

                        pingWrapperWider();

                    }
                }
            });

            lottieAnimationView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // additem 이 클릭된 상태일 경우에만 진행.
                    // 길게 클릭시에 다른 곳 클릭 불가하게 now pressing 변수를 설정.
                    // touch down 이랑 up
                    return false;
                }
            });

            if (recentIndex != 0){
                Space space = new Space(this);
                space.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.home_ping_space), ViewGroup.LayoutParams.MATCH_PARENT));
                home_ping_recent_ll.addView(space, 1);
            }
            home_ping_recent_ll.addView(view, 1);

            recentIndex++;


        }


        horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == event.ACTION_DOWN){
                    // 처음 누를 때
                    Log.d(TAG, "ACTION_DOWN");

                    if(main_ping_swiping_text_cl.getVisibility() == View.INVISIBLE){
                        main_ping_swiping_text_cl.setVisibility(View.VISIBLE);
                    }

                } else if (event.getAction() == event.ACTION_MOVE){
                    // 움직일 떄
                    //if (currentPingClicked && !currentPingIsWide) {
                    if(main_ping_swiping_text_cl.getVisibility() == View.INVISIBLE){
                        main_ping_swiping_text_cl.setVisibility(View.VISIBLE);
                    }
                    Log.d(TAG, "ACTION_MOVE");

                    Log.d(TAG, "scrollX: " + horizontalScrollView.getScrollX()+ "");
                    Log.d(TAG, "scrollX DP: " + pxToDp(getApplicationContext(), horizontalScrollView.getScrollX()));


                } else if (event.getAction() == event.ACTION_UP){
                    // 떨어질 때

                    if(main_ping_swiping_text_cl.getVisibility() == View.VISIBLE){
                        main_ping_swiping_text_cl.setVisibility(View.INVISIBLE);
                        if (main_whole_wrapper.getVisibility() != View.VISIBLE){
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    pingPositionArrange();
                                }
                            });
                        }
                    }
                    Log.d(TAG, "ACTION_UP");
                    Log.d(TAG, "scrollX: " + horizontalScrollView.getScrollX()+ "");
                    Log.d(TAG, "scrollX DP: " + pxToDp(getApplicationContext(), horizontalScrollView.getScrollX()));
                }

                return false;
                //return false 는 그 이후까지 이벤트 전달.
            }
        });

        // main whole wrapper cover on

//        homePingAdapater.notifyDataSetChanged();


    }

    private void findViews() {
        btn_home = findViewById(R.id.tb_fl_home);
        btn_noti = findViewById(R.id.tb_fl_notification);
        btn_search = findViewById(R.id.tb_fl_search);
        btn_user = findViewById(R.id.tb_fl_user);

        btn_home.setOnClickListener(this);
        btn_noti.setOnClickListener(this);

        // main ping ll
        main_ping_fl = findViewById(R.id.main_ping_fl);

        main_ping_swiping_text_cl = findViewById(R.id.main_ping_swiping_text_cl);

        home_ping_tv = findViewById(R.id.main_ping_tv);
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
        pingLineNarrowParams.gravity = Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;
        pingLineNarrowParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.home_ping_bottom_margin);

        main_ping_fl.setLayoutParams(pingLineNarrowParams);
        main_ping_fl.setElevation(getResources().getDimensionPixelSize(R.dimen.home_pings_elevation));
        main_ping_fl.setOutlineProvider(null);
        // make ping wrapper line space
        LinearLayout.LayoutParams narrowParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        space1.setLayoutParams(narrowParams);
        space2.setLayoutParams(narrowParams);

        main_whole_wrapper.setVisibility(View.INVISIBLE);
        main_dim_wrapper.setVisibility(View.INVISIBLE);
        // 단순히 여기서 하면 안된다. 쓰레드시에 프로그레스바 필요한 경우에 보이게 해야 한다. 그리고 쓰레드를 따로 돌려서 푸시가 아니면
        // 지워지게 하는 것을 구현해놔야 한다. 안 사라지는 경우가 있기 때문.
        main_ping_progress_bar_cl.setVisibility(View.INVISIBLE);

        currentPingClicked = false;

        pingPositionArrange();

    }

    public void pingWrapperWider() {

        CoordinatorLayout.LayoutParams ping_line_params = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        ping_line_params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;
        ping_line_params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.home_ping_bottom_margin);
        main_ping_fl.setLayoutParams(ping_line_params);
        main_ping_fl.setElevation(getResources().getDimensionPixelSize(R.dimen.home_pings_elevation));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(space_width, ViewGroup.LayoutParams.MATCH_PARENT);
        space1.setLayoutParams(params);
        space2.setLayoutParams(params);

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
                break;
            case "user":
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

        switch (v.getId()) {

            case R.id.tb_fl_home:
                home_btn_clicked();
                pingPositionArrange();

                //                HomePingItem inithomePingItem = new HomePingItem(ConstantAnimations.list.get(0), false);
//                homePingItemArrayList.add(inithomePingItem);

//                homePingAdapater.notifyDataSetChanged();
//                Log.d(TAG, rv_ping.getWidth()+"");
//                homePingAdapater.notifyItemInserted(homePingItemArrayList.size());
//                rv_ping.scrollTo(rv_ping.getWidth(), rv_ping.getScrollY());
//                homePingAdapater.notifyItemRangeInserted(0, homePingItemArrayList.size());

                // 이런 것 따라서 애니메이션이 뭔가 다르다.
//                rv_ping.addItemDecoration(new HomePingItemDecoration(this));

//                FrameLayout ping_fl = findViewById(R.id.main_ping_fl);
                break;

            case R.id.tb_fl_notification:
                noti_btn_clicked();
                break;

            case R.id.main_addpost: /* add post activity open */
                Intent intent_add_post = new Intent(this, AddPostActivity.class);

                intent_add_post.putExtra(Constants.INTENT_HAS_PING, currentPingClicked);
                if (currentPingClicked){
                    intent_add_post.putExtra(Constants.INTENT_PING_NUM, current_ping_num);
                }

                startActivityForResult(intent_add_post, REQUEST_ADD_POST);
                overridePendingTransition(R.anim.slide_up, R.anim.stay);

                break;
            case R.id.main_whole_wrapper:
                pingWrapperNarrower();
                Log.d(TAG, "mainWholeWrapper");
                break;

            case R.id.main_dim_wrapper_search_cl:
                Intent intent_ping_search = new Intent(this, PingSearchActivity.class);

                intent_ping_search.putExtra("got_ping", currentPingClicked);
                if (currentPingClicked){
                    intent_ping_search.putExtra("ping_num", current_ping_num);
                }

                startActivityForResult(intent_ping_search, REQUEST_PING_SEARCH);
                overridePendingTransition(0, 0); // 아무것도 없는 전환
                
                break;
            default:

        }
    }

    public void pingPositionArrange() {
        // 우선 40 + 24 = 64로 나눠서 나머지를 구한다. 얼마만큼 오차가 있는지 확인하는 것.
        final int xDiffer = pxToDp(this, horizontalScrollView.getScrollX()) % 64;

        Log.d(TAG, xDiffer + "");
        //
        if (xDiffer <= 24) {
            // 계산은 모두 px 단위로 바꿔서 한다.
            horizontalScrollView.smoothScrollTo(horizontalScrollView.getScrollX() - dpToPx(this, xDiffer), horizontalScrollView.getScrollY());

        } else {
            horizontalScrollView.smoothScrollTo(horizontalScrollView.getScrollX() + (dpToPx(this, 64)-dpToPx(this, xDiffer)), horizontalScrollView.getScrollY());


        }
    }

    public void noti_btn_clicked() {
        if (current_fragment.equals("noti")) {
            return;
        }

        Log.d("MainActivityOnClick", "noti");
        lottie_tb_released_case(current_fragment);
        lottie_tb_clicked_case("noti");

        // 이게 추가된 부분.
//                Fragment current_frag2 = fragmentManager.findFragmentById(R.id.main_frame);
//                fragmentManager.beginTransaction().hide(current_frag2).commit();

        if (fragment_notification == null) {
            fragment_notification = new MyFragment2();
            fragmentManager.beginTransaction().add(R.id.main_frame, fragment_notification).commit();

        }
//                if(fragment_home != null){
//                fragmentManager.beginTransaction().hide(fragment_home).commit();
//                }

        ArrayList<Fragment> fragments2 = (ArrayList<Fragment>) fragmentManager.getFragments();
        for (Fragment fragment : fragments2) {

            if (fragment != null && fragment.isVisible()) {
                Log.d("Main_Fragment_LIST", fragment.getClass().getSimpleName());
                fragmentManager.beginTransaction().hide(fragment).commit();

            }
        }
        if (fragment_notification != null) {
            fragmentManager.beginTransaction().show(fragment_notification).commit();
        }
        Fragment current_frag2 = fragmentManager.findFragmentById(R.id.main_frame);
//                fragmentManager.beginTransaction().hide(current_frag2).commit();
        Log.d("Main_Fragment", current_frag2.getClass().getSimpleName());

        current_fragment = "noti";
    }

    public void home_btn_clicked() {
        if (current_fragment.equals("home")) {
            return;
        }

        Log.d("MainActivityOnClick", "home");

        lottie_tb_released_case(current_fragment);
        lottie_tb_clicked_case("home");

        // 이게 추가된 부분.


        if (fragment_home == null) {
            fragment_home = new HomeFragment();
            fragmentManager.beginTransaction().add(R.id.main_frame, fragment_home).commit();
        }

        if (fragment_home != null) {
            fragmentManager.beginTransaction().show(fragment_home).commit();
        }


//                if(fragment_notification != null){
//                fragmentManager.beginTransaction().hide(fragment_notification).commit();
//                }
        ArrayList<Fragment> fragments = (ArrayList<Fragment>) fragmentManager.getFragments();
        for (Fragment fragment : fragments) {

            if (fragment != null && fragment.isVisible()) {
                Log.d("Main_Fragment_LIST", fragment.getClass().getSimpleName());
                fragmentManager.beginTransaction().hide(fragment).commit();

            }
        }
        Fragment current_frag = fragmentManager.findFragmentById(R.id.main_frame);
//                fragmentManager.beginTransaction().hide(current_frag).commit();
        Log.d("Main_Fragment", current_frag.getClass().getSimpleName());

        current_fragment = "home";
    }


    public static int dpToPx(Context context, int dp){
        return dp * (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int pxToDp(Context context, int px){
        return px / (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}


/**
 *
 *
 *
 *         // make ping list
 *         ArrayList<Integer> rawArrayList = new ArrayList<>();
 *
 *         while (rawArrayList.size()<8){
 *             Integer randomInt = utilsCollection.randomInt(0, ConstantAnimations.list.size() - 1);
 *             Integer rawInteger = ConstantAnimations.list.get(randomInt);
 *             if(!rawArrayList.contains(rawInteger)){
 *                 rawArrayList.add(rawInteger);
 *             }
 *         }
 *
 *         homePingItemArrayList = new ArrayList<>();
 *
 *         for (Integer rawInteger: rawArrayList){
 *             homePingItemArrayList.add(new HomePingItem(rawInteger, false));
 *         }
 *
 *         home_ping_ll = findViewById(R.id.main_hsv_ll);
 *
 *
 *         int index = 0;
 *         for (final HomePingItem addItem: homePingItemArrayList){
 *
 *             LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 *             View view = inflater.inflate(R.layout.home_ping_recyclerview_item, home_ping_ll, false);
 *
 *             addItem.setView(view);
 *
 *             addItem.setAnimation();
 *             LottieAnimationView lottieAnimationView = addItem.getLottieAnimationView();
 *
 *             lottieAnimationView.setOnClickListener(new View.OnClickListener() {
 *                 @Override
 *                 public void onClick(View v) {
 *                     if(!addItem.getClicked()){
 *                         for(HomePingItem addedItem: homePingItemArrayList){
 *                             addedItem.setClicked(false);
 *                         }
 *
 *                         addItem.play();
 *                         addItem.setClicked(true);
 *
 *                         currentPingClicked = true;
 *                         currentPingIsWide = true;
 *                         pingWrapperWider();
 *
 *                     }
 *                 }
 *             });
 *
 *             if (index != 0){
 *                 Space space = new Space(this);
 *                 space.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.home_ping_space), ViewGroup.LayoutParams.MATCH_PARENT));
 *                 home_ping_ll.addView(space, 1);
 *             }
 *             home_ping_ll.addView(view, 1);
 *
 *             index++;
 *
 *
 *         }
 *         **/