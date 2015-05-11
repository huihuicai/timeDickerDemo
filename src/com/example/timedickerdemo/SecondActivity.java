package com.example.timedickerdemo;

import com.example.timedickerdemo.DefineWheel.OnDataChangeListener;
import com.example.timedickerdemo.newScrollView.TimeShaftView;
import com.example.timedickerdemo.newScrollView.TimeShaftView.OnValueChangeListener1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SecondActivity extends Activity {

	private TextView mValue, mChange, mData,mBack,mMOvePosition;
	private TimeShaftView mLineView;
	private DefineWheel mDefineWheel;
	
	private int count = 0 ,number = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second_main);
		mData = (TextView) findViewById(R.id.data);
		mData.setText(getString(R.string.current_date, 2015, 4));

		mDefineWheel = (DefineWheel) findViewById(R.id.wheel);
		mDefineWheel.setOnDataChangeListener(new OnDataChangeListener() {
			@Override
			public void onDataChange(int year, int month) {
				mData.setText(getString(R.string.current_date, year, month));
			}
		});
		mValue = (TextView) findViewById(R.id.value);
		mValue.setText(getString(R.string.current_date, 2015, 4));

		mLineView = (TimeShaftView) findViewById(R.id.time_view);
		mLineView.setOnValueChangeListener(new OnValueChangeListener1() {

			@Override
			public void oonValueChanged(boolean isSlipStop, int topValue,
					int bottomValue) {
				mValue.setText(getString(R.string.current_date, topValue,
						bottomValue));
			}
		});

		mChange = (TextView) findViewById(R.id.change);
		mChange.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (count) {
				case 0:
					mDefineWheel.setMarkPosition(2015, 8, 25);
					count ++;
					break;
				case 1:
					mDefineWheel.setMarkPosition(2015, 9, 25);
					count ++;
					break;
				case 2:
					mDefineWheel.setMarkPosition(2015, 10, 25);
					count ++;
					break;
				case 3:
					mDefineWheel.setMarkPosition(2015, 11, 25);
					count ++;
					break;
				case 4:
					mDefineWheel.setMarkPosition(2015, 12, 25);
					count ++;
					break;
				case 5:
					mDefineWheel.setMarkPosition(2015, 4, 25);
					count ++;
					break;
				case 6:
					mDefineWheel.setMarkPosition(2015, 3, 25);
					count = 0;
					break;

				default:
					break;
				}
			}
		});
		
		mBack = (TextView) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mLineView.scrollTimerShaftBack();
			}
		});
		
		mMOvePosition = (TextView) findViewById(R.id.change_position);
		mMOvePosition.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (number) {
				case 0:
					mLineView.setMarkDestion(2015, 8, 25);
					number ++;
					break;
				case 1:
					mLineView.setMarkDestion(2015, 9, 25);
					number ++;
					break;
				case 2:
					mLineView.setMarkDestion(2015, 10, 25);
					number ++;
					break;
				case 3:
					mLineView.setMarkDestion(2015, 11, 25);
					number ++;
					break;
				case 4:
					mLineView.setMarkDestion(2015, 12, 25);
					number ++;
					break;
				case 5:
					mLineView.setMarkDestion(2015, 4, 25);
					number ++;
					break;
				case 6:
					mLineView.setMarkDestion(2015, 3, 25);
					number = 0;
					break;

				default:
					break;
				}
			}
		});
	}

}
