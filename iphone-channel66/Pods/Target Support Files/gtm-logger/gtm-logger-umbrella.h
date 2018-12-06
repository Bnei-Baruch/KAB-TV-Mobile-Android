#ifdef __OBJC__
#import <UIKit/UIKit.h>
#else
#ifndef FOUNDATION_EXPORT
#if defined(__cplusplus)
#define FOUNDATION_EXPORT extern "C"
#else
#define FOUNDATION_EXPORT extern
#endif
#endif
#endif

#import "GTMLogger.h"
#import "GTMDefines.h"
#import "GTMMethodCheck.h"
#import "GTMNSString+URLArguments.h"
#import "GTMNSDictionary+URLArguments.h"
#import "GTMObjC2Runtime.h"

FOUNDATION_EXPORT double gtm_loggerVersionNumber;
FOUNDATION_EXPORT const unsigned char gtm_loggerVersionString[];

