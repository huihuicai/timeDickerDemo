package com.example.timedickerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class SecondActivity extends Activity {

	private TextView mValue;
	private TuneWheel mWheelSimple;
	private OnValueChangeListener changeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_main);
        mValue = (TextView) findViewById(R.id.value);
        mWheelSimple = (TuneWheel) findViewById(R.id.wheel);
        
        mValue.setText(String.valueOf(mWheelSimple.getValue()));
        mWheelSimple.setValueChangeListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(int year,int month) {
				mValue.setText(getString(R.string.current_date, year,month));
//				mValue.setText(String.valueOf(value));
			}
		});
    }

}
