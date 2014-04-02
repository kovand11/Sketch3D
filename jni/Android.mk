LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_SRC_FILES := hu_kovand_sketch3d_geometry_CurveLib.cpp
LOCAL_C_INCLUDES := C:\Development\eigen-eigen-6b38706d90a9
LOCAL_MODULE := hu_kovand_sketch3d_geometry_CurveLib
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)