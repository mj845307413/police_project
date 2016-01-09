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

import android.R.integer;
import android.app.Activity;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//import com.baidu.baidulocationdemo.LocationApplication.MyLocationListener;�����ܳ���������
//�����õ�λ�����ѹ��ܣ���Ҫimport����

public class SecondActivity extends Activity {

	// ����LocationManager����
	LocationManager locManager;
	// �����������е�EditText���
	EditText show;
	private LocationManager lm;  
	private static final String TAG = "GpsActivity";
	private static final int EXIT = 0;
	private static final int HEART = 1;
	private static final int ALARM = 2;
	String result="";  
	private String te1;
	@Override


	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_information);
		show = (EditText) findViewById(R.id.show);
		//Intent intent=getIntent();
		//ExitApplication.getInstance().addActivity(this); 
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		Button MainActivityback=(android.widget.Button)findViewById(R.id.button4);
		Button Ready=(android.widget.Button)findViewById(R.id.button1);
		TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		te1  = tm.getLine1Number();//��ȡ��������
		if(te1==null||te1.equals("")){
			te1 = "+8618210575490";
		}
		Ready.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//sendHeart();
				handler2.sendEmptyMessage(HEART);
				//AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
				//��������ִ�мƻ�
				//am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, 1*1000, sendHeart());//��firstTime�ſ�ʼִ�У�ÿ��1����ִ��
				
				while(result!=""){
					String a[]=result.split("&");
					Intent MainIntent=new Intent(SecondActivity.this,ThirdActivity.class);
					MainIntent.putExtra("telno", a[0]);
					MainIntent.putExtra("longitude", a[1]);
					MainIntent.putExtra("latitude", a[2]);
					startActivity(MainIntent);
				};

			}
		});
		MainActivityback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				send2();
			}
			private Socket socket;
			private DataOutputStream dos;
			private DataInputStream dis;

			private void send2(){
				Thread thread=new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							socket=new Socket();
							socket.connect(new InetSocketAddress(Utils.SERVER_IP
									,Utils.SERVER_PORT), 5000);
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							if (socket.isConnected()) {
								dos = new DataOutputStream(socket.getOutputStream());
								dis = new DataInputStream(socket.getInputStream());
								String sendString = "policelogin"+"@"+"username="+te1+"&"+"password="+"123456"+"&"+"status="+String.valueOf(11);
								Log.i("majun","sendString:"+sendString );
								byte[] ScmHeadbt = sendString.getBytes("UTF8");
								dos.write(ScmHeadbt, 0, ScmHeadbt.length);

								InputStream in = socket.getInputStream();

								InputStreamReader read = new InputStreamReader(new BufferedInputStream(in));
								char[] cbuf = new char[1024];
								Arrays.fill(cbuf, '\0');
								int len = read.read(cbuf, 0, 1024);
								StringBuilder sb = new StringBuilder(1024);
								sb.append(cbuf, 0, len);
								//							byte[] ScmBytes = new byte[1024];
								//							dis.read(ScmBytes, 0, ScmBytes.length);
								String responseMsg=sb.toString();
								Log.i("majun", responseMsg);
								socket.close();
								dos.close();
								dis.close();
							}
						}catch(Exception exception){
							exception.printStackTrace();
						}finally{
							handler2.sendEmptyMessage(EXIT);
						}
					}
				});
				thread.start();
			}

			private void send1() {

				String t="policelogin"+"@"+"username="+te1+"&"+"password="+te1+"&"+"status="+String.valueOf(2);
				String urlStr = "http://111.202.78.206:60008";
				HttpPost request = new HttpPost(urlStr+t);  
				HttpClient httpclient=new DefaultHttpClient();

				try{
					HttpResponse httpResponse=httpclient.execute(request);}catch(UnsupportedEncodingException e1){e1.printStackTrace();}
				catch(ClientProtocolException e){
					e.printStackTrace();
				}catch(IOException e)
				{e.printStackTrace();}
			}

		});

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
	}  

	Handler handler2 = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
			  super.handleMessage(msg);
			  switch (msg.what) {
			case EXIT:
				java.lang.System.exit(0);
				break;
			case HEART:
				sendHeart();
				handler2.sendEmptyMessageDelayed(HEART, 1000);
				break;
			case ALARM:
				String info = (String) msg.obj;
				if(info != null && !info.equals("")){
					Log.i("majun","alarm_info:"+info);
					String[] infos = info.split("&");
					Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
					intent.putExtra("info", infos);
					startActivity(intent);
				}
				break;
			default:
				break;
			}
		  }
	};

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
	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;  

	/** 
	 * ʵʱ�����ı����� 
	 *  
	 * @param location 
	 */  
	private void updateView(Location location) {  
		if (location != null) {  
			sendHeart();
			show.setText("�豸λ����Ϣ\n\n���ȣ�");  
			show.append(String.valueOf(location.getLongitude()));  
			show.append("\nγ�ȣ�");  
			show.append(String.valueOf(location.getLatitude()));  
		} else {  
			// ���EditText����  
			show.getEditableText().clear();  
		}  
	}  

	private PendingIntent sendHeart(){
		new Thread(new Runnable() {
			
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
						dos = new DataOutputStream(socket.getOutputStream());
						dis = new DataInputStream(socket.getInputStream());
						// Ϊ��ȡ����λ����Ϣʱ���ò�ѯ����  
						String bestProvider = lm.getBestProvider(getCriteria(), true);  
						// ��ȡλ����Ϣ  
						// ��������ò�ѯҪ��getLastKnownLocation�������˵Ĳ���ΪLocationManager.GPS_PROVIDER  
						Location location = lm.getLastKnownLocation(bestProvider);  
						String sendString="policeheartbt"+"@"+"telno="+te1+"&"+"Longitude="+String.valueOf(location.getLongitude())+"&"+"Latitude="+String.valueOf(location.getLatitude())+"&"+"status="+String.valueOf(1);	        	
						byte[] ScmHeadbt = sendString.getBytes("UTF8");
						Log.i("majun", "length:"+ScmHeadbt.length+"String.valueOf(((Location) locationListener).getLongitude())"+String.valueOf(location.getLongitude()));
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
						Message message = new Message();
						message.what =ALARM ;
						message.obj=responseMsg;
						handler2.sendMessage(message);
					}
				}catch(Exception exception){
					exception.printStackTrace();
				}
			}
		}).start();
		return null;
	}

	/**
	 * 
	 * û�õ�
	 * @return
	 */
	private PendingIntent send() {
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







