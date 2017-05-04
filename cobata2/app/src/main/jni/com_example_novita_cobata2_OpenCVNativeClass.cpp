#include <com_example_novita_cobata2_OpenCVNativeClass.h>
JNIEXPORT jstring JNICALL Java_com_example_novita_cobata2_OpenCVNativeClass_getMessageFromJNI
  (JNIEnv *env, jclass obj){
    return env-> NewStringUTF("This is Message From JNI");
}