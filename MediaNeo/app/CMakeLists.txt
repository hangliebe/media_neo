cmake_minimum_required(VERSION 3.10.2)

project("native-lib")

set(CURRENT_DIR ${CMAKE_SOURCE_DIR})
set(LIBS_DIR D:/developProject/hangliebe/media_neo/media_neo/MediaNeo/app/libs)

include_directories(${LIBS_DIR}/arm64-v8a/include)

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp
             src/main/cpp/ffmpeg_handle.cpp)

add_library(avcodec
        SHARED
        IMPORTED)
set_target_properties(avcodec
        PROPERTIES IMPORTED_LOCATION
        ${LIBS_DIR}/arm64-v8a/lib/libavcodec.so)

add_library(avdevice
        SHARED
        IMPORTED)
set_target_properties(avdevice
        PROPERTIES IMPORTED_LOCATION
        ${LIBS_DIR}/arm64-v8a/lib/libavdevice.so)

add_library(avformat
        SHARED
        IMPORTED)
set_target_properties(avformat
        PROPERTIES IMPORTED_LOCATION
        ${LIBS_DIR}/arm64-v8a/lib/libavformat.so)

add_library(avutil
        SHARED
        IMPORTED)
set_target_properties(avutil
        PROPERTIES IMPORTED_LOCATION
        ${LIBS_DIR}/arm64-v8a/lib/libavutil.so)

add_library(swresample
        SHARED
        IMPORTED)
set_target_properties(swresample
        PROPERTIES IMPORTED_LOCATION
        ${LIBS_DIR}/arm64-v8a/lib/libswresample.so)

add_library(swscale
        SHARED
        IMPORTED)
set_target_properties(swscale
        PROPERTIES IMPORTED_LOCATION
        ${LIBS_DIR}/arm64-v8a/lib/libswscale.so)

add_library(avfilter
        SHARED
        IMPORTED)
set_target_properties(avfilter
        PROPERTIES IMPORTED_LOCATION
        ${LIBS_DIR}/arm64-v8a/lib/libavfilter.so)


find_library( # Sets the name of the path variable.
              log-lib

              log )

target_link_libraries( # Specifies the target library.
        native-lib
        avformat
        avcodec
        avfilter
        avdevice
        avutil
        swresample
        swscale

        ${log-lib})
