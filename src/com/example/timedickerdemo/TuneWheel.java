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
	 * �������ֵ
	 */
	private int mTopValue = 2015;
	/**
	 * �����ķ���(��ָ���󻬣�true,��ָ���һ�:false)
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
	 * ���ǿ���չ������ʱ����ȣ�ֻ����֧����������Ч��ͼ����������
	 * 
	 * @param value
	 *            ��ʼֵ
	 * @param maxValue
	 *            ���ֵ
	 * @param model
	 *            �̶��̾��ȣ�<br>
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
	 * �������ڽ��ս���ļ�����
	 * 
	 * @param listener
	 */
	public void setValueChangeListener(OnValueChangeListener listener) {
		mListener = listener;
	}

	/**
	 * ��ȡ��ǰ�̶�ֵ
	 * 
	 * @return
	 */
	public float getValue() {
		return mValue;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		Log.e("onLayout", "ִ����onLayout");
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
	 * ���м������߿�ʼ���̶���
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
	 * ���м�ĺ�ɫָʾ�ߡ���Ӱ�ȡ�ָʾ�����˼򵥵������������δ���
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
	 * MOVE��ֵ�����仯��������Ӧ��valueҲ��֮�仯
	 */
	private void changeMoveAndValue() {
		int tValue = (int) (mMove / (mLineDivider * mDensity));
		if (Math.abs(tValue) > 0) {
			mValue += tValue;
			mMove -= tValue * mLineDivider * mDensity;
			Log.e("changeMoveAndValue", "mValue:"+mValue+"   mMove:"+mMove);
			// �����ǰ��topValue��С�ڵ�ǰֵ��2��2013��,��ô��Сֵ����1��ʱ��value��ֵΪ12
			// �����ǰ��topValue�����ڵ�ǰֵ��2��2017��,��ô���ֵ����12��ʱ��value��ֵΪ1
			// �����ǰ��topValue�����˵�ǰֵ��2��2013��,��ô��Сֵ����1��ʱ��value��ֵΪ1
			// �����ǰ��topValue�����˵�ǰֵ��2��2017��,��ô��Сֵ����12��ʱ��value��ֵΪ12
			if (mTopValue == 2017) {
				Log.e("changeMoveAndValue", "����topֵ");
				mValue = mValue > mMaxValue ? mMaxValue : mValue;
			} else if (mTopValue == 2013) {
				Log.e("changeMoveAndValue", "��С��topֵ");
				mValue = mValue < 1 ? 1 : mValue;
			} else {
				if (mValue <= 1) {
					Log.e("changeMoveAndValue", "mValueС��1");
					mValue = mMaxValue;
					mTopValue -= 1;
				} else if (mValue > mMaxValue) {
					Log.e("changeMoveAndValue", "mValue����12");
					mTopValue += 1;
					mValue = 1;
				}
			}
			Log.e("changeMoveAndValue", "���յ�mValue:"+mValue+"   ���յ�mTopValue:"+mTopValue);
			mMove = 0;
			mScroller.forceFinished(true);
			postInvalidate();
		}
	}

	/**
	 * ��������value��ֵ
	 */
	private void countMoveEnd() {
		// ��������
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
	 * ֪ͨvalue��ֵ�����˱仯
	 */
	private void notifyValueChange() {
		if (null != mListener) {
			mListener.onValueChange(mValue);
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		// ����true��ʾ����û�н���
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
