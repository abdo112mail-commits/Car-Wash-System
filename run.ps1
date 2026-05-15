#!/usr/bin/env pwsh

$javafxPath = "C:\Users\CompuMarts\Downloads\openjfx-17.0.19_windows-x64_bin-sdk\javafx-sdk-17.0.19\lib"
$javaExe = "C:\Program Files\Eclipse Adoptium\jdk-17.0.12.7-hotspot\bin\java.exe"

& $javaExe --module-path $javafxPath --add-modules javafx.controls,javafx.fxml carwashsystem.CarWashGUI
