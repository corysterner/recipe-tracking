# Getting Started
## Setup Java 17 JDK
### Download Java 17
- Download from here (I'd recommend the "installer" version): https://www.oracle.com/java/technologies/downloads/#java17
- Install Java using the installer
## Setup JavaFX
### Download JavaFX
- Download from here using the below criteria (assuming Windows): https://gluonhq.com/products/javafx/
- Version: 17.0.7
- OS: Windows
- Arch: Your Choice (I used x64)
- Type: SDK
### Unzip JavaFX
- Follow the directions here to unzip and make available: https://openjfx.io/openjfx-docs/#install-javafx
- Use Powershell to run the commands (on Windows)
  
The above directions assume Windows Command Line. These are the commands I ran using Powershell:
```
$Env:PATH_TO_FX='E:\UserFolders\Ethan\Downloads\openjfx-17.0.7_windows-x64_bin-sdk\javafx-sdk-17.0.7\lib'
```
Use this to check that the path is set correctly:
```
dir env:
```
Build and Run:
```
javac --module-path $Env:PATH_TO_FX --add-modules javafx.controls HelloFX.java
```
```
java --module-path $Env:PATH_TO_FX --add-modules javafx.controls HelloFX
```
