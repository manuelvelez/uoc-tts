#!/bin/bash

DIR=$(dirname $0)

is_espeak_path=$(which espeak)
if [ $? -eq 0 ]
then
	espeak_result="espeak is installed in the following path: $is_espeak_path"
else
	espeak_result="espeak is not installed. You can install it by running sudo apt-get install espeak"
fi

echo [$(date +%F-%H:%M:%S)][Reader][$espeak_result]

java -jar $DIR/reader.jar $@
