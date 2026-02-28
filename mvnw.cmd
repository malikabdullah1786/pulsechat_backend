@echo off
setlocal
set "DIRNAME=%~dp0"
cd /d "%DIRNAME%"
set "WRAPPER_JAR=.mvn\wrapper\maven-wrapper.jar"
set "WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain"

java -Dmaven.multiModuleProjectDirectory=. -cp "%WRAPPER_JAR%" %WRAPPER_LAUNCHER% %*

endlocal
