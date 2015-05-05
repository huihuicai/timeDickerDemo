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
	 * ʱ����
	 */
	private TuneWheel mTimeShaft;
	/**
	 * �ƶ��ı�־��
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
	 * ������ʼ��λ��
	 * @param start
	 * @param end
	 */
	private void startMove(int start,int end){
		//1.ʱ����Ҫ�ػ�,��moveView��ʾ����
		//2.moveView��ʼִ�ж���
		mTimeShaft.postInvalidate();
		TranslateAnimation animation = new TranslateAnimation(start, end, 0, 0);
		animation.setFillAfter(true);
		animation.setDuration(500);
		
	}
}
