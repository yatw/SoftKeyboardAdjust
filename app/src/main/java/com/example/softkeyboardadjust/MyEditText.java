package com.example.softkeyboardadjust;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyEditText extends androidx.appcompat.widget.AppCompatEditText {


    public MyEditText(@NonNull Context context) {
        super(context);
        setOnTouchListener();
    }

    public MyEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener();
    }

    public MyEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListener(){
        // if password, handle the eye icon and shift up
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    MainActivity activity = (MainActivity) MyEditText.this.getContext(); // get activity from view
                    activity.helper.handleShiftUp(MyEditText.this);
                }
                return false;
                // returns true the event is handled and keyboard wont popup.
                // If you'd want the keyboard to still popup and register click you'd have it return false
            }
        });
    }

}
