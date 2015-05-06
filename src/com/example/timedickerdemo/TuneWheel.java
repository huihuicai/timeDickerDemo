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

	private static final int ITEM_MAX_HEIGHT = 50;

	private static final int TEXT_SIZE = 18;

	private float mDensity;
	public int mValue = 1, mMaxValue = 12, mLineDivider;

	private int mLastX, mMove;
	private int mWidth, mHeight;

	private int mMinVelocity;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private OnValueChangeListener mListener;

	public int mTopValue = 2015;

	private int mMaxTop = mTopValue + 2;

	private int mMinTop = mTopValue - 2;

	private int mDrawValue = mTopValue;

	private Bitmap mDrawBitamp;

	public float mBitmapMove;

	private boolean mIsLeftEdge;

	private boolean mIsRightEdge;

	private boolean mIsDrawMark;

	@SuppressWarnings("deprecation")
	public TuneWheel(Context context, AttributeSet attrs) {
		super(context, attrs);

		mScroller = new Scroller(getContext());
		mDensity = getContext().getResources().getDisplayMetrics().density;

		mMinVelocity = ViewConfiguration.get(getContext())
				.getScaledMinimumFlingVelocity();

		mDrawBitamp = BitmapFactory.decodeResource(getResources(),
				R.drawable.car);

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
		mHeight = getHeight();
		mLineDivider = (int) (mWidth / 12.5);
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawScaleLine(canvas);
		drawMarkLine(canvas);
		if (mIsDrawMark) {
			canvas.drawBitmap(mDrawBitamp, mBitmapMove, 0, new Paint());
		}
	}

	/**
	 * 画出刻度线和对应的数字
	 * 
	 * @param canvas
	 */
	private void drawScaleLine(Canvas canvas) {
		canvas.save();

		Paint linePaint = new Paint();
		linePaint.setStrokeWidth(2);
		linePaint.setColor(Color.BLACK);

		TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(TEXT_SIZE * mDensity);

		canvas.drawLine(0, 50, mWidth, 50, linePaint);

		int width = mWidth, drawCount = 0;
		float xPosition = 0, textWidth = Layout.getDesiredWidth("0", textPaint);

		int drawNumber = 1;
		for (int i = 0; drawCount <= width; i++) {
			int numSize = String.valueOf(mValue + i).length();

			xPosition = (float) (-mMove + (i + 0.5) * mLineDivider);

			if (mValue + i <= mMaxValue) {
				drawNumber = mValue + i;
			} else {
				drawNumber = mValue + i - mMaxValue;
			}

			if (drawNumber == 1) {
				canvas.drawLine(xPosition, getPaddingTop(), xPosition, mDensity
						* ITEM_MAX_HEIGHT, linePaint);
				canvas.drawText(String.valueOf(mDrawValue), xPosition, 30,
						textPaint);
			} else {
				canvas.drawLine(xPosition, getPaddingTop() + 50, xPosition,
						mDensity * ITEM_MAX_HEIGHT, linePaint);
			}

			canvas.drawText(String.valueOf(drawNumber), xPosition
					- (textWidth * numSize / 2), getHeight() - textWidth,
					textPaint);

			drawCount += mLineDivider;
		}

		canvas.restore();
	}

	private void drawMarkLine(Canvas canvas) {

		canvas.save();

		Paint redPaint = new Paint();
		redPaint.setStrokeWidth(4);
		redPaint.setColor(Color.BLACK);
		canvas.drawLine((float) (0.5 * mLineDivider), 0,
				(float) (0.5 * mLineDivider), 50, redPaint);

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
			countMoveEnd();
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
			if (mValue <= 1) {
				if (mValue == 1) {
					mBitmapMove += mLineDivider;
				}
				Log.e("changeMoveAndValue", "mValue的值下于1了,tValue:" + tValue);
				if (mTopValue == mMinTop) {
					mValue = 1;
					mDrawValue = mMinTop;
					mTopValue = mMinTop;
					mIsLeftEdge = true;
				} else {
					mValue = mMaxValue;
					mDrawValue = mTopValue;
					mTopValue -= 1;

				}
			} else if (mValue > mMaxValue) {
				mValue = 1;
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
	private void countMoveEnd() {
		int roundMove = Math.round(mMove / ((float) 1.0 * mLineDivider));
		mValue = mValue + roundMove;
		mBitmapMove += mMove % mLineDivider;
		if (mValue < 1) {
			if (mTopValue == mMinTop) {
				mIsLeftEdge = true;
				mDrawValue = mMinTop;
				mTopValue = mMinTop;
				mValue = 1;
			} else {
				mValue = mMaxValue;
				mDrawValue = mTopValue;
				mTopValue -= 1;
			}
		} else if (mValue > mMaxValue) {
			mValue = 1;
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
				countMoveEnd();
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
