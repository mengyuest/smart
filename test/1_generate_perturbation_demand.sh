#!/bin/bash

DATTOCSVSCRIPTFILE=~/student/mengyue/drill/test/script/demand_dat2csv_new.py
PERTURBSCRIPTFILE=~/student/mengyue/drill/test/script/Perturb_demand_new.py
PERTURBINPUTPATH=~/student/mengyue/drill/test/originDemand/
PERTURBDEMANDDATPATH=~/student/mengyue/drill/test/demand_dat/
PERTURBDEMANDCSVPATH=~/student/mengyue/drill/test/demand_csv/
DYNAMITPATH=~/student/mengyue/drill/test/DynaMIT/
MITSIMPATH=~/student/mengyue/drill/test/MITSIM/
DTAPARAMPATH=~/student/mengyue/drill/test/DynaMIT/

# Read the MITSIM demand file name from master.mitsim
DEMANDFILE=$(grep -F "[Trip Table File]" ${MITSIMPATH}master.mitsim)
set -- "$DEMANDFILE" 
IFS="="; declare -a Array=($*)  
DEMANDFILE=$(echo ${Array[1]}|xargs)
echo $DEMANDFILE

DEMANDCSVFILE=${DEMANDFILE::-3}csv
echo $DEMANDCSVFILE

# Generate a number of mitsim demand dat file
echo -e
echo -e
echo -e "\e[36;1mgenerating pertubation demand \e[0m"
python $PERTURBSCRIPTFILE ${PERTURBINPUTPATH}$DEMANDCSVFILE ${PERTURBDEMANDDATPATH} ${PERTURBDEMANDCSVPATH}

