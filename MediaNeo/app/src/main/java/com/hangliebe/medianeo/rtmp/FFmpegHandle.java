package com.hangliebe.medianeo.rtmp;

import com.getkeepsafe.relinker.ReLinker;

public class FFmpegHandle {
    private static FFmpegHandle fFmpegHandle;

    private FFmpegHandle() {

    }
    static {
        System.loadLibrary("avformat");
        System.loadLibrary("avcodec");
        System.loadLibrary("avfilter");
        System.loadLibrary("avdevice");
        System.loadLibrary("avutil");
        System.loadLibrary("swresample");
        System.loadLibrary("swscale");

        System.loadLibrary("native-lib");
    }

    public static FFmpegHandle getInstance() {
        if (fFmpegHandle == null) {
            return new FFmpegHandle();
        }
        return fFmpegHandle;
    }
    public native String stringFromJNI();
    public native int initVideo(String url);

    public native int onFrameCallback(byte[] data);
}
