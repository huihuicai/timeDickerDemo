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

	public interface OnValueChangeListener {
		public void onValueChange(float value);
	}

	public static final int MOD_TYPE_HALF = 2;
	public static final int MOD_TYPE_ONE = 10;

	private static final int ITEM_HALF_DIVIDER = 40;
	private static final int ITEM_ONE_DIVIDER = 10;

	private static final int ITEM_MAX_HEIGHT = 40;

	private static final int TEXT_SIZE = 18;

	private float mDensity;
	private int mValue = 1, mMaxValue = 12, mModType = MOD_TYPE_ONE,
			mLineDivider = ITEM_HALF_DIVIDER;

	private int mLastX, mMove;
	private int mWidth, mHeight;

	private int mMinVelocity;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private OnValueChangeListener mListener;
	/**
	 * 上面的数值
	 */
	private int mTopValue = 2015;
	/**
	 * 滑动的方向(手指向左滑：true,手指向右滑:false)
	 */
	private boolean mOrientation;

	@SuppressWarnings("deprecation")
	public TuneWheel(Context context, AttributeSet attrs) {
		super(context, attrs);

		mScroller = new Scroller(getContext());
		mDensity = getContext().getResources().getDisplayMetrics().density;

		mMinVelocity = ViewConfiguration.get(getContext())
				.getScaledMinimumFlingVelocity();

	}

	/**
	 * 
	 * 考虑可扩展，但是时间紧迫，只可以支持两种类型效果图中两种类型
	 * 
	 * @param value
	 *            初始值
	 * @param maxValue
	 *            最大值
	 * @param model
	 *            刻度盘精度：<br>
	 *            {@link MOD_TYPE_HALF}<br>
	 *            {@link MOD_TYPE_ONE}<br>
	 */
	public void initViewParam(int defaultValue, int maxValue, int model) {

		mModType = MOD_TYPE_ONE;
		mLineDivider = ITEM_ONE_DIVIDER;
		mValue = defaultValue;
		mMaxValue = maxValue;

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
		Log.e("onLayout", "执行了onLayout");
		mWidth = getWidth();
		mHeight = getHeight();
		mLineDivider = (int) (1.0 * mWidth / 12.5);
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawLine(0, 40, mWidth, 40, new Paint());
		drawScaleLine(canvas);
		drawMiddleLine(canvas);
	}

	/**
	 * 从中间往两边开始画刻度线
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

		int width = mWidth, drawCount = 0;
		float xPosition = 0, textWidth = Layout.getDesiredWidth("0", textPaint);

		int drawNumber = mValue;
		int numSize = 0;
		for (int i = 0; i <= 12; i++) {

			xPosition = -mMove + i * mLineDivider
					+ (float) (mLineDivider * 0.5);

			if (mValue + i <= mMaxValue) {
				drawNumber = mValue + i;
			} else {
				drawNumber = mValue + i - mMaxValue;
			}

			numSize = String.valueOf(drawNumber).length();
			canvas.drawText(String.valueOf(drawNumber), xPosition
					- (textWidth * numSize / 2), 120,
					textPaint);

			if (drawNumber == 1) {
				canvas.drawLine(xPosition, 0, xPosition, mDensity
						* ITEM_MAX_HEIGHT, linePaint);
				canvas.drawText(String.valueOf(mTopValue), xPosition
						, 30,
						textPaint);
			} else {
				canvas.drawLine(xPosition, getPaddingTop() + 40, xPosition,
						mDensity * ITEM_MAX_HEIGHT, linePaint);
			}

			// drawCount += mLineDivider;
		}

		canvas.restore();
	}

	/**
	 * 画中间的红色指示线、阴影等。指示线两端简单的用了两个矩形代替
	 * 
	 * @param canvas
	 */
	private void drawMiddleLine(Canvas canvas) {

		canvas.save();

		Paint redPaint = new Paint();
		redPaint.setStrokeWidth(4);
		redPaint.setColor(Color.RED);
		canvas.drawLine((float) (mLineDivider * 0.5), 0,
				(float) (mLineDivider * 0.5), mHeight, redPaint);

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
			if (mLastX - xPosition >= 0) {
				mOrientation = true;
			} else {
				mOrientation = false;
			}
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
	 * MOVE的值发生变化，语气对应的value也随之变化
	 */
	private void changeMoveAndValue() {
		int tValue = (int) (mMove / (mLineDivider * mDensity));
		if (Math.abs(tValue) > 0) {
			mValue += tValue;
			mMove -= tValue * mLineDivider * mDensity;
			Log.e("changeMoveAndValue", "mValue:"+mValue+"   mMove:"+mMove);
			// 如果当前的topValue不小于当前值减2（2013）,那么最小值下于1的时候，value赋值为12
			// 如果当前的topValue不大于当前值加2（2017）,那么最大值大于12的时候，value赋值为1
			// 如果当前的topValue超过了当前值减2（2013）,那么最小值下于1的时候，value赋值为1
			// 如果当前的topValue超过了当前值加2（2017）,那么最小值大于12的时候，value赋值为12
			if (mTopValue == 2017) {
				Log.e("changeMoveAndValue", "最大的top值");
				mValue = mValue > mMaxValue ? mMaxValue : mValue;
			} else if (mTopValue == 2013) {
				Log.e("changeMoveAndValue", "最小的top值");
				mValue = mValue < 1 ? 1 : mValue;
			} else {
				if (mValue <= 1) {
					Log.e("changeMoveAndValue", "mValue小于1");
					mValue = mMaxValue;
					mTopValue -= 1;
				} else if (mValue > mMaxValue) {
					Log.e("changeMoveAndValue", "mValue大于12");
					mTopValue += 1;
					mValue = 1;
				}
			}
			Log.e("changeMoveAndValue", "最终的mValue:"+mValue+"   最终的mTopValue:"+mTopValue);
			mMove = 0;
			mScroller.forceFinished(true);
			postInvalidate();
		}
	}

	/**
	 * 计算最后的value的值
	 */
	private void countMoveEnd() {
		// 四舍五入
		int roundMove = Math.round(mMove / (mLineDivider * mDensity));
		mValue = mValue + roundMove;
		if (mTopValue == 2017) {
			mValue = mValue > mMaxValue ? mMaxValue : mValue;
		} else if (mTopValue == 2013) {
			mValue = mValue < 1 ? 1 : mValue;
		} else {
			if (mValue <= 1) {
				mValue = mMaxValue;
				mTopValue -= 1;
			} else if (mValue > mMaxValue) {
				mTopValue += 1;
				mValue = 1;
			}
		}

		mLastX = 0;
		mMove = 0;

		notifyValueChange();
		postInvalidate();
	}

	/**
	 * 通知value的值发生了变化
	 */
	private void notifyValueChange() {
		if (null != mListener) {
			mListener.onValueChange(mValue);
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		// 返回true表示动画没有结束
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
