package com.example.cchess;

import com.util.ActivityCollector;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TwoVSActivity extends Activity {

	Button btn_bluetooth;
	Button btn_socket;
	Button btn_back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_two_vs);
		
		ActivityCollector.addActivity(this);
		
		init();
		btn_bluetooth.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent it=new Intent();
				it.setClass(getApplicationContext(), ChessDeviceChoiesActivity.class);
				startActivity(it);
				
			}
		});
		btn_socket.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent it=new Intent();
				it.setClass(getApplicationContext(), ChessActivity.class);
				startActivity(it);
			}
		});
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Intent it=new Intent();
				it.setClass(getApplicationContext(), MainActivity.class);
				startActivity(it);
				
				finish(); // 销毁
			}
		});
	}
	
	void init(){
		btn_bluetooth = (Button) findViewById(R.id.btn_bluetooth);
		btn_socket = (Button) findViewById(R.id.btn_socket);
		btn_back = (Button) findViewById(R.id.btn_back);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.two_v, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		ActivityCollector.removeActivity(this); 
		super.onDestroy();
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 获取 back键
			

		}else if (keyCode == KeyEvent.KEYCODE_MENU) { // 获取 Menu键
		
		}else{
			
		}
		return false;
	}
	
	
}
