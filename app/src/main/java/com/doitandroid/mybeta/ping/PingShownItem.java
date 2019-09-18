package com.doitandroid.mybeta.ping;

import android.util.Log;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.doitandroid.mybeta.ConstantAnimations;
import com.doitandroid.mybeta.PingItem;
import com.doitandroid.mybeta.R;

public class PingShownItem {
    Integer resInteger;
    String pingID;
    String pingText;
    Boolean isClicked;
    View view;
    LottieAnimationView lottieAnimationView;

    public PingShownItem(String pingID) {
        this.pingID = pingID;


        for (PingItem pingConstantItem: ConstantAnimations.pingList){
            if (pingConstantItem.getPingID().equals(pingID)){
                this.resInteger = pingConstantItem.getPingRes();
                this.pingText = pingConstantItem.getPingText();
            }
        }

        this.isClicked = false;

    }

    public void setView(View view) {
        this.view = view;
        setAnimationView();

    }

    public void setAnimationView(){
        lottieAnimationView = view.findViewById(R.id.home_ping_item_lav);
        lottieAnimationView.setAnimation(resInteger);
    }
    public void playAnimation(){
        if (lottieAnimationView != null && !lottieAnimationView.isAnimating()) {
            lottieAnimationView.playAnimation();
        }
    }
    public void setIsClicked(Boolean isClicked){
        this.isClicked = isClicked;
        if (view != null){
            if (isClicked){
                view.findViewById(R.id.home_ping_item_underbar).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.home_ping_item_underbar).setVisibility(View.INVISIBLE);

            }
        }
    }

    public LottieAnimationView getLottieAnimationView() {
        return lottieAnimationView;
    }

    public Integer getResInteger() {
        return resInteger;
    }

}
