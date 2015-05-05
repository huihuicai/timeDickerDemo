package com.example.timedickerdemo;

import com.example.timedickerdemo.TuneWheel.OnValueChangeListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class DefineWheel extends FrameLayout {

	public interface OnDataChangeListener {
		void onDataChange(int year, int month);
	}

	/**
	 * 数值变化的监听器
	 */
	private OnDataChangeListener mChangeListener;
	/**
	 * 时间轴的布局参数
	 */
	private FrameLayout.LayoutParams params = new LayoutParams(
			FrameLayout.LayoutParams.MATCH_PARENT,
			FrameLayout.LayoutParams.MATCH_PARENT);
	/**
	 * 移动的标识物的布局参数
	 */
	private FrameLayout.LayoutParams markParams = new LayoutParams(
			FrameLayout.LayoutParams.WRAP_CONTENT,
			FrameLayout.LayoutParams.WRAP_CONTENT);
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

	/**
	 * 初始化组件
	 * 
	 * @param context
	 * @param attrs
	 */
	private void init(Context context, AttributeSet attrs) {
		mTimeShaft = new TuneWheel(getContext(), attrs);
		addView(mTimeShaft, params);
		
		mMoveMarkView = new ImageView(context);
		mMoveMarkView.setImageResource(R.drawable.car);
		addView(mMoveMarkView, markParams);
		mMoveMarkView.setVisibility(View.GONE);

		mTimeShaft.setValueChangeListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(int year, int month) {
				if (mChangeListener != null) {
					mChangeListener.onDataChange(year, month);
				}
			}
		});
	}

	/**
	 * 设置监听器
	 * 
	 * @param listener
	 */
	public void setOnDataChangeListener(OnDataChangeListener listener) {
		mChangeListener = listener;
	}

	/**
	 * 动画的始终位置
	 * 
	 * @param start
	 * @param end
	 */
	private void startMove(int start, int end) {
		// 1.时间轴要重绘,将moveView显示出来
		// 2.moveView开始执行动画
		mTimeShaft.postInvalidate();
		mMoveMarkView.setVisibility(View.VISIBLE);
		TranslateAnimation animation = new TranslateAnimation(start, end, 0, 0);
		animation.setFillAfter(true);
		animation.setDuration(500);
		animation.setInterpolator(new AccelerateDecelerateInterpolator());
		mMoveMarkView.startAnimation(getAnimation());
	}
	/**
	 * 初始化时间轴上
	 * @param year
	 * @param month
	 */
	public void initTimeShaft(int year,int month){
		mTimeShaft.initViewParam(year, month);
	} 
	/**
	 * 移动标志物
	 * @param year
	 * @param month
	 * @param day
	 */
	private void moveMark(int year,int month,int day){
		
	}
	
}
