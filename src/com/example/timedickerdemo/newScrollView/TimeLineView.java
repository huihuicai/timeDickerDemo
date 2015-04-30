package com.example.timedickerdemo.newScrollView;

import com.example.timedickerdemo.R;
import com.example.timedickerdemo.newScrollView.MyHorizontalScrollView.StopListenter;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class TimeLineView extends FrameLayout {

	public interface OnValueChangeListener {
		void oonValueChanged(int topValue, int bottomValue);
	}

	private OnValueChangeListener mOnValueChangeListener;

	private MyHorizontalScrollView mHorizontalScrollView;
	private LinearLayout mContainer;

	private int mScreenWidth;
	private int mLineGap;
	private float mMarkLeft;
	
	private int mTopValue;
	private int mBottomValue;
	private int mDeltayValue;

	LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
			1080, LinearLayout.LayoutParams.WRAP_CONTENT);

	public TimeLineView(Context context) {
		super(context);
	}

	public TimeLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
		mLineGap = (int) (mScreenWidth / 12.5);
		mMarkLeft = (float) (0.5 * mScreenWidth / 12.5);
		init(context);
	}

	public TimeLineView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.time_line_view, this,
				true);
		mHorizontalScrollView = (MyHorizontalScrollView) findViewById(R.id.time_line_scroll);
		mContainer = (LinearLayout) findViewById(R.id.time_line_container);
		for (int i = 0; i < 5; i++) {
			TimeLine timeLine = new TimeLine(context, null);
			timeLine.setCurrentTopValue(2015 + i - 2);
			mContainer.addView(timeLine, localLayoutParams);
		}

		mHorizontalScrollView.setStopListener(new StopListenter() {
			@Override
			public void stop(boolean isStop) {
				// TODO 处理停止后的事件处理
				int scrollX = mHorizontalScrollView.getScrollX();
				int deltay = (int) ((scrollX - 40) / mLineGap);
				mDeltayValue = (scrollX - 40) % mLineGap;
				mTopValue = deltay / 12 + 2013;
				mBottomValue = deltay % 12;
				Log.e("stop===", "year:" + mTopValue + "    month:" + mBottomValue
						+ "   scrollX:" + scrollX);

				if (isStop) {
					Log.e("mHorizontalScrollView", "已经停止了");
					handleStop();
				}

				if (mOnValueChangeListener != null) {
					mOnValueChangeListener.oonValueChanged(mTopValue, mBottomValue);
				}
			}
		});
	}

	public void setOnValueChangeListener(OnValueChangeListener changeListener) {
		mOnValueChangeListener = changeListener;
	}

	private void handleStop() {
		// TODO 计算当前的值，如果不是正好的位置，那就要回弹到一个最近位置
		int deltay = mLineGap/2 - mDeltayValue;
		
		if(deltay > 0){
			mBottomValue += 1;
		}else{
			mBottomValue -= 1;
		}
		mHorizontalScrollView.smoothScrollBy(deltay, 0);
		
	}

}
