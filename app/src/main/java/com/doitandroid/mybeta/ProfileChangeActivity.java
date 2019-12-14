package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class ProfileChangeActivity extends AppCompatActivity implements View.OnClickListener {

    Intent receivedIntent;

    AppCompatEditText username_et, full_name_et, email_et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change);

        receivedIntent = getIntent();

        username_et = findViewById(R.id.profile_change_username_et);
        full_name_et = findViewById(R.id.profile_change_full_name_et);
        email_et = findViewById(R.id.profile_change_email_et);

        username_et.setText(receivedIntent.getStringExtra(ConstantStrings.INTENT_PROFILE_USERNAME));
        full_name_et.setText(receivedIntent.getStringExtra(ConstantStrings.INTENT_PROFILE_FULL_NAME));
        email_et.setText(receivedIntent.getStringExtra(ConstantStrings.INTENT_PROFILE_EMAIL));



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profile_change_exit:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else finish();
                break;
            case R.id.profile_change_save_cl:

                saveProfile();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else finish();

                // finishAffinity();
                // 이건 java.lang.IllegalStateException: Can not be called to deliver a result 일으킴.

            default:
                break;
        }

    }

    public void saveProfile(){

        // 서버에 call

        Intent result_intent = new Intent();
        result_intent.putExtra(ConstantStrings.INTENT_PROFILE_CHANGE, ConstantIntegers.RESULT_PROFILE_CHANGED);
        setResult(RESULT_OK, result_intent);

    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.stay, R.anim.slide_down);
        overridePendingTransition(R.anim.stay, R.anim.slide_right_out); // 오른쪽으로 빠짐
    }
}
