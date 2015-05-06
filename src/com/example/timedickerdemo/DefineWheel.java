package com.example.timedickerdemo;

import com.example.timedickerdemo.TuneWheel.OnValueChangeListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
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

	private float mMarkMarginLeft;

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
	private void startMove(float start, float end) {
		// 1.时间轴要重绘,将moveView显示出来
		// 2.moveView开始执行动画
		mMoveMarkView.clearAnimation();
		mMoveMarkView.setVisibility(View.VISIBLE);
		mTimeShaft.whetherDrawMark(false, mMarkMarginLeft);
		TranslateAnimation animation = new TranslateAnimation(start, end, 0, 0);
		animation.setFillAfter(true);
		animation.setDuration(500);
		animation.setInterpolator(new AccelerateDecelerateInterpolator());
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mMoveMarkView.clearAnimation();
				mMoveMarkView.setVisibility(View.GONE);
				mTimeShaft.whetherDrawMark(true, mMarkMarginLeft);
			}
		});
		mMoveMarkView.startAnimation(animation);
	}

	/**
	 * 初始化时间轴上
	 * 
	 * @param year
	 * @param month
	 */
	public void initTimeShaft(int year, int month) {
		mTimeShaft.initViewParam(year, month);
	}

	/**
	 * 移动标志物最终需要停止的位置
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	public void setMarkPosition(int year, int month, int day) {
		float len = Math.round(((year - mTimeShaft.mTopValue) * 12
				+ (month - mTimeShaft.mValue) + day / 30.0)
				* mTimeShaft.mLineDivider);
		mMarkMarginLeft = mTimeShaft.mBitmapMove;
		startMove(mMarkMarginLeft, len);
		mMarkMarginLeft = len;
	}

}
