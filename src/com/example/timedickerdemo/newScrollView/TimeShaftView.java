package com.example.timedickerdemo.newScrollView;

import com.example.timedickerdemo.R;
import com.example.timedickerdemo.newScrollView.HorizontalScrollViewShaft.StopListenter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class TimeShaftView extends FrameLayout {

	public interface OnValueChangeListener1 {
		void oonValueChanged(boolean isSlipStop, int topValue, int bottomValue);
	}

	private OnValueChangeListener1 mOnValueChangeListener;

	private HorizontalScrollViewShaft mHorizontalScrollView;
	private LinearLayout mContainer;
	private ImageView mMoveMark;
	private View mMarkView;

	private int mScreenWidth;
	private int mLineGap;
	private int mHalfLineGap;
	/**
	 * ��ǰ��ʱ��ֵ
	 */
	private int mCurrentYear;
	private int mCurrentMonth;
	private int mMinYear;

	private int deltaGap = 0;

	private LinearLayout.LayoutParams normalLayoutParams;
	private LinearLayout.LayoutParams bigLayoutParams;

	public TimeShaftView(Context context) {
		super(context);
	}

	public TimeShaftView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
		mLineGap = (int) (mScreenWidth / 12.5);
		mHalfLineGap = (int) (0.5 * mLineGap);

		deltaGap = mScreenWidth - 12 * mLineGap - mHalfLineGap;

		normalLayoutParams = new LinearLayout.LayoutParams(mScreenWidth
				- mHalfLineGap - deltaGap,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		bigLayoutParams = new LinearLayout.LayoutParams(mScreenWidth,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		initTime();
		initWidget(context);
	}

	/**
	 * ��ʼ��ʱ����ĸ���ֵ
	 */
	private void initTime() {
		Time time = new Time("GTM+8");
		time.setToNow();
		mCurrentYear = time.year;
		mCurrentMonth = time.month + 1;
		mMinYear = mCurrentYear - 2;

	}

	/**
	 * ��ʼ�����
	 * 
	 * @param context
	 */
	private void initWidget(Context context) {
		LayoutInflater.from(context).inflate(R.layout.time_line_view, this,
				true);

		mMarkView = new View(context);
		mMarkView.setBackgroundColor(Color.BLACK);
		FrameLayout.LayoutParams layoutParams = new LayoutParams(2, 45);
		layoutParams.leftMargin = mHalfLineGap;
		addView(mMarkView, layoutParams);

		mMoveMark = (ImageView) findViewById(R.id.move_mark);
		mHorizontalScrollView = (HorizontalScrollViewShaft) findViewById(R.id.time_line_scroll);
		mContainer = (LinearLayout) findViewById(R.id.time_line_container);
		for (int i = 0; i < 5; i++) {
			TimeShaftLine timeLine = new TimeShaftLine(context, null);
			timeLine.setCurrentTopValue(mCurrentYear + i - 2);
			if (i < 4) {
				mContainer.addView(timeLine, normalLayoutParams);
			} else {
				mContainer.addView(timeLine, bigLayoutParams);
			}
		}

		mHorizontalScrollView.setStopListener(new StopListenter() {
			@Override
			public void stop(boolean isStop) {
				int scrollX = mHorizontalScrollView.getScrollX();
				int deltay = (int) (scrollX / mLineGap);
				int deltayValue = Math.abs(scrollX % mLineGap);
				mCurrentYear = deltay / 12 + mMinYear;
				mCurrentMonth = deltay % 12 + 1;

				if (isStop) {
					handleStop(deltayValue);
				}

				if (mOnValueChangeListener != null) {
					mOnValueChangeListener.oonValueChanged(isStop,
							mCurrentYear, mCurrentMonth);
				}
			}
		});

		post(new Runnable() {
			@Override
			public void run() {
				scrollTimerShaftBack();
			}
		});
	}

	/**
	 * ֹͣ������ʱ���п��ܲ����������̶ȣ���Ҫ�ص������̶�
	 */
	private void handleStop(int deltaDay) {
		if (deltaDay == 0) {
			return;
		}
		int deltaMove = -deltaDay;
		if (deltaDay >= mHalfLineGap) {
			mCurrentMonth += 1;
			deltaMove = mLineGap - deltaDay;
		}
		mHorizontalScrollView.smoothScrollBy(deltaMove, 0);
	}

	/**
	 * ������ʱ���ᵽָ����λ��
	 */
	public void scrollTimerShaftBack() {
		// 1.��ǰ��ֵ���·� 2.���������룬Ȼ��smoothScrollָ���ľ���
		Time time = new Time("GTM+8");
		time.setToNow();
		int year = time.year;
		int month = time.month + 1;
		int lenth = (12 * (year - mMinYear) + month - 1) * mLineGap;
		Log.e("scroll", "�ƶ��ľ��룺" + lenth + "    �·ݣ�"
				+ (12 * (year - mMinYear) + month - 1));
		mHorizontalScrollView.smoothScrollTo(lenth, 0);
	}

	/**
	 * С��־����ƶ�
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	public void setMarkDestion(int year, int month, int day) {
		float markMoveLenth = Math
				.round(((year - mMinYear) * 12 + (month - 1) + day / 30.0)
						* mLineGap + mHalfLineGap);
		if (markMoveLenth == 0) {
			return;
		}
		Log.e("setMarkDestion", "year:" + year + "    month:" + month
				+ "   ���룺" + markMoveLenth);

		mMoveMark.clearAnimation();

		ObjectAnimator animator = ObjectAnimator.ofFloat(mMoveMark, "x",
				markMoveLenth);
		animator.start();
	}

	public void setOnValueChangeListener(OnValueChangeListener1 changeListener) {
		mOnValueChangeListener = changeListener;
	}

}
