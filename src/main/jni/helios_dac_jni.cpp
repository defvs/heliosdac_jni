#include <jni.h>
#include "HeliosDac.h"  // Include the header file from the Helios DAC submodule
#include <iostream>

// Single instance of the HeliosDac class (make sure to guard access in a multi-threaded environment)
static HeliosDac heliosDacInstance;

// Define the JNIEXPORT methods for each native method in your Kotlin class

extern "C" JNIEXPORT jint JNICALL
Java_dev_defvs_heliosdac_HeliosDacNative_openDevices(JNIEnv* env, jobject obj) {
    return heliosDacInstance.OpenDevices();
}

extern "C" JNIEXPORT jint JNICALL
Java_dev_defvs_heliosdac_HeliosDacNative_closeDevices(JNIEnv* env, jobject obj) {
    return heliosDacInstance.CloseDevices();
}

extern "C" JNIEXPORT jint JNICALL
Java_dev_defvs_heliosdac_HeliosDacNative_getStatus(JNIEnv* env, jobject obj, jint devNum) {
    return heliosDacInstance.GetStatus(devNum);
}

extern "C" JNIEXPORT jint JNICALL
Java_dev_defvs_heliosdac_HeliosDacNative_getFirmwareVersion(JNIEnv* env, jobject obj, jint devNum) {
    return heliosDacInstance.GetFirmwareVersion(devNum);
}

extern "C" JNIEXPORT jint JNICALL
Java_dev_defvs_heliosdac_HeliosDacNative_getName(JNIEnv* env, jobject obj, jint devNum, jbyteArray name) {
    // Convert Java byte array to C++ char array
    jbyte* nameBuffer = env->GetByteArrayElements(name, NULL);
    jint result = heliosDacInstance.GetName(devNum, reinterpret_cast<char*>(nameBuffer));
    env->ReleaseByteArrayElements(name, nameBuffer, 0);  // Release the byte array back to Java
    return result;
}

extern "C" JNIEXPORT jint JNICALL
Java_dev_defvs_heliosdac_HeliosDacNative_setName(JNIEnv* env, jobject obj, jint devNum, jstring name) {
    // Convert Java string to C++ char array
    const char* nameStr = env->GetStringUTFChars(name, NULL);
    jint result = heliosDacInstance.SetName(devNum, const_cast<char*>(nameStr));
    env->ReleaseStringUTFChars(name, nameStr);  // Release the string back to Java
    return result;
}

extern "C" JNIEXPORT jint JNICALL
Java_dev_defvs_heliosdac_HeliosDacNative_writeFrame(JNIEnv* env, jobject obj, jint devNum, jint pps, jbyte flags, jobjectArray pointArray, jint numOfPoints) {
    if (pointArray == NULL || numOfPoints <= 0) {
        return HELIOS_ERROR_NULL_POINTS;
    }

    // Allocate a buffer to hold the frame data
    HeliosPoint* points = new HeliosPoint[numOfPoints];

    // Fill the buffer with point data from the Java array
    for (int i = 0; i < numOfPoints; i++) {
        jobject point = env->GetObjectArrayElement(pointArray, i);
        jclass pointClass = env->GetObjectClass(point);

        // Extract the fields from the Point class (assuming it has x, y, r, g, b, i as fields)
        jfieldID xField = env->GetFieldID(pointClass, "x", "S");  // 'S' is the type signature for short (16-bit)
        jfieldID yField = env->GetFieldID(pointClass, "y", "S");
        jfieldID rField = env->GetFieldID(pointClass, "r", "B");  // 'B' is the type signature for byte (8-bit)
        jfieldID gField = env->GetFieldID(pointClass, "g", "B");
        jfieldID bField = env->GetFieldID(pointClass, "b", "B");
        jfieldID iField = env->GetFieldID(pointClass, "i", "B");

        // Set the corresponding fields in the C++ HeliosPoint struct
        points[i].x = env->GetShortField(point, xField);
        points[i].y = env->GetShortField(point, yField);
        points[i].r = env->GetByteField(point, rField);
        points[i].g = env->GetByteField(point, gField);
        points[i].b = env->GetByteField(point, bField);
        points[i].i = env->GetByteField(point, iField);

        env->DeleteLocalRef(point);
        env->DeleteLocalRef(pointClass);
    }

    // Write the frame to the DAC
    jint result = heliosDacInstance.WriteFrame(devNum, pps, flags, points, numOfPoints);

    // Free the buffer
    delete[] points;
    return result;
}

extern "C" JNIEXPORT jint JNICALL
Java_dev_defvs_heliosdac_HeliosDacNative_stop(JNIEnv* env, jobject obj, jint devNum) {
    return heliosDacInstance.Stop(devNum);
}

extern "C" JNIEXPORT jint JNICALL
Java_dev_defvs_heliosdac_HeliosDacNative_setShutter(JNIEnv* env, jobject obj, jint devNum, jboolean level) {
    return heliosDacInstance.SetShutter(devNum, level);
}

extern "C" JNIEXPORT jint JNICALL
Java_dev_defvs_heliosdac_HeliosDacNative_setLibusbDebugLogLevel(JNIEnv* env, jobject obj, jint logLevel) {
    return heliosDacInstance.SetLibusbDebugLogLevel(logLevel);
}

extern "C" JNIEXPORT jint JNICALL
Java_dev_defvs_heliosdac_HeliosDacNative_eraseFirmware(JNIEnv* env, jobject obj, jint devNum) {
    return heliosDacInstance.EraseFirmware(devNum);
}
