@ECHO off

"%~DP0\jre7\bin\java.exe" -jar "%~DP0\excel2debitpro.jar" -log true -input %1

pause;