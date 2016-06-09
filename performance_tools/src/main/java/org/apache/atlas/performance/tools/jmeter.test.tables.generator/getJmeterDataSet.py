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
from random import randint;

file_path ="guids_logs.txt"
file=open(file_path);
content=file.readlines()

guid_file=open('table_guid.txt','w')
for line in content:
	line1=line[0:550]
	match=re.match('(.*)Sending message for topic ATLAS_ENTITIES(.*)\"id\"\:\"(.*)\"\,\"version\"(.*)name\"\:\"(.*)\"\,\"createTime(.*)',line1)
	if match:
		print line1
		match1=re.match('((.*)\.table_(.*))\@(.*)',match.group(5))
		if match1:
			table_no=int(match1.group(3))
			print table_no
			fqn=match.group(5)
			table_name="table_"+str(table_no)
			guid=match.group(3)
			guid_file.write("%s|%s,%s,%s\n" %(table_no,table_name,fqn,guid));
	else:
		print "no match"

args=str(sys.argv)
total=int((sys.argv[1]))
s_end=int((sys.argv[2]))
m_end=int((sys.argv[3]))
l_end=int((sys.argv[4]))

guid_file=open('mixed_table_guid.txt','w')
f=open('table_guid.txt')
lines=f.readlines()
for num in range(1,total+1):
	if num%3 == 1:
		llim=0
		ulim=s_end-1
	elif num%3 ==2 :
		llim=s_end
		ulim=m_end-1
	elif num%3==0:
		llim=m_end
		ulim=l_end-1
	tableno=randint(llim,ulim)
	print tableno
	print lines[tableno]
        guid_file.write("%s" %(lines[tableno]));



