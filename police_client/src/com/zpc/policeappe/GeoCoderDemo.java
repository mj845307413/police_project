package com.zpc.policeappe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

/**
 * ��demo����չʾ��ν��е�������������õ�ַ�������꣩����������������������������ַ��
 */
public class GeoCoderDemo extends Activity implements
OnGetGeoCoderResultListener {
	GeoCoder mSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	BaiduMap mBaiduMap = null;
	MapView mMapView = null;
	private double latitude;
	private double longtitude;
	private BitmapDescriptor bA;
	private BitmapDescriptor bB;
	private LocationClient mLocClient;
	private MyLocationListenner myListener=new MyLocationListenner();
	public boolean isFirstLoc=true;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_geocoder);
		//CharSequence titleLable = "������빦��";
		//setTitle(titleLable);;
		Intent intent=getIntent();
		String[] infos=intent.getStringArrayExtra("info");
		longtitude=Double.parseDouble(infos[1]);
		latitude=Double.parseDouble(infos[2]);
		// ��ͼ��ʼ��
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		bA=BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
		bB=BitmapDescriptorFactory.fromResource(R.drawable.icon_markb);
		// ��ʼ������ģ�飬ע���¼�����
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		LatLng ptCenter = new LatLng(latitude,longtitude);
		//LatLng latLng=new LatLng(22, 22);
		ReverseGeoCodeOption codeOption=new ReverseGeoCodeOption();
		codeOption.location(ptCenter);
		// ��Geo����
		mSearch.reverseGeoCode(codeOption);
		// ��ͼ��ʼ��
		// ������λͼ��
		mBaiduMap.setMyLocationEnabled(true);
		// ��λ��ʼ��
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}
	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mLocClient.stop();
		mMapView.onDestroy();
		mSearch.destroy();
		super.onDestroy();
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(GeoCoderDemo.this, "��Ǹ��δ���ҵ����", Toast.LENGTH_LONG)
			.show();
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		Toast.makeText(GeoCoderDemo.this, result.getAddress(),
				Toast.LENGTH_LONG).show();
	}
	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub

	}
	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null) {
				return;
			}
			MyLocationData locData = new MyLocationData.Builder()
			.accuracy(location.getRadius())
			// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
			.direction(100).latitude(location.getLatitude())
			.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}
	}
}
