package com.example.timedickerdemo.newScrollView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class TimeLine extends View {

	private float mLineGap;
	private float mDensity;
	private int mWidth;
	private int mTopValue;
	private final int TEXT_SIZE = 16;

	public TimeLine(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDensity = getContext().getResources().getDisplayMetrics().density;
		mWidth = getContext().getResources().getDisplayMetrics().widthPixels;
		mLineGap = (float) (mWidth / 12.5);
	}
	
	public void setCurrentTopValue(int topValue){
		mTopValue = topValue;
		postInvalidate();
	}


	@SuppressLint("DrawAllocation") 
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint linePaint = new Paint();
		linePaint.setStrokeWidth(2);
		linePaint.setColor(Color.BLACK);

		TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(TEXT_SIZE * mDensity);

		float textWidth = Layout.getDesiredWidth("0", textPaint);
		int numberSize = 1;

		canvas.drawLine(0, TEXT_SIZE * mDensity, mWidth, TEXT_SIZE
				* mDensity, linePaint);

		float marginLeft = 0;
		for (int i = 0; i < 12; i++) {
			numberSize = String.valueOf(i + 1).length();
			marginLeft = (float) ((i + 0.5) * mLineGap);
			if (i == 0) {
				canvas.drawLine(marginLeft, 0, marginLeft, 2 * TEXT_SIZE
						* mDensity, linePaint);
				canvas.drawText(String.valueOf(mTopValue), marginLeft, 2 * TEXT_SIZE, textPaint);
			} else {
				canvas.drawLine(marginLeft, TEXT_SIZE * mDensity, marginLeft, 2
						* TEXT_SIZE * mDensity, linePaint);
			}

			canvas.drawText(String.valueOf(i + 1), marginLeft
					- (textWidth * numberSize / 2), 4 * TEXT_SIZE * mDensity,
					textPaint);
		}
	}

}
