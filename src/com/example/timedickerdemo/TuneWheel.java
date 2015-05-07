package com.example.timedickerdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

@SuppressLint("ClickableViewAccessibility")
public class TuneWheel extends View {

	public interface OnValueChangeListener {
		public void onValueChange(int year, int month);
	}

	private Paint mLinePaint;
	private TextPaint mTextPaint;
	/**
	 * 数值发生变化的监听器
	 */
	private OnValueChangeListener mListener;
	/**
	 * 滑动辅助
	 */
	private Scroller mScroller;
	/**
	 * 速度跟踪
	 */
	private VelocityTracker mVelocityTracker;
	/**
	 * 刻度的高度(dp)
	 */
	private int mItemHeight;
	/**
	 * 刻度值的高度(dp)
	 */
	private int mTextHeight;
	/**
	 * 像素密度
	 */
	private float mDensity;
	/**
	 * 上一次滑动的距离
	 */
	private int mLastX;
	/**
	 * move事件的时候滑动距离
	 */
	private int mMove;
	/**
	 * 屏幕的宽度
	 */
	private int mWidth;
	/**
	 * 刻度间隔值
	 */
	public int mLineDivider;
	/**
	 * 最小速度
	 */
	private int mMinVelocity;
	/**
	 * 时间轴上部的值(年)
	 */
	public int mTopValue = 2015;
	/**
	 * 时间轴上部的最大值(年)
	 */
	private int mMaxTop = mTopValue + 2;
	/**
	 * 时间轴上部的最小值(年)
	 */
	private int mMinTop = mTopValue - 2;
	/**
	 * 时间轴下部的最大值(月)
	 */
	private int mMaxValue = 12;
	/**
	 * 时间轴下部的最大值(月)
	 */
	private int mMinValue = 1;
	/**
	 * 时间轴下部的值(月)
	 */
	public int mValue = 1;
	/**
	 * 需要在时间轴的上部draw的值
	 */
	private int mDrawValue = mTopValue;
	/**
	 * 需要draw的bitmap(时间轴上的小车)
	 */
	private Bitmap mDrawBitamp;
	/**
	 * 时间轴上的标志物(小车)在move事件时的移动距离
	 */
	public float mBitmapMove;
	/**
	 * 是否到了左边界
	 */
	private boolean mIsLeftEdge;
	/**
	 * 是否到了右边界
	 */
	private boolean mIsRightEdge;
	/**
	 * 是否要draw标志物(小车)
	 */
	private boolean mIsDrawMark;

	@SuppressWarnings("deprecation")
	public TuneWheel(Context context, AttributeSet attrs) {
		super(context, attrs);

		mScroller = new Scroller(getContext());
		mDensity = getContext().getResources().getDisplayMetrics().density;

		mMinVelocity = ViewConfiguration.get(getContext())
				.getScaledMinimumFlingVelocity();

		mItemHeight = (int) (mDensity * 18);
		mTextHeight = (int) (mDensity * 16);

		mDrawBitamp = BitmapFactory.decodeResource(getResources(),
				R.drawable.car);

		initPaint();
	}

	public void initViewParam(int year, int month) {
		mTopValue = year;
		mMaxTop = year + 2;
		mMinTop = year - 2;
		mValue = month;
		if (month < 2) {
			mDrawValue = mTopValue + 1;
		} else {
			mDrawValue = mTopValue;
		}
		invalidate();

		mLastX = 0;
		mMove = 0;
		notifyValueChange();
	}

	/**
	 * 是否要draw标记的mark
	 * 
	 * @param isDraw
	 */
	public void whetherDrawMark(boolean isDraw, float startPosition) {
		mIsDrawMark = isDraw;
		mBitmapMove = startPosition;
		postInvalidate();
	}

	/**
	 * 设置用于接收结果的监听器
	 * 
	 * @param listener
	 */
	public void setValueChangeListener(OnValueChangeListener listener) {
		mListener = listener;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		mWidth = getWidth();
		mLineDivider = (int) (mWidth / 12.5);
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawNumberAndLine(canvas);
		drawMarkLine(canvas);
		if (mIsDrawMark) {
			canvas.drawBitmap(mDrawBitamp, mBitmapMove, 0, new Paint());
		}
	}

	private void initPaint() {
		mLinePaint = new Paint();
		mLinePaint.setStrokeWidth(2);
		mLinePaint.setColor(Color.BLACK);

		mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextSize(mTextHeight);
	}

	/**
	 * 画出刻度线和对应的数字
	 * 
	 * @param canvas
	 */
	private void drawNumberAndLine(Canvas canvas) {
		canvas.save();

		canvas.drawLine(0, mItemHeight, mWidth, mItemHeight, mLinePaint);

		int width = mWidth, drawCount = 0;
		float xPosition = 0, textWidth = Layout
				.getDesiredWidth("0", mTextPaint);

		int drawNumber = 1, numSize;
		for (int i = 0; drawCount <= width; i++) {

			xPosition = (float) (-mMove + (i + 0.5) * mLineDivider);

			if (mValue + i <= mMaxValue) {
				drawNumber = mValue + i;
			} else {
				drawNumber = mValue + i - mMaxValue;
			}

			numSize = String.valueOf(drawNumber).length();

			if (drawNumber == 1) {
				canvas.drawLine(xPosition, getPaddingTop(), xPosition,
						2 * mItemHeight, mLinePaint);
				canvas.drawText(String.valueOf(mDrawValue), xPosition, 30,
						mTextPaint);
			} else {
				canvas.drawLine(xPosition, getPaddingTop() + mItemHeight,
						xPosition, 2 * mItemHeight, mLinePaint);
			}

			canvas.drawText(String.valueOf(drawNumber), xPosition
					- (textWidth * numSize / 2), 3 * mItemHeight, mTextPaint);

			drawCount += mLineDivider;
		}

		canvas.restore();
	}

	private void drawMarkLine(Canvas canvas) {

		canvas.save();

		canvas.drawLine((float) (0.5 * mLineDivider), 0,
				(float) (0.5 * mLineDivider), mItemHeight, mLinePaint);

		canvas.restore();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int xPosition = (int) event.getX();

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		switch (action) {
		case MotionEvent.ACTION_DOWN:

			mScroller.forceFinished(true);

			mLastX = xPosition;
			mMove = 0;
			break;
		case MotionEvent.ACTION_MOVE:
			mMove += (mLastX - xPosition);
			if (mIsLeftEdge && mMove < 0) {
				mMove = 0;
				break;
			}
			if (mIsRightEdge && mMove > 0) {
				mMove = 0;
				break;
			}

			mIsLeftEdge = false;
			mIsRightEdge = false;

			mBitmapMove += (xPosition - mLastX);
			changeMoveAndValue();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			calculateMoveEnd();
			countVelocityTracker(event);
			return false;
		default:
			break;
		}

		mLastX = xPosition;
		return true;
	}

	private void countVelocityTracker(MotionEvent event) {
		mVelocityTracker.computeCurrentVelocity(1000, 3000);
		float xVelocity = mVelocityTracker.getXVelocity();
		if (Math.abs(xVelocity) > mMinVelocity) {
			mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE,
					Integer.MAX_VALUE, 0, 0);
		}
	}

	/**
	 * 滑动的时候，时刻计算此时的值
	 */
	private void changeMoveAndValue() {
		int tValue = Math.round(mMove / ((float) 1.0 * mLineDivider));
		if (Math.abs(tValue) > 0) {
			mValue += tValue;
			mMove -= tValue * mLineDivider;
			if (mValue <= mMinValue) {
				if (mValue == mMinValue) {
					mBitmapMove += mLineDivider;
				}
				Log.e("changeMoveAndValue", "mValue的值下于1了,tValue:" + tValue);
				if (mTopValue == mMinTop) {
					mValue = mMinValue;
					mDrawValue = mMinTop;
					mTopValue = mMinTop;
					mIsLeftEdge = true;
				} else {
					mValue = mMaxValue;
					mDrawValue = mTopValue;
					mTopValue -= 1;

				}
			} else if (mValue > mMaxValue) {
				mValue = mMinValue;
				if (mTopValue >= mMaxTop - 1) {
					mDrawValue = mMaxTop;
					mTopValue = mMaxTop;
					mIsRightEdge = true;
					Log.e("changeMoveAndValue", "mValue > mMaxValue");
				} else {
					mTopValue += 1;
					mDrawValue = mTopValue;
				}
			} else if (mTopValue == mDrawValue) {
				if (mTopValue < mMaxTop) {
					mDrawValue += 1;
				} else {
					Log.e("changeMoveAndValue", "mTopValue == mDrawValue");
					mIsRightEdge = true;
				}
			}

			notifyValueChange();
		}
		postInvalidate();
	}

	/**
	 * 结束的时候，计算相应的value值
	 */
	private void calculateMoveEnd() {
		int roundMove = Math.round(mMove / ((float) 1.0 * mLineDivider));
		mValue = mValue + roundMove;
		mBitmapMove += mMove % mLineDivider;
		if (mValue < mMinValue) {
			if (mTopValue == mMinTop) {
				mIsLeftEdge = true;
				mDrawValue = mMinTop;
				mTopValue = mMinTop;
				mValue = mMinValue;
			} else {
				mValue = mMaxValue;
				mDrawValue = mTopValue;
				mTopValue -= 1;
			}
		} else if (mValue > mMaxValue) {
			mValue = mMinValue;
			if (mTopValue == mMaxTop - 1) {
				mIsRightEdge = true;
				mDrawValue = mMaxTop;
				mTopValue = mMaxTop;
			} else {
				mTopValue += 1;
				mDrawValue = mTopValue;
			}
		}

		mLastX = 0;
		mMove = 0;

		notifyValueChange();
		postInvalidate();
	}

	private void notifyValueChange() {
		if (null != mListener) {
			mListener.onValueChange(mTopValue, mValue);
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			if (mScroller.getCurrX() == mScroller.getFinalX()) { // over
				calculateMoveEnd();
			} else {
				int xPosition = mScroller.getCurrX();
				mMove += (mLastX - xPosition);
				if (mIsLeftEdge && mMove < 0) {
					mMove = 0;
					return;
				}
				if (mIsRightEdge && mMove > 0) {
					mMove = 0;
					return;
				}

				mIsLeftEdge = false;
				mIsRightEdge = false;

				mBitmapMove += (xPosition - mLastX);
				changeMoveAndValue();
				mLastX = xPosition;
			}
		}
	}
}
