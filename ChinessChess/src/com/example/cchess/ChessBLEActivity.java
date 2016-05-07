package com.example.cchess;


import org.json.JSONObject;

import com.util.AddressGetter;
import com.util.JsonBeanUtil;

import simple.game.chess.ChessView;
import simple.game.chess.ChessView.onStepLisenner;
import simple.game.chess.Chess_Item_Base;
import simple.game.chess.Chess_Status;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import bluetooth.client.BluetoothChatService;

public class ChessBLEActivity extends Activity {

	public ListenerThread StartListenThread;
	private BluetoothAdapter mBluetoothAdapter;
	private String TAG = "Bluetooth_activity";
	// Member object for the chat services
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;

	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	// Member object for the chat services
	private BluetoothChatService mChatService = null;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	private ChessView chess;
	private TextView tx_ip, tx_status;
	private Button btn_change, btn_getconnect, btn_connect;
	private String address, name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.chessble);
		
		// 获取传递过来的对方蓝牙设备信息
		address = getIntent().getStringExtra("address");
		name = getIntent().getStringExtra("name");
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",Toast.LENGTH_LONG).show();
			return;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			// 弹出对话框提示用户是后打开
			//Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			//startActivityForResult(enabler, REQUEST_ENABLE);
			// 不做提示，强行打开
			mBluetoothAdapter.enable(); 
		}

		//初始化蓝牙服务 
		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothChatService(this, mHandler);

		chess = (ChessView) findViewById(R.id.chessView1);
		chess.setCanClick(false);
		chess.change();
		tx_ip = (TextView) findViewById(R.id.tx_ip);
		tx_status = (TextView) findViewById(R.id.tx_status);
		btn_getconnect = (Button) findViewById(R.id.btn_getconnect);
		btn_change = (Button) findViewById(R.id.btn_change);
		btn_connect = (Button) findViewById(R.id.btn_connect);
		tx_ip.setText("对方设备:" + name);
		
		/*
		 *  点击棋子后走下一步时 进入该方法
		 *  oldx oldy是 点击的棋子
		 *  Cx cy是该棋子要当前要走到的位置
		 */
		chess.setLisenner(new onStepLisenner() {

			@Override
			public void onStep(int oldx, int oldy, int cx, int cy) {
				chess.setCanClick(false);
				if (getConStatus()) {
					Chess_Status status = new Chess_Status();
					status.setCx(cx);
					status.setCy(9 - cy);
					status.setOldx(oldx);
					status.setOldy(9 - oldy);
					status.setIschange(true);
					status.setMessage("");
					
					String msg = JsonBeanUtil.toJson(status); // 棋子走动的状态 类 转换成 json字符串
					sendMessage(msg); 
					
					chess.setLastAction(null, null);
				}
			}
		});
		
		new Thread(new Runnable() { //onCreate()里面开一个线程，判断是否双方已经完成了连接
			
			@Override
			public void run() {
		
				while (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
					runOnUiThread( new Runnable() {
						public void run() {
							tx_status.setText("蓝牙连接未成功(未成功之前,请勿操作)\n若长时间连接不成功，请关闭此页面重试");
						}
					});
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				runOnUiThread( new Runnable() {
					public void run() {
						tx_status.setText("蓝牙连接成功，确认双方都连接成功后再点击抢红旗开始");
					}
				});
			}
		}).start();

	}// end onCreate()

	private boolean getConStatus()
	{
		return mChatService.getState() == BluetoothChatService.STATE_CONNECTED;
	}
	
	private void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, "未连接", Toast.LENGTH_SHORT).show();
			return;
		}
		// if (!getConStatus()) {
		// return;
		// }
		// =true;
		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);

			// Reset out string buffer to zero and clear the edit text field
			// mOutStringBuffer.setLength(0);
			// mOutEditText.setText(mOutStringBuffer);
		}
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case MESSAGE_READ: {
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				// String readMessage = new String(readBuf, 0, msg.arg1);
				try {
					final Chess_Status status = JsonBeanUtil.convertObjectFromJsonObject(Chess_Status.class,new JSONObject(new String(readBuf)));
					if (status.isIschange()) {
						new Thread(new Runnable() {

							@Override
							public void run() {

								chess.setCanClick(false);
								chess.setClick(status.getOldx(),status.getOldy());
								
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

								chess.setClick(status.getCx(), status.getCy());
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

								chess.setClick(-1, -1);
								chess.setLastAction(
										new Chess_Item_Base(status.getOldx(),status.getOldy()),
										new Chess_Item_Base(status.getCx(),status.getCy()));
								
								chess.setCanClick(true);
							}
						}).start();
					} else if (status.isIsfirst()) {
						chess.change();
						btn_change.setEnabled(false);
						
						tx_status.setText("当前状态:开始游戏");
					} else if (status.isConn()) {

						Toast.makeText(ChessBLEActivity.this, "连接成功，选中红方请点击换边",
								200).show();

						btn_getconnect.setEnabled(false);
						btn_connect.setEnabled(false);
						tx_status.setText("当前状态:连接成功，选中红方请点击换边");
					} else {

						Toast.makeText(ChessBLEActivity.this,
								status.getMessage(), 200).show();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				// strReceiveData += readMessage+"\n";
				// System.out.println("rev :"+readMessage);
				// mTextView.setText(strReceiveData);
				// mConversationArrayAdapter.add(mConnectedDeviceName+":  " +
				// readMessage);
			}
				break;
			}
		}
	};

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/

	public void onClick(View view) {
		switch (view.getId()) {
		//抢红旗
		case R.id.btn_change: 
			
			if (getConStatus()) {
				Chess_Status status = new Chess_Status();
				status.setIschange(false);
				status.setIsfirst(true);// 设置为红方
				status.setMessage("");
				sendMessage(JsonBeanUtil.toJson(status));
				// socket.SendMessage(.getBytes());
				btn_change.setEnabled(false);
				chess.setCanClick(true);

				tx_status.setText("当前状态:开始游戏");
			}

			break;
		case R.id.btn_getconnect:
			tx_status.setText("当前状态:正在等待连接...");
			btn_getconnect.setEnabled(false);
			btn_connect.setEnabled(false);

			new Thread(new Runnable() {

				@Override
				public void run() {

					runOnUiThread(new Runnable() {
						public void run() {

							Chess_Status status = new Chess_Status();
							status.setConn(true);
							status.setMessage("");
							// socket.SendMessage();
							sendMessage(JsonBeanUtil.toJson(status));

							Toast.makeText(ChessBLEActivity.this,"连接成功，选中红方请点击换边", 200).show();

							tx_status.setText("当前状态:连接成功，选中红方请点击换边");
							btn_getconnect.setEnabled(false);
						}
					});

				}
			}).start();

			break;

		case R.id.btn_connect:

			break;

		case R.id.btn_gaokuai:
			if (getConStatus()) {
				Chess_Status status = new Chess_Status();
				status.setIschange(false);
				status.setMessage("搞快点，我等到花儿都谢了");
				sendMessage(JsonBeanUtil.toJson(status));
				// socket.SendMessage(.getBytes());
				chess.setLastAction(null, null);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		Log.e(TAG, "+ ON RESUME +");

		String address = mBluetoothAdapter.getAddress();
		
		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				// Start the Bluetooth chat services
				StartListenThread = new ListenerThread();
				StartListenThread.start();

			}
		}
	}

	private class ListenerThread extends Thread {
		// The local server socket

		public void run() {
			// Listen to the server socket if we're not connected
			
			for (int i = 0; i < 100; i++) {
				if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
					// mChatService.start();
					// String address = "58:44:98:4B:5A:F6";//hm
					// String address = "18:DC:56:7E:3C:29";//kp

					// String address =mBluetoothAdapter.getAddress();

					if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED)
						connectDevice(address);
					break;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
		if(chess != null)
		{
			chess.setIsgo(false);
			chess=null;
		}
		Log.e(TAG, "--- ON DESTROY ---");

	}

	private void connectDevice(String address) {
		// Get the device MAC address

		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device, false);
	}
	
}