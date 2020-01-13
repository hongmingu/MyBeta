package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViewById(R.id.setting_exit).setOnClickListener(this);
        findViewById(R.id.setting_logout).setOnClickListener(this);
        findViewById(R.id.setting_password_set_tv).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_exit:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else finish();
                break;
            case R.id.setting_logout:
                Intent result_intent = new Intent();
                result_intent.putExtra(ConstantStrings.INTENT_LOGOUT_INFO, ConstantIntegers.RESULT_LOGGED_OUT);
                setResult(RESULT_OK, result_intent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else finish();

                // finishAffinity();
                // 이건 java.lang.IllegalStateException: Can not be called to deliver a result 일으킴.

            case R.id.setting_password_set_tv:
                Intent passwordIntent = new Intent(this, PasswordSetActivity.class);

                startActivity(passwordIntent);
                overridePendingTransition(0, 0);

            default:
                break;
        }

    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.stay, R.anim.slide_down);
//        overridePendingTransition(R.anim.stay, R.anim.slide_right_out); // 오른쪽으로 빠짐
        overridePendingTransition(0, 0);

    }
}
