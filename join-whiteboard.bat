@echo off
echo Starting SyncBoard Client (Join Mode)...

REM Set Java RMI properties
set JAVA_OPTS=-Djava.rmi.server.hostname=localhost -Djava.net.preferIPv4Stack=true

REM Create security policy if it doesn't exist
if not exist security.policy (
    echo Creating security policy file...
    echo grant { > security.policy
    echo     permission java.net.SocketPermission "*:1024-65535", "connect,accept,resolve"; >> security.policy
    echo     permission java.net.SocketPermission "*:80", "connect"; >> security.policy
    echo     permission java.net.SocketPermission "*:8001", "connect,accept,resolve"; >> security.policy
    echo     permission java.net.SocketPermission "*:1099", "connect,accept,resolve"; >> security.policy
    echo     permission java.io.FilePermission "<<ALL FILES>>", "read,write,execute,delete"; >> security.policy
    echo     permission java.util.PropertyPermission "*", "read,write"; >> security.policy
    echo     permission java.security.AllPermission; >> security.policy
    echo }; >> security.policy
)

REM Get username if provided
set USERNAME=user
if not "%~1"=="" set USERNAME=%1

REM Start the client
echo Running WhiteboardClient in join mode connecting to localhost:8001 as %USERNAME%...
java -cp target/sync-board-1.0.0.jar -Djava.rmi.server.hostname=localhost -Djava.security.policy=security.policy com.jaiswal.WhiteboardLauncher join localhost 8001 %USERNAME%

pause