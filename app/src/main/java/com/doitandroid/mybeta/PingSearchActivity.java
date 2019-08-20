package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PingSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_search);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
