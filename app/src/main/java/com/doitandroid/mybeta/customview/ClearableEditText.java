package com.doitandroid.mybeta.customview;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.doitandroid.mybeta.R;


public class ClearableEditText extends RelativeLayout {
    LayoutInflater inflater = null;
    EditText editText;
    ImageButton clearBtn;

    public ClearableEditText(Context context) {
        super(context);
        init();
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.clearable_edit_text, this, true);
        editText = findViewById(R.id.clearable_edit_text_edit_text);
        clearBtn = findViewById(R.id.clearable_edit_text_btn);

        clearBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(null);
                editText.setError(null);
            }
        });



    }

    public Editable getText(){
        Editable text = editText.getText();
        return text;
    }

    public void setHint(String hint){
        editText.setHint(hint);
    }

    public void setInputType(int inputType){
        editText.setInputType(inputType);
    }

    public int getCursorPosition(){
        return editText.getSelectionStart();
    }
    public void setCursorPosition(int position){
        editText.setSelection(position);
    }

    public int getInputType(){
        return editText.getInputType();
    }

    public EditText getEditText() {
        return editText;
    }
}
