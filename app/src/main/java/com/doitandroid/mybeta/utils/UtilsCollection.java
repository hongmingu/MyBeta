package com.doitandroid.mybeta.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

public class UtilsCollection {

    Activity activity;

    public UtilsCollection(Activity activity) {
        this.activity = activity;
    }

    public void makeStatusBarColor(String colorCode) {
        int color = Color.parseColor(colorCode);

        View view = activity.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상에 회색 아이콘 색상을 설정
                // view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                // view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); 이건 글자색: 밝거나 어둡거나 둘 중 하나.
                activity.getWindow().setStatusBarColor(color);
            }
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 21 버전 이상일 때
            // getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark_light, this.getTheme()));

            activity.getWindow().setStatusBarColor(color);
        }
    }
}
