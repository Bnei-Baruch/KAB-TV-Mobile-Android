/*
    SDL - Simple DirectMedia Layer
    Copyright (C) 1997-2009 Sam Lantinga

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Sam Lantinga
    slouken@libsdl.org
*/

#ifndef _SDL_config_minimal_h
#define _SDL_config_minimal_h

#include "SDL_platform.h"

/* This is the minimal configuration that can be used to build SDL */

#include <stdarg.h>
#include <stdint.h>


#define SDL_AUDIO_DRIVER_ANDROID	1

#define SDL_CDROM_DISABLED	1

#define SDL_JOYSTICK_DISABLED	1

#define SDL_LOADSO_DISABLED	1

#define SDL_THREAD_PTHREAD	1
#define SDL_THREAD_PTHREAD_RECURSIVE_MUTEX	1

#define SDL_TIMER_UNIX	1

#define SDL_VIDEO_DRIVER_ANDROID	1

#define HAVE_STDIO_H	1

#define NO_SDL_GLEXT	1

/* FireSlash found that SDL native memcpy crashes sometimes, these defines fix it */
#define HAVE_MEMSET 1
#define HAVE_MEMCPY 1
#define HAVE_MEMMOVE 1

// FIXME: Gerald
#if 0
#ifndef printf
#define printf(fmt, args...) \
        printf("%s:%d:\n    ", __FILE__, __LINE__); \
        printf(fmt, ##args); \
        fflush(stdout)
#endif
#endif

#define ANDROID_PLATFROM
#define ANDROID_DEBUG

#ifdef ANDROID_PLATFROM
#ifdef ANDROID_DEBUG

#define HAVE_MALLOC 1
#define HAVE_CALLOC 1
#define HAVE_REALLOC 1
#define HAVE_FREE 1

#undef printf
#include <android/log.h>
#include <string.h>
extern char __android_dbg_buf[];
#ifndef ALOG_DEBUG
#define ALOG_DEBUG(fmt, args...) \
    do { \
        snprintf(__android_dbg_buf, 512, "%s:%d:  ", __FILE__, __LINE__); \
        snprintf(__android_dbg_buf+strlen(__android_dbg_buf), 512-strlen(__android_dbg_buf), fmt, ##args); \
        __android_log_print(ANDROID_LOG_DEBUG,  "adosbox", "%s", __android_dbg_buf); \
    } while(0)
#endif

#define ALOG_INFO(fmt, args...) \
    do { \
        snprintf(__android_dbg_buf, 512, "%s:%d:  ", __FILE__, __LINE__); \
        snprintf(__android_dbg_buf+strlen(__android_dbg_buf), 512-strlen(__android_dbg_buf), fmt, ##args); \
        __android_log_print(ANDROID_LOG_INFO,  "adosbox", "%s", __android_dbg_buf); \
    } while(0)

#define ALOG_ERROR(fmt, args...) \
    do { \
        snprintf(__android_dbg_buf, 512, "%s:%d:  ", __FILE__, __LINE__); \
        snprintf(__android_dbg_buf+strlen(__android_dbg_buf), 512-strlen(__android_dbg_buf), fmt, ##args); \
        __android_log_print(ANDROID_LOG_ERROR,  "adosbox", "%s", __android_dbg_buf); \
    } while(0)

#define ALOG_WARN(fmt, args...) \
    do { \
        snprintf(__android_dbg_buf, 512, "%s:%d:  ", __FILE__, __LINE__); \
        snprintf(__android_dbg_buf+strlen(__android_dbg_buf), 512-strlen(__android_dbg_buf), fmt, ##args); \
        __android_log_print(ANDROID_LOG_WARN,  "adosbox", "%s", __android_dbg_buf); \
    } while(0)

#else // ANDROID_DEBUG else
#define ALOG_DEBUG(fmt, args...) 1

#define ALOG_INFO(fmt, args...) 1

#define ALOG_ERROR(fmt, args...) 1

#define ALOG_WARN(fmt, args...) 1

#endif// ANDROID_DEBUG end
#endif // ANDROID_LOG_DEBUG

#endif /* _SDL_config_minimal_h */
