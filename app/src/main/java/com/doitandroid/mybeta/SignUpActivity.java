package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.doitandroid.mybeta.customview.ClearableEditText;
import com.doitandroid.mybeta.customview.MyDialog;
import com.doitandroid.mybeta.customview.MyDialogListener;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.NotLoggedInAPIClient;
import com.doitandroid.mybeta.utils.BackPressCloseHandler;
import com.doitandroid.mybeta.utils.UtilsCollection;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpAtyTAG";
    private Activity activity;
    private BackPressCloseHandler backPressCloseHandler;
    ClearableEditText reg_full_name, reg_email, reg_password;
    AppCompatTextView password_hide_show;
    CoordinatorLayout back_cl, ok_cl;
    APIInterface apiInterface;
    ScrollView main_layout;

    AppCompatDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        UtilsCollection utilsCollection = new UtilsCollection(this);
        utilsCollection.makeStatusBarColor("#cccccc");


        ////////////////////////////////////////////////////////////////////////////////////////////

        apiInterface = NotLoggedInAPIClient.getClient().create(APIInterface.class);
        activity = this;
        back_cl = findViewById(R.id.signup_back);
        back_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        main_layout = findViewById(R.id.signup_layout);
        ViewTreeObserver observer = main_layout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int viewHeight = main_layout.getMeasuredHeight();
                int contentHeight = main_layout.getChildAt(0).getHeight();
                if(viewHeight - contentHeight < 0) {
                    View lastChild = main_layout.getChildAt(main_layout.getChildCount() - 1);
                    int bottom = lastChild.getBottom() + main_layout.getPaddingBottom();
                    int sy = main_layout.getScrollY();
                    int sh = main_layout.getHeight();
                    int delta = bottom - (sy + sh);

                    main_layout.smoothScrollBy(0, delta);

                    // scrollable
                }
            }
        });



        reg_full_name = findViewById(R.id.reg_full_name);
        reg_full_name.setHint("full name");

        reg_email = findViewById(R.id.reg_email);
        reg_email.setHint("email");
        reg_email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        reg_password = findViewById(R.id.reg_password);
        reg_password.setHint("password");
        reg_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        password_hide_show = findViewById(R.id.reg_password_hide_show);

        password_hide_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editView = reg_password.getEditText();
                int position = editView.getSelectionStart();
                if(editView.getTransformationMethod() instanceof PasswordTransformationMethod){
                    editView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    editView.setSelection(position);
                }else{
                    editView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    editView.setSelection(position);
                }
            }
        });

        ok_cl = findViewById(R.id.signup_ok);
        ok_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_str = reg_email.getEditText().getText().toString().trim();
                String full_name_str = reg_full_name.getEditText().getText().toString().trim();
                String password_str = reg_password.getEditText().getText().toString();
                RequestBody email = RequestBody.create(MediaType.parse("multipart/form-data"), email_str);
                RequestBody password = RequestBody.create(MediaType.parse("multipart/form-data"), password_str);
                RequestBody full_name = RequestBody.create(MediaType.parse("multipart/form-data"), full_name_str);



                Call<JsonObject> call = apiInterface.sign_up(full_name, email, password);

                progressON(activity, "now signing");
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if(response.isSuccessful()){
                            JsonObject jsonObject = response.body();

                            if (jsonObject != null) {
                                int rc = jsonObject.get("rc").getAsInt();
                                JsonObject jsonObjectContent = jsonObject.get("content").getAsJsonObject();

                                if (rc != ConstantIntegers.SUCCESS){
                                    // sign up 실패
                                    call.cancel();

                                    progressOFF();
                                    Toast.makeText(getApplicationContext(), getValidateErrorFromCode(jsonObjectContent.get("code").getAsInt()), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                SharedPreferences sp = getSharedPreferences(ConstantStrings.SP_INIT_APP, MODE_PRIVATE);

                                //todo: 로그인실패시 코드처리해줘야한다.
                                Log.d(TAG, jsonObjectContent.toString());
                                SharedPreferences.Editor editor = sp.edit();

                                editor.putInt(ConstantStrings.SP_ARG_AUTO_LOGIN, ConstantIntegers.IS_LOGINED);

                                editor.putString(ConstantStrings.SP_ARG_TOKEN, jsonObjectContent.get("token").getAsString());
                                editor.putString(ConstantStrings.SP_ARG_PROFILE_PHOTO, jsonObjectContent.get("profile_photo").getAsString());
                                editor.putString(ConstantStrings.SP_ARG_PROFILE_USERNAME, jsonObjectContent.get("profile_username").getAsString());
                                editor.putString(ConstantStrings.SP_ARG_PROFILE_FULLNAME, jsonObjectContent.get("profile_full_name").getAsString());
                                editor.putString(ConstantStrings.SP_ARG_PROFILE_USERID, jsonObjectContent.get("profile_user_id").getAsString());
                                editor.putString(ConstantStrings.SP_ARG_PROFILE_EMAIL, jsonObjectContent.get("profile_email").getAsString());

                                editor.commit();

                                progressOFF();

                                Intent intent = new Intent(activity, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                        call.cancel();

                        progressOFF();
                        Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });




    }

    @Override
    public void onBackPressed() {

        String email = reg_email.getEditText().getText().toString().trim();
        String full_name = reg_full_name.getEditText().getText().toString().trim();
        String password = reg_password.getEditText().getText().toString();

        if(email.equals("") && full_name.equals("") && password.equals("")){
            super.onBackPressed();
        } else {
            MyDialog dialog = new MyDialog(this, "뒤로가기", "작업중인 내용이 있다", "뒤로갈래잉", "안갈래잉");
            dialog.setDialogListener(new MyDialogListener() {
                @Override
                public void onPositiveClicked() {
                    SignUpActivity.super.onBackPressed();
                }

                @Override
                public void onNegativeClicked() {

                }
            });
            dialog.show();
        }

    }

    public void progressON(Activity activity, String message) {

        if (activity == null || activity.isFinishing()) {
            return;
        }


        if (progressDialog != null && progressDialog.isShowing()) {
            progressSET(message);
        } else {

            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.progress_loading);
            progressDialog.show();

        }


        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }


    }

    public void progressSET(String message) {

        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }


        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }

    }

    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void startProgress() {

        progressON(this,"Loading...");

/*        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3500);*/

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
