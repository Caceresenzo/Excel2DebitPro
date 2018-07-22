

NET SESSION
IF %ERRORLEVEL% NEQ 0 GOTO ELEVATE
GOTO ADMINTASKS

:ELEVATE
CD /d %~dp0
MSHTA "javascript: var shell = new ActiveXObject('shell.application'); shell.ShellExecute('%~nx0', '', '', 'runas', 1);close();"
EXIT

:ADMINTASKS

%SystemRoot%\System32\reg.exe ADD HKEY_CLASSES_ROOT\SystemFileAssociations\.xlsx\shell\excel2debitpro /ve /d "Excel vers DebitPro" /f
%SystemRoot%\System32\reg.exe ADD HKEY_CLASSES_ROOT\SystemFileAssociations\.xlsx\shell\excel2debitpro /v Icon /t REG_SZ /d \"%~DP0\ressources\icon.ico\"
%SystemRoot%\System32\reg.exe ADD HKEY_CLASSES_ROOT\SystemFileAssociations\.xlsx\shell\excel2debitpro\command /ve /d "\"%~DP0\start.cmd\" \"%%1\"" /f

%SystemRoot%\System32\reg.exe ADD HKEY_CLASSES_ROOT\SystemFileAssociations\.xlsx\shell\excel2debitprodebug /ve /d "Excel vers DebitPro (DEBUG)" /f
%SystemRoot%\System32\reg.exe ADD HKEY_CLASSES_ROOT\SystemFileAssociations\.xlsx\shell\excel2debitprodebug /v Icon /t REG_SZ /d \"%~DP0\ressources\icon.ico\"
%SystemRoot%\System32\reg.exe ADD HKEY_CLASSES_ROOT\SystemFileAssociations\.xlsx\shell\excel2debitprodebug\command /ve /d "\"%~DP0\startpause.cmd\" \"%%1\"" /f

pause;