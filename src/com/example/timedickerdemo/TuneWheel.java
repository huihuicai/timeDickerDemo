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
	 * ��ֵ�����仯�ļ�����
	 */
	private OnValueChangeListener mListener;
	/**
	 * ��������
	 */
	private Scroller mScroller;
	/**
	 * �ٶȸ���
	 */
	private VelocityTracker mVelocityTracker;
	/**
	 * �̶ȵĸ߶�(dp)
	 */
	private int mItemHeight;
	/**
	 * �̶�ֵ�ĸ߶�(dp)
	 */
	private int mTextHeight;
	/**
	 * �����ܶ�
	 */
	private float mDensity;
	/**
	 * ��һ�λ����ľ���
	 */
	private int mLastX;
	/**
	 * move�¼���ʱ�򻬶�����
	 */
	private int mMove;
	/**
	 * ��Ļ�Ŀ��
	 */
	private int mWidth;
	/**
	 * �̶ȼ��ֵ
	 */
	public int mLineDivider;
	/**
	 * ��С�ٶ�
	 */
	private int mMinVelocity;
	/**
	 * ʱ�����ϲ���ֵ(��)
	 */
	public int mTopValue = 2015;
	/**
	 * ʱ�����ϲ������ֵ(��)
	 */
	private int mMaxTop = mTopValue + 2;
	/**
	 * ʱ�����ϲ�����Сֵ(��)
	 */
	private int mMinTop = mTopValue - 2;
	/**
	 * ʱ�����²������ֵ(��)
	 */
	private int mMaxValue = 12;
	/**
	 * ʱ�����²������ֵ(��)
	 */
	private int mMinValue = 1;
	/**
	 * ʱ�����²���ֵ(��)
	 */
	public int mValue = 1;
	/**
	 * ��Ҫ��ʱ������ϲ�draw��ֵ
	 */
	private int mDrawValue = mTopValue;
	/**
	 * ��Ҫdraw��bitmap(ʱ�����ϵ�С��)
	 */
	private Bitmap mDrawBitamp;
	/**
	 * ʱ�����ϵı�־��(С��)��move�¼�ʱ���ƶ�����
	 */
	public float mBitmapMove;
	/**
	 * �Ƿ�����߽�
	 */
	private boolean mIsLeftEdge;
	/**
	 * �Ƿ����ұ߽�
	 */
	private boolean mIsRightEdge;
	/**
	 * �Ƿ�Ҫdraw��־��(С��)
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
	 * �Ƿ�Ҫdraw��ǵ�mark
	 * 
	 * @param isDraw
	 */
	public void whetherDrawMark(boolean isDraw, float startPosition) {
		mIsDrawMark = isDraw;
		mBitmapMove = startPosition;
		postInvalidate();
	}

	/**
	 * �������ڽ��ս���ļ�����
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
	 * �����̶��ߺͶ�Ӧ������
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
	 * ������ʱ��ʱ�̼����ʱ��ֵ
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
				Log.e("changeMoveAndValue", "mValue��ֵ����1��,tValue:" + tValue);
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
	 * ������ʱ�򣬼�����Ӧ��valueֵ
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
