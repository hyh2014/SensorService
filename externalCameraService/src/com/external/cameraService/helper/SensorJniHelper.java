/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.external.cameraService.helper;

public class SensorJniHelper {
    static {
        System.loadLibrary("sensorjni_jni");
    }
  //  public native int sendSensorEventRemote(int sensorType,int x, int y, int z, int accuracy,int timeStamp); 
    public native int sendSensorDataRemote(int sensorType,float values[], int accuracy,int timeStamp); 
}

