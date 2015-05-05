package com.example.timedickerdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.TranslateAnimation;
import android.widget.Scroller;

@SuppressLint("ClickableViewAccessibility")
public class TuneWheel extends View {

	public interface OnValueChangeListener {
		public void onValueChange(int year, int month);
	}

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

	private int mMaxTop = mTopValue + 2;

	private int mMinTop = mTopValue - 2;

	private int mDrawValue = mTopValue;

	private Bitmap mDrawBitamp;

	private int mBitmapMove;

	private boolean mIsLeftEdge;

	private boolean mIsRightEdge;

	private Rect normal = new Rect();

	private float mMarkMoveLenth;

	private boolean mMoveMark;

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
	
	public int getTopValue(){
		return mTopValue;
	}
	
	public int getBottomValue(){
		return mValue;
	}
	
	public int getLineGap(){
		return mLineDivider;
	}

	/**
	 * 标志物需要移动到的位置
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	public void setMoveDestination(int year, int month, int day) {
		mMoveMark = true;
		mMarkMoveLenth = Math.round(((year - mTopValue) * 12 + (month - mValue)
				+ day / 30.0 + 0.5)
				* mLineDivider);
		Log.e("setMoveDestination", "mMarkMoveLenth:" + mMarkMoveLenth);
		postInvalidate();
		mLastX = 0;
		mMove = 0;
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
		drawMarker(canvas);
	}

	private void drawMarker(Canvas canvas) {
		if (mMoveMark) {
			int len = 0;
			int i = 0;
			while (len <= Math.abs(mMarkMoveLenth)) {
				canvas.drawColor(Color.TRANSPARENT);
				if (mMarkMoveLenth >= 0) {
					canvas.drawBitmap(mDrawBitamp, len, 0, new Paint());
					len += 20;
				} else {
					canvas.drawBitmap(mDrawBitamp, len, 0, new Paint());
					len = mWidth - 20 * i;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		} else {
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
			// break;
		default:
			break;
		}

		mLastX = xPosition;
		return true;
	}

	/**
	 * 从边缘的位置拖拽
	 * 
	 * @param preX
	 * @param currentX
	 */
	private void pullToEdge(int preX, int currentX) {
		int deltay = (preX - currentX) / 4;
		if (normal.isEmpty()) {
			normal.set(getLeft(), getTop(), getRight(), getBottom());
		}
		layout(getLeft() - deltay, getTop(), getRight() - deltay, getBottom());
	}

	/**
	 * 回弹时候的动画
	 */
	private void reBackAnimation() {
		TranslateAnimation ta = new TranslateAnimation(0, 0, getTop(),
				normal.top);
		ta.setDuration(200);
		startAnimation(ta);
		layout(normal.left, normal.top, normal.right, normal.bottom);
		normal.setEmpty();
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
				// if (mIsEdge) {
				// mIsEdge = false;
				// return;
				// }
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
