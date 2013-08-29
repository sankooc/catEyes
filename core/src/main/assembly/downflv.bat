@echo off

set DIRNAME=%~dp0%
set PROGNAME=%~nx0%
set ARGS=%*

goto BEGIN
:warn
    echo %PROGNAME%: %*
goto :EOF

:BEGIN

rem # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

if not "%CEYE_HOME%" == "" (
    call :warn Ignoring predefined value for CEYE_HOME
)
set CEYE_HOME=%DIRNAME%..
if not exist "%CEYE_HOME%" (
    call :warn CEYE_HOME is not valid: %CEYE_HOME%
    goto END
)

set PATH=%PATH%;%CEYE_HOME%\lib

rem Setup the Java Virtual Machine
if not "%JAVA%" == "" goto :Check_JAVA_END
    set JAVA=java
    if "%JAVA_HOME%" == "" call :warn JAVA_HOME not set; results may vary
    if not "%JAVA_HOME%" == "" set JAVA=%JAVA_HOME%\bin\java
    if not exist "%JAVA_HOME%" (
        call :warn JAVA_HOME is not valid: "%JAVA_HOME%"
        goto END
    )
:Check_JAVA_END

if "%JAVA_OPTS%" == "" set JAVA_OPTS=%DEFAULT_JAVA_OPTS%

rem Setup the classpath
set CLASSPATH=%LOCAL_CLASSPATH%
pushd "%CEYE_HOME%\lib"
for %%G in (*.*) do call:APPEND_TO_CLASSPATH %%G
popd
goto CLASSPATH_END

: APPEND_TO_CLASSPATH
set filename=%~1
set suffix=%filename:~-4%
if %suffix% equ .jar set CLASSPATH=%CLASSPATH%;%CEYE_HOME%\lib\%filename%
goto :EOF

:CLASSPATH_END
SET ARGS=%1 %2
rem Execute the Java Virtual Machine
cd %CEYE_HOME%
"%JAVA%" -classpath "%CLASSPATH%" ${mainclass} %CEYE_HOME% %ARGS%
@pause
:END