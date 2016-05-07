package mysocket.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//服务端
public class Server { 
	public static boolean isWaitting = false;
	public static ConnectCallBack callback;
	public static int port = 8888 ;

	public static Socket StartServer() {

		ServerSocket serverSocket = null;
		Socket client = null;
		try {

			serverSocket = new ServerSocket(port);
			isWaitting = true;
			while (client == null) {
				client = serverSocket.accept(); // 等待客户端的连接
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			isWaitting = false;
			if (callback != null) {
				callback.OnConnect(client);
				callback = null;
			}
		}
		return client; // 返回客户端
	}

	public interface ConnectCallBack {
		public void OnConnect(Socket socketconn);
	}
	
}
