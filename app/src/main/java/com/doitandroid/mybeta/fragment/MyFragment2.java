package com.doitandroid.mybeta.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.adapter.MyRecyclerViewAdapter;

import java.util.ArrayList;

public class MyFragment2 extends Fragment {
    //app 버전?

    TextView tv_count;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);

        ArrayList<String> string_list = new ArrayList<>();

        for (int i=0; i<105; i++ ) {
            string_list.add("some" + i);
            Log.d("for문", String.valueOf(i));
        }

        string_list.add("who");
        // 그리드뷰로 만든다.
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment2_recyclerview);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // 어댑터를 연결시킨다.
        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(string_list);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myRecyclerViewAdapter);
/*
        tv_count = rootView.findViewById(R.id.fragment2_counter);
        Button tv_btn = rootView.findViewById(R.id.fragment2_button);
*//*        if (savedInstanceState != null){
            tv_count.setText(Integer.toString(savedInstanceState.getInt("counter")));
            Log.d("fragment1_OCV", "instate: "+savedInstanceState.getInt("counter"));

        }*//*
        tv_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(tv_count.getText().toString());
                tv_count.setText(Integer.toString(count+1));
            }
        });*/

        return rootView;
        // return inflater.inflate(R.layout.fragment2, container, false);
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
