package com.example.timedickerdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class CalendarLine extends LinearLayout {

	public CalendarLine(Context context) {
		super(context);
	}
	
	public CalendarLine(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.calendar_view, this,true);
	}

}
