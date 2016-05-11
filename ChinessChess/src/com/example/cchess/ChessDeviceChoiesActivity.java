package com.example.cchess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChessDeviceChoiesActivity  extends Activity {
	
	private ListView listView;	//listView控件
	private BluetoothAdapter mBluetoothAdapter;	//蓝牙适配器
	private List<BlueToothItems> list;	//蓝牙设备list
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deviceitems_choies);
		
		listView=(ListView) findViewById(R.id.listView);
		list=new ArrayList<BlueToothItems>();
		
		//Toast.makeText(getApplicationContext(), "如未加载出蓝牙设备，请重试", Toast.LENGTH_SHORT).show();
		
		showDevice();
		
		listView.setAdapter(new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.list_item_devices, null);
				TextView tx_devicename = (TextView) convertView.findViewById(R.id.tx_devicename);
				TextView tx_deviceaddress = (TextView) convertView.findViewById(R.id.tx_deviceaddress);
				TextView tx_devicestatus = (TextView) convertView.findViewById(R.id.tx_devicestatus);
				BlueToothItems item = list.get(position);
				tx_devicename.setText(item.getDeviceName());
				tx_deviceaddress.setText(item.getDeviceAdress());
				tx_devicestatus.setText(item.getStatus());
				return convertView;
			}
			
			@Override
			public long getItemId(int position) {
				return 0;
			}
			
			@Override
			public Object getItem(int position) {
				return null;
			}
			
			@Override
			public int getCount() {
				return list.size();
			}
		});
		/**
		 * 点击某一个Item弹出相应的事件，把蓝牙设备名字 地址传到ChessBLEActivity
		 */
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Intent it = new Intent();
				it.setClass(ChessDeviceChoiesActivity.this, ChessBLEActivity.class);
				it.putExtra("address", list.get(arg2).getDeviceAdress());
				it.putExtra("name", list.get(arg2).getDeviceName());
				startActivity(it);
				finish();
			}
		});
	}
	
	private void showDevice()
	{
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    if(mBluetoothAdapter==null)
		{
	    	Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			return;
		}
			
		if(!mBluetoothAdapter.isEnabled())
		{
		
			//弹出对话框提示用户是否打开
			//Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			//startActivityForResult(enabler, REQUEST_ENABLE);
		
			//不做提示，强行打开
			 mBluetoothAdapter.enable();
		}
		
		// 得到附近的蓝牙设备
		// Initialize the BluetoothChatService to perform bluetooth connections
		Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
		BlueToothItems item;
		if(devices.size()>0)
		{
			Iterator<BluetoothDevice> iterator = devices.iterator();
			while(iterator.hasNext())
			{
				item=new BlueToothItems();
				BluetoothDevice device = (BluetoothDevice)iterator.next();
				item.setDeviceAdress(device.getAddress());
				item.setDeviceName(device.getName());
				item.setStatus(device.getBondState()==12?"已连接":"未连接");
				list.add(item);
			}
		}else{
			Toast.makeText(this, "还未发现其它蓝牙设备，请返回重试", Toast.LENGTH_LONG).show();
		}
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/
	/*public void onClick(View view)
	{
		switch (view.getId()) {
		case R.id.button1:
			break;

		case R.id.button2:
			break;
		default:
			break;
		}
	}*/
}
