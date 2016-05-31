#!/bin/bash

DIR=$(dirname $0)
JAVA_OPTS="-Dlog4j.configuration=file:./config/log4j.properties -Xms512m -Xmx2048m"

./espeak-linux/espeak --path=./espeak-linux -wtestFile.wav "hola"
if [ $? -eq 0 ]
then
	rm testFile.wav
	espeak_result="espeak is usable"
else
	espeak_result="There is a problem with the espeak distribution"
fi

echo [$(date +%F-%H:%M:%S)][Reader][$espeak_result]

java $JAVA_OPTS -jar $DIR/reader.jar $@
