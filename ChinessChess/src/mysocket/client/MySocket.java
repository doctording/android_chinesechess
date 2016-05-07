package mysocket.client;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.os.Handler;
import android.os.Message;

public class MySocket {
	private final int BYTES = 0;
	private final int SUCESS = 1;
	private final int ERRER = 2;
	private onSocketCallBack callback; // 消息处理类
	private Handler hander;
	InetAddress ip;
	public static int point = 8888;// 服务端端口固定为8888
	private static Socket socket = null;
	private static Socket stsocket = null;
	InputStream input;
	
	
	public MySocket() { // 构造函数1  初始化handle
		hander = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				super.handleMessage(msg);
				switch (msg.what) {
				case BYTES:
					if (callback != null)
						callback.onGetMessage((byte[]) msg.obj);
					break;
				case SUCESS:
					if (callback != null)
						callback.onSendMessageSucess();
					break;
				case ERRER:
					if (callback != null)
						callback.onSendMessageErrer();
					break;

				default:
					break;
				}
			}

		};
	}
		
	public MySocket(String ip) {// 构造函数1  初始化handle 设置IP
		hander = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case BYTES:
					if (callback != null)
						callback.onGetMessage((byte[]) msg.obj);
					break;

				case SUCESS:
					if (callback != null)
						callback.onSendMessageSucess();
					break;
				case ERRER:
					if (callback != null)
						callback.onSendMessageErrer();;
					break;

				default:
					break;
				}
			}

		};
		try {
			this.ip = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void setSocetCallBack(onSocketCallBack callback) {
		this.callback = callback;
	}
	public void setSocket(Socket socket)
	{
		stsocket=socket;
	}
	
	public void SetConSocket() //设置服务端
	{
		this.socket = stsocket;
		try {
			socket.setKeepAlive(true);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		if(socket!=null)
		if(socket.isConnected())
		{
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						while (true) {
								input = socket.getInputStream();
								int len = input.read();
								byte[] buffer = new byte[len];
								input.read(buffer);
								Message msg = new Message();
								msg.what = BYTES;
								msg.obj = buffer;
								hander.sendMessage(msg);

						}
					} catch (Exception e) {
						e.printStackTrace();
						//System.out.println("socet err:" + e.getMessage());
					}
				}
			}).start();
		}
	}
	
	public void linkto() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					socket = new Socket(ip, point);
					socket.setKeepAlive(true);
					while (true) {
							input = socket.getInputStream();  //读取服务器发来的数据  
							int len = input.read();
							byte[] buffer = new byte[len];
							input.read(buffer);
							Message msg = new Message();
							msg.what = BYTES;
							msg.obj = buffer;
							hander.sendMessage(msg);

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//System.out.println("socet err:" + e.getMessage());
				}
			}
		}).start();
	}

	public void onDestroy()
	{
		callback=null;
		input=null;
		try {
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void SendMessage(final byte[] send) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					socket.getOutputStream().write(send.length);
					socket.getOutputStream().write(send);
					hander.sendEmptyMessage(SUCESS);
				} catch (Exception e) {
					e.printStackTrace();
					Message msg=new Message();
					msg.what=ERRER;
					hander.sendMessage(msg);
				}
			}
		}).start();
	}

	public interface onSocketCallBack {
		public void onGetMessage(byte[] buffer);
		public void onSendMessageSucess();
		public void onSendMessageErrer();
	}

}