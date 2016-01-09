package com.beidou.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import android.location.Criteria;  
import android.location.GpsSatellite;  
import android.location.GpsStatus;  
import android.location.Location;  
import android.location.LocationListener;  
import android.location.LocationManager;  
import android.location.LocationProvider; 
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.*;
public class Normal extends Activity {
	String result="";
	String t1="0x1004";
	String t2="0x1005";
	String t3="0x1006";
	String t4="0x1007";
	String t5="0x1008";
	String t6="0x1009";
	private static final int NORMAL_RESPONSE=0;
	private LocationManager lm;  
	private static final String TAG = "GpsActivity";
	String ta="";
	private TextView show;
	private String te1;
	private String sendString;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.normal);
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Intent intent=getIntent();
		Button crime_rob=(Button)findViewById(R.id.crime_rob);
		Button crime_accident=(Button)findViewById(R.id.crime_accident);
		Button crime_steal=(Button)findViewById(R.id.crime_steal);
		Button crime_fire=(Button)findViewById(R.id.crime_fire);
		Button crime_bangjia=(Button)findViewById(R.id.crime_bangjia);
		Button crime_others=(Button)findViewById(R.id.crime_others);
		Button NextEND=(Button)findViewById(R.id.NextEND);
		show=(TextView)findViewById(R.id.nmshow);
		TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		te1  = tm.getLine1Number();//获取本机号码
		if(te1==null||te1.equals("")){
			te1 = "+8618210575490";
		}
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




		crime_rob.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String bestProvider = lm.getBestProvider(getCriteria(), true);  
				// 获取位置信息  
				// 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER  
				Location location = lm.getLastKnownLocation(bestProvider);  
				sendString="passerbyreport@"+"telephonenumber="+te1+"&"+"Longitude="+String.valueOf(location.getLongitude())+"&"+"Latitude="+String.valueOf(location.getLatitude())+"&"+"status="+"1004";
				sendreport();
			}}

				);

		crime_accident.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String bestProvider = lm.getBestProvider(getCriteria(), true);  
				// 获取位置信息  
				// 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER  
				Location location = lm.getLastKnownLocation(bestProvider);  
				sendString="passerbyreport@"+"telephonenumber="+te1+"&"+"Longitude="+String.valueOf(location.getLongitude())+"&"+"Latitude="+String.valueOf(location.getLatitude())+"&"+"status="+"1005";
				sendreport();
			}
		});

		crime_steal.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String bestProvider = lm.getBestProvider(getCriteria(), true);  
				// 获取位置信息  
				// 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER  
				Location location = lm.getLastKnownLocation(bestProvider);  
				sendString="passerbyreport@"+"telephonenumber="+te1+"&"+"Longitude="+String.valueOf(location.getLongitude())+"&"+"Latitude="+String.valueOf(location.getLatitude())+"&"+"status="+"1006";
				sendreport();
			}

		});

		crime_fire.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String bestProvider = lm.getBestProvider(getCriteria(), true);  
				// 获取位置信息  
				// 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER  
				Location location = lm.getLastKnownLocation(bestProvider);  
				sendString="passerbyreport@"+"telephonenumber="+te1+"&"+"Longitude="+String.valueOf(location.getLongitude())+"&"+"Latitude="+String.valueOf(location.getLatitude())+"&"+"status="+"1007";
				sendreport();
			}
		});

		crime_bangjia.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String bestProvider = lm.getBestProvider(getCriteria(), true);  
				// 获取位置信息  
				// 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER  
				Location location = lm.getLastKnownLocation(bestProvider);  
				sendString="passerbyreport@"+"telephonenumber="+te1+"&"+"Longitude="+String.valueOf(location.getLongitude())+"&"+"Latitude="+String.valueOf(location.getLatitude())+"&"+"status="+"1008";
				sendreport();
			}
		});

		crime_others.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String bestProvider = lm.getBestProvider(getCriteria(), true);  
				// 获取位置信息  
				// 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER  
				Location location = lm.getLastKnownLocation(bestProvider);  
				sendString="passerbyreport@"+"telephonenumber="+te1+"&"+"Longitude="+String.valueOf(location.getLongitude())+"&"+"Latitude="+String.valueOf(location.getLatitude())+"&"+"status="+"1003";
				sendreport();
			}
		});
		NextEND.setOnClickListener(new View.OnClickListener() {
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
			}
			public void send() {
				String a[]=result.split("/");
				String target="http://192.168.61.232:8000";
				HttpClient httpclient=new DefaultHttpClient();
				HttpPost httpRequest=new HttpPost(target+"/"+a[0]);


				try{
					HttpResponse httpResponse=httpclient.execute(httpRequest);
					if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
						result=EntityUtils.toString(httpResponse.getEntity());
						if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
							result=EntityUtils.toString(httpResponse.getEntity());
						}else{result="";}}
				}catch(UnsupportedEncodingException e1){e1.printStackTrace();}
				catch(ClientProtocolException e){
					e.printStackTrace();
				}catch(IOException e)
				{e.printStackTrace();}
			}
			@Override
			public void onClick(View v) {
				result="";
				sendupdate();
				Intent intent=new Intent(Normal.this,MainActivity.class);
				startActivity(intent);}
		});}

	protected PendingIntent send() {
		TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		String te1  = tm.getLine1Number();
		String s="passerbyreport@"+"telno="+te1+"&"+"Longitude="+String.valueOf(((Location) locationListener).getLongitude())+"&"+"Latitude="+String.valueOf(((Location) locationListener).getLatitude())+"&"+"status="+ta;	        	
		String target="http://192.168.61.232:8000";
		HttpClient httpclient=new DefaultHttpClient();
		HttpPost httpRequest=new HttpPost(target+"/"+s);


		try{
			HttpResponse httpResponse=httpclient.execute(httpRequest);

			if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				result=EntityUtils.toString(httpResponse.getEntity());
				if(result!="Sorry!"&&result!=null){
					String a[]=result.split("/");
					show.setText("一名民警正在向您靠近，其电话号码为："+a[0]+"距离："+a[1]+"米");
				}else{result="Sorry";}
				if(result=="Sorry"){Intent intent = new Intent();
				//这个意图就是调用系统的拨打活动
				intent.setAction(Intent.ACTION_CALL);
				//设置要拨打的电话号号码
				intent.setData(Uri.parse("tel:18811793208"));//uriString的格式为“tel:电话号码”
				//开始调整到打电话的活动
				startActivity(intent);}
			}
		}catch(UnsupportedEncodingException e1){e1.printStackTrace();}
		catch(ClientProtocolException e){
			e.printStackTrace();
		}catch(IOException e)
		{e.printStackTrace();}
		return null;

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
						normalhandler.sendEmptyMessage(NORMAL_RESPONSE);
					}
				}catch(Exception exception){
					exception.printStackTrace();
				}
			}
		}).start();
	}
	// 位置监听  
	private LocationListener locationListener = new LocationListener() {  

		/** 
		 * 位置信息变化时触发 
		 */  
		public void onLocationChanged(Location location) {  

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

		}  

		/** 
		 * GPS禁用时触发 
		 */  
		public void onProviderDisabled(String provider) {  

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
	Handler normalhandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case NORMAL_RESPONSE:
				if(result!="Sorry"&&result!=null){
					String a[]=result.split("&");
					String distance = "";
					if(a[1].contains(".")){
						distance = a[1].substring(0, a[1].indexOf(".")+3);
					}else{
						distance= a[1];
					}
					show.setText("一名民警正在向您靠近，其电话号码为："+a[0]+"距离："+distance+"米");
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
		}

	};

}
