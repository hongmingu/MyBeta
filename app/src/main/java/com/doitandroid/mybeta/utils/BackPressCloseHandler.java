package com.doitandroid.mybeta.utils;

import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;
    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            // showGuide();
            // Log.d("태그임", "눌렸다"+activity.getClass().getSimpleName() + "이거임");

            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {

            if((activity.getClass().getSimpleName()).equals("LoginActivity")){
                activity.finishAffinity();
            } else {
                activity.finish();
            }

            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity, "double press to close this app", Toast.LENGTH_SHORT);
        toast.show();
    }
}

