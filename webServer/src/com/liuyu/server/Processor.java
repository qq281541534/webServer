package com.liuyu.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Processor extends Thread{
	
	private Socket socket;
	private InputStream in;
	private PrintStream out;
	//允许访问的路径
	private static String WEB_ROOT = "D:\\tools\\Workspaces\\MyEclipse 8.6\\webServer";
	
	
	public Processor(Socket socket1){
		this.socket = socket1;
		try {
			//从端口中获取字节流信息
			in = socket.getInputStream();
			out = new PrintStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run(){
		String filename = this.parse(in);
		this.sendFile(filename);
	}
	
	/**
	 * 用于解析http请求访问的路径内容,输出filename
	 * @param in
	 * @return
	 */
	public String parse(InputStream in){
		//将输入流构造为字符流
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String filename = null;
		try {
			String httpMessage = br.readLine();
			String [] content = httpMessage.split(" ");
			//如果长度不为三说明请求错误
			if(content.length != 3){
				this.sendErrorMessage(400, "Client query error");
			}
			
			
			System.out.println("code"+content[0]+",filename"+content[1]+",http version"+content[2]);
			filename = content[1];
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filename;
	}
	
	/**
	 * 当请求的信息资源不存在时，返回错误信息
	 * @param errorCode
	 * @param errorMessage
	 */
	public void sendErrorMessage(int errorCode, String errorMessage){
		out.println("HTTP/1.0 "+errorCode+" "+errorMessage);
		out.println("content-type: text/html");
		out.println();
		out.println("<html>");
		out.println("<title>Error Message");
		out.println("</title>");
		out.println("<body>");
		out.println("<h1>"+errorCode+" "+errorMessage+"</h1>");
		out.println("</body>");
		out.println("</html>");
		out.flush();
		out.close();
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 返回找到的文件
	 * @param fileName
	 */
	public void sendFile(String fileName){
		
		File file = new File(Processor.WEB_ROOT+fileName);
		
		//如果文件不存在，返回错误信息并结束方法
		if(!file.exists()){
			this.sendErrorMessage(404, "File Not Found");
			return;
		}
		
		try {
			InputStream in = new FileInputStream(file);
			byte[] content = new byte[(int)file.length()];
			System.out.println("1.0");
			//将文件读入进输入流
			in.read(content);
			//返回http信息(固定格式)
			out.println("HTTP/1.0 200 queryfile");
			System.out.println("2.0");
			out.println("content-length:"+content.length);
			out.println();
			out.write(content);
			out.flush();
			out.close();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
