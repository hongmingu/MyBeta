package com.doitandroid.mybeta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.doitandroid.mybeta.fragment.ContentListFragment;
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
        UserItem userItem = (UserItem) gotIntent.getSerializableExtra("userItem");

        switch (gotIntent.getStringExtra(ConstantStrings.INTENT_CONTENT_START)){
            case ConstantStrings.INTENT_CONTENT_USER:
                addUserFragment(userItem);
                break;
            case ConstantStrings.INTENT_CONTENT_FOLLOW:
                if (gotIntent.getBooleanExtra(ConstantStrings.INTENT_CONTENT_FOLLOW_BOOLEAN, true)){
                    //true
                    addListFragment(userItem, true);
                } else {
                    addListFragment(userItem, false);

                }
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

        limitFragmentNumber();

        Log.d(TAG, fragments.toString());

    }

    public void addUserFragment(UserItem userItem){

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
        limitFragmentNumber();

        Toast.makeText(this, gotIntent.getStringExtra(ConstantStrings.INTENT_USER_ID) + "", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, userItem.getFullName(), Toast.LENGTH_SHORT).show();

    }


    public void addListFragment(UserItem userItem, boolean initFollowing){

        ContentListFragment contentListFragmen = new ContentListFragment(userItem, initFollowing);

        singleton.contentFragmentList.add(contentListFragmen);
        fragmentManager.beginTransaction().add(R.id.content_frame_cl, contentListFragmen).commit();

//                ArrayList<Fragment> fragments = (ArrayList<Fragment>) fragmentManager.getFragments();
//                for (Fragment fragment : fragments) {
//                    if (fragment != null && fragment.isVisible()) {
//                        fragmentManager.beginTransaction().hide(fragment).commit();
//
//                    }
//                }
        fragmentManager.beginTransaction().show(contentListFragmen).commit();
        limitFragmentNumber();
    }


    public void limitFragmentNumber(){
        if (fragmentManager.getFragments().size() > 5){
            Fragment fragment = fragmentManager.getFragments().get(0);
            fragmentManager.beginTransaction().remove(fragment).commit();

        }
    }
    @Override
    public void finish() {

        Toast.makeText(this, "onBackPressed: "+fragmentManager.getFragments().size(), Toast.LENGTH_SHORT).show();

        Log.d(TAG, fragmentManager.getFragments().toString());
        if (fragmentManager.getFragments().size() > 1){
            // 여기서 순차적으로 지워지도록 한다.
            Fragment fragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size()-1);
            fragmentManager.beginTransaction().remove(fragment).commit();
        } else if (fragmentManager.getFragments().size() == 1){
            Fragment fragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size()-1);
            fragmentManager.beginTransaction().remove(fragment).commit();
            super.finish();
        } else {
            super.finish();
//        overridePendingTransition(R.anim.stay, R.anim.slide_down);
            overridePendingTransition(0, 0);
        }

    }


    @Override
    public void onBackPressed() {

        finish();
    }
}
