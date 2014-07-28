package com.external.camera.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class Util {
    public static final String START_CONN = "start_conn";
    public static final String END = "&";
    public static final String DEVIDE = "messagedevide";

    public static final String UP = "up";
    public static final String DOWN = "down";
    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String OK = "ok";
    public static final String BACK = "back";
    public static final String ACTION_DOWN = "action_down";
    public static final String ACTION_UP = "action_up";
    public static final String ACTION_CANCEL = "action_cancel";
    public static final String ACTION_MOVE = "action_move";
    
    public static int SCREEN_HEIGHT = 0;
    public static int SCREEN_WIDTH = 0;
    public static int DOCOR_HEIGHT = 0;
    public static int STATUSBAR_HEIGHT = 0;
    public static final String ORIENTATION_LANDSCAPE = "orientation_landscape";    
    public static final String ORIENTATION_PORTRAIT = "orientation_portrait";    
    public static final String MODE_TOUCH = "mode_touch";
    public static final String MODE_KEYBOARD = "mode_keyboard";

    public static final String SENSOR_MODE = "sensormode";

    public static Point SCREEN_SIZE = new Point();
    
    private static final boolean LOGEV = true;

    public static void initScreenRect(Context context) {
    	WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    	Display display = manager.getDefaultDisplay();
		display.getRealSize(SCREEN_SIZE);
		
		if (SCREEN_SIZE.x > SCREEN_SIZE.y) {
			int x = SCREEN_SIZE.x;
			SCREEN_SIZE.x = SCREEN_SIZE.y;
			SCREEN_SIZE.y = x;
		}
		
		SCREEN_WIDTH = SCREEN_SIZE.x;
		SCREEN_HEIGHT = SCREEN_SIZE.y;
    } 
    
    public static void logd(Class<?> class1, String log) {
    	if (LOGEV) {
    		Log.d(class1.getName(), log);
    	}
    }
    
    public static void logi(Class<?> class1, String log) {
    	if (LOGEV) {
    		Log.i(class1.getName(), log);
    	}
    }
    
    public static void logv(Class<?> class1, String log) {
    	if (LOGEV) {
    		Log.v(class1.getName(), log);
    	}
    }
    
    public static void loge(Class<?> class1, String log) {
    	if (LOGEV) {
    		Log.e(class1.getName(), log);
    	}
    }
}

