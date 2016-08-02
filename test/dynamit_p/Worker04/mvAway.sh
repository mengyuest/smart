#!/bin/bash
cd output
targetFolder=$HOME/Dropbox/DYNAMIT/HMatAnalysis/calibResultComparison/
if [ "$1" == "" ]
then
    echo "Usage: "
    echo "    $0 newFolderName"
    exit
fi
mkdir $targetFolder/$1
mv ./i3d_flw_Est* $targetFolder/$1 
