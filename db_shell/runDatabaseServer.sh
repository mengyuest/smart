# For running the server of the postgre database.
# Make sure that this is the path of where you init the postgre database.
# Recommand to run the killListeningProcess shell first when first booting.
# The logfile is writing in the same folder. Please check if any need.

echo "Starting the server~"

a=$(pwd) 
cd ~/student/mengyue/drill/dbserver/
postgres -D $(pwd) >logfile 2>&1 &

cd $a
