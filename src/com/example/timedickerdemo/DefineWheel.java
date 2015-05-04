package com.example.timedickerdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class DefineWheel extends FrameLayout {
	
	private FrameLayout.LayoutParams params = new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

	public DefineWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		TuneWheel wheel = new TuneWheel(getContext(), attrs);
		addView(wheel, params);
	}
}
