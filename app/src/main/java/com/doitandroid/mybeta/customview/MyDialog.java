package com.doitandroid.mybeta.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.doitandroid.mybeta.R;


public class MyDialog extends Dialog {

    private static final int LAYOUT = R.layout.dialog_2btns;

    private Context context;

    private AppCompatTextView textView_title, textView_content, textView_positive, textView_negative;

    private String title, content, positive, negative;

    private MyDialogListener dialogListener;

    public MyDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public MyDialog(@NonNull Context context, String title, String content, String positive, String negative) {
        super(context);
        this.context = context;
        this.title = title;
        this.content = content;
        this.positive = positive;
        this.negative = negative;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        textView_title = findViewById(R.id.dialog_title);
        textView_content = findViewById(R.id.dialog_content);
        textView_positive = findViewById(R.id.dialog_positive);
        textView_negative = findViewById(R.id.dialog_negative);

        if(title != null){
            textView_title.setText(title);
        }

        if(content != null){
            textView_content.setText(content);
        }
        if(title != null){
            textView_positive.setText(positive);
        }
        if(title != null){
            textView_negative.setText(negative);
        }

        textView_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // String string = "some word";
                // String string_s = "some word_s";
                // 여기서 다이얼로그 내 뷰의 값을 가져오면 된다.
                // 여기서 다이얼로그의 뷰들을 다루는 것이므로.
                // dialogListener.onPositiveClicked(string, string_s);
                dialogListener.onPositiveClicked();
                dismiss();
            }
        });

        textView_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListener.onNegativeClicked();
                cancel();
            }
        });
    }


    public void setDialogListener(MyDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }
}
