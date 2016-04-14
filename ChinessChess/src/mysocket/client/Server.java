package mysocket.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static boolean isWaitting = false;
	public static ConnectCallBack callback;
	static int port = 2000 ;
	// 构造一个服务端 端口号为2000,并返回等待的客户端socket
	public static Socket StartServer() {

		ServerSocket serverSocket = null;
		Socket client = null;
		try {

			serverSocket = new ServerSocket(port);
			isWaitting = true;
			while (client == null) {
				client = serverSocket.accept();
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
		// 每一个SOcket就是一个用户
		return client;
	}

	public interface ConnectCallBack {
		public void OnConnect(Socket socketconn);
	}
	
}
