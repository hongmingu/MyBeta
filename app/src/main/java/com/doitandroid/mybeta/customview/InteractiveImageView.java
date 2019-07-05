package com.doitandroid.mybeta.customview;


import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.doitandroid.mybeta.R;

public class InteractiveImageView extends RelativeLayout {

    LayoutInflater inflater = null;
    RelativeLayout relativeLayout;
    ImageView imageView_before, imageView_after;
    LottieAnimationView animationView;
    public Boolean isClicked = false;
    public Boolean isPlaying = false;
    int cancel_id = 0;
    int anim_id = R.raw.icon_home;
    int width = 0;



    public InteractiveImageView(Context context) {
        super(context);
        init();
    }
    public InteractiveImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        getAttrs(attrs);
    }



    public InteractiveImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        getAttrs(attrs);

    }


    private void init() {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.interactive_image_view, this, false);
        addView(v);

        animationView = findViewById(R.id.iiv_anim);

    }
    private void getAttrs(AttributeSet attrs) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.InteractiveImageView);
        setTypeArray(typedArray);
    }

    private void setTypeArray(TypedArray typedArray) {

        anim_id = typedArray.getResourceId(R.styleable.InteractiveImageView_play_anim, R.raw.icon_home);
        animationView.setAnimation(anim_id);
        cancel_id = typedArray.getResourceId(R.styleable.InteractiveImageView_cancel, 0);
    }

    public void interactive_click(){
        if(isPlaying){
            Log.d("animation: ","isPlaying");

            return;
        }
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d("animation: ","onAnimationStart");
                isPlaying = true;

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("animation: ","onAnimationEnd");
//
//                if(isClicked){
//                    imageView_before.setVisibility(VISIBLE);
//
//                }else {
//                    imageView_after.setVisibility(VISIBLE);
//                }
//
//                animationView.setVisibility(INVISIBLE);
                isPlaying = false;


            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d("animation: ","onAnimationCancel");

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.d("animation: ","onAnimationRepeat");

            }
        });

        if (isClicked){

            animationView.setAnimation(R.raw.home_test);
//            animationView.setVisibility(VISIBLE);

            animationView.setSpeed(-1.0f);
            animationView.playAnimation();

//            imageView_before.setVisibility(INVISIBLE);
//            imageView_after.setVisibility(INVISIBLE);
        } else {


//            animationView.setVisibility(VISIBLE);

            animationView.setAnimation(anim_id);

            animationView.setSpeed(1.0f);
            animationView.playAnimation();
//            imageView_before.setVisibility(INVISIBLE);
//            imageView_after.setVisibility(INVISIBLE);
//            animationView.loop(true);

//            animationView.reverseAnimationSpeed();

        }

        if (isClicked) {
            isClicked = false;
        } else {
            isClicked = true;
        }


    }

}
