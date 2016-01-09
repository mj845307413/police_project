package com.zpc.policeappe;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
public class MainActivity extends Activity {
	private EditText inputUsername;  
	private CheckBox saveInfoItem;
	private EditText inputPassword;   
	private ProgressDialog mDialog;  
	private String responseMsg = "";  
	private static final int REQUEST_TIMEOUT = 5*1000;//设置请求超时10秒钟    
	private static final int SO_TIMEOUT = 10*1000;  //设置等待数据超时时间10秒钟    
	private SharedPreferences sp;
	private String te1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ExitApplication.getInstance().addActivity(this); 
		inputUsername = (EditText)findViewById(R.id.et_name);  
		inputPassword = (EditText)findViewById(R.id.et_content); 
		saveInfoItem = (CheckBox)findViewById(R.id.checkBox1); 
		Button loginBtn=(android.widget.Button)findViewById(R.id.et_button);
		final TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		te1  = tm.getLine1Number();//获取本机号码
		if(te1==null||te1.equals("")){
			te1 = "+8618210575490";
		}
		sp = getSharedPreferences("userdata",0);  
		//初始化数据  
		LoadUserdata();  

		//检查网络  
		CheckNetworkState();  
		saveInfoItem.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()    
		{    
			@Override    
			public void onCheckedChanged(CompoundButton buttonView,    
					boolean isChecked) {    
				// TODO Auto-generated method stub    
				//载入用户信息  

				Editor editor = sp.edit();  

				if(saveInfoItem.isChecked())  
				{  
					//获取已经存在的用户名和密码  
					String realUsername = sp.getString("username", "");  
					String realPassword = sp.getString("password", "");  
					editor.putBoolean("checkstatus", true);  
					editor.commit();  

					if((!realUsername.equals(""))&&!(realUsername==null)||(!realPassword.equals(""))||!(realPassword==null))  
					{  
						//清空输入框  
						inputUsername.setText("");  
						inputPassword.setText("");  
						//设置已有值  
						inputUsername.setText(realUsername);  
						inputPassword.setText(realPassword);  
					}  
				}else  
				{  
					editor.putBoolean("checkstatus", false);  
					editor.commit();  
					//清空输入框  
					inputUsername.setText("");  
					inputPassword.setText("");  
				}  

			}    

		});    
		loginBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog = new ProgressDialog(MainActivity.this);  
				mDialog.setTitle("登陆");  
				mDialog.setMessage("正在登陆服务器，请稍后...");  
				mDialog.show();  
				Thread loginThread = new Thread(new LoginThread());   
				loginThread.start();  



			}
		});
		Button backBtn=(android.widget.Button)findViewById(R.id.button2);
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ExitApplication.getInstance().exit();

			}

		});}
	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;
	private String otherName;
	private boolean loginServer(String name, String password, int flag) {
		PoliceName.getInstance().setPoliceName(name);
		try {

			socket=new Socket();
			socket.connect(new InetSocketAddress(Utils.SERVER_IP
					,Utils.SERVER_PORT), 5000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (socket.isConnected()) {
				dos = new DataOutputStream(socket.getOutputStream());
				dis = new DataInputStream(socket.getInputStream());
				String sendString = "policelogin"+"@"+"username="+te1+"&"+"password="+"123456"+"&"+"status="+String.valueOf(1);
				byte[] ScmHeadbt = sendString.getBytes("UTF8");
				dos.write(ScmHeadbt, 0, ScmHeadbt.length);

				InputStream in = socket.getInputStream();

				InputStreamReader read = new InputStreamReader(new BufferedInputStream(in));
				char[] cbuf = new char[1024];
				Arrays.fill(cbuf, '\0');
				int len = read.read(cbuf, 0, 1024);
				StringBuilder sb = new StringBuilder(1024);
				sb.append(cbuf, 0, len);
				//				byte[] ScmBytes = new byte[1024];
				//				dis.read(ScmBytes, 0, ScmBytes.length);
				responseMsg=sb.toString();
				Log.i("majun", responseMsg);
				socket.close();
				dos.close();
				dis.close();
			}
		}catch(Exception exception){

		}
		return true;
	}
	//没用到
	private boolean loginServer(String username, String password)  
	{  
		boolean loginValidate = false;  
		//使用apache HTTP客户端实现  
		String urlStr = "http://111.202.78.206:60008";
		String t="policelogin"+"@"+"status="+String.valueOf(1)+"&"+"username="+username+"&"+"password="+password;
		HttpPost request = new HttpPost(urlStr);  
		//如果传递参数多的话，可以丢传递的参数进行封装   
		try  
		{  
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("par", t));
			HttpEntity httpEntity = new UrlEncodedFormEntity(params, "gb2312");
			request.setEntity(httpEntity);


			//HttpClient client = getHttpClient();  
			HttpClient client = new DefaultHttpClient();  
			//执行请求返回相应  
			HttpResponse response = client.execute(request);  

			//判断是否请求成功  
			if(response.getStatusLine().getStatusCode()==200)  
			{  
				loginValidate = true;  
				//获得响应信息  
				responseMsg = EntityUtils.toString(response.getEntity());  
			}  
		}catch(Exception e)  
		{  
			e.printStackTrace();  
		}  
		return loginValidate;  
	}  

	public HttpClient getHttpClient()  
	{  
		BasicHttpParams httpParams = new BasicHttpParams();  
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);  
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);  
		HttpClient client = new DefaultHttpClient(httpParams);  
		return client;  
	}  


	//判断是否记住密码，默认记住  
	private boolean isRemembered() {  
		try {  
			if (saveInfoItem.isChecked()) {  
				return true;  
			}  
		} catch (Exception e) {  
			// TODO Auto-generated catch block  
			e.printStackTrace();  
		}  
		return false;  
	}  
	//初始化用户数据  
	private void LoadUserdata()  
	{  
		boolean checkstatus = sp.getBoolean("checkstatus", false);  
		if(checkstatus)  
		{  
			saveInfoItem.setChecked(true);  
			//载入用户信息  
			//获取已经存在的用户名和密码  
			String realUsername = sp.getString("username", "");  
			String realPassword = sp.getString("password", "");  
			if((!realUsername.equals(""))&&!(realUsername==null)||(!realPassword.equals(""))||!(realPassword==null))  
			{  
				inputUsername.setText("");  
				inputPassword.setText("");  
				inputUsername.setText(realUsername);  
				inputPassword.setText(realPassword);  
			}      
		}else  
		{  
			saveInfoItem.setChecked(false);  
			inputUsername.setText("");  
			inputPassword.setText("");  
		}  

	}  
	//检查网络状态  
	public void CheckNetworkState()  
	{  
		boolean flag = false;  
		ConnectivityManager manager = (ConnectivityManager)getSystemService(  
				Context.CONNECTIVITY_SERVICE);  
		State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();  
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
		//如果3G、wifi、2G等网络状态是连接的，则退出，否则显示提示信息进入网络设置界面  
		if(mobile == State.CONNECTED||mobile==State.CONNECTING)  
			return;  
		if(wifi == State.CONNECTED||wifi==State.CONNECTING)  
			return;  
		showTips();  
	}  

	private void showTips()  
	{  
		AlertDialog.Builder builder = new AlertDialog.Builder(this);  
		builder.setIcon(android.R.drawable.ic_dialog_alert);  
		builder.setTitle("没有可用网络");  
		builder.setMessage("当前网络不可用，是否设置网络？");  
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  

			@Override  
			public void onClick(DialogInterface dialog, int which) {  
				// 如果没有网络连接，则进入网络设置界面  
				startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));  
			}  
		});  
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  

			@Override  
			public void onClick(DialogInterface dialog, int which) {  
				dialog.cancel();  
				MainActivity.this.finish();  
			}  
		});  
		builder.create();  
		builder.show();  
	}  
	//Handler  
	Handler handler = new Handler()  
	{  
		public void handleMessage(Message msg)  
		{  
			switch(msg.what)  
			{  
			case 0:  
				mDialog.cancel();  

				Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT).show(); 
				Log.i("majun","登陆成功");
				//Intent intent = new Intent();  
				Intent MainIntent=new Intent(MainActivity.this,SecondActivity.class);
				startActivity(MainIntent);
				finish();  
				break;  
			case 1:  
				mDialog.cancel();  
				Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();  
				break;  
			case 2:  
				mDialog.cancel();  
				Toast.makeText(getApplicationContext(), "URL验证失败", Toast.LENGTH_SHORT).show();  
				break;  

			}  

		}  
	};  

	//LoginThread线程类  
	class LoginThread implements Runnable  
	{  

		@Override  
		public void run() {  
			String username = inputUsername.getText().toString();  
			String password = inputPassword.getText().toString();      
			boolean checkstatus = sp.getBoolean("checkstatus", false);  
			if(checkstatus)  
			{  
				//获取已经存在的用户名和密码  
				String realUsername = sp.getString("username", "");  
				String realPassword = sp.getString("password", "");  
				if((!realUsername.equals(""))&&!(realUsername==null)||(!realPassword.equals(""))||!(realPassword==null))  
				{  
					if(username.equals(realUsername)&&password.equals(realPassword))  
					{  
						username = inputUsername.getText().toString();  
						password = inputPassword.getText().toString();  
					}  
				}  
			}else  
			{  
				password = md5(password);  
			}  
			System.out.println("username="+username+":password="+password);  

			//URL合法，但是这一步并不验证密码是否正确  
			boolean loginValidate = loginServer(username, password,0);  
			System.out.println("----------------------------bool is :"+loginValidate+"----------response:"+responseMsg);  
			Message msg = handler.obtainMessage();  
			if(loginValidate)  
			{  
				if(responseMsg.equals("success"))  
				{  
					msg.what = 0;  
					handler.sendMessage(msg);  
				}else  
				{  
					msg.what = 1;  
					handler.sendMessage(msg);  
				}  

			}else  
			{  
				msg.what = 2;  
				handler.sendMessage(msg);  
			}  
		}  

	}  


	/** 
	 * MD5单向加密，32位，用于加密密码，因为明文密码在信道中传输不安全，明文保存在本地也不安全   
	 * @param str 
	 * @return 
	 */  
	public static String md5(String str)    
	{    
		MessageDigest md5 = null;    
		try    
		{    
			md5 = MessageDigest.getInstance("MD5");    
		}catch(Exception e)    
		{    
			e.printStackTrace();    
			return "";    
		}    

		char[] charArray = str.toCharArray();    
		byte[] byteArray = new byte[charArray.length];    

		for(int i = 0; i < charArray.length; i++)    
		{    
			byteArray[i] = (byte)charArray[i];    
		}    
		byte[] md5Bytes = md5.digest(byteArray);    

		StringBuffer hexValue = new StringBuffer();    
		for( int i = 0; i < md5Bytes.length; i++)    
		{    
			int val = ((int)md5Bytes[i])&0xff;    
			if(val < 16)    
			{    
				hexValue.append("0");    
			}    
			hexValue.append(Integer.toHexString(val));    
		}    
		return hexValue.toString();    
	}    


}





