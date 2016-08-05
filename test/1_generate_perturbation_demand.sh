#!/bin/bash
source init.sh

# Read the MITSIM demand file name from master.mitsim
Demand_File=$(grep -F "[Trip Table File]" ${MITSIM_Path}master.mitsim)
set -- "$Demand_File" 
IFS="="; declare -a Array=($*)  
Demand_File=$(echo ${Array[1]}|xargs)
echo $Demand_File

Demand_Csv_File=${Demand_File::-3}csv
echo $Demand_Csv_File

python $Dat2Csv_Script_File ${Perturb_Input_Path}$Demand_File ${Perturb_Input_Path}$Demand_Csv_File

# Generate a number of mitsim demand dat file
echo -e
echo -e
echo -e "\e[36;1mgenerating pertubation demand \e[0m"
python $Perturb_Script_File ${Perturb_Input_Path}$Demand_Csv_File ${Perturb_Demand_Csv_Path} ${Perturb_Demand_Dat_Path} $DAY_NUM

