package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.doitandroid.mybeta.customview.ClearableEditText;
import com.doitandroid.mybeta.utils.BackPressCloseHandler;
import com.doitandroid.mybeta.utils.UtilsCollection;

public class SignUpActivity extends AppCompatActivity {
    private BackPressCloseHandler backPressCloseHandler;
    ClearableEditText reg_full_name, reg_email, reg_password;
    TextView password_hide_show;
    ScrollView main_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        UtilsCollection utilsCollection = new UtilsCollection(this);
        utilsCollection.makeStatusBarColor("#cccccc");


        ////////////////////////////////////////////////////////////////////////////////////////////



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


        backPressCloseHandler = new BackPressCloseHandler(this);
    }
}
