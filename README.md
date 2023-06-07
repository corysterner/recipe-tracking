# recipe-tracking
# Getting Started
## Install Java 17 JDK
## Download JavaFX
### Download JavaFX
- Download from here using the below criteria (assumin Windows): https://gluonhq.com/products/javafx/
- Version: 17.0.7
- OS: Windows
- Arch: Your Choice (I used x64)
- Type: SDK
### Unzip JavaFX
- Follow the directions here to unzip and make available: https://openjfx.io/openjfx-docs/#install-javafx
- Use Powershell to run the commands (on Windows)
  
The above directions assume Windows Command Line. These are the commnands I ran using Powershell:
```
PS E:\UserFolders\Ethan\projects\JavaFX\samples-master\HelloFX\CLI\hellofx> $Env:PATH_TO_FX='E:\UserFolders\Ethan\Downloads\openjfx-17.0.7_windows-x64_bin-sdk\javafx-sdk-17.0.7\lib'
```
Use this to check that the path is set correctly:
```
PS E:\UserFolders\Ethan\projects\JavaFX\samples-master\HelloFX\CLI\hellofx> dir env:
```
Build and Run:
```
PS E:\UserFolders\Ethan\projects\JavaFX\samples-master\HelloFX\CLI\hellofx> javac --module-path $Env:PATH_TO_FX --add-modules javafx.controls HelloFX.java
```
```
PS E:\UserFolders\Ethan\projects\JavaFX\samples-master\HelloFX\CLI\hellofx> java --module-path $Env:PATH_TO_FX --add-modules javafx.controls HelloFX
```
