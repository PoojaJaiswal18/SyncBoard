# SyncBoard

**SyncBoard** is a collaborative whiteboard application built in Java using Remote Method Invocation (RMI). It allows multiple users to create and join shared whiteboards in real time.

## Features

- Create or join shared whiteboards  
- Real-time drawing and updates  
- Built using Java RMI  
- Simple command-line interface to launch components  

## How To Use:

**1. Build the application**

Run the following command to compile and package the application:

*mvn clean package*

**2. Start the server**

Launch the RMI server:

*start-server.bat*

**3. Create a whiteboard**

In a new command window, create a whiteboard session:

*create-whiteboard.bat [username]*

**4. Join a whiteboard**

In another command window, join an existing whiteboard session:

*join-whiteboard.bat [username]*
