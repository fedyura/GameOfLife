package com.github.dz2.gameoflife;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyCell extends TextView {
	
	public MyCell(Context context) {
        super(context);
    }

    public MyCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCell(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }

}
