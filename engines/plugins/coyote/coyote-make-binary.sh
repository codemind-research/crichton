#!/bin/sh
# USER CUSTOM -----------------
ARRAY_LEN=1024
# DIR -------------------------
COYOTE=./coyote
CLANG_INCLUDE=/usr/lib/clang/12.0.0/include
#------------------------------
if [ "$#" -ne 1 ]; then
  echo
  echo HELP: $0 [target-name \(.c/.cpp\)]
  echo
  exit
fi

FILENAME="${1%%.*}"

echo
echo --= COYOTE Analysis Tool \(Binary Builder\) =--
echo

echo [MAKE FROG: $FILENAME.frog]
clang++ -g -S -emit-llvm -femit-all-decls -D__COYOTE_ENABLED -D__COYOTE_IS_CPP -I$COYOTE/api -I$CLANG_INCLUDE $1 -o $FILENAME.ll
dotnet $COYOTE/magic.dll --array-length $ARRAY_LEN --max-array-length $ARRAY_LEN --default-stub --calltrace --keeplist $COYOTE/template/keep.list --userstub $COYOTE/template/predefined.userstub $FILENAME.ll
echo
echo

echo [MAKE INFOS]
dotnet $COYOTE/magic.dll --gen-infos --default-stub --keeplist $COYOTE/template/keep.list --userstub $COYOTE/template/predefined.userstub -o $FILENAME $FILENAME.ll
echo
echo

echo [MAKE ASSEMBLY: $FILENAME.s]
llc-12 $FILENAME.frog -o $FILENAME.frog.s
llc-12 $FILENAME.glob -o $FILENAME.glob.s
echo
echo

echo [BUILD EXECUTABLE: $FILENAME.out]
clang++ -L$COYOTE/api/ $FILENAME.frog.s $FILENAME.glob.s -lcoyote -o $FILENAME.out
echo

echo -----------------------------------------
echo DONE
echo -----------------------------------------
echo
