package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ScrollView;

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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private BackPressCloseHandler backPressCloseHandler;
    ClearableEditText login_account, login_password;
    CoordinatorLayout btn_back, btn_complete;
    ScrollView main_layout;
    APIInterface apiInterface;

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        UtilsCollection utilsCollection = new UtilsCollection(this);
        utilsCollection.makeStatusBarColor("#cccccc");

        ////////////////////////////////////////////////////////////////////////////////////////////
        apiInterface = NotLoggedInAPIClient.getClient().create(APIInterface.class);

        activity = this;
        main_layout = findViewById(R.id.login_layout);
        ViewTreeObserver observer = main_layout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int viewHeight = main_layout.getMeasuredHeight();
                int contentHeight = main_layout.getChildAt(0).getHeight();
                if (viewHeight - contentHeight < 0) {

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


        login_account = findViewById(R.id.login_account);
        login_account.setHint("email or username");

        login_password = findViewById(R.id.login_password);
        login_password.setHint("password");
        login_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        findViewById(R.id.login_back).setOnClickListener(this);
        findViewById(R.id.login_complete).setOnClickListener(this);

        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    @Override
    public void onBackPressed() {
        String login_account_trim = login_account.getText().toString().trim();
        String login_password_raw = login_password.getText().toString();
        if (!(login_account_trim.equals("") && login_password_raw.equals(""))) {
            MyDialog dialog = new MyDialog(this, "Go back", "Really?", "okay--", "noooo");
            dialog.setDialogListener(new MyDialogListener() {
                @Override
                public void onPositiveClicked() {
                    activity.finish();
                }

                @Override
                public void onNegativeClicked() {

                }
            });
            dialog.show();
        } else {
            activity.finish();
        }


        // this.finish();
        // super.onBackPressed();
        // backPressCloseHandler.onBackPressed();
        // this.finishAffinity();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_back:
                onBackPressed();


                break;
            case R.id.login_complete:

                String account_str = login_account.getEditText().getText().toString().trim();
                String password_str = login_password.getEditText().getText().toString();
                RequestBody account = RequestBody.create(MediaType.parse("multipart/form-data"), account_str);
                RequestBody password = RequestBody.create(MediaType.parse("multipart/form-data"), password_str);

                Call<JsonObject> call = apiInterface.log_in(account, password);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            JsonObject jsonObject = response.body();

                            if (jsonObject != null) {
                                Integer rc = jsonObject.get("rc").getAsInt();
                                JsonObject content = jsonObject.get("content").getAsJsonObject();

                                if (rc != ConstantIntegers.SUCCEED_RESPONSE) {
                                    // sign up 실패
                                    call.cancel();
                                    return;
                                }
                                SharedPreferences sp = getSharedPreferences(ConstantStrings.SP_INIT_APP, MODE_PRIVATE);

                                SharedPreferences.Editor editor = sp.edit();
                                editor.putInt(ConstantStrings.SP_ARG_AUTO_LOGIN, ConstantIntegers.IS_LOGINED);
                                editor.putString(ConstantStrings.SP_ARG_TOKEN, content.get("token").getAsString());

                                editor.putString(ConstantStrings.SP_ARG_PROFILE_PHOTO, content.get("profile_photo").getAsString());
                                editor.putString(ConstantStrings.SP_ARG_PROFILE_USERNAME, content.get("profile_username").getAsString());
                                editor.putString(ConstantStrings.SP_ARG_PROFILE_FULLNAME, content.get("profile_full_name").getAsString());
                                editor.putString(ConstantStrings.SP_ARG_PROFILE_USERID, content.get("profile_user_id").getAsString());
                                editor.putString(ConstantStrings.SP_ARG_PROFILE_EMAIL, content.get("profile_email").getAsString());

                                editor.commit();

                                Intent intent = new Intent(activity, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        call.cancel();

                    }
                });

                break;
            default:
                break;
        }
    }
}

/*
    intent.addFlags(
            Intent.FLAG_ACTIVITY_SINGLE_TOP |
            Intent.FLAG_ACTIVITY_CLEAR_TOP |
            Intent.FLAG_ACTIVITY_NO_HISTORY |
            Intent.FLAG_ACTIVITY_CLEAR_TASK |
            Intent.FLAG_ACTIVITY_NEW_TASK
    );
*/
