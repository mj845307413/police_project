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
	private static final int REQUEST_TIMEOUT = 5*1000;//��������ʱ10����    
	private static final int SO_TIMEOUT = 10*1000;  //���õȴ����ݳ�ʱʱ��10����    
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
		te1  = tm.getLine1Number();//��ȡ��������
		if(te1==null||te1.equals("")){
			te1 = "+8618210575490";
		}
		sp = getSharedPreferences("userdata",0);  
		//��ʼ������  
		LoadUserdata();  

		//�������  
		CheckNetworkState();  
		saveInfoItem.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()    
		{    
			@Override    
			public void onCheckedChanged(CompoundButton buttonView,    
					boolean isChecked) {    
				// TODO Auto-generated method stub    
				//�����û���Ϣ  

				Editor editor = sp.edit();  

				if(saveInfoItem.isChecked())  
				{  
					//��ȡ�Ѿ����ڵ��û���������  
					String realUsername = sp.getString("username", "");  
					String realPassword = sp.getString("password", "");  
					editor.putBoolean("checkstatus", true);  
					editor.commit();  

					if((!realUsername.equals(""))&&!(realUsername==null)||(!realPassword.equals(""))||!(realPassword==null))  
					{  
						//��������  
						inputUsername.setText("");  
						inputPassword.setText("");  
						//��������ֵ  
						inputUsername.setText(realUsername);  
						inputPassword.setText(realPassword);  
					}  
				}else  
				{  
					editor.putBoolean("checkstatus", false);  
					editor.commit();  
					//��������  
					inputUsername.setText("");  
					inputPassword.setText("");  
				}  

			}    

		});    
		loginBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog = new ProgressDialog(MainActivity.this);  
				mDialog.setTitle("��½");  
				mDialog.setMessage("���ڵ�½�����������Ժ�...");  
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
	//û�õ�
	private boolean loginServer(String username, String password)  
	{  
		boolean loginValidate = false;  
		//ʹ��apache HTTP�ͻ���ʵ��  
		String urlStr = "http://111.202.78.206:60008";
		String t="policelogin"+"@"+"status="+String.valueOf(1)+"&"+"username="+username+"&"+"password="+password;
		HttpPost request = new HttpPost(urlStr);  
		//������ݲ�����Ļ������Զ����ݵĲ������з�װ   
		try  
		{  
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("par", t));
			HttpEntity httpEntity = new UrlEncodedFormEntity(params, "gb2312");
			request.setEntity(httpEntity);


			//HttpClient client = getHttpClient();  
			HttpClient client = new DefaultHttpClient();  
			//ִ�����󷵻���Ӧ  
			HttpResponse response = client.execute(request);  

			//�ж��Ƿ�����ɹ�  
			if(response.getStatusLine().getStatusCode()==200)  
			{  
				loginValidate = true;  
				//�����Ӧ��Ϣ  
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


	//�ж��Ƿ��ס���룬Ĭ�ϼ�ס  
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
	//��ʼ���û�����  
	private void LoadUserdata()  
	{  
		boolean checkstatus = sp.getBoolean("checkstatus", false);  
		if(checkstatus)  
		{  
			saveInfoItem.setChecked(true);  
			//�����û���Ϣ  
			//��ȡ�Ѿ����ڵ��û���������  
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
	//�������״̬  
	public void CheckNetworkState()  
	{  
		boolean flag = false;  
		ConnectivityManager manager = (ConnectivityManager)getSystemService(  
				Context.CONNECTIVITY_SERVICE);  
		State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();  
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
		//���3G��wifi��2G������״̬�����ӵģ����˳���������ʾ��ʾ��Ϣ�����������ý���  
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
		builder.setTitle("û�п�������");  
		builder.setMessage("��ǰ���粻���ã��Ƿ��������磿");  
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {  

			@Override  
			public void onClick(DialogInterface dialog, int which) {  
				// ���û���������ӣ�������������ý���  
				startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));  
			}  
		});  
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {  

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

				Toast.makeText(getApplicationContext(), "��¼�ɹ���", Toast.LENGTH_SHORT).show(); 
				Log.i("majun","��½�ɹ�");
				//Intent intent = new Intent();  
				Intent MainIntent=new Intent(MainActivity.this,SecondActivity.class);
				startActivity(MainIntent);
				finish();  
				break;  
			case 1:  
				mDialog.cancel();  
				Toast.makeText(getApplicationContext(), "�������", Toast.LENGTH_SHORT).show();  
				break;  
			case 2:  
				mDialog.cancel();  
				Toast.makeText(getApplicationContext(), "URL��֤ʧ��", Toast.LENGTH_SHORT).show();  
				break;  

			}  

		}  
	};  

	//LoginThread�߳���  
	class LoginThread implements Runnable  
	{  

		@Override  
		public void run() {  
			String username = inputUsername.getText().toString();  
			String password = inputPassword.getText().toString();      
			boolean checkstatus = sp.getBoolean("checkstatus", false);  
			if(checkstatus)  
			{  
				//��ȡ�Ѿ����ڵ��û���������  
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

			//URL�Ϸ���������һ��������֤�����Ƿ���ȷ  
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
	 * MD5������ܣ�32λ�����ڼ������룬��Ϊ�����������ŵ��д��䲻��ȫ�����ı����ڱ���Ҳ����ȫ   
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





