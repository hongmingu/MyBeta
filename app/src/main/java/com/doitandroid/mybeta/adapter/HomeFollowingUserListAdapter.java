package com.doitandroid.mybeta.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.doitandroid.mybeta.ConstantIntegers;
import com.doitandroid.mybeta.ConstantPings;
import com.doitandroid.mybeta.ConstantStrings;
import com.doitandroid.mybeta.ContentActivity;
import com.doitandroid.mybeta.MainActivity;
import com.doitandroid.mybeta.PingItem;
import com.doitandroid.mybeta.R;
import com.doitandroid.mybeta.itemclass.FeedItem;
import com.doitandroid.mybeta.itemclass.UserItem;
import com.doitandroid.mybeta.rest.APIInterface;
import com.doitandroid.mybeta.rest.ConstantREST;
import com.doitandroid.mybeta.utils.InitializationOnDemandHolderIdiom;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFollowingUserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "HFULAdapterTAG";
    ArrayList<UserItem> userItemArrayList;
    Context context;
    APIInterface apiInterface;
    InitializationOnDemandHolderIdiom singleton = InitializationOnDemandHolderIdiom.getInstance();

    public HomeFollowingUserListAdapter(ArrayList<UserItem> userItemArrayList, Context context) {
        this.userItemArrayList = userItemArrayList;
        this.context = context;
        apiInterface = singleton.apiInterface;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RecyclerView.ViewHolder viewHolder = null;
        View view = null;
        switch (viewType){
            default:
                view = inflater.inflate(R.layout.item_following_grid_user, parent, false);
                viewHolder = new HomeFollowingUserViewHolder(view);
                break;
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, userItemArrayList.get(position).getFullName());
        final UserItem userItem = userItemArrayList.get(position);
        int width = context.getResources().getDisplayMetrics().widthPixels/ConstantIntegers.GRID_SPAN;

        Glide.with(context)
                //.load(feeditem.getUser().getUserPhoto())
                .load((ConstantREST.URL_HOME).substring(0, ConstantREST.URL_HOME.length()-1) + userItem.getUserPhoto())
                .override(width, width)
                .into(((HomeFollowingUserViewHolder) holder).fugu_civ);

        ((HomeFollowingUserViewHolder) holder).fugu_tv.setText(userItem.getFullName());

        ((HomeFollowingUserViewHolder) holder).fugu_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContentActivity.class);
                intent.putExtra(ConstantStrings.INTENT_CONTENT_START, ConstantStrings.INTENT_CONTENT_USER);

                intent.putExtra("userID", userItem.getUserID());

                ((MainActivity) context).startActivityForResult(intent, ConstantIntegers.REQUEST_CONTENT);
                ((MainActivity) context).overridePendingTransition(0, 0); //

            }
        });
        ((HomeFollowingUserViewHolder) holder).fugu_civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContentActivity.class);
                intent.putExtra(ConstantStrings.INTENT_CONTENT_START, ConstantStrings.INTENT_CONTENT_USER);
                intent.putExtra("userID", userItem.getUserID());

                ((MainActivity) context).startActivityForResult(intent, ConstantIntegers.REQUEST_CONTENT);
                ((MainActivity) context).overridePendingTransition(0, 0); //
            }
        });
    }

    @Override
    public int getItemCount() {
        return userItemArrayList.size();
    }

    private class HomeFollowingUserViewHolder extends RecyclerView.ViewHolder {

        CircleImageView fugu_civ;
        AppCompatTextView fugu_tv;
        public HomeFollowingUserViewHolder(View view) {
            super(view);
            fugu_civ = view.findViewById(R.id.item_following_grid_user_civ);
            fugu_tv= view.findViewById(R.id.item_following_grid_user_tv);

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
