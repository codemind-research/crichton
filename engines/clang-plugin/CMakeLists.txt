set(CMAKE_C_COMPILER clang)
set(CMAKE_CXX_COMPILER clang++)
set(CMAKE_C_COMPILER_ID Clang)
set(CMAKE_CXX_COMPILER_ID Clang)

find_package(LLVM REQUIRED)
find_package(Clang REQUIRED)
find_package(Protobuf REQUIRED)

add_compile_definitions(GOOGLE_PROTOBUF_NO_RTTI)
add_compile_definitions(protobuf_MSVC_STATIC_RUNTIME=OFF)

protobuf_generate_cpp(PROTO_SRCS PROTO_HDRS fun.proto)

include_directories(
  ${Protobuf_LIBRARIES}
  ${CMAKE_CURRENT_BINARY_DIR}
)

add_compile_options(
  "$<$<AND:$<PLATFORM_ID:Linux>,$<CXX_COMPILER_ID:Clang>>:-fno-rtti;-Wall>"
)

link_libraries(
)

add_library(
  crichton
    MODULE
      ${PROTO_SRCS}
      CrichtonAction.cpp
      CrichtonConsumer.cpp
      CrichtonItems.cpp
)

target_include_directories(
  crichton
    PRIVATE
      ${Protobuf_LIBRARIES}
      ${CMAKE_CURRENT_BINARY_DIR}
)

target_link_libraries(
  crichton
    PRIVATE
      ${Protobuf_LIBRARIES}   
)