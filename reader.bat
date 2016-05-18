set READER_CMD_LINE_ARGS=%*
set JAVA_OPTS="-Dlog4j.configuration=file:config\log4j.properties"

@echo %READER_CMD_LINE_ARGS%

echo off
espeak -wtestFile.wav "hola"
set RESULT=%errorLevel%

IF %RESULT% == 0 (
	del testFile.wav
	set MESSAGE="[INFO ] espeak binary is installed in your system and properly set"
	) ELSE (
	set MESSAGE="[ERROR] espeak binary is not installed in your system, not added to the PATH, or ESPEAK_DATA_PATH is not set"
	)
echo on
@echo %MESSAGE%

java.exe %JAVA_OPTS% -jar ./reader.jar %READER_CMD_LINE_ARGS%
 
