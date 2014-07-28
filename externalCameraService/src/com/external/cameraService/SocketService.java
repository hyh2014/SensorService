package com.external.cameraService;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import com.external.camera.bluetooth.BluetoothHelper;
import com.external.camera.utils.Util;
import com.external.cameraService.helper.SensorHelper;
import com.external.cameraService.helper.UdpHelper;
import com.external.cameraService.inter.ServiceInterface;

public class SocketService extends Service implements ServiceInterface {
    
    private Thread mThread;
    private UdpHelper mHelper;
    private BluetoothHelper mBluetoothHelper;
    private Context mContext = this;
    private static View sView;
    private WindowManager mManager;
    private static final int REFRESH_VIEW = 0;
    private float[] mPoint = new float[2];
  
    public static final int STATUS_BAR_DISABLE_HOME = 0x00200000;

    /**
     * @hide
     *
     * NOTE: This flag may only be used in subtreeSystemUiVisibility. It is masked
     * out of the public fields to keep the undefined bits out of the developer's way.
     *
     * Flag to hide only the back button. Don't use this
     * unless you're a special part of the system UI (i.e., setup wizard, keyguard).
     */
    public static final int STATUS_BAR_DISABLE_BACK = 0x00400000;
    
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case REFRESH_VIEW:
                mPoint = (float[]) msg.obj;
                moveIndicator(mPoint[0], mPoint[1]);
                break;
            default:
                break;
            }
        }
    };
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public int onStartCommand(Intent intent, int flags, int startId) {
    	SensorManager mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        try {
        	mSensorManager.registerListener(new SensorEventListener() {
				
				@Override
				public void onSensorChanged(SensorEvent event) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// TODO Auto-generated method stub
				}
			}, mSensor, SensorManager.SENSOR_DELAY_GAME); 
        } catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
        
        	new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//while (true) {
			        	SensorHelper.disposSensor(null);
					//}
				}
			}).start();
        
    	startListen();
        if (sView == null) {
        	showFloatWindow();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    
    public void onDestroy() {
        super.onDestroy();
        stopListen();
        if (mBluetoothHelper != null) {
        	mBluetoothHelper.Close(true);
        }
        Log.e("SocketService", "service destroy");
    }
    
    public void startListen() {
        if (mThread != null) {
            return;
        }
        Log.e("SocketService", "ddd");

        mThread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
            	//if (true) {
            		mBluetoothHelper = new BluetoothHelper();
            		mBluetoothHelper.CreatePortListen(SocketService.this, mContext);
            	//} else {
	                WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
	                mHelper = new UdpHelper(wifiManager, SocketService.this, mContext);
	                mHelper.createSocket();
	                mHelper.StartListen();
            	//}
            }
        });
        mThread.start();
    }
    
    public void stopListen() {
        mHelper.mIsThreadDisable = true;
        mHelper.destroySocket();
        mHelper = null;
        mThread = null;
    }
    
    private void showFloatWindow() {
        mManager = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        initView();
        try {
        	View view = initView();
            mManager.addView(view, initParams());
            int a = view.getWindowSystemUiVisibility();
            Rect rect = new Rect();
            view.getWindowVisibleDisplayFrame(rect);
            Log.e("SocketService", "a = " + a + "rect = " + rect.top
            		+ rect.bottom);
            Log.e("SocketService", "sView.getBottom() = " + sView.getX() +
            		sView.getTop() + " " + sView.getLeft() + 
            		"  " + sView.getRight());
            Util.DOCOR_HEIGHT = sView.getBottom();
            //Util.DOCOR_HEIGHT = sView.getBottom();
            Util.STATUSBAR_HEIGHT = Util.SCREEN_HEIGHT - Util.DOCOR_HEIGHT;
            //moveIndicator(0, 0);
        } catch (Exception e) {
            // TODO: handle exception
        	e.printStackTrace();
        }
    }
    
    private View initView() {
    	View view = null;
        if (view == null) {
            view = LayoutInflater.from(this).inflate(R.layout.window, null);
            sView = view.findViewById(R.id.indi);
            //sView.setBackgroundResource(R.drawable.indicator);
        }
        //view.sc
        return view;
    }
    
    public void moveIndicator(float x, float y) {
        /*LayoutParams params = (LayoutParams)sView.getLayoutParams();
        Log.e("SocketService", "y = " + y + "\nparams.y = " + params.y);

        params.x = (int) x;
        params.y = (int) y;*/
        //mManager.updateViewLayout(sView, params);  
        //sView.scrollTo((int)x, (int)y);
    	int x1 = (int) x;
    	int y1 = (int) y;
    	sView.layout(x1, y1, x1 + 50, y1 + 50);
    	sView.invalidate();
        Log.e("SocketService", "sView.getBottom() = " + sView.getBottom());

    }
    
    private LayoutParams initParams() {
        LayoutParams params = new LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.format = PixelFormat.RGBA_8888;// 设置图片格式，效果为背景透明

        params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL |
         LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE
         | LayoutParams.FLAG_FULLSCREEN;
        params.x = 50;
        params.y = 1000;
        params.gravity = Gravity.LEFT|Gravity.TOP;
        //params.width = 50;
        //params.height = 50;
        return params;
    }

    @Override
    public LayoutParams getViewParams() {
        // TODO Auto-generated method stub
        if (sView != null) {
            return (LayoutParams) sView.getLayoutParams();
        }
        LayoutParams params = new LayoutParams();
        params.x = 0;
        params.y = 0;
        return params; 
    }
    
    @Override
    public void refreshView(float x, float y) {
        // TODO Auto-generated method stub
        Message message = new Message();
        message.what = REFRESH_VIEW;
        mPoint[0] = x;
        mPoint[1] = y;
        Log.e("SocketService", "y = " + y + "\nmPoint[1] = " + mPoint[1]);

        message.obj = mPoint;
        if (mHandler.hasMessages(REFRESH_VIEW)) {
            //mHandler.removeMessages(REFRESH_VIEW);
        }
        mHandler.sendMessage(message);
    }

    @Override
    public View getFocusView() {
        // TODO Auto-generated method stub       
        return sView;
    }
    
    private void getStatusBarHeight(Context context) {
    	PackageManager packageManager = (PackageManager) context.getPackageManager();
    	try {
			ComponentName componentName = getComponentName();
			if (componentName == null) {
				return;
			}
    		PackageInfo packageInfo = packageManager.getPackageInfo(
    				componentName.getPackageName(), PackageManager.GET_ACTIVITIES);
			ActivityInfo[] activityInfos = packageInfo.activities;
			for (int i = 0; i < activityInfos.length; i++) {
				if (activityInfos[i].name.equals(componentName.getClassName())) {
					//ActivityInfo info = activityInfos[i];
					//info.
				}
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private ComponentName getComponentName() {
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		try {
			ActivityManager.RunningTaskInfo info = activityManager.getRunningTasks(1).get(0);
			ComponentName cName = info.topActivity;
			// Log.e("SocketService", cName.toString());
			return cName;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
}
