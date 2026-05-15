@echo off
cd /d "%~dp0"
java --module-path "C:\Users\CompuMarts\Downloads\openjfx-17.0.19_windows-x64_bin-sdk\javafx-sdk-17.0.19\lib" --add-modules javafx.controls,javafx.fxml carwashsystem.CarWashGUI
pause
