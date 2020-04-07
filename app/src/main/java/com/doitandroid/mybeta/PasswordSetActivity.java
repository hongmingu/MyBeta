package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordSetActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatEditText current_et, new_et, new_confirm_et;

    CoordinatorLayout save_cl, exit_cl;

    APIInterface apiInterface;

    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_set);

        apiInterface = singleton.apiInterface;

        current_et = findViewById(R.id.password_set_current_et);
        new_et = findViewById(R.id.password_set_new_et);
        new_confirm_et = findViewById(R.id.password_set_new_confirm_et);

        save_cl = findViewById(R.id.password_set_save_cl);
        save_cl.setOnClickListener(this);
        exit_cl = findViewById(R.id.password_set_exit_cl);
        exit_cl.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.password_set_exit_cl:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else finish();
                break;
            case R.id.password_set_save_cl:


                if(!(current_et.getText().toString().equals(""))||
                        !(new_et.getText().toString().equals(""))||
                        !(new_confirm_et.getText().toString().equals(""))){
                    Toast.makeText(getApplicationContext(), "no content", Toast.LENGTH_SHORT).show();
                } else if (!(new_et.getText().toString().equals(new_confirm_et.getText().toString()))){
                    Toast.makeText(getApplicationContext(), "no same", Toast.LENGTH_SHORT).show();

                } else {

                    passwordSet(current_et.getText().toString(),
                            new_et.getText().toString(),
                            new_confirm_et.getText().toString());

                }
                // finishAffinity();
                // 이건 java.lang.IllegalStateException: Can not be called to deliver a result 일으킴.

            default:
                break;
        }

    }

    public void passwordSet(final String currentPassword, final String newPassword, final String newPasswordConfirm) {

        // 서버에 call
        RequestBody requestCurrent = RequestBody.create(MediaType.parse("multipart/form-data"), currentPassword);
        RequestBody requestNew = RequestBody.create(MediaType.parse("multipart/form-data"), newPassword);
        RequestBody requestNewConfirm = RequestBody.create(MediaType.parse("multipart/form-data"), newPasswordConfirm);

        Call<JsonObject> call = apiInterface.passwordSet(requestCurrent, requestNew, requestNewConfirm);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        int rc = jsonObject.get("rc").getAsInt();
                        if (rc != ConstantIntegers.SUCCEED_RESPONSE) {
                            JsonObject content = jsonObject.get("content").getAsJsonObject();
                            int code = content.get("code").getAsInt();

                            // sign up 실패
                            call.cancel();
                            Toast.makeText(getApplicationContext(), getValidateErrorFromCode(code), Toast.LENGTH_SHORT).show();


                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Password SET!", Toast.LENGTH_SHORT).show();
                        // 접속성공
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();

            }
        });


    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.stay, R.anim.slide_down);
//        overridePendingTransition(R.anim.stay, R.anim.slide_right_out); // 오른쪽으로 빠짐
        overridePendingTransition(0, 0);
    }
    private String getValidateErrorFromCode(int code) {

        String codeToString = "fill it differently";
        switch (code){
            case ConstantIntegers.USER_USERNAME_VALIDATE_REGEX_PROBLEM:
                codeToString = "other username please";
                break;

            case ConstantIntegers.USER_USERNAME_VALIDATE_LENGTH_PROBLEM:
                codeToString = "username length is not available";
                break;
            case ConstantIntegers.USER_USERNAME_VALIDATE_DIGIT_PROBLEM:
                codeToString = "digit username is not available";
                break;
            case ConstantIntegers.USER_USERNAME_VALIDATE_BANNED_PROBLEM:
                codeToString = "unavailable username...";
                break;

            case ConstantIntegers.USER_FULL_NAME_VALIDATE_LENGTH_PROBLEM:
                codeToString = "too long fullname";
                break;


            case ConstantIntegers.USER_EMAIL_VALIDATE_REGEX_PROBLEM:
                codeToString = "email is not available";
                break;
            case ConstantIntegers.USER_EMAIL_VALIDATE_LENGTH_PROBLEM:
                codeToString = "plz change email length";
                break;
            case ConstantIntegers.USER_EMAIL_EXIST_PROBLEM:
                codeToString = "email is already using";
                break;

            case ConstantIntegers.USER_PASSWORD_VALIDATE_SELF_EQUAL_PROBLEM:
                codeToString = "password not match correctly ";
                break;
            case ConstantIntegers.USER_PASSWORD_VALIDATE_USERNAME_EQUAL_PROBLEM:
                codeToString = "password and username cannot be the same";
                break;
            case ConstantIntegers.USER_PASSWORD_VALIDATE_LENGTH_PROBLEM:
                codeToString = "plz change password length";
                break;
            case ConstantIntegers.USER_PASSWORD_VALIDATE_BANNED_PROBLEM:
                codeToString = "password unavailable";
                break;
            default:
                break;

        }
        return codeToString;

    }
}
