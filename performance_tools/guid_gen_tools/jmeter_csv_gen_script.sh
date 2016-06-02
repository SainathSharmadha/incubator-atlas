#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

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


