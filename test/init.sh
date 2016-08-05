#!/bin/sh
########################################  init.sh  ################ Meng Yue # August 04 2016 ###
#################################################################################################
# this is a init shell and will run at every beginning of the shell script under this directory #
# mainly to declare some global variables and global parameters.                                #
# please do not arbitary alter the format of this file(may cause error)                         #
#################################################################################################


# loading file path
test_Dir=~/student/mengyue/drill/test/
Config_File=~/student/mengyue/drill/db_java/db_manager/config/param.config
Dat2Csv_Script_File=~/student/mengyue/drill/test/script/demand_dat2csv_new.py
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
DB_Save_Path=~/student/mengyue/drill/test/DBSAVE/

# running parameters
# the days you want to run at
# the DAY_NUM should no bigger than the size of the DATE array!!! 
DAY_NUM=10
DATE[0]="2016/08/20"
DATE[1]="2016/08/21"
DATE[2]="2016/08/22"
DATE[3]="2016/08/23"
DATE[4]="2016/08/24"
DATE[5]="2016/08/25"
DATE[6]="2016/08/26"
DATE[7]="2016/08/27"
DATE[8]="2016/08/28"
DATE[9]="2016/08/29"

# defining global functions
# output normal message 
# usage: print "message"
print(){
	echo -e
	echo -e
	printf "\e[36;1m$1\n\e[0m"
}

# output error message
# usage: error "error message"
error(){
	echo -e
	printf "\e[31;1m[ERROR]:$1\n\e[0m"
}

# output warning message
# usage: warning "warning message"
warning(){
	echo -e
	printf "\e[33;1m[WARN]:$1\n\e[0m"
}

# output welcome message
# usage: print_welcome "welcome message"
print_welcome(){
	echo -e
	printf "\e[34;1m$1\n\e[0m"
}

#WELCOME MESSAGE
# usage: welcome
welcome(){
	if [[ $COLUMNS >79 ]] ;then
	print_welcome "==============================OcSimu2Db_Platform==============================
======================================================MENG YUE==August 3,2016="

	elif [[ $COLUMNS > 59 ]] ;then
	print_welcome "====================OcSimu2Db_Platform====================
==================================MENG YUE==August 3,2016="

	elif [[ $COLUMNS > 39 ]] ;then
	print_welcome "==========OcSimu2Db_Platform==========
==============MENG YUE==August 3,2016="

	else
	print_welcome "=====OcSimu2Db_Platform=====
===MENG YUE===August 3,2016="
	fi
}
