package com.example.timedickerdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class DefineWheel extends FrameLayout {
	
	private FrameLayout.LayoutParams params = new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
	private FrameLayout.LayoutParams markParams = new LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
	/**
	 * 时间轴
	 */
	private TuneWheel mTimeShaft;
	/**
	 * 移动的标志物
	 */
	private ImageView mMoveMarkView;

	public DefineWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		mTimeShaft = new TuneWheel(getContext(), attrs);
		addView(mTimeShaft, params);
		mMoveMarkView = new ImageView(context);
		mMoveMarkView.setImageResource(R.drawable.car);
		addView(mMoveMarkView,markParams);
	}
	/**
	 * 动画的始终位置
	 * @param start
	 * @param end
	 */
	private void startMove(int start,int end){
		//1.时间轴要重绘,将moveView显示出来
		//2.moveView开始执行动画
		mTimeShaft.postInvalidate();
		TranslateAnimation animation = new TranslateAnimation(start, end, 0, 0);
		animation.setFillAfter(true);
		animation.setDuration(500);
		
	}
}
