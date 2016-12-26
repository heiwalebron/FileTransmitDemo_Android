package com.ssydiai.filetransmit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Environment;

public class SocketManager {
	private ServerSocket server;
	public SocketManager(ServerSocket server){
		this.server = server;
	}
	/*
	* 接收文件
	* */
	public String ReceiveFile(){
		try{
			//接收文件名
			Socket name = server.accept();
			InputStream nameStream = name.getInputStream();
			InputStreamReader streamReader = new InputStreamReader(nameStream);
			BufferedReader br = new BufferedReader(streamReader);
			String fileName = br.readLine();
			br.close();
			streamReader.close();
			nameStream.close();
			name.close();
			//接收文件数据
			Socket data = server.accept();
			InputStream dataStream = data.getInputStream();
			String savePath = Environment.getExternalStorageDirectory().getPath() + "/" + fileName;
			FileOutputStream file = new FileOutputStream(savePath, false);
			byte[] buffer = new byte[1024];
			int size = -1;
			while ((size = dataStream.read(buffer)) != -1){
				file.write(buffer, 0 ,size);
			}
			file.close();
			dataStream.close();
			data.close();
			return fileName + " 接收完成";
		}catch(Exception e){
			return "接收错误:\n" + e.getMessage();
		}
	}
	/*
	* 发送文件
	* */
	public String SendFile(String fileName, String path, String ipAddress, int port){
		try {
			Socket name = new Socket(ipAddress, port);
			OutputStream outputName = name.getOutputStream();
			OutputStreamWriter outputWriter = new OutputStreamWriter(outputName);
			BufferedWriter bwName = new BufferedWriter(outputWriter);
			bwName.write(fileName);
			bwName.close();
			outputWriter.close();
			outputName.close();
			name.close();
			
			Socket data = new Socket(ipAddress, port);
			OutputStream outputData = data.getOutputStream();
			FileInputStream fileInput = new FileInputStream(path);
			int size = -1;
			byte[] buffer = new byte[1024];
			while((size = fileInput.read(buffer, 0, 1024)) != -1){
				outputData.write(buffer, 0, size);
			}
			outputData.close();
			fileInput.close();
			data.close();
			return fileName + " 发送完成";
		} catch (Exception e) {
			return "发送错误:\n" + e.getMessage();
		} 
	}
}
