package com.example.timedickerdemo;

import android.annotation.SuppressLint;
import android.content.Context;
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

	public static final int MOD_TYPE_HALF = 2;
	public static final int MOD_TYPE_ONE = 10;

	private static final int ITEM_HALF_DIVIDER = 40;
	private static final int ITEM_ONE_DIVIDER = 10;

	private static final int ITEM_MAX_HEIGHT = 50;

	private static final int TEXT_SIZE = 18;

	private float mDensity;
	private int mValue = 1, mMaxValue = 12, mModType = MOD_TYPE_HALF,
			mLineDivider = ITEM_HALF_DIVIDER;

	private int mLastX, mMove;
	private int mWidth, mHeight;

	private int mMinVelocity;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private OnValueChangeListener mListener;

	private int mTopValue = 2015;
	
	private int mDrawValue = mTopValue;

	@SuppressWarnings("deprecation")
	public TuneWheel(Context context, AttributeSet attrs) {
		super(context, attrs);

		mScroller = new Scroller(getContext());
		mDensity = getContext().getResources().getDisplayMetrics().density;

		mMinVelocity = ViewConfiguration.get(getContext())
				.getScaledMinimumFlingVelocity();

	}

	public void initViewParam(int defaultValue, int maxValue, int model) {
		switch (model) {
		case MOD_TYPE_HALF:
			mModType = MOD_TYPE_HALF;
			mLineDivider = ITEM_HALF_DIVIDER;
			mValue = defaultValue * 2;
			mMaxValue = maxValue * 2;
			break;
		case MOD_TYPE_ONE:
			mModType = MOD_TYPE_ONE;
			mLineDivider = ITEM_ONE_DIVIDER;
			mValue = defaultValue;
			mMaxValue = maxValue;
			break;

		default:
			break;
		}
		invalidate();

		mLastX = 0;
		mMove = 0;
		notifyValueChange();
	}

	/**
	 * 设置用于接收结果的监听器
	 * 
	 * @param listener
	 */
	public void setValueChangeListener(OnValueChangeListener listener) {
		mListener = listener;
	}

	/**
	 * 获取当前刻度值
	 * 
	 * @return
	 */
	public float getValue() {
		return mValue;
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
		drawMiddleLine(canvas);
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

//		canvas.restore();
	}

	/**
	 * 画中间的红色指示线、阴影等。指示线两端简单的用了两个矩形代替
	 * 
	 * @param canvas
	 */
	private void drawMiddleLine(Canvas canvas) {

		canvas.save();

		Paint redPaint = new Paint();
		redPaint.setStrokeWidth(10);
		redPaint.setColor(Color.RED);
		canvas.drawLine((float) (0.5 * mLineDivider), 0,
				(float) (0.5 * mLineDivider), mHeight, redPaint);

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
			changeMoveAndValue();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			countMoveEnd();
			countVelocityTracker(event);
			return false;
			// break;
		default:
			break;
		}

		mLastX = xPosition;
		return true;
	}

	private void countVelocityTracker(MotionEvent event) {
		mVelocityTracker.computeCurrentVelocity(1000);
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
				if (mTopValue == 2013) {
					mValue = 1;
					mDrawValue = 2013;
					mTopValue = 2013;
					Log.e("现在value的值是在2013", "mTopValue:"+mTopValue+"   mDrawValue:"+mDrawValue);
				} else {
					mValue = mMaxValue;
					mDrawValue = mTopValue;
					mTopValue -= 1;
					Log.e("现在value的值不是在2013", "mTopValue:"+mTopValue+"   mDrawValue:"+mDrawValue);

				}
			} else if (mValue > mMaxValue) {
				mValue = 1;
				if (mTopValue == 2016) {
					Log.e("value是2016", "mTopValue:"+mTopValue+"   mDrawValue:"+mDrawValue);
					mDrawValue = 2017;
					mTopValue = 2017;
				}else{
					Log.e("最大值大于12", "mTopValue:"+mTopValue+"   mDrawValue:"+mDrawValue);
					mTopValue += 1;
					mDrawValue = mTopValue; 
				}
			}else if(mTopValue == mDrawValue){
				mDrawValue += 1;
			}

//			 mMove = 0;
//			 mScroller.forceFinished(true);
			// if (mValue <= 1 || mValue > mMaxValue) {
			// mValue = mValue <= 1 ? 1 : mMaxValue;
			// mMove = 0;
			// mScroller.forceFinished(true);
			// }
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
		Log.e("countMoveEnd", "mMove:"+mMove+"    roundMove:"+roundMove+"   mValue:"+mValue);

		if (mValue < 1) {
			Log.e("countMoveEnd", "进入到了<1的状态"+mDrawValue);
			if (mTopValue == 2013) {
				mDrawValue = 2013;
				mTopValue = 2013;
				mValue = 1;
			} else {
				mValue = mMaxValue;
				mDrawValue = mTopValue;
				mTopValue -= 1;
			}
		} else if (mValue > mMaxValue) {
			Log.e("countMoveEnd", "超过了>12的状态"+mDrawValue);
			mValue = 1;
			if (mTopValue == 2016) {
				mDrawValue = 2017;
				mTopValue = 2017;
			} else{
				mTopValue += 1;
				mDrawValue = mTopValue; 
			}
		}

		// mValue = mValue <= 1 ? 1 : mValue;
		// mValue = mValue > mMaxValue ? mMaxValue : mValue;

		mLastX = 0;
		mMove = 0;

		notifyValueChange();
		postInvalidate();
	}

	private void notifyValueChange() {
		if (null != mListener) {
			mListener.onValueChange(mTopValue,mValue);
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
				changeMoveAndValue();
				mLastX = xPosition;
			}
		}
	}
}
