package com.example.timedickerdemo.newScrollView;

import com.example.timedickerdemo.R;
import com.example.timedickerdemo.newScrollView.MyHorizontalScrollView.StopListenter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class TimeLineView extends FrameLayout {

	public interface OnValueChangeListener1 {
		void oonValueChanged(boolean isSlipStop, int topValue, int bottomValue);
	}

	private OnValueChangeListener1 mOnValueChangeListener;

	private MyHorizontalScrollView mHorizontalScrollView;
	private LinearLayout mContainer;
	private ImageView mMoveMark;
	private View mMarkView;

	private int mScreenWidth;
	private int mLineGap;
	private int mMarkLeft;

	private int mTopValue;
	private int mBottomValue;
	private int mDeltayValue;
	private float mMarkMoveLenth;
	private float mMarkLenth = 0;

	private int mYear = 2013;
	private int mMonth = 1;
	private int mDay = 1;

	private LinearLayout.LayoutParams normalLayoutParams;
	private LinearLayout.LayoutParams bigLayoutParams;

	public TimeLineView(Context context) {
		super(context);
	}

	public TimeLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
		mLineGap = (int) (mScreenWidth / 12.5);
		mMarkLeft = (int) (0.5 * mLineGap);
		normalLayoutParams = new LinearLayout.LayoutParams(mScreenWidth
				- mMarkLeft, LinearLayout.LayoutParams.WRAP_CONTENT);

		bigLayoutParams = new LinearLayout.LayoutParams(mScreenWidth,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		init(context);
	}

	public TimeLineView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.time_line_view, this,
				true);
		
		mMarkView = new View(context);
		mMarkView.setBackgroundColor(Color.BLACK);
		FrameLayout.LayoutParams layoutParams = new LayoutParams(2, 32);
		layoutParams.leftMargin = mMarkLeft;
		addView(mMarkView, layoutParams);
		
		mMoveMark = (ImageView) findViewById(R.id.move_mark);
		mHorizontalScrollView = (MyHorizontalScrollView) findViewById(R.id.time_line_scroll);
		mContainer = (LinearLayout) findViewById(R.id.time_line_container);
		for (int i = 0; i < 5; i++) {
			TimeLine timeLine = new TimeLine(context, null);
			timeLine.setCurrentTopValue(2015 + i - 2);
			if (i < 4) {
				mContainer.addView(timeLine, normalLayoutParams);
			} else {
				mContainer.addView(timeLine, bigLayoutParams);
			}
		}

		mHorizontalScrollView.setStopListener(new StopListenter() {
			@Override
			public void stop(boolean isStop) {
				// TODO 处理停止后的事件处理
				int scrollX = mHorizontalScrollView.getScrollX();
				int deltay = (int) (scrollX / mLineGap);
				mDeltayValue = Math.abs(scrollX % mLineGap);
				mTopValue = deltay / 12 + 2013;
				mBottomValue = deltay % 12 + 1;
				Log.e("stop===", "year:" + mTopValue + "    month:"
						+ mBottomValue + "   scrollX:" + scrollX);

				if (isStop) {
					Log.e("mHorizontalScrollView", "已经停止了");
					handleStop();
				}

				if (mOnValueChangeListener != null) {
					mOnValueChangeListener.oonValueChanged(isStop, mTopValue,
							mBottomValue);
				}
			}
		});
	}

	public void setOnValueChangeListener(OnValueChangeListener1 changeListener) {
		mOnValueChangeListener = changeListener;
	}

	private void handleStop() {
		// TODO 计算当前的值，如果不是正好的位置，那就要回弹到一个最近位置
		if (mDeltayValue == 0) {
			return;
		}
		int delta = mDeltayValue - Math.round((float) (mLineGap / 2.0));
		Log.e("handleStop",
				"delta:" + delta + "   mDeltayValue:" + mDeltayValue
						+ "   mLineGap/2.0: "
						+ Math.round((float) (mLineGap / 2.0)));
		if (delta > 0) {
			mBottomValue += 1;
		} else {
			delta = -mDeltayValue;
		}

		mHorizontalScrollView.smoothScrollBy(delta, 0);

	}

	/**
	 * 小标志物的移动
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	public void setMarkDestion(int year, int month, int day) {
		mMarkMoveLenth = Math
				.round(((year - mYear) * 12 + (month - mMonth) + (day - mDay) / 30.0)
						* mLineGap);
		if (mMarkMoveLenth == 0) {
			return;
		}
		Log.e("setMarkDestion", "year:" + (year - mYear) + "    month:"
				+ (month - mMonth) + "   距离：" + mMarkMoveLenth);
		TranslateAnimation animation;
		if (year >= mYear && month >= mMonth && day >= mDay) {
			animation = new TranslateAnimation(mMarkLenth, mMarkMoveLenth, 0, 0);
		} else {
			animation = new TranslateAnimation(mMarkLenth, -mMarkMoveLenth, 0,
					0);
		}

		animation.setDuration(1000);
		animation.setFillAfter(true);

		mMoveMark.startAnimation(animation);

		mYear = year;
		mMonth = month;
		mDay = day;

		mMarkLenth += mMarkMoveLenth;
	}

}
