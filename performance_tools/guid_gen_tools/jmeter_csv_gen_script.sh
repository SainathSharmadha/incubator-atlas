#!/bin/bash

rm guids_logs.txt
rm mixed_table_guid.txt
rm table_guid.txt
logfile=$1

echo $logfile

echo "Extracting logs with GUIDS.. "
sh extract-guid-logs.sh $logfile

python find_guid.py

read -p "Enter the number of tables generated :" ntable

read -p "Enter the percentage of small tables :" stable

read -p "Enter the percentage of medium tables :" mtable

# calculating number of small tables from the percentage
stable=`echo "$ntable * ($stable / 100)" | bc -l`
mtable=`echo "$ntable * ($mtable / 100)" | bc -l`

#rounding the variables to 0 precision (integer)
stable=`echo $stable | xargs printf "%.*f\n" 0`
mtable=`echo $mtable | xargs printf "%.*f\n" 0`
ltable=$(($ntable - ($stable + $mtable) ))

mtable=`echo "$stable + $mtable" | bc -l`
ltable=`echo "$mtable + $ltable" | bc -l`
read -p "Enter the number of tables in test plan : " test_tables 

python mixtables.py $test_tables $stable $mtable $ltable


echo "mixed_table_guid.txt is generated which can be used for jmeter test plan"


