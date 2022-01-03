//
// Created by Administrator on 2021/12/11.
//
#include <android/log.h>

#ifndef MEDIANEO_LOG_H
#define MEDIANEO_LOG_H

//#define LOG_TAG _CTagHead(__FILE__,__func__)
#define LOG_TAG "C_TAG"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,_ _VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

#endif //MEDIANEO_LOG_H

