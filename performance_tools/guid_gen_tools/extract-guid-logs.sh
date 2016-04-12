#!/bin/bash
rm guids_logs.txt
touch guids_logs.txt
cat ../logs/application.log | grep -E "Sending message for topic ATLAS_ENTITIES.*hive_table.*ENTITY_CREATE" > guids_logs.txt


 
