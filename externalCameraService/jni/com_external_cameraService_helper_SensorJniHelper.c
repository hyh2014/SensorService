
#include <jni.h>
#include <utils/Log.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <errno.h>

/*
#ifndef LOGE
#define LOGE ALOGE
#endif
*/

//porting from java : sensor.java
#define TYPE_ACCELEROMETER 	1
#define TYPE_MAGNETIC_FIELD 	2
#define TYPE_ORIENTATION 		3
#define TYPE_GYROSCOPE 		4
#define TYPE_GRAVITY 			9


#define MAX_VSENSOR_NUM    2      //surport accel  and gravity sensor
#define MAX_SENSOR_DEVICE_NAME    100
#define ACCEL_SENSOR_DATA_DEVICE "/dev/input/event1"
#define GRAVITY_SENSOR_DATA_DEVICE "/dev/input/event2"
#define MAX_SENSOR_VALUES_LEN    20
#define SENSOR_TRANSFER_PROPORTION 1000000

 

static int s_accel_fd = -1;

// from <linux/input.h>
struct input_event {
    struct timeval time;
    __u16 type;
    __u16 code;
    __s32 value;
};

struct SensorInfo{
	int type;
	int fd;
	int maxValueNum;
	char* sensorName;
};


static int s_iValuesBuffer[MAX_SENSOR_VALUES_LEN];
static struct SensorInfo s_sensorsInfo[] =
{
	{
		TYPE_ACCELEROMETER,
		-1,
		3,	
		ACCEL_SENSOR_DATA_DEVICE
	},

	{
		TYPE_GRAVITY,
		-1,
		3,
		GRAVITY_SENSOR_DATA_DEVICE			
	}
};




int sendSensorData(struct SensorInfo *pSensorInfo, int *pValues, int valueNum)
{
	struct input_event event;
	int ret = 0;
	int i = 0;
	int fd = 0;
	const int aiCodes[]={0,1,2};

	if(NULL == pSensorInfo)
	{
		return -1;
	}
	
	fd = pSensorInfo->fd;
	if( fd < 0)
	{
		 ALOGE( "SensorJni:sendSensorData: device %s not opened \n", pSensorInfo->sensorName);
		return -1;
	}
	
	if(valueNum <= 0 || valueNum > (sizeof(aiCodes)/sizeof(int)) )
	{
		ALOGE( "SensorJni:sendSensorData: %d values, too many values to send for device %s \n",valueNum,  pSensorInfo->sensorName);
		return -1;
	}

	for(i=0; i < valueNum; i++)
	{
	    memset(&event, 0, sizeof(event));
	    event.type = 3;
	    event.code = aiCodes[i];
	    event.value = *pValues++;
	    ALOGE("SensorJni:sendSensorData write sensor event , type=%d code=%d , value=%d to device %s \n",event.type,event.code,event.value,pSensorInfo->sensorName);
	    ret = write(fd, &event, sizeof(event));
	    if(ret < sizeof(event))
	    {
	        ALOGE( "SensorJni:write event failed, %s\n", strerror(errno));
	        return -1;
	    }
	}

	//input 设备最后需要发送全0 的sync  事件
	memset(&event, 0, sizeof(event));
	ALOGE("SensorJni:sendSensorData write sensor event to %s, type=%d code=%d , value=%d",pSensorInfo->sensorName,event.type,event.code,event.value);
	ret = write(fd, &event, sizeof(event));
	if(ret < sizeof(event)) {
	    ALOGE( "SensorJni:write event failed, %s\n", strerror(errno));
	    return -1;
	}
	
    return 0;
}



int openDevice(char * deviceName)
{
	int fd = -1;
	if(NULL == deviceName)
	{
		return -1;
	}
	
	fd = open(deviceName, O_RDWR);
	if(fd < 0)
	{
		ALOGE( "SensorJni:could not open %s, %s\n", deviceName, strerror(errno));
		return -1;
	}
	return fd;
}

int initSensorDevice()
{
	int sensorNum = -1;
	int i = 0;
	int fd = -1;
	int deviceFailedNum = 0;
	sensorNum = sizeof(s_sensorsInfo)/sizeof(struct SensorInfo);
	for(i=0; i < sensorNum ; i++)
	{
		 fd = openDevice(s_sensorsInfo[i].sensorName);	
		 if(fd < 0 )
	 	{
	 		deviceFailedNum++;
	 	}
		 s_sensorsInfo[i].fd = fd;
	}

	//IF all devices open failed ,then return failed
	if(deviceFailedNum == i)
	{
		return -1;
	}
	return 0;
}

struct SensorInfo * getSensorInfo(int sensorType)
{
	int i = 0;
	int sensorNum = sizeof(s_sensorsInfo)/sizeof(struct SensorInfo);
	for( i = 0; i < sensorNum; i++)
	{
		if(s_sensorsInfo[i].type == sensorType)
		{
			return &(s_sensorsInfo[i]);
		}
	}

	ALOGE("SensorJni: getSensorInfo: unsupport sensor type:  %d \n", sensorType);
	return NULL;	
}


void transferSensorData(float *pfValues,int *piValues,int valuesNum)
{
	int i = 0;
	for(i=0; i < valuesNum; i++)
	{
		*piValues++ = (int)(*pfValues++ * SENSOR_TRANSFER_PROPORTION);
	}

	return ;
}

void DebugPrintReceivedData(int sensorType, float *pValues, int valueNum)
{
	int i = 0;
	
	ALOGE("SensorJni: get Sensor Data: sensorType=%d , valueNum=%d ,  values : \n", sensorType,valueNum);
	for(i=0; i < valueNum; i++)
	{
		ALOGE("SensorJni: values[%d]=%f  ", i,*pValues++);
	}

	return;
	
}

/*
 * Class:     com_external_cameraService_helper_SensorJniHelper
 * Method:    sendSensorDataRemote
 * Signature: (I[FII)I
 */
JNIEXPORT jint JNICALL Java_com_external_cameraService_helper_SensorJniHelper_sendSensorDataRemote
  (JNIEnv *env, jobject obj, jint sensorType, jfloatArray values, jint accuracy, jint timeStamp)
{
	jfloat *pValues = NULL;
	int valueNum = 0;
	struct SensorInfo *pSensorInfo  = NULL;
	int ret = 0;

	//ALOGE("SensorJni:  sendSensorDataRemote enter,sensorType=%d!! \n", sensorType);
	valueNum = (*env)->GetArrayLength(env,values);
	if( valueNum > MAX_SENSOR_VALUES_LEN )
	{
		ALOGE("SensorJni: ERROR: too many values=%d, out of memory!! \n", valueNum);
		return -1;
		
	}

	//ALOGE("SensorJni: valueNum=%d!! \n", valueNum);
	memset(s_iValuesBuffer,0,sizeof(s_iValuesBuffer));
	pValues = (*env)->GetFloatArrayElements(env,values,JNI_FALSE);
	DebugPrintReceivedData(sensorType,pValues,valueNum); 

	//ALOGE("SensorJni: transferSensorData !! \n");
	transferSensorData(pValues,s_iValuesBuffer,valueNum);
	(*env)->ReleaseFloatArrayElements(env,values,pValues,JNI_ABORT);

	//ALOGE("SensorJni: getSensorInfo !! \n");
	pSensorInfo = getSensorInfo(sensorType);
	if(NULL == pSensorInfo)
	{
		ALOGE("SensorJni: ERROR: cannot find sensorInfo:  %d \n", sensorType);
		return -1;
	}

	//ALOGE("SensorJni: sendSensorData !! \n");
	ret = sendSensorData(pSensorInfo,s_iValuesBuffer,valueNum);
	if(ret < 0 )
	{
		ALOGE("SensorJni:  failed to write sensor data to device:  %s  \n", pSensorInfo->sensorName);
	}
	//ALOGE("SensorJni: sendSensorDataRemote exit  !! \n"); 
	return ret;
		
}




/* This function will be call when the library first be load.
* You can do some init in the libray. return which version jni it support.
*/
jint JNI_OnLoad(JavaVM* vm, void* reserved) 
{

    JNIEnv* env = NULL;
    jint result = -1;
	int ret = -1;

    if ((*vm)->GetEnv(vm,(void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		ALOGE("SensorJni: ERROR: GetEnv failed\n");
		return result;
	}

	ret = initSensorDevice();
	if(ret < 0 )
	{
		ALOGE("SensorJni: init all sensor failed \n");
		return -1;
	}
	memset(s_iValuesBuffer,0,sizeof(s_iValuesBuffer));

    /* success -- return valid version number */
    return JNI_VERSION_1_4;

}

