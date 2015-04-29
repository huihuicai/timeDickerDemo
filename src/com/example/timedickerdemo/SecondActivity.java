package com.example.timedickerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class SecondActivity extends Activity {

	private TextView mValue,mChange;
	private TuneWheel mWheelSimple;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_main);
        mValue = (TextView) findViewById(R.id.value);
        mValue.setText(getString(R.string.current_date, 2015,4));
        
        mWheelSimple = (TuneWheel) findViewById(R.id.wheel);
        mWheelSimple.setValueChangeListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(int year,int month) {
				mValue.setText(getString(R.string.current_date, year,month));
			}
		});
        
        mChange = (TextView) findViewById(R.id.change);
        mChange.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO 改变小车的位置
				mWheelSimple.setMoveDestination(2015,7,25);
			}
		});
    }

}
