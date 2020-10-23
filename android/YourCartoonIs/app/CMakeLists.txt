cmake_minimum_required(VERSION 3.6.0)

set(pathPROJECT C:/Users/min/AndroidStudioProjects/YourCartoonIs)
set(pathOPENCV C:/OpenCV-android-sdk/sdk)
set(pathLIBOPENCV_JAVA ${pathOPENCV}/native/libs/${ANDROID_ABI}/libopencv_java4.so)
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

include_directories(${pathOPENCV}/native/jni/include)

find_library(log-lib
        log)

add_library( lib_opencv SHARED IMPORTED )

set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${pathLIBOPENCV_JAVA})


add_library(native-lib SHARED
        src/main/cpp/native-lib.cpp)

target_link_libraries(
        native-lib

        lib_opencv
        ${log-lib})