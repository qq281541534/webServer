package com.liuyu.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 该类主要是用户对80端口的监听，一旦有请求80端口，就生成Processor的实例，对请求的http内容进行处理
 * @author Administrator
 *
 */
public class WebServer {

	//启动服务器
	public void serverStart(int port){
		try {
			//监听服务器的80端口
			ServerSocket serverSocket = new ServerSocket(port);
			
			while (true) {
				Socket socket = serverSocket.accept();
				new Processor(socket).start();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
	public static void main(String[] args) {
		int port = 80;
		if(args.length == 1){
			port = Integer.parseInt(args[0]);
		}
		new WebServer().serverStart(port);
	}

}
