#!/bin/bash
# <Last updated: Yang WEN, 06/24/2008 Tue 17:10:08>
# setenv LD_LIBRARY_PATH
#     <mcr_root>/<ver>/runtime/glnx86:
#     <mcr_root>/<ver>/bin/glnx86:
#     <mcr_root>/<ver>/sys/os/glnx86:
#     <mcr_root>/<ver>/sys/java/jre/glnx86/jre1.5.0/lib/i386/native_threads:
#     <mcr_root>/<ver>/sys/java/jre/glnx86/jre1.5.0/lib/i386/client:
#     <mcr_root>/<ver>/sys/java/jre/glnx86/jre1.5.0/lib/i386:
# setenv XAPPLRESDIR <mcr_root>/<ver>/X11/app-defaults

# for tcsh
# setenv LD_LIBRARY_PATH /home/wenyang/MCR/v72/runtime/glnx86:/home/wenyang/MCR/v72/bin/glnx86:/home/wenyang/MCR/v72/sys/os/glnx86:/home/wenyang/MCR/v72/sys/java/jre/glnx86/jre1.5.0/lib/i386/native_threads:/home/wenyang/MCR/v72/sys/java/jre/glnx86/jre1.5.0/lib/i386/client:/home/wenyang/MCR/v72/sys/java/jre/glnx86/jre1.5.0/lib/i386:

# setenv XAPPLRESDIR /home/wenyang/MCR/v72/X11/app-defaults

# For trumpet, where Matlab is installed
# setenv LD_LIBRARY_PATH /opt/matlabR14SP2/bin/glnx86:/opt/matlabR14SP2/sys/os/glnx86:/opt/matlabR14SP2/sys/java/jre/glnx86/jre1.5.0/lib/i386/native_threads:/opt/matlabR14SP2/sys/java/jre/glnx86/jre1.5.0/lib/i386/client:/opt/matlabR14SP2/sys/java/jre/glnx86/jre1.5.0/lib/i386:
# 
# setenv XAPPLRESDIR /opt/matlabR14SP2/X11/app-defaults

###########
# For bash
###########
#
# Set matlab compiler runtime environment first 
mcr_root=$HOME/MCR
ver=v72
export LD_LIBRARY_PATH=$mcr_root/$ver/runtime/glnx86:$mcr_root/$ver/bin/glnx86:$mcr_root/$ver/sys/os/glnx86:$mcr_root/$ver/sys/java/jre/glnx86/jre1.5.0/lib/i386/native_threads:$mcr_root/$ver/sys/java/jre/glnx86/jre1.5.0/lib/i386/client:$mcr_root/$ver/sys/java/jre/glnx86/jre1.5.0/lib/i386:/usr/lib:

./estimateOD $1 $2 $3 $4 $5 $6 $7

