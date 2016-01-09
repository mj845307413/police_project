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
		te1  = tm.getLine1Number();//��ȡ��������
		if(te1==null||te1.equals("")){
			te1 = "+8618210575490";
		}
		// �ж�GPS�Ƿ���������  
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {  
			Toast.makeText(this, "�뿪��GPS����...", Toast.LENGTH_SHORT).show();  
			// ���ؿ���GPS�������ý���  
			Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
			startActivityForResult(intent1, 0);  
			return;  
		}  

		// Ϊ��ȡ����λ����Ϣʱ���ò�ѯ����  
		String bestProvider = lm.getBestProvider(getCriteria(), true);  
		// ��ȡλ����Ϣ  
		// ��������ò�ѯҪ��getLastKnownLocation�������˵Ĳ���ΪLocationManager.GPS_PROVIDER  
		Location location = lm.getLastKnownLocation(bestProvider);  

		// ����״̬  
		lm.addGpsStatusListener(listener);  
		// �󶨼�������4������  
		// ����1���豸����GPS_PROVIDER��NETWORK_PROVIDER����  
		// ����2��λ����Ϣ�������ڣ���λ����  
		// ����3��λ�ñ仯��С���룺��λ�þ���仯������ֵʱ��������λ����Ϣ  
		// ����4������  
		// ��ע������2��3���������3��Ϊ0�����Բ���3Ϊ׼������3Ϊ0����ͨ��ʱ������ʱ���£�����Ϊ0������ʱˢ��  

		// 1�����һ�Σ�����Сλ�Ʊ仯����1�׸���һ�Σ�  
		// ע�⣺�˴�����׼ȷ�ȷǳ��ͣ��Ƽ���service��������һ��Thread����run��sleep(10000);Ȼ��ִ��handler.sendMessage(),����λ��  
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);  




		crime_rob.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String bestProvider = lm.getBestProvider(getCriteria(), true);  
				// ��ȡλ����Ϣ  
				// ��������ò�ѯҪ��getLastKnownLocation�������˵Ĳ���ΪLocationManager.GPS_PROVIDER  
				Location location = lm.getLastKnownLocation(bestProvider);  
				sendString="passerbyreport@"+"telephonenumber="+te1+"&"+"Longitude="+String.valueOf(location.getLongitude())+"&"+"Latitude="+String.valueOf(location.getLatitude())+"&"+"status="+"1004";
				sendreport();
			}}

				);

		crime_accident.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String bestProvider = lm.getBestProvider(getCriteria(), true);  
				// ��ȡλ����Ϣ  
				// ��������ò�ѯҪ��getLastKnownLocation�������˵Ĳ���ΪLocationManager.GPS_PROVIDER  
				Location location = lm.getLastKnownLocation(bestProvider);  
				sendString="passerbyreport@"+"telephonenumber="+te1+"&"+"Longitude="+String.valueOf(location.getLongitude())+"&"+"Latitude="+String.valueOf(location.getLatitude())+"&"+"status="+"1005";
				sendreport();
			}
		});

		crime_steal.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String bestProvider = lm.getBestProvider(getCriteria(), true);  
				// ��ȡλ����Ϣ  
				// ��������ò�ѯҪ��getLastKnownLocation�������˵Ĳ���ΪLocationManager.GPS_PROVIDER  
				Location location = lm.getLastKnownLocation(bestProvider);  
				sendString="passerbyreport@"+"telephonenumber="+te1+"&"+"Longitude="+String.valueOf(location.getLongitude())+"&"+"Latitude="+String.valueOf(location.getLatitude())+"&"+"status="+"1006";
				sendreport();
			}

		});

		crime_fire.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String bestProvider = lm.getBestProvider(getCriteria(), true);  
				// ��ȡλ����Ϣ  
				// ��������ò�ѯҪ��getLastKnownLocation�������˵Ĳ���ΪLocationManager.GPS_PROVIDER  
				Location location = lm.getLastKnownLocation(bestProvider);  
				sendString="passerbyreport@"+"telephonenumber="+te1+"&"+"Longitude="+String.valueOf(location.getLongitude())+"&"+"Latitude="+String.valueOf(location.getLatitude())+"&"+"status="+"1007";
				sendreport();
			}
		});

		crime_bangjia.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String bestProvider = lm.getBestProvider(getCriteria(), true);  
				// ��ȡλ����Ϣ  
				// ��������ò�ѯҪ��getLastKnownLocation�������˵Ĳ���ΪLocationManager.GPS_PROVIDER  
				Location location = lm.getLastKnownLocation(bestProvider);  
				sendString="passerbyreport@"+"telephonenumber="+te1+"&"+"Longitude="+String.valueOf(location.getLongitude())+"&"+"Latitude="+String.valueOf(location.getLatitude())+"&"+"status="+"1008";
				sendreport();
			}
		});

		crime_others.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String bestProvider = lm.getBestProvider(getCriteria(), true);  
				// ��ȡλ����Ϣ  
				// ��������ò�ѯҪ��getLastKnownLocation�������˵Ĳ���ΪLocationManager.GPS_PROVIDER  
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
								//	        						final String te1  = tm.getLine1Number();//��ȡ��������
								dos = new DataOutputStream(socket.getOutputStream());
								dis = new DataInputStream(socket.getInputStream());
								// Ϊ��ȡ����λ����Ϣʱ���ò�ѯ����  
								//String bestProvider = lm.getBestProvider(getCriteria(), true);  
								// ��ȡλ����Ϣ  
								// ��������ò�ѯҪ��getLastKnownLocation�������˵Ĳ���ΪLocationManager.GPS_PROVIDER  
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
					show.setText("һ��������������������绰����Ϊ��"+a[0]+"���룺"+a[1]+"��");
				}else{result="Sorry";}
				if(result=="Sorry"){Intent intent = new Intent();
				//�����ͼ���ǵ���ϵͳ�Ĳ���
				intent.setAction(Intent.ACTION_CALL);
				//����Ҫ����ĵ绰�ź���
				intent.setData(Uri.parse("tel:18811793208"));//uriString�ĸ�ʽΪ��tel:�绰���롱
				//��ʼ��������绰�Ļ
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
						//	        						final String te1  = tm.getLine1Number();//��ȡ��������
						dos = new DataOutputStream(socket.getOutputStream());
						dis = new DataInputStream(socket.getInputStream());
						// Ϊ��ȡ����λ����Ϣʱ���ò�ѯ����  
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
	// λ�ü���  
	private LocationListener locationListener = new LocationListener() {  

		/** 
		 * λ����Ϣ�仯ʱ���� 
		 */  
		public void onLocationChanged(Location location) {  

			Log.i(TAG, "ʱ�䣺" + location.getTime());  
			Log.i(TAG, "���ȣ�" + location.getLongitude());  
			Log.i(TAG, "γ�ȣ�" + location.getLatitude());  
			Log.i(TAG, "���Σ�" + location.getAltitude());  
		}  

		/** 
		 * GPS״̬�仯ʱ���� 
		 */  
		public void onStatusChanged(String provider, int status, Bundle extras) {  
			switch (status) {  
			// GPS״̬Ϊ�ɼ�ʱ  
			case LocationProvider.AVAILABLE:  
				Log.i(TAG, "��ǰGPS״̬Ϊ�ɼ�״̬");  
				break;  
				// GPS״̬Ϊ��������ʱ  
			case LocationProvider.OUT_OF_SERVICE:  
				Log.i(TAG, "��ǰGPS״̬Ϊ��������״̬");  
				break;  
				// GPS״̬Ϊ��ͣ����ʱ  
			case LocationProvider.TEMPORARILY_UNAVAILABLE:  
				Log.i(TAG, "��ǰGPS״̬Ϊ��ͣ����״̬");  
				break;  
			}  
		}  

		/** 
		 * GPS����ʱ���� 
		 */  
		public void onProviderEnabled(String provider) {  
			Location location = lm.getLastKnownLocation(provider);  

		}  

		/** 
		 * GPS����ʱ���� 
		 */  
		public void onProviderDisabled(String provider) {  

		}  

	};  

	// ״̬����  
	GpsStatus.Listener listener = new GpsStatus.Listener() {  
		public void onGpsStatusChanged(int event) {  
			switch (event) {  
			// ��һ�ζ�λ  
			case GpsStatus.GPS_EVENT_FIRST_FIX:  
				Log.i(TAG, "��һ�ζ�λ");  
				break;  
				// ����״̬�ı�  
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:  
				Log.i(TAG, "����״̬�ı�");  
				// ��ȡ��ǰ״̬  
				GpsStatus gpsStatus = lm.getGpsStatus(null);  
				// ��ȡ���ǿ�����Ĭ�����ֵ  
				int maxSatellites = gpsStatus.getMaxSatellites();  
				// ����һ��������������������  
				Iterator<GpsSatellite> iters = gpsStatus.getSatellites()  
						.iterator();  
				int count = 0;  
				while (iters.hasNext() && count <= maxSatellites) {  
					GpsSatellite s = iters.next();  
					count++;  
				}  
				System.out.println("��������" + count + "������");  
				break;  
				// ��λ����  
			case GpsStatus.GPS_EVENT_STARTED:  
				Log.i(TAG, "��λ����");  
				break;  
				// ��λ����  
			case GpsStatus.GPS_EVENT_STOPPED:  
				Log.i(TAG, "��λ����");  
				break;  
			}  
		};  
	};  



	/** 
	 * ���ز�ѯ���� 
	 *  
	 * @return 
	 */  
	private Criteria getCriteria() {  
		Criteria criteria = new Criteria();  
		// ���ö�λ��ȷ�� Criteria.ACCURACY_COARSE�Ƚϴ��ԣ�Criteria.ACCURACY_FINE��ȽϾ�ϸ  
		criteria.setAccuracy(Criteria.ACCURACY_FINE);  
		// �����Ƿ�Ҫ���ٶ�  
		criteria.setSpeedRequired(false);  
		// �����Ƿ�������Ӫ���շ�  
		criteria.setCostAllowed(false);  
		// �����Ƿ���Ҫ��λ��Ϣ  
		criteria.setBearingRequired(false);  
		// �����Ƿ���Ҫ������Ϣ  
		criteria.setAltitudeRequired(false);  
		// ���öԵ�Դ������  
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
					show.setText("һ��������������������绰����Ϊ��"+a[0]+"���룺"+distance+"��");
				}else{
					Intent intent = new Intent();
					//�����ͼ���ǵ���ϵͳ�Ĳ���
					intent.setAction(Intent.ACTION_CALL);
					//����Ҫ����ĵ绰�ź���
					intent.setData(Uri.parse("tel:18811793208"));//uriString�ĸ�ʽΪ��tel:�绰���롱
					//��ʼ��������绰�Ļ
					startActivity(intent);
				}
				break;

			default:
				break;
			}
		}

	};

}
