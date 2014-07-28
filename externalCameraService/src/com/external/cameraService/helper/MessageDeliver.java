package com.external.cameraService.helper;

import android.content.Context;
import android.util.Log;

import com.external.camera.data.SensorEventByte;
import com.external.camera.utils.Util;
import com.external.cameraService.inter.ServiceInterface;

public class MessageDeliver {
	
    private String mMode = Util.MODE_KEYBOARD;
    private static float sScale = 1.0f;
    private float[] mPosition = new float[2];
    private ServiceInterface mListener;
    
    public MessageDeliver(ServiceInterface listener) {
    	if (listener == null) {
    		throw (new IllegalArgumentException());
    	} else {
    		mListener = listener;
    	}
    }
    
    public void decodeMessage(String string) {
		String[] infos = string.split(Util.DEVIDE);
		for (int i = 0; i < infos.length; i++) {
	        Log.e("MessageDeliver", "infos[i] = " + infos[i]);

			if (infos[i] != null && !infos[i].equals("")) {
				decodeKeyEvent(infos[i]);
			}
		}
    }
    
	public void decodeKeyEvent(String string) {
        if (string == null || string.isEmpty()) {
            return;
        }
        Log.e("MessageDeliver", "string = " + string);

        DepartString departString = new DepartString();
        int index = string.indexOf(Util.END);
        if (string.startsWith(Util.SENSOR_MODE)) {
            Log.e("MessageDeliver", "SENSOR_MODE = " + string);
            SensorHelper.disposSensor(new SensorEventByte(string).mEvent);
            return;
        }
        Log.e("MessageDeliver", "index = " + index);

        if (-1 == index) {
            sendKeyEvent(string);
            return;
        } else {
        	sendMotionEvent(string, departString);
        }
    }
    
    private void sendMotionEvent(String string, DepartString departString) {
        int motionEventAction = EventHelper.getMotionEventAction(string, departString);
        EventHelper.getPosition(departString, mPosition, sScale, mListener, isTouchMode());
        Log.e("MessageDeliver", "mPosition[1] = " + mPosition[1]);

        mListener.refreshView(mPosition[0], mPosition[1]);
        Log.e("MessageDeliver", "mPosition[1] = " + mPosition[1]);

        EventHelper.sendPointerSync(motionEventAction, mPosition[0], mPosition[1]);
    }

    private boolean isTouchMode() {
    	if (Util.MODE_TOUCH.equals(mMode)) {
    		return true;
    	} else {
    		return false;
    	}
    }
 
    private void sendKeyEvent(String string) {
    	if (string == null) {
    		return;
    	}
        if (string.startsWith(Util.START_CONN)) {
            int width = Integer.valueOf(string.replace(Util.START_CONN, ""));
        	sScale = (float)Util.SCREEN_SIZE.x / (float)width;
        	Log.e("MessageDeliver", "sScale = " + sScale);
            return;
        }
        
        int keycode = EventHelper.getKeyCode(string);
        if (keycode != -1) {
        	EventHelper.sendKeyEvent(keycode);
        	return;
        }
        
        if (Util.OK.equals(string)) {
        	float left = mListener.getFocusView().getLeft();
        	float top = mListener.getFocusView().getTop();
        	EventHelper.sendUpDownEvent(left, top);
            return;
        }
        
        if (Util.MODE_KEYBOARD.equals(string) || Util.MODE_TOUCH.equals(string)) {
        	mMode = string;
        	return;
        }
        
        boolean portraitNeedChange = Util.ORIENTATION_PORTRAIT.equals(string) && Util.SCREEN_HEIGHT < Util.SCREEN_WIDTH;
        boolean landscapeNeedChange = Util.ORIENTATION_LANDSCAPE.equals(string) && Util.SCREEN_HEIGHT > Util.SCREEN_WIDTH;
        if (portraitNeedChange || landscapeNeedChange){
        	changeTwoNum();
        	return;
        }
    }
    
    private void changeTwoNum() {
    	int num = Util.SCREEN_HEIGHT;
    	Util.SCREEN_HEIGHT = Util.SCREEN_WIDTH;
    	Util.SCREEN_WIDTH = num;
    }
}
