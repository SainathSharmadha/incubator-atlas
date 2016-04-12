#!/bin/bash
rm guids_logs_ctas.txt
touch guids_logs_ctas.txt
cat ../logs/application.log | grep -E "Sending message for topic ATLAS_ENTITIES.*hive_table.*ctas.*ENTITY_CREATE" > guids_logs_ctas.txt


 
