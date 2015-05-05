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
	 * ��ֵ�仯�ļ�����
	 */
	private OnDataChangeListener mChangeListener;
	/**
	 * ʱ����Ĳ��ֲ���
	 */
	private FrameLayout.LayoutParams params = new LayoutParams(
			FrameLayout.LayoutParams.MATCH_PARENT,
			FrameLayout.LayoutParams.MATCH_PARENT);
	/**
	 * �ƶ��ı�ʶ��Ĳ��ֲ���
	 */
	private FrameLayout.LayoutParams markParams = new LayoutParams(
			FrameLayout.LayoutParams.WRAP_CONTENT,
			FrameLayout.LayoutParams.WRAP_CONTENT);
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

	/**
	 * ��ʼ�����
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
	 * ���ü�����
	 * 
	 * @param listener
	 */
	public void setOnDataChangeListener(OnDataChangeListener listener) {
		mChangeListener = listener;
	}

	/**
	 * ������ʼ��λ��
	 * 
	 * @param start
	 * @param end
	 */
	private void startMove(int start, int end) {
		// 1.ʱ����Ҫ�ػ�,��moveView��ʾ����
		// 2.moveView��ʼִ�ж���
		mTimeShaft.postInvalidate();
		mMoveMarkView.setVisibility(View.VISIBLE);
		TranslateAnimation animation = new TranslateAnimation(start, end, 0, 0);
		animation.setFillAfter(true);
		animation.setDuration(500);
		animation.setInterpolator(new AccelerateDecelerateInterpolator());
		mMoveMarkView.startAnimation(getAnimation());
	}
	/**
	 * ��ʼ��ʱ������
	 * @param year
	 * @param month
	 */
	public void initTimeShaft(int year,int month){
		mTimeShaft.initViewParam(year, month);
	} 
	/**
	 * �ƶ���־��
	 * @param year
	 * @param month
	 * @param day
	 */
	private void moveMark(int year,int month,int day){
		
	}
	
}
