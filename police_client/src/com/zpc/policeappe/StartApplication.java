package com.zpc.policeappe;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class StartApplication extends Application {
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		SDKInitializer.initialize(getApplicationContext());
		super.onCreate();
	}
}
