

CONFIGFILE=~/student/mengyue/drill/filepath.config
DYNAMITPATH=~/student/mengyue/drill/DynaMIT/
DTAPARAMPATH=~/student/mengyue/drill/DynaMIT/
GSONJAR=~/student/mengyue/drill/db_java/db_manager/lib/gson-2.5.jar
POSTGRESQLJAR=~/student/mengyue/drill/db_java/db_manager/lib/postgresql-9.3-1103.jdbc3.jar
SRCPATH=~/student/mengyue/drill/db_java/db_manager/out/production/db_manager/

#TODO: Check the validity of the parameters and path to increase robust.


#TODO: Algorithm to generate the proper historical OD flow for the on-going simulation.
 
# Cleanup files from the backup dir
echo -e
echo -e
echo -e "\e[36;1mClear backup~ \e[0m"
(cd  ${DYNAMITPATH};./backupToDir)

# Run simulation to get the new estimated od flow
#TODO: At run-time, save hmatrix to the database
echo -e
echo -e
echo -e "\e[36;1mRun DynaMIT~\e[0m"

(cd ${DYNAMITPATH};./runDynaMIT.sh dtaparam.dat)


# Insert to database
echo -e
echo -e
echo -e "\e[36;1mInsert to database~\e[0m"
java -cp $GSONJAR:$POSTGRESQLJAR:${SRCPATH} db_insert $CONFIGFILE

echo -e
echo -e
echo -e
echo -e "\e[36;1mFinished Process~\e[0m"

