package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class CommentActivity extends AppCompatActivity {

    Intent gotIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        gotIntent = getIntent();

        Toast.makeText(getApplicationContext(), gotIntent.getStringExtra(ConstantStrings.INTENT_POST_ID), Toast.LENGTH_SHORT).show();


    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.stay, R.anim.slide_down);
        overridePendingTransition(0, 0);

    }
}
