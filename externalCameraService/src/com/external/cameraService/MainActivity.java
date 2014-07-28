package com.external.cameraService;

import com.external.camera.utils.Util;

import android.app.Activity;
import android.os.Bundle;
import com.external.cameraService.helper.SensorJniHelper;
import android.util.Log;


/*
#define TYPE_ACCELEROMETER 	1
#define TYPE_MAGNETIC_FIELD 	2
#define TYPE_ORIENTATION 		3
#define TYPE_GYROSCOPE 		4
#define TYPE_GRAVITY 			9

*/

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.initScreenRect(this);
/*
	  float values[]={1.0f, -0.9f, 89.333f};
	  int ret = 0;
	  final String TAG = "TestHelper";


	  Log.i(TAG,"test send accel sensor data");	  
	  //sensor accel
        SensorJniHelper sensorHelper = new SensorJniHelper();
	 ret=sensorHelper.sendSensorDataRemote(1,  values, 0, 0);
	 if(ret < 0)
 	{
 		Log.e(TAG, "send accelermeter error \n");
 	}


	 Log.i(TAG," send accel sensor ok, test send gravity now");	

	 //sensor gravity
	 values[0] = 90.0f;
	 values[1] = -0.9f;
	 values[2] = -100.00f;



	 ret=sensorHelper.sendSensorDataRemote(9,  values, 0, 0);
	  if(ret < 0)
	 {
	 	Log.e("TestHelper", "send gravity  error \n");
	 }

	Log.i(TAG," send gravity sensor ok, test send magnetic \n");	
	 //sensor TYPE_MAGNETIC_FIELD: unsurpport sensor type
         ret=sensorHelper.sendSensorDataRemote(4,  values, 0, 0);
	  if(ret < 0)
	 {
	 	Log.e("TestHelper", "send accelermeter error \n");
	 }
 
      Log.i(TAG," send magnetic sensor ok,finish \n");	
*/    
		
        finish();
    }
}
