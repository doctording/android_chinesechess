package com.example.cchess;


import java.net.Socket;

import mysocket.client.MySocket;
import mysocket.client.MySocket.onSocketCallBack;
import mysocket.client.Server;
import mysocket.client.Server.ConnectCallBack;

import org.json.JSONObject;

import com.util.ActivityHelper;
import com.util.AddressGetter;
import com.util.JsonBeanUtil;

import simple.game.chess.ChessView;
import simple.game.chess.ChessView.onStepLisenner;
import simple.game.chess.Chess_Item_Base;
import simple.game.chess.Chess_Status;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChessActivity extends Activity {
	private ChessView chess;
	private TextView tx_ip,tx_status;
	private MySocket socket;
	private onSocketCallBack callback;
	private boolean hasconn = false;
	private Button btn_change,btn_getconnect,btn_connect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		
		setContentView(R.layout.chess);
		chess = (ChessView) findViewById(R.id.chessView1);
		chess.setCanClick(false);
		chess.change();
		tx_ip = (TextView) findViewById(R.id.tx_ip);
		tx_status = (TextView) findViewById(R.id.tx_status);
		btn_getconnect = (Button) findViewById(R.id.btn_getconnect);
		btn_change = (Button) findViewById(R.id.btn_change);
		btn_connect= (Button) findViewById(R.id.btn_connect);
		tx_ip.setText("我的ip:" + AddressGetter.GetIp()); // 显示IP地址
		
		chess.setLisenner(new onStepLisenner() {

			@Override
			public void onStep(int oldx, int oldy, int cx, int cy) {
				
				chess.setCanClick(false);
				if (socket != null && hasconn) {
					Chess_Status status = new Chess_Status();
					status.setCx(cx);
					status.setCy(9 - cy);
					status.setOldx(oldx);
					status.setOldy(9 - oldy);
					status.setIschange(true);
					status.setMessage("");
					socket.SendMessage(JsonBeanUtil.toJson(status).getBytes());
					chess.setLastAction(null,null);
					
				}
			}
		});
		callback = new onSocketCallBack() {

			@Override
			public void onSendMessageSucess() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSendMessageErrer() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetMessage(byte[] buffer) {
				// TODO Auto-generated method stub
				try {
					final Chess_Status status = JsonBeanUtil
							.convertObjectFromJsonObject(Chess_Status.class,
									new JSONObject(new String(buffer)));
					if (status.isIschange()) {
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub

								chess.setCanClick(false);
								chess.setClick(status.getOldx(),
										status.getOldy());
								
								try {
									Thread.sleep(200);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								chess.setClick(status.getCx(), status.getCy());
								try {
									Thread.sleep(200);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								chess.setClick(-1, -1);
								chess.setLastAction(new Chess_Item_Base(status.getOldx(),
										status.getOldy()), new Chess_Item_Base(status.getCx(), status.getCy()));
								chess.setCanClick(true);
							}
						}).start();
					} else if (status.isIsfirst()) {
						chess.change();
						btn_change.setEnabled(false);

						tx_status.setText("当前状态:开始游戏");
					}
					else if(status.isConn())
					{
						hasconn = true;
						
						Toast.makeText(ChessActivity.this,"连接成功，选中红方请点击换边", Toast.LENGTH_SHORT).show();

						tx_status.setText("当前状态:连接成功，选中红方请点击换边");
					}
					else {

						Toast.makeText(ChessActivity.this, status.getMessage(),
								200).show();
						;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		try {
			this.finish();
			socket.onDestroy();
		} catch (Exception e) {
			
		}
	}

	public void onClick(View view) {
		switch (view.getId()) {

		//抢红旗
		case R.id.btn_change:
			if (socket != null && hasconn) {
				Chess_Status status = new Chess_Status();
				status.setIschange(false);
				status.setIsfirst(true);
				status.setMessage("");
				socket.SendMessage(JsonBeanUtil.toJson(status).getBytes());
				btn_change.setEnabled(false);
				chess.setCanClick(true);

				tx_status.setText("当前状态:开始游戏");
			}
			break;
		//等待连接
		case R.id.btn_getconnect:
			tx_status.setText("当前状态:正在等待连接...");
			btn_getconnect.setEnabled(false);
			btn_connect.setEnabled(false);
			socket = new MySocket();
			new Thread(new Runnable() {

				@Override
				public void run() {

					if(Server.isWaitting)//避免重复等待造成异常退出
					{
						Server.callback=new ConnectCallBack() {
							
							@Override
							public void OnConnect(Socket socketconn) {
								socket.setSocket(socketconn);
								socket.SetConSocket();
								socket.setSocetCallBack(callback);
								hasconn = true;
								try {
									Thread.sleep(200);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								
								Chess_Status status = new Chess_Status();
								status.setConn(true);
								status.setMessage("");
								socket.SendMessage(JsonBeanUtil.toJson(status).getBytes());
								
								runOnUiThread(new Runnable() {
									public void run() {
										
										Toast.makeText(ChessActivity.this,
												"连接成功，选中红方请点击换边", 200).show();

										tx_status.setText("当前状态:连接成功，选中红方请点击换边");
										btn_getconnect.setEnabled(false);
									}
								});
								
								;
							}
						};
						return;
					}
					
					Socket socketconn = Server.StartServer();
					
					socket.setSocket(socketconn);
					socket.SetConSocket();
					socket.setSocetCallBack(callback);
					hasconn = true;
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					Chess_Status status = new Chess_Status();
					status.setConn(true);
					status.setMessage("");
					socket.SendMessage(JsonBeanUtil.toJson(status).getBytes());
					
					runOnUiThread(new Runnable() {
						public void run() {
							
							Toast.makeText(ChessActivity.this,
									"连接成功，选中红方请点击换边", 200).show();

							tx_status.setText("当前状态:连接成功，选中红方请点击换边");
							btn_getconnect.setEnabled(false);
						}
					});
					
					;
				}
			}).start();

			break;
		
		// 连接到
		case R.id.btn_connect:

			btn_getconnect.setEnabled(false);
			btn_connect.setEnabled(false);
			ActivityHelper helper = new ActivityHelper(this);
			final EditText text = new EditText(this);
			text.setText(AddressGetter.GetIp());
			text.setSelection(AddressGetter.GetIp().length());
			helper.openDialogWithDefaultStyle("连接到ip", "", "确定", "", text,
					new ActivityHelper.onDialogCallBack() {

						@Override
						public void onBtn2CallBack() {

						}

						@Override
						public void onBtn1CallBack() {
							socket = new MySocket(text.getText().toString());
							socket.linkto();
							socket.setSocetCallBack(callback);
				
							btn_connect.setEnabled(false);

							tx_status.setText("当前状态:正在连接到ip地址==>>"+text.getText().toString());
						}
					});
			
			break;
		// 催一催
		case R.id.btn_gaokuai:
			if (socket != null && hasconn) {
				Chess_Status status = new Chess_Status();
				status.setIschange(false);
				status.setMessage("搞快点，我等到花儿都谢了");
				socket.SendMessage(JsonBeanUtil.toJson(status).getBytes());
				chess.setLastAction(null,null);
			}
			break;
		default:
			break;
		}
		
	}
	
}