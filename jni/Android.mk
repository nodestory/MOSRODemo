LOCAL_PATH := $(call my-dir)

OPENCV_CAMERA_MODULES:=off
OPENCV_INSTALL_MODULES:=on

include $(CLEAR_VARS)

ifeq ("$(wildcard $(OPENCV_MK_PATH))","")
	#try to load OpenCV.mk from default install location
	#include $(TOOLCHAIN_PREBUILT_ROOT)/user/share/OpenCV/OpenCV.mk
	include /cygdrive/c/Users/Linzy/OpenCV-2.3.1/share/OpenCV/OpenCV.mk
else
	include $(OPENCV_MK_PATH)
endif

LOCAL_C_INCLUDES += /cygdrive/c/Users/Linzy/OpenCV-2.3.1/include
LOCAL_MODULE    := mixed_sample
LOCAL_SRC_FILES := jni_part.cpp
LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)