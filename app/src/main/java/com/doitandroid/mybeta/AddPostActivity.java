package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import com.doitandroid.mybeta.ping.PingShownItem;

import java.util.List;
import java.util.Objects;

public class AddPostActivity extends AppCompatActivity implements View.OnClickListener{
    Intent got_intent;
    Toolbar toolbar_exit;
    PingShownItem pingShownItem;
    CoordinatorLayout add_post_ping_cl, add_post_complete_cl;
    AppCompatEditText add_post_ping_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        toolbar_exit = findViewById(R.id.tb_tb_exit);
        add_post_ping_cl = findViewById(R.id.add_post_ping_cl);
        add_post_ping_et = findViewById(R.id.add_post_ping_et);
        add_post_complete_cl = findViewById(R.id.add_post_complete_cl);
        add_post_complete_cl.setOnClickListener(this);

        setSupportActionBar(toolbar_exit);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        got_intent = getIntent();

        String gotPingID = got_intent.getStringExtra(ConstantStrings.INTENT_PING_SHOWN_ITEM_ID);


        if (gotPingID.equals(ConstantStrings.INTENT_NO_PING)){
            pingShownItem = null;
            add_post_ping_cl.setVisibility(View.GONE);

            Toast.makeText(this, "null ping shown item", Toast.LENGTH_SHORT).show();
        } else {

            List<PingItem> pingConstant = ConstantAnimations.pingList;
            // Constant 리스트에서 정보를 파악함.
            for (PingItem pingItem : pingConstant) {
                if (pingItem.getPingID().equals(gotPingID)) {
                    pingShownItem = new PingShownItem(gotPingID);
                }
            }
            add_post_ping_cl.setVisibility(View.VISIBLE);
            add_post_ping_et.setHint(pingShownItem.getPingText());

            Toast.makeText(this, pingShownItem.getPingText(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_post_complete_cl:
                if(pingShownItem != null){
                    String ping_text = Objects.requireNonNull(add_post_ping_et.getText()).toString().trim();
                    if(ping_text.equals("")){
                        Toast.makeText(this, "has ping but no text", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, ping_text, Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(this, "it's null", Toast.LENGTH_SHORT).show();

                }

                break;
            default:
                break;
        }

    }
}
