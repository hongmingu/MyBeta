package com.doitandroid.mybeta.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.doitandroid.mybeta.R;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<String> data_list = null;

    public MyRecyclerViewAdapter(ArrayList<String> data_list) {
        this.data_list = data_list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recyclerview_item, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        // xml 디자인 한 부분 적용
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        // xml 디자인 한 부분 안의 내용 변경

        String text = data_list.get(i);
        Log.d("onBindViewHolder", String.valueOf(i));
        Log.d("onBindViewHolder text", text);

        ((MyViewHolder)viewHolder).textView.setText(text);
    }

    @Override
    public int getItemCount() {
        // 아이템 측정 카운터
        return data_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        private MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.tv_text);
        }
    }

}
