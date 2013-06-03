//
//  LogManager.h
//  kxmovie
//
//  Created by Asher on 4/29/13.
//
//

#define SvivaTova_DEBUG_LOGGING NO
#define SvivaTova_ERROR_LOGGING YES
#define SvivaTova_WARNING_LOGGING YES


#ifdef SvivaTova_WARNING_LOGGING
#define LogWarn(format, ...) NSLog(@"WARNING: %s [Line %d]:%@", __PRETTY_FUNCTION__, __LINE__, [NSString stringWithFormat:format, ## __VA_ARGS__]);
#else
#define LogWarn(format, ...)
#endif

#ifdef SvivaTova_DEBUG_LOGGING
#define LogDebug(format, ...) NSLog(@"DEBUG: %s [Line %d]:%@", __PRETTY_FUNCTION__, __LINE__, [NSString stringWithFormat:format, ## __VA_ARGS__]);
#else
#define LogDebug(format, ...)
#endif

#ifdef SvivaTova_ERROR_LOGGING
#define LogErr(format, ...) NSLog(@"ERROR: %s [Line %d]:%@", __PRETTY_FUNCTION__, __LINE__, [NSString stringWithFormat:format, ## __VA_ARGS__]);
#else
#define LogErr(format, ...)
#endif


