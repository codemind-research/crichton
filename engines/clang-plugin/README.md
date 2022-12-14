## Prerequisite (Ubuntu 20.04 기준)
    * cmake, ninja-build, build-essential, python3, protobuf-compiler   

### protobuf-compiler
* [install guide](https://github.com/protocolbuffers/protobuf/blob/master/src/README.md)
* [release package](https://github.com/protocolbuffers/protobuf/releases)
1. Checkout protobuf (skip this if you are using a release .tar.gz or .zip package)
    * ``git clone https://github.com/protocolbuffers/protobuf.git``
    * ``cd protobuf``
    * ``git submodule update --init --recursive``
    * ``./autogen.sh``

2. Configure and install
    * ``./configure``
    * ``make -j$(nproc)`` --- $(nproc) ensures it uses all cores for compilation
    * ``make check``
    * ``sudo make install``
    * ``sudo ldconfig`` --- refresh shared library cache.
    * ``protoc --version`` --- version check

## Build 
1. Symbolic link
    * ``cd engines/clang-plugin`` 
    * ``ln -s ../../proto/fun.proto ./fun.proto``

2. build
    * ``cd engines/clang-plugin``
    * ``mkdir build``
    * ``cd build``
    * ``cmake -GNinja ../``