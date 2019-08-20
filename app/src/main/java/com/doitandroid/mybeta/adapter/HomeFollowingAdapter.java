package com.doitandroid.mybeta.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doitandroid.mybeta.R;

import java.util.List;

public class HomeFollowingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.home_following_recyclerview_item, parent, false);
        HomeFollowingViewHolder homeFollowingViewHolder = new HomeFollowingViewHolder(view);

        LinearLayout home_layout = homeFollowingViewHolder.home_layout;

        LinearLayout inner_ll = new LinearLayout(context);



        inner_ll.setTag("idis_"+viewType);
        inner_ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        inner_ll.setOrientation(LinearLayout.VERTICAL);



        final TextView textView = new TextView(parent.getContext());
        ViewGroup.LayoutParams tv_layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        textView.setLayoutParams(tv_layoutParams);
        textView.setText(String.valueOf(viewType));
        textView.setTag("rv_tv_"+viewType);
        inner_ll.addView(textView);

        Button button = new Button(context);
        button.setLayoutParams(new ViewGroup.LayoutParams(200, 100));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int integer= Integer.parseInt((String) textView.getText());
                textView.setText(String.valueOf(integer+1));
            }
        });
        inner_ll.addView(button);

        home_layout.addView(inner_ll);

        return homeFollowingViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("RecyclerViewAdapter", "onBindViewHolder" + " position: " + position);
        HomeFollowingViewHolder homeFollowingViewHolder = (HomeFollowingViewHolder)holder;
        TextView textView = homeFollowingViewHolder.home_layout.findViewWithTag("rv_tv_"+position);
        int integer= Integer.parseInt((String) textView.getText());
        textView.setText(String.valueOf(integer+1));

//        HomeFollowingViewHolder homeFollowingViewHolder = (HomeFollowingViewHolder) holder;
//        LinearLayout linearLayout = homeFollowingViewHolder.home_layout;
//        Log.d("RecyclerViewAdapter", "onBindViewHolder" + " position: " + position);
//        Context context = linearLayout.getContext();
//
//        TextView textView = new TextView(context);
//        ViewGroup.LayoutParams tv_layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        textView.setLayoutParams(tv_layoutParams);
//        textView.setText("text view dynamically: " + position);
//        linearLayout.addView(textView);
//
//        CustomView customView = new CustomView(context);
//        customView.setLayoutParams(new RelativeLayout.LayoutParams(300, 300));
//        customView.setBackgroundColor(context.getResources().getColor(R.color.skyblue));
//        linearLayout.addView(customView);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        }else {
            for (Object payload : payloads) {
                if (payload instanceof String) {
                    String type = (String) payload;
                    if (TextUtils.equals(type, "color_red")) {
/*                        LinearLayout layout = ((HomeFollowingViewHolder) holder).home_layout;
                        View view = layout.findViewWithTag("idis_"+holder.getAdapterPosition());
                        Context context = layout.getContext();
                        view.setBackgroundColor(context.getResources().getColor(R.color.red));*/

                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    private class HomeFollowingViewHolder extends RecyclerView.ViewHolder {
        LinearLayout home_layout;
        public HomeFollowingViewHolder(View view) {
            super(view);
            home_layout = view.findViewById(R.id.home_f_rv_i_ll);

        }
    }

    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        return position;
    }

    @Override
    public long getItemId(int position) {
//        return super.getItemId(position);

        return position;
    }
}
