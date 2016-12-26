package com.ssydiai.filetransmit;

import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private TextView tvMsg;
	private EditText /*txtIP, txtPort,*/
			txtEt;
	private Button btnSend;
	private Handler handler;
	private ServerSocket server;
	private SocketManager socketManager;
	private Spinner spinner_nurse;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tvMsg = (TextView)findViewById(R.id.tvMsg);
		/*txtIP = (EditText)findViewById(R.id.txtIP);
		txtPort = (EditText)findViewById(R.id.txtPort);*/
		txtEt = (EditText)findViewById(R.id.et);
		spinner_nurse=(Spinner)findViewById(R.id.spinner_nurse);
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.nurse_array, android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_nurse.setAdapter(adapter1);
		btnSend = (Button)findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), FilesViewActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case 0:
					SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
					txtEt.append("\n[" + format.format(new Date()) + "]" + msg.obj.toString());
					break;
				case 1:
					tvMsg.setText(msg.obj.toString());
				case 2:
					Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		};
		new Thread(new Runnable(){
			@Override
			public void run() {
				//绑定端口
				int port = 9999;
				while(port > 9000){
					try {
						server = new ServerSocket(port);
						break;
					} catch (Exception e) {
						port--;
					}
				}
				if (server != null){
					socketManager = new SocketManager(server);
					Message.obtain(handler, 1, "本机IP：" + GetIpAddress() + " 监听端口:" + port).sendToTarget();
					while(true){//接收文件
						String response = socketManager.ReceiveFile();
						Message.obtain(handler, 0, response).sendToTarget();
					}
				}else{
					Message.obtain(handler, 1, "未能绑定端口").sendToTarget();
				}
			}
		}).start();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//选择了文件发送
		if (resultCode == RESULT_OK) {
			if ("护理室1".equals(spinner_nurse.getSelectedItem().toString())) {
				final String fileName = data.getStringExtra("FileName");
				final String path = data.getStringExtra("FilePath");

				final String ipAddress = "192.168.1.118";
				//final int port = Integer.parseInt(txtPort.getText().toString());
				final int port = 9999;
				Message.obtain(handler, 0, fileName + " 正在发送至" + ipAddress + ":" + port).sendToTarget();
				new Thread(new Runnable() {
					@Override
					public void run() {
						String response = socketManager.SendFile(fileName, path, ipAddress, port);
						Message.obtain(handler, 0, response).sendToTarget();
					}
				}).start();
			}else if ("护理室2".equals(spinner_nurse.getSelectedItem().toString())){
				final String fileName = data.getStringExtra("FileName");
				final String path = data.getStringExtra("FilePath");

				final String ipAddress = "192.168.1.101";
				//final int port = Integer.parseInt(txtPort.getText().toString());
				final int port = 9999;
				Message.obtain(handler, 0, fileName + " 正在发送至" + ipAddress + ":" + port).sendToTarget();
				new Thread(new Runnable() {
					@Override
					public void run() {
						String response = socketManager.SendFile(fileName, path, ipAddress, port);
						Message.obtain(handler, 0, response).sendToTarget();
					}
				}).start();

			}else if ("护理室3".equals(spinner_nurse.getSelectedItem().toString())){
				final String fileName = data.getStringExtra("FileName");
				final String path = data.getStringExtra("FilePath");

				final String ipAddress = "192.168.1.121";
				//final int port = Integer.parseInt(txtPort.getText().toString());
				final int port = 9999;
				Message.obtain(handler, 0, fileName + " 正在发送至" + ipAddress + ":" + port).sendToTarget();
				new Thread(new Runnable() {
					@Override
					public void run() {
						String response = socketManager.SendFile(fileName, path, ipAddress, port);
						Message.obtain(handler, 0, response).sendToTarget();
					}
				}).start();
			}

		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}
	public String GetIpAddress() {     
	    WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);     
	    WifiInfo wifiInfo = wifiManager.getConnectionInfo();     
	    int i = wifiInfo.getIpAddress();
	    return (i & 0xFF) + "." +     
	    	   ((i >> 8 ) & 0xFF) + "." +     
	    	   ((i >> 16 ) & 0xFF)+ "." +     
	           ((i >> 24 ) & 0xFF );     
	}    
}
