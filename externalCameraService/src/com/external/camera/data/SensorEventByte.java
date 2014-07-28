package com.external.camera.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

import com.external.camera.utils.Util;

public class SensorEventByte {
	public SensorEvent mEvent;
	String mString = "";
	String[] mSensorPro = {"mName", "mVendor", "mVersion", "mHandle", 
			"mType", "mMaxRange", "mResolution", "mPower", "mMinDelay"};
	
	public SensorEventByte(SensorEvent event) {
		mEvent = event;
	}
	
	public Byte[] getByte() {
		Byte[] bytes = new Byte[1000];
		return bytes;
	}
	
	public SensorEventByte(String string) {
		try {
			mString = string;
			String[] infos = mString.split(Util.END);
			Sensor sensor = getSensor(infos);
			Log.e("SensorEventByte", "sensor = " + sensor);
			int index = 0;
			String value = "";
			String info = "";
			for (; index < infos.length; index++) {
				info = infos[index];
				if (info.contains("values")) {				
					value = info;
					break;
				}
			}
			String[] strValues = value.split("values");
			int length = strValues.length;
			mEvent = newSensorEvent(length - 1);
			mEvent.sensor = sensor;
			for (int i = 1; i < length; i++) {
				mEvent.values[i - 1] = Float.valueOf(strValues[i]);
			}
			index++;
			info = infos[index];
			if (info.contains("accuracy")) {

				info = info.replace("accuracy", "");
				mEvent.accuracy = Integer.valueOf(info);
			}
			
			index++;
			info = infos[index];
			if (info.contains("timestamp")) {
				info = info.replace("timestamp", "");
				mEvent.timestamp = Long.valueOf(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
			mEvent = null;
		}
		
	}
	
	public String toString() {
		Sensor sensor = mEvent.sensor;
		mString += sensorToString(sensor);
		mString += "values" + mEvent.values[0] + "values" + mEvent.values[1] + "values"
				+ mEvent.values[2] + Util.END;
		mString += "accuracy" + mEvent.accuracy + Util.END;
		mString += "timestamp" + mEvent.timestamp + Util.END;
		
		Log.e("SensorEventByte", "mString = " + mString);
		return mString;
	}
	
    public String sensorToString(Object object) {
		try {
			String str = "";
			Sensor sensor = (Sensor) object;
			Log.e("SensorEventByte", "" + sensor);
			Class<?> cSensor = object.getClass();
			for (int i = 0; i < mSensorPro.length; i++) {
				Field field = cSensor.getDeclaredField(mSensorPro[i]);
				field.setAccessible(true);
				Class fieldClass = field.getType();
				str += mSensorPro[i];
				if (float.class.equals(fieldClass)) {
					str += field.getFloat(object);
				} else if (int.class.equals(fieldClass)) {
					str += field.getInt(object);
				} else if (String.class.equals(fieldClass)) {
					str += field.get(object);
				}
				str += Util.END;				
			}
			Log.e("SensorEventByte", "str = " + str);		
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
    
    private SensorEvent newSensorEvent (int size) {
    	try {
			Class<?> cSensorEvent = SensorEvent.class;
			Constructor con = cSensorEvent.getDeclaredConstructor(int.class);
			con.setAccessible(true);
			return (SensorEvent)con.newInstance(size);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
    
    private Sensor getSensor(String[] infos) {
		try {
			//String str = "";
			Class<?> cSensor = Sensor.class;
			Constructor con = cSensor.getDeclaredConstructor();
			con.setAccessible(true);
			Sensor sensor = (Sensor) con.newInstance(); 

			for (int i = 0; i < mSensorPro.length; i++) {
				Field field = cSensor.getDeclaredField(mSensorPro[i]);;
				field.setAccessible(true);
				String info = infos[i];
				Log.e("SensorEventByte", "info = " + info);
				if (!info.contains(mSensorPro[i])) {
					return null;
				}
				info = info.replace(field.getName(), "");
				Log.e("SensorEventByte", "info = " + info);

				info = info.replace(Util.END, "");
				Log.e("SensorEventByte", "info = " + info);
				
				Class fieldClass = field.getType();
				if (float.class.equals(fieldClass)) {
					field.setFloat(sensor, Float.valueOf(info));
				} else if (int.class.equals(fieldClass)) {
					field.setInt(sensor, Integer.valueOf(info));
				} else if (String.class.equals(fieldClass)) {
					field.set(sensor, info);
				}
			}
			return sensor;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
