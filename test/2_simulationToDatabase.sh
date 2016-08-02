#!/bin/bash

Config_File=~/student/mengyue/drill/db_java/db_manager/config/param.config
Dat_To_Csv_Script_File=~/student/mengyue/drill/test/script/demand_dat2csv_new.py
Perturb_Script_File=~/student/mengyue/drill/test/script/Perturb_demand_new.py
Perturb_Input_Path=~/student/mengyue/drill/test/originDemand/
Perturb_Demand_Dat_Path=~/student/mengyue/drill/test/demand_dat/
Perturb_Demand_Csv_Path=~/student/mengyue/drill/test/demand_csv/
DynaMIT_Path=~/student/mengyue/drill/test/DynaMIT/
MITSIM_Path=~/student/mengyue/drill/test/MITSIM/
Dtaparam_Path=~/student/mengyue/drill/test/DynaMIT/
Gson_Jar=~/student/mengyue/drill/db_java/db_manager/lib/gson-2.5.jar
PostgreSQL_Jar=~/student/mengyue/drill/db_java/db_manager/lib/postgresql-9.4.1209.jre6.jar
Src_Path=~/student/mengyue/drill/db_java/db_manager/out/production/db_manager

#TODO: Check the validity of the parameters and path to increase robust.


#TODO: Algorithm to generate the proper historical OD flow for the on-going simulation.


# Read the DynaMIT histOD file name from dtsparam.dat
Hist_File=$(grep -F "HistODFile" ${DynaMIT_Path}dtaparam.dat)
set -- "$Hist_File" 
IFS="="; declare -a Array=($*)  
Hist_File=$(echo ${Array[1]}|xargs)
echo $Hist_File

Hist_Csv_File=${Hist_File::-3}csv
echo $Hist_Csv_File

# Read the MITSIM demand file name from master.mitsim
Demand_File=$(grep -F "[Trip Table File]" ${MITSIM_Path}master.mitsim)
set -- "$Demand_File" 
IFS="="; declare -a Array=($*)  
Demand_File=$(echo ${Array[1]}|xargs)
echo $Demand_File

Demand_Csv_File=${Demand_File::-3}csv
echo $Demand_Csv_File

# Transform dynamit histOD dat format to csv format in DynaMIT dir
python $Dat_To_Csv_Script_File ${DynaMIT_Path}$Hist_File ${DynaMIT_Path}$Hist_Csv_File

## Transform mitsim demand dat format to csv format in MITSIM dir
#python $Dat_To_Csv_Script_File ${MITSIM_Path}$Demand_File ${MITSIM_Path}$Demand_Csv_File

## Generate a number of mitsim demand dat file
#echo -e
#echo -e
#echo -e "\e[36;1mgenerating pertubation demand \e[0m"
#python $Perturb_Script_File ${MITSIM_Path}$Demand_Csv_File ${Perturb_Demand_Dat_Path} ${Perturb_Demand_Csv_Path}

## For-loop for simulation and insertion to database
#for I in 1 2 3 4 5
#do
	printf -v index "%02d" $I
	echo -e
	echo -e
	echo -e "\e[36;1mLOOP$I|=> \e[0m"

	# Alternate the demand file in MITSIM directory with the ith demand file genertated
	(cp ${Perturb_Demand_Dat_Path}demand_MY_${index}.dat ${MITSIM_Path}$Demand_File)
	(cp ${Perturb_Demand_Csv_Path}demand_MY_${index}.csv ${MITSIM_Path}$Demand_Csv_File)

	# Cleanup files from the backup dir
	echo -e
	echo -e
	echo -e "\e[36;1mClear backup... \e[0m"
	(cd  ${DynaMIT_Path};sudo ./backupToDir)

	# Run DynaMIT and MITSIM to get the new estimated od flow
	#TODO: At run-time, save hmatrix to the database
	echo -e
	echo -e
	echo -e "\e[36;1mRun DynaMIT&MITSIM...\e[0m"

	gnome-terminal -e "bash -c \"cd $MITSIM_Path; ./run_xmitsim.sh;exec bash\""
	(cd ${DynaMIT_Path}; ./runDynaMIT.sh dtaparam.dat)
	
	# Insert to database
	echo -e
	echo -e
	echo -e "\e[36;1mInsert to database...\e[0m"
	java -cp $Gson_Jar:$PostgreSQL_Jar:${Src_Path} util/InsertProcess $Config_File

	# Make a copy of DynaMIT file at the test file from origin DynaMIT.
	echo -e
	echo -e
	echo -e "\e[36;1mCopy DynaMIT... \e[0m"
	(cd ${DynaMIT_Path};./backUpResult.sh $index) 

	read

	echo -e
	echo -e
	echo -e
	echo -e "\e[36;1mFinished Process!\e[0m"
#done

