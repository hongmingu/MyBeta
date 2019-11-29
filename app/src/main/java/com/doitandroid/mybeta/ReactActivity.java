package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ReactActivity extends AppCompatActivity {

    private static final String TAG = "ReactActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_react);

        //todo: 이제 commentActivity 에서 내용 가져오고 adapter 구성한다.
    }
}
