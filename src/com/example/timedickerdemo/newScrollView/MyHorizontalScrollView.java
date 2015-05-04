package com.example.timedickerdemo.newScrollView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class MyHorizontalScrollView extends HorizontalScrollView {

	public interface StopListenter {
		void stop(boolean isStop);
	}

	private StopListenter mStopListenter;
	private Runnable mStopRunnable;
	private int mLastX;
	private int DELAY = 10;

	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mStopRunnable = new Runnable() {
			@Override
			public void run() {
				int currentX = getScrollX();
				if (mLastX - currentX == 0) {
					// TODO 通知滑动时停止的
					if (mStopListenter != null) {
						mStopListenter.stop(true);
					}
				} else {
					if (mStopListenter != null) {
						mStopListenter.stop(false);
					}
					mLastX = currentX;
					postDelayed(mStopRunnable, DELAY);
				}
			}
		};
	}

	public void setStopListener(StopListenter listenter) {
		mStopListenter = listenter;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (/*ev.getAction() == MotionEvent.ACTION_DOWN || */ev.getAction() == MotionEvent.ACTION_UP) {
			// TODO 启动一个线程
			postDelayed(mStopRunnable, DELAY);
		}
		return super.onTouchEvent(ev);
	}

}
