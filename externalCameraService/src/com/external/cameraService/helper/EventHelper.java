package com.external.cameraService.helper;

import com.external.camera.utils.Util;
import com.external.cameraService.inter.ServiceInterface;
import android.app.Instrumentation;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.util.Log;

public class EventHelper {
	
	private static final HandlerThread mWorkerThread;
    public static final Handler mWorker;
    static {
        mWorkerThread = new HandlerThread("launcher-loader");
        mWorkerThread.start();
        mWorker = new Handler(mWorkerThread.getLooper());
    }
    
	public static void sendUpDownEvent(float left, float top) {
        sendPointerSync(MotionEvent.ACTION_DOWN, left, top);
        sendPointerSync(MotionEvent.ACTION_UP, left, top);
	}
	
    public static void sendPointerSync(int action, float left, float top) {
    	long time = SystemClock.uptimeMillis();
    	final MotionEvent event = MotionEvent.obtain(time, time, action, left, top, 0);
        try {
            mWorker.post(new Runnable() {
                
                @Override
                public void run() {
                    Instrumentation instrumentation = new Instrumentation();
                    instrumentation.sendPointerSync(event);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static int getMotionEventAction(String string, DepartString departString) {
    	subString(string, departString);
    	String motionEventCode = departString.start;
        int motionEventAction = -1;
        if (Util.ACTION_DOWN.equals(motionEventCode)) {
            motionEventAction = MotionEvent.ACTION_DOWN;
        } else if (Util.ACTION_MOVE.equals(motionEventCode)){
            motionEventAction = MotionEvent.ACTION_MOVE;
        } else if (Util.ACTION_UP.equals(motionEventCode)) {
            motionEventAction = MotionEvent.ACTION_UP;
        } else if (Util.ACTION_CANCEL.equals(motionEventCode)){
            motionEventAction = MotionEvent.ACTION_CANCEL;                    
        }
        
        return motionEventAction;
    }
    
    public static boolean getPosition(DepartString departString, float[] position, float scale,
    		ServiceInterface listener, boolean isTouchMode) {
      
        try {
            subString(departString.end, departString);
            position[0] = Float.parseFloat(departString.start);
            subString(departString.end, departString);
            position[1] = Float.parseFloat(departString.start); 
        } catch (Exception e) {
            e.printStackTrace();
            position[0] = position[1] = 0;
        }
        
        position[0] = position[0] * scale;
		position[1] = position[1] * scale;
        if (listener != null && !isTouchMode) {
        	View view = listener.getFocusView();
    		position[0] = position[0] + view.getLeft();
    		position[1] = position[1] + view.getTop();
    		Log.e("EventHelper", "position[0] 11= " + position[0]);
    		Log.e("EventHelper", "position[1] 11= " + position[1]);
        } else {
        	return false;
        }
		Log.e("EventHelper", "position[0] 11= " + position[0]);

        if (position[0] < 0) {
        	position[0] = 0;
        }
		Log.e("EventHelper", "position[0] 11= " + position[0]);

        if (position[1] < 0) {
        	position[1] = 0;
        }
        if (position[0] > Util.SCREEN_WIDTH) {
        	position[0] = Util.SCREEN_WIDTH;
        }
		Log.e("EventHelper", "position[0] 11= " + position[0]);

        if (position[1] > Util.SCREEN_HEIGHT) {
        	position[1] = Util.SCREEN_HEIGHT;
        }
        return true;
    }
    
    public static String subString(String string, DepartString departString) {
        int index = string.indexOf(Util.END);
        
        if (index == -1) {
            departString.start = "";
            departString.end = "";
            return "";
        }
        String start = string.substring(0, index);
        departString.start = start;
        departString.end = string.substring(index + Util.END.length());
        return start;
    }  
    
    public static int getKeyCode(String string) {
    	int keycode = -1;
    	if (Util.LEFT.equals(string)) {       
            keycode = KeyEvent.KEYCODE_DPAD_LEFT;
        } else if (Util.RIGHT.equals(string)){
            keycode = KeyEvent.KEYCODE_DPAD_RIGHT;
        } else if (Util.UP.equals(string)) {
            keycode = KeyEvent.KEYCODE_DPAD_UP;
        } else if (Util.BACK.equals(string)) {
            keycode = KeyEvent.KEYCODE_BACK;
        } else if (Util.DOWN.equals(string)){
            keycode = KeyEvent.KEYCODE_DPAD_DOWN;                    
        } 
    	return keycode;
    }
    
    public static void sendKeyEvent(final int keycode) {
        try {
            mWorker.post(new Runnable() {
                
                @Override
                public void run() {
                    Instrumentation instrumentation = new Instrumentation();
                    instrumentation.sendKeyDownUpSync(keycode);
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
