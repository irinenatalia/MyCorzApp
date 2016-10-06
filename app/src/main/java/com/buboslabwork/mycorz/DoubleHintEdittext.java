package com.buboslabwork.mycorz;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class DoubleHintEdittext extends RelativeLayout{
    EditText activeField;
    EditText nonActiveEditText;

    final CharSequence hint;

    public DoubleHintEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.edittext_double_hints, this);
        activeField = (EditText)findViewById(R.id.active_edit_text);
        nonActiveEditText = (EditText)findViewById(R.id.non_active_edit_text);
        hint = nonActiveEditText.getHint();
        activeField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nonActiveEditText.setHint(s.length() !=0 ? "" : hint);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
