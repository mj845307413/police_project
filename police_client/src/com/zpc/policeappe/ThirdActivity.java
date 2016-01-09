package com.zpc.policeappe;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ThirdActivity extends Activity {
	private LocationManager lm;  
	private static final String TAG = "GpsActivity";
	String result="";
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private String te1;
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_information2);
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Intent intent=getIntent();
		final String[] a = intent.getStringArrayExtra("info");
		TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		te1  = tm.getLine1Number();//��ȡ��������
		ExitApplication.getInstance().addActivity(this); 
		Button MainActivityback=(android.widget.Button)findViewById(R.id.button3);
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
		updateView(location);  
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





		MainActivityback.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent MainIntent=new Intent(ThirdActivity.this,SecondActivity.class);
				startActivity(MainIntent);

			}
		});
		Button MainActivitygo=(android.widget.Button)findViewById(R.id.button1);
		MainActivitygo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {//������ӹ�����
				sendHeart();
				//						AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
				//						//��������ִ�мƻ�
				//						am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, 5*1000, sendHeart());
				Intent MainIntent=new Intent(ThirdActivity.this,GeoCoderDemo.class);
				MainIntent.putExtra("info", a);
				startActivity(MainIntent);

			}
		});





	}
	private PendingIntent sendHeart(){
		Thread thread=new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
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
						//						TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
						//						final String te1  = tm.getLine1Number();//��ȡ��������
						// Ϊ��ȡ����λ����Ϣʱ���ò�ѯ����  
						String bestProvider = lm.getBestProvider(getCriteria(), true);  
						// ��ȡλ����Ϣ  
						// ��������ò�ѯҪ��getLastKnownLocation�������˵Ĳ���ΪLocationManager.GPS_PROVIDER  
						Location location = lm.getLastKnownLocation(bestProvider); 
						dos = new DataOutputStream(socket.getOutputStream());
						dis = new DataInputStream(socket.getInputStream());
						String sendString="policeheartbt"+"@"+"telno="+te1+"&"+"Longitude="+String.valueOf(location.getLongitude())+"&"+"Latitude="+String.valueOf(location.getLatitude())+"&"+"status="+String.valueOf(1);	        	
						byte[] ScmHeadbt = sendString.getBytes("UTF8");
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

				}
			}
		});
		thread.start();
		return null;
	}
	/**
	 * û�õ�
	 * @return
	 */
	private PendingIntent send1() {

		TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		final String te1  = tm.getLine1Number();//��ȡ��������
		String t="policeheartbt"+"@"+"telno="+te1+"/"+"Longitude="+String.valueOf(((Location) locationListener).getLongitude())+"/"+"Latitude="+String.valueOf(((Location) locationListener).getLatitude());	        	

		String target="http://111.202.78.206:60008";
		HttpClient httpclient=new DefaultHttpClient();
		HttpPost httpRequest=new HttpPost(target+"/"+t);
		try{

			HttpResponse httpResponse=httpclient.execute(httpRequest);
			if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				result=EntityUtils.toString(httpResponse.getEntity());
			}else{result="";}
		}catch(UnsupportedEncodingException e1){e1.printStackTrace();}
		catch(ClientProtocolException e){
			e.printStackTrace();
		}catch(IOException e)
		{e.printStackTrace();}
		return null;}
	// λ�ü���  
	private LocationListener locationListener = new LocationListener() {  

		/** 
		 * λ����Ϣ�仯ʱ���� 
		 */  
		public void onLocationChanged(Location location) {  
			updateView(location);  
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
			updateView(location);  
		}  

		/** 
		 * GPS����ʱ���� 
		 */  
		public void onProviderDisabled(String provider) {  
			updateView(null);  
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
	 * ʵʱ�����ı����� 
	 *  
	 * @param location 
	 */  
	private void updateView(Location location) {  
		if (location != null) {  
			sendHeart();
		} else {  
			;  
		}  
	}  
	/**
	 * û�õ�
	 * @return
	 */
	private PendingIntent send() {
		TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		final String te1  = tm.getLine1Number();//��ȡ��������
		String t="policeheartbt"+"@"+"telno="+te1+"&"+"Longitude="+String.valueOf(((Location) locationListener).getLongitude())+"&"+"Latitude="+String.valueOf(((Location) locationListener).getLatitude());	        	

		String target="http://111.202.78.206:60008";
		HttpClient httpclient=new DefaultHttpClient();
		HttpPost httpRequest=new HttpPost(target+"/"+t);
		try{

			HttpResponse httpResponse=httpclient.execute(httpRequest);
			if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				result=EntityUtils.toString(httpResponse.getEntity());
			}else{result="";}
		}catch(UnsupportedEncodingException e1){e1.printStackTrace();}
		catch(ClientProtocolException e){
			e.printStackTrace();
		}catch(IOException e)
		{e.printStackTrace();}
		return null;
	}

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



}
