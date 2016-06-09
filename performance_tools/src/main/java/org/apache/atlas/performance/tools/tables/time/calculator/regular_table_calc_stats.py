#!/usr/bin/env python

#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import re;
import time;
import subprocess;
import commands;
import sys;
import datetime;

args=str(sys.argv)
total=int((sys.argv[1]))
file_path=sys.argv[2]
stat_file=open('atlas_stat.txt','w')
class Table:
   def __init__(self,num):
      self.table_num=num
      self.table_name=""
      self.start_time=""
      self.end_time=""

   def setTableName(self,name):
     self.table_name=name

   def getTableName(self):
     return self.table_name

   def setStartTime(self,start_time):
     self.start_time=start_time
   
   def setEndTime(self,end_time):
      self.end_time=end_time 

   def displayTable(self):
      print "Name : ", self.table_name,  ", Start Time: ", self.start_time, "End time :", self.end_time,"  Total time : ",self.findTotalTime()
      stat_file.write("%s,%s,%s,%s\n" % (self.table_name,self.start_time,self.end_time,self.findTotalTime()))

   def findTotalTime(self):
      d1=self.start_time
      d2=self.end_time
      dd1=datetime.datetime.strptime(d1,"%Y-%m-%d %H:%M:%S,%f")
      dd2=datetime.datetime.strptime(d2,"%Y-%m-%d %H:%M:%S,%f")
      diff=dd2-dd1
      return diff
      #return datetime.datetime.strptime(str(diff),"%H:%M:%S.%f")

tables=[]
for table_num in range(0,total+1) :
    tables.append(Table(table_num))


status,cmd=commands.getstatusoutput("rm atlas_stat.txt")
status,cmd=commands.getstatusoutput("touch atlas_stat.txt")
file=open(file_path);
content=file.readlines()
importStartTime=0
importEndTime=0
startTimeNotSet=1
start_match=""
for lines in content:
	start_match=re.match('(([2][0-9][0-9][0-9]\-[0-2][0-9]\-[0-3][0-9] [0-2][0-9]\:[0-5][0-9]\:[0-5][0-9])\,([0-9]*))(.*)Read message(.*)MANAGED_TABLE(.*)name\"\:\"(.*)\"\,\"createTime\"(.*)',lines)
	end_match=re.match('(([2][0-9][0-9][0-9]\-[0-2][0-9]\-[0-3][0-9] [0-2][0-9]\:[0-5][0-9]\:[0-5][0-9])\,([0-9]*)) DEBUG(.*)Sending message for topic ATLAS_ENTITIES(.*)\"name\"\:\"(.*)\"\,\"createTime\"\:(.*)ENTITY_CREATE(.*)',lines)
	if start_match:
		qualified_name=start_match.group(7)
	   	table_name_match=re.match('default\.table_([0-9]*)@erie-perf-test-cluster',qualified_name)
	   	if table_name_match:
			table_num=int(table_name_match.group(1))
			if not tables[table_num].getTableName() :
				start_time=start_match.group(1)
				if startTimeNotSet:
					importStartTime=start_time
					startTimeNotSet=0
				tables[table_num].setStartTime(start_time)
				tables[table_num].setTableName(qualified_name)

	if end_match:	
		qualified_name=end_match.group(6)
		table_name_match=re.match('default\.table_([0-9]*)@erie-perf-test-cluster',qualified_name)
                if table_name_match:
                        table_num=int(table_name_match.group(1))
                	end_time=end_match.group(1)
			print "Processed  : "+str(table_num)
			tables[table_num].setEndTime(end_time)
			importEndTime=end_time

for table_num in range(0,total+1):
        tables[table_num].displayTable()
allTables=Table(-1)
allTables.setTableName("-1")
allTables.setStartTime(importStartTime)
allTables.setEndTime(importEndTime)
print "Total table import statistics"
allTables.displayTable()



