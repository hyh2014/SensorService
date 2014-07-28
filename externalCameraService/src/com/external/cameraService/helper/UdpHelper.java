package com.external.cameraService.helper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.external.cameraService.inter.ServiceInterface;

public class UdpHelper {
    public Boolean mIsThreadDisable = false;
    private static WifiManager.MulticastLock sLock;
    InetAddress mInetAddress;

    private static final int BYTE_SIZE = 1024 * 64;
    private static final Integer sPort = 8800;
    private static String sIP = "";
    
    private DatagramSocket mDatagramSocket;
    private DatagramPacket mDatagramPacket;
    private MessageDeliver mDeliver;

     
    public UdpHelper(WifiManager manager, ServiceInterface listener, Context context) {
        if (sLock == null) {
        	sLock= manager.createMulticastLock("UDPwifi");
        } else {
            try {
            	sLock.release();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
        mDeliver = new MessageDeliver(listener);
    }
    
    public void createSocket() {
        byte[] message = new byte[BYTE_SIZE];
        try {
            mDatagramSocket = new DatagramSocket(sPort);
            mDatagramSocket.setBroadcast(true);
            mDatagramPacket = new DatagramPacket(message, message.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void destroySocket() {
        mDatagramSocket.close();
        mDatagramSocket = null;
        mDatagramPacket = null;
    }
    
    public void StartListen()  {
        try {
            while (!mIsThreadDisable) {
            	sLock.acquire();
                mDatagramSocket.receive(mDatagramPacket);

                String string = new String(mDatagramPacket.getData()).trim();
                string = string.substring(0, mDatagramPacket.getLength());
                String strMsg = URLDecoder.decode(string, "utf-8");
                sLock.release();
                mDeliver.decodeMessage(strMsg);
            }
        } catch (Exception e) {//IOException
            e.printStackTrace();
        }
    }
    
   

    public static void setIp(String ip) {
        sIP = ip;
        Log.e("UdpHelper", "ip = " + ip);
    }
    
    public static String send(String message) {
        try {
            message = (message == null ? "hello" : 
                URLEncoder.encode(message, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DatagramSocket s = null;
        try {
            s = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress local = null;
        try {
            if (sIP == null || sIP.isEmpty()) {
                return null;
            } else {
                local = InetAddress.getByName(sIP);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int msg_length = message.length();
        byte[] messageByte = message.getBytes();
        DatagramPacket p = new DatagramPacket(messageByte, msg_length, local,
                sPort);
        try {
            s.send(p);
            s.close();           
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sIP;
    }
    
    public static String GetHostIp() {
        try { 
            for (Enumeration<NetworkInterface> en = NetworkInterface 
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); 
                        ipAddr.hasMoreElements();) {
                    InetAddress inetAddress = ipAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                     }
                 }
             }
        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (Exception e) { 
            e.printStackTrace();
        }
         return null;
     }
    


}
