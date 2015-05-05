package com.example.timedickerdemo;

import com.example.timedickerdemo.DefineWheel.OnDataChangeListener;
import com.example.timedickerdemo.newScrollView.TimeLineView;
import com.example.timedickerdemo.newScrollView.TimeLineView.OnValueChangeListener1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SecondActivity extends Activity {

	private TextView mValue, mChange, mData;
	private TimeLineView mLineView;
	private DefineWheel mDefineWheel;

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

		mLineView = (TimeLineView) findViewById(R.id.time_view);
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
				Log.e("onclik", "点击了改变数值");
				mLineView.setMarkDestion(2015, 7, 25);
			}
		});
	}

}
