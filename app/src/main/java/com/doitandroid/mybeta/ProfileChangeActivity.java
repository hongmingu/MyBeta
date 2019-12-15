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

public class ProfileChangeActivity extends AppCompatActivity implements View.OnClickListener {

    Intent receivedIntent;

    AppCompatEditText username_et, full_name_et, email_et;

    CoordinatorLayout save_cl, exit_cl;

    APIInterface apiInterface;

    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change);

        apiInterface = singleton.apiInterface;
        receivedIntent = getIntent();

        username_et = findViewById(R.id.profile_change_username_et);
        full_name_et = findViewById(R.id.profile_change_full_name_et);
        email_et = findViewById(R.id.profile_change_email_et);

        save_cl = findViewById(R.id.profile_change_save_cl);
        save_cl.setOnClickListener(this);

        exit_cl = findViewById(R.id.profile_change_exit_cl);
        exit_cl.setOnClickListener(this);

        username_et.setText(receivedIntent.getStringExtra(ConstantStrings.INTENT_PROFILE_USERNAME));
        full_name_et.setText(receivedIntent.getStringExtra(ConstantStrings.INTENT_PROFILE_FULL_NAME));
        email_et.setText(receivedIntent.getStringExtra(ConstantStrings.INTENT_PROFILE_EMAIL));




    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profile_change_exit_cl:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else finish();
                break;
            case R.id.profile_change_save_cl:

                if(!(full_name_et.getText().toString().equals(""))||
                        !(username_et.getText().toString().equals(""))||
                        !(email_et.getText().toString().equals(""))){
                    Toast.makeText(getApplicationContext(), "no content", Toast.LENGTH_SHORT).show();
                } else {

                    saveProfile(full_name_et.getText().toString(),
                            username_et.getText().toString(),
                            email_et.getText().toString());

                }


                // finishAffinity();
                // 이건 java.lang.IllegalStateException: Can not be called to deliver a result 일으킴.

            default:
                break;
        }

    }

    public void saveProfile(final String fullName, final String username, final String email){

        // 서버에 call
        RequestBody requestUsername = RequestBody.create(MediaType.parse("multipart/form-data"), username);
        RequestBody requestFullname = RequestBody.create(MediaType.parse("multipart/form-data"), fullName);
        RequestBody requestEmail = RequestBody.create(MediaType.parse("multipart/form-data"), email);

        Call<JsonObject> call = apiInterface.profileChange(requestUsername, requestFullname, requestEmail);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        int rc = jsonObject.get("rc").getAsInt();
                        if (rc != ConstantIntegers.SUCCEED_RESPONSE) {
                            // sign up 실패
                            call.cancel();
                            return;
                        }

                        int contentInt = jsonObject.get("content").getAsInt();
                        if(contentInt == ConstantIntegers.VALIDATE_OK){
                            Toast.makeText(getApplicationContext(), "validate ok", Toast.LENGTH_SHORT).show();

                            SharedPreferences sp = getSharedPreferences(ConstantStrings.SP_INIT_APP, MODE_PRIVATE);

                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(ConstantStrings.SP_ARG_PROFILE_USERNAME, username);
                            editor.putString(ConstantStrings.SP_ARG_PROFILE_FULLNAME, fullName);
                            editor.putString(ConstantStrings.SP_ARG_PROFILE_EMAIL, email);
                            editor.commit();


                        } else {
                            Toast.makeText(getApplicationContext(), "validate failed", Toast.LENGTH_SHORT).show();
                        }


/*
                        FeedItem feedItem = new FeedItem(contentObject);
                        Log.d(TAG, "called_feeditem_good: "+feedItem.getPostID());

                        boolean isExist = false;
                        for(FeedItem item : singleton.followFeedList){
                            if(feedItem.getPostID().equals(item.getPostID())){
                                isExist = true;
                            }
                        }
                        if(!isExist){
                            singleton.followFeedList.add(0, feedItem);
                            singleton.homeFollowAdapter.notifyDataSetChanged();

                            Log.d(TAG, "called_feeditem_good");
                        }
*/





                        // homeFollowFragment 띄우면서 맨 위에 리스트에 넣고 어댑터에 노티파이 해줘야겠다.

                        // 접속 성공.
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();

            }
        });


        Intent result_intent = new Intent();
        result_intent.putExtra(ConstantStrings.INTENT_PROFILE_CHANGE, ConstantIntegers.RESULT_PROFILE_CHANGED);
        setResult(RESULT_OK, result_intent);
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.stay, R.anim.slide_down);
        overridePendingTransition(R.anim.stay, R.anim.slide_right_out); // 오른쪽으로 빠짐
    }
}
