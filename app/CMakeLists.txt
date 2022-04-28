cmake_minimum_required(VERSION 3.6.4111159)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -pedantic")

aux_source_directory(src/main/cpp/vad_src/ DIR_LIB_SRCS)

add_definitions(-DWEBRTC_POSIX)
add_definitions(-DWEBRTC_ANDROID)

add_library( native-lib
             SHARED
             src/main/cpp/native-lib.cpp
             ${DIR_LIB_SRCS})

include_directories(src/main/cpp/vad_src/)

find_library( log-lib
              log )

target_link_libraries( native-lib
                       ${log-lib} )
