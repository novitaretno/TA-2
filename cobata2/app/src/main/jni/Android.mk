LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCVROOT:= D:\BISMILLAH\project\OpenCV-android-sdk
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include $(OPENCVROOT)/sdk/native/jni/OpenCV.mk
LOCAL_SRC_FILES := com_example_novita_cobata2_OpenCVNativeClass.cpp

LOCAL_LDLIBS += -llog
LOCAL_MODULE := MyLibs

include $(BUILD_SHARED_LIBRARY)