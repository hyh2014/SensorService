package com.external.cameraService;

import android.app.Application;
import android.content.Intent;

public class CameraApplication extends Application {
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, SocketService.class));
    }
}
