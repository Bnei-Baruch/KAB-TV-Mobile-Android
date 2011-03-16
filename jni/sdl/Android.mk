LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := sdl


LOCAL_CFLAGS := -I$(LOCAL_PATH)/include \
	-DSDL_JAVA_PACKAGE_PATH=$(SDL_JAVA_PACKAGE_PATH) \
	-DSDL_CURDIR_PATH=\"$(SDL_CURDIR_PATH)\" \
	-DSDL_TRACKBALL_KEYUP_DELAY=$(SDL_TRACKBALL_KEYUP_DELAY) \

LOCAL_CFLAGS := -I$(LOCAL_PATH)/../stlport/stlport $(LOCAL_CFLAGS) \
				-I$(SYSROOT)/usr/include
LOCAL_CPPFLAGS := $(LOCAL_CFLAGS)
LOCAL_CXXFLAGS := $(LOCAL_CFLAGS)

SDL_SRCS := \
	src/*.c \
	src/audio/*.c \
	src/cdrom/*.c \
	src/cpuinfo/*.c \
	src/events/*.c \
	src/file/*.c \
	src/joystick/*.c \
	src/stdlib/*.c \
	src/thread/*.c \
	src/timer/*.c \
	src/video/*.c \
	src/video/dummy/*.c \
	src/audio/android/*.c \
	src/joystick/dummy/*.c \
	src/cdrom/dummy/*.c \
	src/thread/pthread/*.c \
	src/timer/unix/*.c \
	src/loadso/dummy/*.c \

LOCAL_CPP_EXTENSION := .cpp

# Note this simple makefile var substitution, you can find even simpler examples in different Android projects
LOCAL_SRC_FILES := $(foreach F, $(SDL_SRCS), $(addprefix $(dir $(F)),$(notdir $(wildcard $(LOCAL_PATH)/$(F)))))

LOCAL_LDLIBS := -lGLESv1_CM -ldl -llog

include $(BUILD_STATIC_LIBRARY)

