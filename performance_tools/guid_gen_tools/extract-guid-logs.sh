#!/bin/bash
rm guids_logs.txt
touch guids_logs.txt
logfile=$1
cat $1 | grep -E "Sending message for topic ATLAS_ENTITIES.*hive_table.*ENTITY_CREATE" > guids_logs.txt


 
