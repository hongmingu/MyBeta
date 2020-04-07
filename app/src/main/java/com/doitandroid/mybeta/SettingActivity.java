package com.doitandroid.mybeta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private final static String TAG = "SettingActivity";

    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    APIInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViewById(R.id.setting_exit).setOnClickListener(this);
        findViewById(R.id.setting_logout).setOnClickListener(this);
        findViewById(R.id.setting_password_set_tv).setOnClickListener(this);

        apiInterface = singleton.apiInterface;

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
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        pushLogOut(token);
                    }
                });


                break;


            // finishAffinity();
                // 이건 java.lang.IllegalStateException: Can not be called to deliver a result 일으킴.

            case R.id.setting_password_set_tv:
                Intent passwordIntent = new Intent(this, PasswordSetActivity.class);

                startActivity(passwordIntent);
                overridePendingTransition(0, 0);
                break;

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


    public void pushLogOut(String token) {

        RequestBody requestToken = RequestBody.create(MediaType.parse("multipart/form-data"), token);

        Call<JsonObject> call = apiInterface.logOut(requestToken);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        int rc = jsonObject.get("rc").getAsInt();

                        if (rc != ConstantIntegers.SUCCEED_RESPONSE) {
                            // sign up 실패
                            call.cancel();
                            return;
                        }

                        // 접속 성공.
                    }

                    Intent result_intent = new Intent();
                    result_intent.putExtra(ConstantStrings.INTENT_LOGOUT_INFO, ConstantIntegers.RESULT_LOGGED_OUT);
                    setResult(RESULT_OK, result_intent);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAfterTransition();
                    } else finish();

                }


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();

            }
        });
    }

}
