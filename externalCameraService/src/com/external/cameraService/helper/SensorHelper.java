package com.external.cameraService.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;



public class SensorHelper {
    private static ArrayList<Object> listBack = new ArrayList<Object> ();
    private static int iCount = 0;
	
    public static void disposSensor(SensorEvent event) {
		try {
	/*
			Class<?> sensorManager = Class.forName("android.hardware.SystemSensorManager");
			
			Field expand = sensorManager.getDeclaredField("sListeners");
			expand.setAccessible(true);
			ArrayList<Object> list = (ArrayList<Object>) expand.get(sensorManager);
			Log.e("SensorHelper", "list.size() = " + list.size() + list);
			
			if (!list.isEmpty()) {
				listBack.clear();
			}
			
			if (listBack.isEmpty()) {
				listBack.addAll(list);
			}
			//list.clear();

			Log.e("SensorHelper", "listBack.size() = " + listBack.size());
			if (event == null) {
				return;
			}
			
			Class<?> listenerDelegate = Class.forName("android.hardware.SystemSensorManager$ListenerDelegate");
	*/		
			Sensor sensor = event.sensor;
	             float[] floats = event.values;
	             long[] timestamp = {event.timestamp};
	             int accuracy = event.accuracy;
			int type =sensor.getType();
			
			int x = (int)(floats[0]*1000000);
			int y = (int)(floats[1]*1000000);
			int z = (int)(floats[1]*1000000);
			int ret = 0;
			SensorJniHelper sensorJni = new SensorJniHelper();
/*
			iCount++;
			if(iCount > 10)
			{
				return;
			}
*/
			Log.e("SensorHelper", "x="+floats[0]+" y="+floats[1]+" z="+floats[2]);
			ret = sensorJni.sendSensorDataRemote(type, floats, 0, 0); 
			if(ret < 0)
			{
				Log.e("SensorHelper", "sendSensor data error ret="+ret);
				return;
			}
			
			

			/*
			
			Class<?> [] params = {sensor.getClass(), 
					floats.getClass(), timestamp.getClass(), int.class};

			Method method = listenerDelegate.getDeclaredMethod("onSensorChangedLocked",
					params[0], params[1], params[2], params[3]);
			method.setAccessible(true);

			for (int i = 0; i < listBack.size(); i++) {
				method.invoke(listBack.get(i), sensor, floats, timestamp, accuracy);
			}	
			*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
