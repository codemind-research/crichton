set(CMAKE_C_COMPILER clang)
set(CMAKE_CXX_COMPILER clang++)
set(CMAKE_C_COMPILER_ID Clang)
set(CMAKE_CXX_COMPILER_ID Clang)

find_package(LLVM REQUIRED)
find_package(Clang REQUIRED)

add_compile_options(
  "$<$<AND:$<PLATFORM_ID:Linux>,$<CXX_COMPILER_ID:Clang>>:-fno-rtti;-Wall>"
)

add_library(
  crichton
    MODULE
      CrichtonAction.cpp
      CrichtonConsumer.cpp
)