package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.doitandroid.mybeta.customview.MyDialog;
import com.doitandroid.mybeta.customview.MyDialogListener;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.NotLoggedInAPIClient;
import com.doitandroid.mybeta.utils.UtilsCollection;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    ScrollView main_layout;
    CoordinatorLayout forgot_password_back_cl, forgot_password_complete_cl, forgot_password_init_cl;

    AppCompatEditText forgot_password_account_et;

    AppCompatTextView forgot_password_text_tv, forgot_password_complete_tv;

    Activity activity;

    APIInterface apiInterface;

    boolean mailSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        UtilsCollection utilsCollection = new UtilsCollection(this);
        utilsCollection.makeStatusBarColor("#cccccc");

        ////////////////////////////////////////////////////////////////////////////////////////////
        activity = this;
        main_layout = findViewById(R.id.forgot_password_layout);
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
        mailSent = false;

        apiInterface = NotLoggedInAPIClient.getClient().create(APIInterface.class);


        forgot_password_init_cl = findViewById(R.id.forgot_password_init_cl);
        forgot_password_account_et = findViewById(R.id.forgot_password_account_et);
        forgot_password_account_et.setOnClickListener(this);
        forgot_password_back_cl = findViewById(R.id.forgot_password_back_cl);
        forgot_password_back_cl.setOnClickListener(this);
        forgot_password_complete_cl = findViewById(R.id.forgot_password_complete_cl);
        forgot_password_complete_cl.setOnClickListener(this);

        forgot_password_complete_tv = findViewById(R.id.forgot_password_send_tv);

        forgot_password_text_tv = findViewById(R.id.forgot_password_text_tv);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.forgot_password_back_cl:
                onBackPressed();
                break;
            case R.id.forgot_password_complete_cl:
                if (mailSent){
                    activity.finish();
                    return;
                }
                RequestBody requestAccount = RequestBody.create(MediaType.parse("multipart/form-data"), forgot_password_account_et.getText().toString().trim());

                Call<JsonObject> call = apiInterface.forgotPassword(requestAccount);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            JsonObject jsonObject = response.body();

                            if (jsonObject != null) {
                                Integer rc = jsonObject.get("rc").getAsInt();

                                if (rc != ConstantIntegers.SUCCEED_RESPONSE) {
                                    // sign up 실패
                                    call.cancel();
                                    return;
                                }
                                String email = jsonObject.get("content").getAsString();

                                mailSent = true;

                                hideSoftKeyboard();
                                forgot_password_init_cl.setVisibility(View.GONE);
                                forgot_password_account_et.setVisibility(View.GONE);

                                CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams.gravity = Gravity.CENTER;
                                layoutParams.setMarginStart(60);

                                forgot_password_text_tv.setText("confirmation has sent to "+ email);
                                forgot_password_text_tv.setLayoutParams(layoutParams);

                                forgot_password_complete_tv.setText("OK");



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
    @Override
    public void onBackPressed() {
        if (mailSent){
            activity.finish();

        } else {
            String get_trim = forgot_password_account_et.getText().toString().trim();
            if (!(get_trim.equals(""))) {
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

        }

    }
    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

}
