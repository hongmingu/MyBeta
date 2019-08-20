package com.doitandroid.mybeta.homeping;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomePingItemNarrowDecoration extends RecyclerView.ItemDecoration {
    private final static String TAG = "HomePingItemDecoTAG";

    private int shortMargin;
    private int longMargin;


    public HomePingItemNarrowDecoration(Context context) {

        shortMargin = dpToPx(context, 16);
        longMargin = dpToPx(context, 0);
    }

    // dp -> pixel 단위로 변경
    private int dpToPx(Context context, int dp) {

        return (int) TypedValue.applyDimension
                (TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }


    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();
        Log.d(TAG, "itemCount: "+ itemCount);
        Log.d(TAG, "position: "+ position);

        //상하 설정
        if(position == 0 && position == itemCount - 1) {
            // 첫 아이템이 끝임
            outRect.left = longMargin;
            outRect.right = longMargin;
        } else if(position == 0 && position != itemCount -1){
            //첫 아이템이지만 끝은 아님
            outRect.left = longMargin;
        }
        else if (position == itemCount - 1){
            // 끝 아이템
            outRect.left = shortMargin;
            outRect.right = longMargin;
        } else {
            outRect.left = shortMargin;

        }

        // spanIndex = 0 -> 왼쪽
        // spanIndex = 1 -> 오른쪽
//
//        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
//        int spanIndex = lp.getSpanIndex();
//
//        if(spanIndex == 0) {
//            //왼쪽 아이템
//            outRect.shortMargin = shortMargin;
//            outRect.longMargin = longMargin;
//
//        } else if(spanIndex == 1) {
//            //오른쪽 아이템
//            outRect.shortMargin = longMargin;
//            outRect.longMargin = shortMargin;
//        }

    }
}
