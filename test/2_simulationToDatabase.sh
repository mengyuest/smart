#!/bin/bash
source init.sh

#WELCOME!!!
welcome
print ""

#TODO: Check the validity of the parameters and path to increase robust.
#TODO: Algorithm to generate the proper historical OD flow for the on-going simulation.

# Read the DynaMIT histOD file name from dtsparam.dat
Hist_File=$(grep -F "HistODFile" ${DynaMIT_Path}dtaparam.dat)
set -- "$Hist_File" 
IFS="="; declare -a Array=($*)  
Hist_File=$(echo ${Array[1]}|xargs)
Hist_Csv_File=${Hist_File::-3}csv

# Read the MITSIM demand file name from master.mitsim
Demand_File=$(grep -F "[Trip Table File]" ${MITSIM_Path}master.mitsim)
set -- "$Demand_File" 
IFS="="; declare -a Array=($*)  
Demand_File=$(echo ${Array[1]}|xargs)
Demand_Csv_File=${Demand_File::-3}csv



# Transform dynamit histOD dat format to csv format in DynaMIT dir
python $Dat2Csv_Script_File ${DynaMIT_Path}$Hist_File ${DynaMIT_Path}$Hist_Csv_File


rm -fr COPY
mkdir COPY
rm -fr DBSAVE
mkdir DBSAVE

print "histFile=$Hist_File \ndemandFile=$Demand_File \nPreparation finished, start loop..."

## For-loop for simulation and insertion to database
for (( I=1; I<=$DAY_NUM; I++))
do
	printf -v index "%02d" $I
	print "LOOP$I|=>SIMUDATE: ${DATE[$I-1]}"

	# Inserting metadata file (kind of fake)
	metadataFileName=$(grep -F "METADATA" $Config_File)
	set -- "$metadataFileName" 
	IFS="="; declare -a tempArray=($*)  
	metadataFileName=$(echo ${tempArray[1]}|xargs)
	echo "simulationDate=${DATE[$I-1]}" > $metadataFileName

	print "Check date: ${DATE[$I-1]}"
	java -cp $Gson_Jar:$PostgreSQL_Jar:${Src_Path} util/RowDateChecker "${DATE[$I-1]}"	
	STATUS=$?
	if [[ $STATUS = 1 ]];then
		error "The date is already exist, abort this simulation and go next loop"
		continue
	fi

	let simIndex=($I-1)%10+1 
	printf -v simIndex "%02d" $simIndex

	# Alternate the demand file in MITSIM directory with the ith demand file genertated
	(cp ${Perturb_Demand_Dat_Path}demand_MY_${simIndex}.dat ${MITSIM_Path}$Demand_File)
	(cp ${Perturb_Demand_Csv_Path}demand_MY_${simIndex}.csv ${MITSIM_Path}$Demand_Csv_File)

	# TODO:Update historical data in DynaMIT
	print "Update process: HOD for ${DATE[$I-1]}"
	java -cp $Gson_Jar:$PostgreSQL_Jar:${Src_Path} util/UpdateProcess "${DATE[$I-1]}"

	# Cleanup files from the backup dir
	print "Clear backup..."
	(cd  ${DynaMIT_Path}; ./backupToDir)

	# Run DynaMIT and MITSIM to get the new estimated od flow
	# TODO: At run-time, save hmatrix to the database
	print "Run DynaMIT&MITSIM..."
	gnome-terminal -e "bash -c \"cd $MITSIM_Path; ./run_mitsim.sh;exit;exec bash\""
	(cd ${DynaMIT_Path}; ./runDynaMIT.sh dtaparam.dat)
	
	# Insert to database
	# Currently, datas that don't provide in DynaMIT and MITSIM but are defined in database
	# (like metadata:Weather, temperature, date etc) are default sourced from metadata.dat under DynaMIT dir
	# The path of metadata.dat can be altered in config.param under 
	# $MENG/drill/db_java/db_manager/config/param.config
	print "Insert to database..."
	java -cp $Gson_Jar:$PostgreSQL_Jar:${Src_Path} util/InsertProcess $Config_File

	# Make a copy of DynaMIT file at the test file from origin DynaMIT.
	print "Backup DynaMIT results..."
	(. 4_backUpResult.sh $index) 

	
	# Make a copy of DynaMIT file from the database.
	print "Load from database and save to files..."
	(. 5_writeToFile.sh ${DB_Save_Path}DynaMIT_FILE$index/  ${DATE[$I-1]}) 

	print "Finished Loop$index !"
done
print "All finished :)"


