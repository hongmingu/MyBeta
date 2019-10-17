package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.doitandroid.mybeta.fragment.ContentUserFragment;
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;

import java.util.ArrayList;

public class ContentActivity extends AppCompatActivity {
    public static final String TAG = "ContentActivityTAG";
    FragmentManager fragmentManager;
    Intent gotIntent;
    ArrayList<Fragment> fragments;
    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        fragmentManager = getSupportFragmentManager();

        gotIntent = getIntent();
        switch (gotIntent.getStringExtra(ConstantStrings.INTENT_CONTENT_START)){
            case ConstantStrings.INTENT_CONTENT_USER:

                UserItem userItem = (UserItem) gotIntent.getSerializableExtra("userItem");
                ContentUserFragment contentUserFragment = new ContentUserFragment(userItem);

                singleton.contentFragmentList.add(contentUserFragment);
                fragmentManager.beginTransaction().add(R.id.content_frame_cl, contentUserFragment).commit();

//                ArrayList<Fragment> fragments = (ArrayList<Fragment>) fragmentManager.getFragments();
//                for (Fragment fragment : fragments) {
//                    if (fragment != null && fragment.isVisible()) {
//                        fragmentManager.beginTransaction().hide(fragment).commit();
//
//                    }
//                }
                fragmentManager.beginTransaction().show(contentUserFragment).commit();

                Toast.makeText(this, gotIntent.getStringExtra(ConstantStrings.INTENT_USER_ID) + "", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, userItem.getFullName(), Toast.LENGTH_SHORT).show();

                break;
            default:
                break;
        }


    }

    /*                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else finish();*/


    public void addTestFragment(){
        UserItem userItem = (UserItem) gotIntent.getSerializableExtra("userItem");
        ContentUserFragment contentUserFragment = new ContentUserFragment(userItem);

        singleton.contentFragmentList.add(contentUserFragment);
        ArrayList<Fragment> fragments  = (ArrayList<Fragment>) fragmentManager.getFragments();
//        for (Fragment fragment : fragments) {
//            if (fragment != null && fragment.isVisible()) {
//                fragmentManager.beginTransaction().hide(fragment).commit();
//
//            }
//        }
        fragmentManager.beginTransaction().add(R.id.content_frame_cl, contentUserFragment).commit();
        fragmentManager.beginTransaction().show(contentUserFragment);

        if (fragmentManager.getFragments().size() > 3){
            Fragment fragment = fragmentManager.getFragments().get(0);
            fragmentManager.beginTransaction().remove(fragment).commit();
        }

        Log.d(TAG, fragments.toString());

    }
    @Override
    public void finish() {
        if (fragmentManager.getFragments().size() > 1){
            Fragment fragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size()-1);
            fragmentManager.beginTransaction().remove(fragment).commit();
        } else {
            super.finish();
//        overridePendingTransition(R.anim.stay, R.anim.slide_down);
            overridePendingTransition(0, 0);
        }

    }
}
