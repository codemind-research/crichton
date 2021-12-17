#!/bin/sh
# USER CUSTOM -----------------
LOOP_VISIT_LIMIT=10000000
TESTCASE_NUM=40
STRATEGY=dfs
# DIR -------------------------
COYOTE=./coyote
#------------------------------

if [ "$#" -ne 2 ]; then
  echo
  echo HELP: $0 [target-name] [fun number]
  echo
  exit
fi

FILENAME="${1%%.*}"

rm *.log > /dev/null

echo
echo --= COYOTE Analysis Tool \(Analyzer\) =--
echo

dotnet $COYOTE/miracle.dll --exe ./$FILENAME.out --img $FILENAME.img --func $2 --loop 1 --loopvisitlimit $LOOP_VISIT_LIMIT --num $TESTCASE_NUM --timeout 10 --search $STRATEGY --out temp

echo -----------------------------------------
echo DONE
echo -----------------------------------------
echo
