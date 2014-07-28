package com.external.camera.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import com.external.cameraService.R;
import com.external.cameraService.helper.MessageDeliver;
import com.external.cameraService.inter.ServiceInterface;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class BluetoothHelper {
	
	private static final String sTag = "BusniessBluetooth";  
    private BluetoothAdapter mBluetoothAdapter; 

    private InputStream mInputStream;  
    private OutputStream mOutputStream;  
    private int mState;  
    private int mStateConnected = 0;  
    private int mStateDisConnect = 1;  
    private boolean mIsNormalClose = false;  
    private Message mMessage = new Message();  
    private PortListenThread mPortListenThread;  
    
    private MessageDeliver mDeliver;
    Toast mToast;
    
    public BluetoothHelper() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  
    }
    
    private static final String SNAME = "BluetoothChat";  
    private static final UUID SUUID = UUID  
            .fromString("00001101-0000-1000-8000-00805F9B34FB");  
    
	public void CreatePortListen(ServiceInterface listener, Context context) {  
        try {    
            if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {  
                BluetoothServerSocket BluetoothServerSocket = mBluetoothAdapter  
                        .listenUsingRfcommWithServiceRecord(SNAME, SUUID);  
  
                if (mPortListenThread == null) {  
                    mPortListenThread = new PortListenThread(  
                            BluetoothServerSocket);
                    mPortListenThread.start();  
                }
                Log.e(sTag, "CreatePortListen");
                mDeliver = new MessageDeliver(listener);
            } else {
            	if (mToast == null) {
            		mToast = Toast.makeText(context, R.string.bluetooth_disable, Toast.LENGTH_SHORT);
            	}
            }
        } catch (Exception e) {  
        	e.printStackTrace();
        }  
    }  
  
    public class PortListenThread extends Thread {  
  
        private BluetoothServerSocket mBluetoothServerSocket;  
  
        public PortListenThread(BluetoothServerSocket pBluetoothServerSocket) {  
            mBluetoothServerSocket = pBluetoothServerSocket;  
        }  
  
        @Override  
        public void run() {  
            try {
                Log.e(sTag, "run = " + "before");

                BluetoothSocket bluetoothSocket = mBluetoothServerSocket  
                        .accept();  
                Log.e(sTag, "run = " + "after");

                mState = mStateConnected;  
                while (mState == mStateConnected) {  
                    Log.e(sTag, "run = " + "after1");

                    mInputStream = bluetoothSocket.getInputStream();  
                    ReceiverData();  
                }  
            } catch (Exception e) {  
                Log.i(sTag, e.getMessage());  
                if (!mBluetoothAdapter.isEnabled()) {  
                    //mHandler.sendMessage(mMessage);  
                }  
            }  
        }  
  
        public void Close() {  
            try {  
                mBluetoothServerSocket.close();  
                if (mInputStream != null) {  
                    mInputStream.close();  
                }  
            } catch (Exception e) {  
                Log.i(sTag, e.getMessage());  
            }  
        }  
    }  
  
    public void ReceiverData() {  
        try {
            byte[] bytes = new byte[1024 * 64];  
            mInputStream.read(bytes);
            String msg = new String(bytes, 0, bytes.length).trim(); 
            Log.e(sTag, "msg = " + msg);
            mDeliver.decodeMessage(msg);
        } catch (Exception e) {  
            e.printStackTrace();  
            //if (!mIsNormalClose) {  
            Close(true);  
            //}  
        }  
    }  
    
    public void SendData(String pData) {
        try {  
            mOutputStream.write(pData.getBytes());  
        } catch (Exception e) {  
            Log.i(sTag, e.getMessage());  
        }  
    }  
  
    public void Close(boolean pIsNormalClose) {  
        mIsNormalClose = pIsNormalClose;  
        mState = mStateDisConnect;
        if (mPortListenThread != null) {  
            mPortListenThread.Close();  
            mPortListenThread = null;  
        }  
    }
    
    public void findBluetoothDevice() {
	    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
	    Log.e(sTag, "pairedDevices.size = " + pairedDevices.size());
	    if (pairedDevices.size() > 1) {
	    	return;
	    } else {
	    	for (BluetoothDevice device : pairedDevices) {
	    		try {
					BluetoothSocket socket = device.createRfcommSocketToServiceRecord(SUUID);
					socket.connect();
					mOutputStream = socket.getOutputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	} 

	    }  
    }
}
