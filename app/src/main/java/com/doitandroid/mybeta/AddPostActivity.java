package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class AddPostActivity extends AppCompatActivity {
    Intent got_intent;
    Toolbar toolbar_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        toolbar_exit = findViewById(R.id.tb_tb_exit);

        setSupportActionBar(toolbar_exit);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        got_intent = getIntent();
        if(got_intent.getBooleanExtra(ConstantStrings.INTENT_HAS_PING, false)){
            Toast.makeText(this, got_intent.getStringExtra(ConstantStrings.INTENT_PING_NUM), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
}
