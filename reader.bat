set READER_CMD_LINE_ARGS=%*
set HEAP=-Xms512m -Xmx2048m
set LOG_CONFIG=-Dlog4j.configuration=file:config\log4j.properties
set JAVA_OPTS= %HEAP% %LOG_CONFIG%

@echo %READER_CMD_LINE_ARGS%

echo off
.\espeak-win\espeak.exe --path=.\espeak-win -wtestFile.wav "hola"
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
 
