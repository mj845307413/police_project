package com.beidou.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;  
import java.util.List;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import android.view.View;
import android.app.Activity;  
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;  
import android.content.Intent;  
import android.location.Criteria;  
import android.location.GpsSatellite;  
import android.location.GpsStatus;  
import android.location.Location;  
import android.location.LocationListener;  
import android.location.LocationManager;  
import android.location.LocationProvider;  
import android.net.Uri;
import android.os.Bundle;  
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;  
import android.telephony.TelephonyManager;
import android.util.Log;  
import android.widget.Button;
import android.widget.EditText;  
import android.widget.TextView;
import android.widget.Toast;  
import android.os.Handler;

import java.io.*;
import java.net.*;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import android.util.Base64;


public class MainActivity extends Activity{
	private EditText editText;  
	private LocationManager lm;  
	private static final String TAG = "GpsActivity";
	TextView set;
	private static final int URGENT_RESPONSE=0;
	TextView Show;
	String result="";  
	private boolean issafe;
	private  String te1;
	@Override  
	public void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.activity_main);  
		Intent intent=getIntent();
		editText = (EditText) findViewById(R.id.editText);  
		Show=(TextView)findViewById(R.id.show1);
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Button phonecall=(Button)findViewById(R.id.phonecall);
		Button help_urgent=(Button)findViewById(R.id.help_urgent);
		Button help_normal=(Button)findViewById(R.id.help_normal);
		Button END=(Button)findViewById(R.id.END);
		issafe=true;
		final TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		te1  = tm.getLine1Number();//获取本机号码
		if(te1==null||te1.equals("")){
			te1 = "+8618210575490";
		}
		phonecall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				//这个意图就是调用系统的拨打活动
				intent.setAction(Intent.ACTION_CALL);
				//设置要拨打的电话号号码
				intent.setData(Uri.parse("tel:18811793208"));//uriString的格式为“tel:电话号码”
				//开始调整到打电话的活动
				startActivity(intent);

			}
		});
		END.setOnClickListener(new View.OnClickListener() {
			String a[]=result.split("&");
			String t="passerbyupdate"+"@"+"telno="+a[0]+"&"+"status="+"100A";
		
			
			public void sendupdate(){
				new Thread(new Runnable() {

					private Socket socket;
					private DataOutputStream dos;
					private DataInputStream dis;

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							socket=new Socket();
							socket.connect(new InetSocketAddress(Utils.SERVER_IP
									,Utils.SERVER_PORT), 5000);
							Log.i("majun", "socket");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							if (socket.isConnected()) {
								Log.i("majun", "socket2");
								//	        						TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
								//	        						final String te1  = tm.getLine1Number();//获取本机号码
								dos = new DataOutputStream(socket.getOutputStream());
								dis = new DataInputStream(socket.getInputStream());
								// 为获取地理位置信息时设置查询条件  
								//String bestProvider = lm.getBestProvider(getCriteria(), true);  
								// 获取位置信息  
								// 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER  
								//Location location = lm.getLastKnownLocation(bestProvider);  
								String sendString="passerbyupdate"+"@"+"telno="+te1+"&"+"status="+"100A";        	
								byte[] ScmHeadbt = sendString.getBytes("UTF8");
								Log.i("majun", "length:"+ScmHeadbt.length);
								dos.write(ScmHeadbt, 0, ScmHeadbt.length);

								InputStream in = socket.getInputStream();

								InputStreamReader read = new InputStreamReader(new BufferedInputStream(in));
								char[] cbuf = new char[1024];
								Arrays.fill(cbuf, '\0');
								int len = read.read(cbuf, 0, 1024);
								StringBuilder sb = new StringBuilder(1024);
								sb.append(cbuf, 0, len);
								//					byte[] ScmBytes = new byte[1024];
								//					dis.read(ScmBytes, 0, ScmBytes.length);
								String responseMsg=sb.toString();
								Log.i("majun", responseMsg);
								socket.close();
								dos.close();
								dis.close();
							}
						}catch(Exception exception){
							exception.printStackTrace();
						}
					}
				}).start();
				issafe=true;
			}
			public void send() {
				String target="http://192.168.61.232:8000";
				HttpClient httpclient=new DefaultHttpClient();
				HttpPost httpRequest=new HttpPost(target+a[0]);
				try{
					HttpResponse httpResponse=httpclient.execute(httpRequest);
					if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
						result=EntityUtils.toString(httpResponse.getEntity());
						if(result=="success "){
							Toast.makeText(MainActivity.this, result,Toast.LENGTH_SHORT).show();}else{Toast.makeText(MainActivity.this, "请求失败",Toast.LENGTH_SHORT).show();}
					}else{Toast.makeText(MainActivity.this, "请求失败",Toast.LENGTH_SHORT).show();
					}
				}catch(UnsupportedEncodingException e1){e1.printStackTrace();}
				catch(ClientProtocolException e){
					e.printStackTrace();
				}catch(IOException e)
				{e.printStackTrace();}
			}
			@Override
			public void onClick(View v) {

				sendupdate();

			}
		});

		help_normal.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent1=new Intent(MainActivity.this,Normal.class);
				startActivity(intent1);

			}
		});


		help_urgent.setOnClickListener(new View.OnClickListener() {	        	

			@Override
			public void onClick(View v) {
				//send();
				sendreport();
				//issafe=false;
				//handler.sendEmptyMessage(0);
				//				AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
				//				//设置任务执行计划
				//				am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, 2*1000, send());//从firstTime才开始执行，每隔1秒再执行

			}
			public void sendreport(){
				new Thread(new Runnable() {

					private Socket socket;
					private DataOutputStream dos;
					private DataInputStream dis;

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							socket=new Socket();
							socket.connect(new InetSocketAddress(Utils.SERVER_IP
									,Utils.SERVER_PORT), 5000);
							Log.i("majun", "socket");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							if (socket.isConnected()) {
								Log.i("majun", "socket2");
								//	        						TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
								//	        						final String te1  = tm.getLine1Number();//获取本机号码
								dos = new DataOutputStream(socket.getOutputStream());
								dis = new DataInputStream(socket.getInputStream());
								// 为获取地理位置信息时设置查询条件  
								String bestProvider = lm.getBestProvider(getCriteria(), true);  
								// 获取位置信息  
								// 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER  
								Location location = lm.getLastKnownLocation(bestProvider);  
								String sendString="passerbyreport@"+"telephonenumber="+te1+"&"+"Longitude="+String.valueOf(location.getLongitude())+"&"+"Latitude="+String.valueOf(location.getLatitude())+"&"+"status="+"1003";	        	        	
								byte[] ScmHeadbt = sendString.getBytes("UTF8");
								Log.i("majun", "length:"+ScmHeadbt.length);
								dos.write(ScmHeadbt, 0, ScmHeadbt.length);

								InputStream in = socket.getInputStream();

								InputStreamReader read = new InputStreamReader(new BufferedInputStream(in));
								char[] cbuf = new char[1024];
								Arrays.fill(cbuf, '\0');
								int len = read.read(cbuf, 0, 1024);
								StringBuilder sb = new StringBuilder(1024);
								sb.append(cbuf, 0, len);
								//					byte[] ScmBytes = new byte[1024];
								//					dis.read(ScmBytes, 0, ScmBytes.length);
								result=sb.toString();
								Log.i("majun", result);
								socket.close();
								dos.close();
								dis.close();
								handler.sendEmptyMessage(URGENT_RESPONSE);
							}
						}catch(Exception exception){
							exception.printStackTrace();
						}
					}
				}).start();
			}
			private PendingIntent send() {
				//final String te1  = tm.getLine1Number();//获取本机号码
				String te1 = "";
				String t="passerbyreport@"+"telephonenumber="+te1+"&"+"Longitude="+String.valueOf(((Location) locationListener).getLongitude())+"&"+"Latitude="+String.valueOf(((Location) locationListener).getLatitude())+"&"+"status="+0x1003;	        	

				String target="http://192.168.61.232:8000";
				HttpClient httpclient=new DefaultHttpClient();
				HttpPost httpRequest=new HttpPost(target+"/"+t);


				try{
					HttpResponse httpResponse=httpclient.execute(httpRequest);
					if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
						result=EntityUtils.toString(httpResponse.getEntity());
						if(result!="Sorry"&&result!=null){
							String a[]=result.split("&");
							Show.setText("一名民警正在向您靠近，其电话号码为："+a[0]+"距离："+a[1]+"米");
						}
					}else{result="Sorry";}
				}catch(UnsupportedEncodingException e1){e1.printStackTrace();}
				catch(ClientProtocolException e){
					e.printStackTrace();
				}catch(IOException e)
				{e.printStackTrace();}
				return null;
			}
		});

		// 判断GPS是否正常启动  
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {  
			Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();  
			// 返回开启GPS导航设置界面  
			Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
			startActivityForResult(intent1, 0);  
			return;  
		}  

		// 为获取地理位置信息时设置查询条件  
		String bestProvider = lm.getBestProvider(getCriteria(), true);  
		// 获取位置信息  
		// 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER  
		Location location = lm.getLastKnownLocation(bestProvider);  
		updateView(location);  
		// 监听状态  
		lm.addGpsStatusListener(listener);  
		// 绑定监听，有4个参数  
		// 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种  
		// 参数2，位置信息更新周期，单位毫秒  
		// 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息  
		// 参数4，监听  
		// 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新  

		// 1秒更新一次，或最小位移变化超过1米更新一次；  
		// 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置  
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);  
	}  


	// 位置监听  
	private LocationListener locationListener = new LocationListener() {  

		/** 
		 * 位置信息变化时触发 
		 */  
		public void onLocationChanged(Location location) {  
			updateView(location);  
			Log.i(TAG, "时间：" + location.getTime());  
			Log.i(TAG, "经度：" + location.getLongitude());  
			Log.i(TAG, "纬度：" + location.getLatitude());  
			Log.i(TAG, "海拔：" + location.getAltitude());  
		}  

		/** 
		 * GPS状态变化时触发 
		 */  
		public void onStatusChanged(String provider, int status, Bundle extras) {  
			switch (status) {  
			// GPS状态为可见时  
			case LocationProvider.AVAILABLE:  
				Log.i(TAG, "当前GPS状态为可见状态");  
				break;  
				// GPS状态为服务区外时  
			case LocationProvider.OUT_OF_SERVICE:  
				Log.i(TAG, "当前GPS状态为服务区外状态");  
				break;  
				// GPS状态为暂停服务时  
			case LocationProvider.TEMPORARILY_UNAVAILABLE:  
				Log.i(TAG, "当前GPS状态为暂停服务状态");  
				break;  
			}  
		}  

		/** 
		 * GPS开启时触发 
		 */  
		public void onProviderEnabled(String provider) {  
			Location location = lm.getLastKnownLocation(provider);  
			updateView(location);  
		}  

		/** 
		 * GPS禁用时触发 
		 */  
		public void onProviderDisabled(String provider) {  
			updateView(null);  
		}  

	};  

	// 状态监听  
	GpsStatus.Listener listener = new GpsStatus.Listener() {  
		public void onGpsStatusChanged(int event) {  
			switch (event) {  
			// 第一次定位  
			case GpsStatus.GPS_EVENT_FIRST_FIX:  
				Log.i(TAG, "第一次定位");  
				break;  
				// 卫星状态改变  
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:  
				Log.i(TAG, "卫星状态改变");  
				// 获取当前状态  
				GpsStatus gpsStatus = lm.getGpsStatus(null);  
				// 获取卫星颗数的默认最大值  
				int maxSatellites = gpsStatus.getMaxSatellites();  
				// 创建一个迭代器保存所有卫星  
				Iterator<GpsSatellite> iters = gpsStatus.getSatellites()  
						.iterator();  
				int count = 0;  
				while (iters.hasNext() && count <= maxSatellites) {  
					GpsSatellite s = iters.next();  
					count++;  
				}  
				System.out.println("搜索到：" + count + "颗卫星");  
				break;  
				// 定位启动  
			case GpsStatus.GPS_EVENT_STARTED:  
				Log.i(TAG, "定位启动");  
				break;  
				// 定位结束  
			case GpsStatus.GPS_EVENT_STOPPED:  
				Log.i(TAG, "定位结束");  
				break;  
			}  
		};  
	};  

	/** 
	 * 实时更新文本内容 
	 *  
	 * @param location 
	 */  
	private void updateView(Location location) {  
		if (location != null) {  
			editText.setText("设备位置信息\n\n经度：");  
			editText.append(String.valueOf(location.getLongitude()));  
			editText.append("\n纬度：");  
			editText.append(String.valueOf(location.getLatitude()));  
		} else {  
			// 清空EditText对象  
			editText.getEditableText().clear();  
		}  
	}  

	/** 
	 * 返回查询条件 
	 *  
	 * @return 
	 */  
	private Criteria getCriteria() {  
		Criteria criteria = new Criteria();  
		// 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细  
		criteria.setAccuracy(Criteria.ACCURACY_FINE);  
		// 设置是否要求速度  
		criteria.setSpeedRequired(false);  
		// 设置是否允许运营商收费  
		criteria.setCostAllowed(false);  
		// 设置是否需要方位信息  
		criteria.setBearingRequired(false);  
		// 设置是否需要海拔信息  
		criteria.setAltitudeRequired(false);  
		// 设置对电源的需求  
		criteria.setPowerRequirement(Criteria.POWER_LOW);  
		return criteria;  
	}  
	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case URGENT_RESPONSE:
				if(result!="Sorry"&&result!=null){
					String a[]=result.split("&");
					String distance = "";
					if(a[1].contains(".")){
						distance = a[1].substring(0, a[1].indexOf(".")+3);
					}else{
						distance= a[1];
					}
					Show.setText("一名民警正在向您靠近，其电话号码为："+a[0]+"距离："+distance+"米");
				}else{
					Intent intent = new Intent();
					//这个意图就是调用系统的拨打活动
					intent.setAction(Intent.ACTION_CALL);
					//设置要拨打的电话号号码
					intent.setData(Uri.parse("tel:18811793208"));//uriString的格式为“tel:电话号码”
					//开始调整到打电话的活动
					startActivity(intent);
				}
				break;

			default:
				break;
			}
			if (issafe) {

			}else {
				handler.sendEmptyMessageDelayed(0, 1000);
			}
		}

	};


}
