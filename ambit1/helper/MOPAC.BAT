@echo off
copy mopac.dat For005
echo Mopac job has started
mopac.exe
echo Mopac job has finished
move For006 mopac.out
del For0*
del Shutdown
rem del mopac.dat
exit


